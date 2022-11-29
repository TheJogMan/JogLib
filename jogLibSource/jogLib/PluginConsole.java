package jogLib;

import jogLib.command.executor.*;
import jogLib.command.filter.*;
import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.commander.argument.*;
import jogUtil.commander.command.*;
import jogUtil.commander.command.Command;
import jogUtil.data.values.StringValue;
import jogUtil.indexable.*;
import jogUtil.richText.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.*;
import org.bukkit.plugin.*;

import java.lang.reflect.*;
import java.util.*;

public class PluginConsole extends Console
{
	PluginConsole()
	{
		super("PluginConsole", '/', "Commands.");
		
		addContextFiller(new VanillaCommandFiller(this));
	}
	
	
	private static SimpleCommandMap getCommandMap()
	{
		try
		{
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);
			return (SimpleCommandMap)bukkitCommandMap.get(Bukkit.getServer());
		}
		catch (Exception e)
		{
			System.err.println("PluginConsole could not get command map!");
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	@Override
	public Result addComponent(CommandComponent component)
	{
		SimpleCommandMap commandMap = getCommandMap();
		if (commandMap == null)
			return new Result("Could not retrieve command map!");
		if (commandMap.getCommand(component.name()) == null)
		{
			ComponentHolder holder = new ComponentHolder(component);
			if (!commandMap.register(name(), holder))
				return new Result("Could not register component in server's command map.");
			Result result = super.addComponent(component);
			if (!result.success())
			{
				holder.unregister(commandMap);
				return new Result(RichStringBuilder.start("Could not add component to console: ").append(result.description()).build());
			}
			return new Result();
		}
		else
			return new Result("There is already a component with that name.");
	}
	
	private static class ComponentHolder extends org.bukkit.command.Command
	{
		final CommandComponent component;
		
		protected ComponentHolder(CommandComponent component)
		{
			super(component.name(), component.description().encode(EncodingType.CODED), "/" + component.name(), new ArrayList<>());
			this.component = component;
		}
		
		@Override
		public boolean execute(CommandSender commandSender, String commandLabel, String[] arguments)
		{
			return component.interpret(StringValue.indexer(rebuild(arguments)), PluginExecutor.convert(commandSender)).success();
		}
		
		@Override
		public List<String> tabComplete(CommandSender sender, String alias, String[] args)
		{
			return component.getCompletions(StringValue.indexer(rebuild(args)), PluginExecutor.convert(sender));
		}
		
		static String rebuild(String[] arguments)
		{
			StringBuilder argumentString = new StringBuilder();
			for (int index = 0; index < arguments.length; index++)
			{
				argumentString.append(arguments[index]);
				if (index < arguments.length - 1)
					argumentString.append(' ');
			}
			return argumentString.toString();
		}
	}
	
	private record VanillaCommandFiller(PluginConsole console) implements ContextFiller
		{
			
			@Override
			public Collection<CommandComponent> getComponents(Executor executor)
			{
				SimpleCommandMap commandMap = getCommandMap();
				if (commandMap == null)
					return null;
				
				ArrayList<CommandComponent> components = new ArrayList<>();
				HashMap<Plugin, Category> plugins = new HashMap<>();
				for (org.bukkit.command.Command command : commandMap.getCommands())
				{
					if (command instanceof ComponentHolder && console.equals(((ComponentHolder) command).component.parent()))
						continue;
					
					components.add(new VanillaCommand(command));
				}
				return components;
			}
		}
	
	private static class VanillaCommand extends Command
	{
		final org.bukkit.command.Command command;
		
		public VanillaCommand(org.bukkit.command.Command command)
		{
			super(command.getName(), getDescriptionFromUsage(command.getDescription(), command.getUsage()));
			command.getAliases().forEach(this::addAlias);
			this.command = command;
			if (command instanceof PluginCommand)
			{
				String usage = parseUsage(command.getUsage())[0];
				if (usage.length() > 0)
				{
					addArgument(VanillaArgumentsArgument.class, usage, new Object[] {false});
					if (usage.charAt(0) != '<')
						addArgumentList();
				}
			}
			else
			{
				addArgument(VanillaArgumentsArgument.class);
				addArgumentList();
			}
			addFilter(new PermissionFilter(command.getPermission(), command.getPermissionMessage()));
		}
		
		static String getDescriptionFromUsage(String baseDescription, String usage)
		{
			String furtherDescription = parseUsage(usage)[1];
			return baseDescription + (furtherDescription.length() > 0 ? '\n' + furtherDescription : "");
		}
		
		static String[] parseUsage(String usage)
		{
			StringBuilder arguments = new StringBuilder();
			StringBuilder furtherDescription = new StringBuilder();
			int position = usage.indexOf(' ');
			
			if (position != -1)
			{
				position++;
				boolean pastArguments = false;
				int bracketDepth = 0;
				while (position < usage.length())
				{
					char ch = usage.charAt(position);
					if (pastArguments)
						furtherDescription.append(ch);
					else if (ch == '<' || ch == '[')
					{
						if (bracketDepth == 0 && arguments.length() > 0)
							arguments.append(' ');
						bracketDepth++;
						arguments.append(ch);
					}
					else if (ch == '>' || ch == ']' && bracketDepth > 0)
					{
						bracketDepth--;
						arguments.append(ch);
					}
					else if (bracketDepth > 0)
						arguments.append(ch);
					else if (bracketDepth == 0 && ch != ' ')
					{
						pastArguments = true;
						furtherDescription.append(ch);
					}
					position++;
				}
			}
			
			return new String[] {arguments.toString(), furtherDescription.toString()};
		}
		
		@Override
		protected void execute(AdaptiveInterpretation result, Executor executor)
		{
			String[] arguments;
			if (result.listNumber() == 1)
			{
				if (result.value().length > 0)
				{
					String string = (String) result.value()[0];
					arguments = string.split(" ");
				}
				else
					arguments = new String[0];
			}
			else
				arguments = new String[0];
			command.execute(((PluginExecutor)executor).sender(), command.getLabel(), arguments);
		}
	}
	
	public static ReturnResult<Boolean> executeCommand(String command, PluginExecutor executor)
	{
		if (executor == null)
			return new ReturnResult<>("Must provide an executor");
		
		if (command == null || command.length() == 0)
			return new ReturnResult<>("Command string empty or null");
		
		if (command.charAt(0) == JogLib.commandConsole.prefix)
			command = command.substring(1);
		
		return JogLib.commandConsole.interpret(StringValue.indexer(command), executor);
	}
	
	static class CommandEventListener implements Listener
	{
		@EventHandler(priority = EventPriority.MONITOR)
		public void onServerCommand(ServerCommandEvent event)
		{
			executeCommand(event.getCommand(), PluginExecutor.convert(event.getSender()));
			event.setCancelled(true);
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
		{
			executeCommand(event.getMessage(), PluginExecutor.convert(event.getPlayer()));
			event.setCancelled(true);
		}
	}
	
	public static class VanillaArgumentsArgument extends PlainArgument<String>
	{
		boolean addBrackets = true;
		
		@Override
		public void initArgument(Object[] data)
		{
			if (data.length == 1 && data[0] instanceof Boolean value)
				addBrackets = value;
		}
		
		@Override
		public boolean addBrackets()
		{
			return addBrackets;
		}
		
		@Override
		public String defaultName()
		{
			return "Arguments";
		}
		
		@Override
		public List<String> argumentCompletions(Indexer<Character> source, Executor executor)
		{
			return null;
		}
		
		@Override
		public ReturnResult<String> interpretArgument(Indexer<Character> source, Executor executor)
		{
			return new ReturnResult<>(true, StringValue.consumeString(source));
		}
	}
}