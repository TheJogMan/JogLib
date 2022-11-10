package jogLib;

import jogLib.command.*;
import jogUtil.Result;
import jogUtil.commander.command.*;
import jogUtil.data.values.StringValue;
import jogUtil.richText.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.command.Command;

import java.lang.reflect.*;
import java.util.*;

public class PluginConsole extends Console
{
	PluginConsole(String name, String description)
	{
		super(name, '/', description);
	}
	
	@Override
	public Result addComponent(CommandComponent component)
	{
		CommandMap commandMap;
		try
		{
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);
			commandMap = (CommandMap)bukkitCommandMap.get(Bukkit.getServer());
		}
		catch (Exception e)
		{
			return new Result("Could not retrieve command map. " + Result.describeThrowable(e));
		}
		if (commandMap.getCommand(component.name()) == null)
		{
			Result result = super.addComponent(component);
			if (result.success())
				commandMap.register(name(), new ComponentHolder(component));
			return new Result();
		}
		else
			return new Result("There is already a component with that name.");
	}
	
	private static class ComponentHolder extends Command
	{
		CommandComponent component;
		
		protected ComponentHolder(CommandComponent component)
		{
			super(component.name(), component.description().encode(EncodingType.CODED), "/" + component.name(), new ArrayList<>());
			this.component = component;
		}
		
		@Override
		public boolean execute(CommandSender commandSender, String commandLabel, String[] arguments)
		{
			return component.interpret(StringValue.indexer(rebuild(arguments)), new PluginExecutor(commandSender)).success();
		}
		
		@Override
		public List<String> tabComplete(CommandSender sender, String alias, String[] args)
		{
			return component.getCompletions(StringValue.indexer(rebuild(args)), new PluginExecutor(sender));
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
}