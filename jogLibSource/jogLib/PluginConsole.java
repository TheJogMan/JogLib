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
import org.bukkit.command.defaults.*;
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
	
	
	static SimpleCommandMap getCommandMap()
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
			super(command.getName(), command.getDescription());
			command.getAliases().forEach(this::addAlias);
			this.command = command;
			String[] usage = null;
			if (command instanceof BukkitCommand && command.getDescription().equals("A Mojang provided command."))
			{
				if (vanillaUsages.containsKey(command.getName()))
					usage = vanillaUsages().get(command.getName());
				else
					Bukkit.getLogger().warning(command.getName() + " is a vanilla command with no set usage info, using default.");
			}
			else
			{
				String usageString = command.getUsage();
				int index = usageString.indexOf(' ');
				if (index != -1)
					usage = new String[] {usageString.substring(index + 1)};
				else
					usage = new String[] {""};
			}
			
			if (usage == null)
				addArgument(VanillaArgumentsArgument.class);
			else
			{
				boolean addEmpty = false;
				boolean argumentsAdded = false;
				for (String variant : usage)
				{
					if (variant.length() > 0)
					{
						argumentsAdded = true;
						addArgument(VanillaArgumentsArgument.class, variant, new Object[]{false});
						if (variant.charAt(0) == '[')
							addEmpty = true;
					}
					else
						addEmpty = true;
				}
				if (addEmpty && argumentsAdded)
					addArgumentList();
			}
			addFilter(new PermissionFilter(command.getPermission(), command.getPermissionMessage()));
		}
		
		@Override
		protected void execute(AdaptiveInterpretation result, Executor executor)
		{
			String[] arguments;
			if (result.value().length > 0)
			{
				String string = (String) result.value()[0];
				arguments = string.split(" ");
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
		public List<String> argumentCompletions(Indexer<Character> source, Executor executor, Object[] data)
		{
			return null;
		}
		
		@Override
		public ReturnResult<String> interpretArgument(Indexer<Character> source, Executor executor, Object[] data)
		{
			return new ReturnResult<>(true, StringValue.consumeString(source));
		}
	}
	
	private static final HashMap<String, String[]> vanillaUsages = vanillaUsages();
	
	private static HashMap<String, String[]> vanillaUsages()
	{
		HashMap<String, String[]> usages = new HashMap<>();
		
		usages.put("give", new String[] {"<targets> <item> [<count>]"});
		usages.put("summon", new String[] {"<entity> [<pos>]"});
		usages.put("defaultgamemode", new String[] {"<gamemode>"});
		usages.put("spreadplayers", new String[] {"<center> <spreadDistance> <maxRange> (<respectTeams>|under)"});
		usages.put("jfr", new String[] {"<start|stop>"});
		usages.put("tellraw", new String[] {"<targets> <message>"});
		usages.put("bossbar", new String[] {"add <id> <name>", "remove <id>", "list", "set <id> (name|color|style|value|max|visible|players", "get <id> (value|max|visible|players)"});
		usages.put("schedule", new String[] {"function <function> <time> [append|replace]", "clear <function>"});
		usages.put("weather", new String[] {"clear [<duration>]", "rain [<duration>]", "thunder [<duration>]"});
		usages.put("effect", new String[] {"clear [<targets>]", "give <targets> <effect> [<seconds>]"});
		usages.put("stopsound", new String[] {"<targets> [*|master|music|record|weather|block|hostile|neutral|player|ambient|voice]"});
		usages.put("place", new String[] {"feature <feature> [<pos>]", "jigsaw <pool> <target> <max_depth> [<position>]", "structure <structure> [<pos>]", "template <template> [<pos>]"});
		usages.put("clear", new String[] {"[<targets>] [<item>]"});
		usages.put("locate", new String[] {"structure <structure>", "biome <biome>", "poi <poi>"});
		usages.put("teleport", new String[] {"<location>", "<destination>", "<targets> (<location>|<destination>)"});
		usages.put("debug", new String[] {"start", "stop", "function <name>"});
		usages.put("recipe", new String[] {"give <targets> (<recipe>|*)", "take <targets> (<recipe>|*)"});
		usages.put("title", new String[] {"<targets> (clear|reset|title|subtitle|actionbar|times)"});
		usages.put("fill", new String[] {"<from> <to> <block> [replace|keep|outline|hollow|destroy]"});
		usages.put("clone", new String[] {"<begin> <end> <destination> [replace|masked|filtered]"});
		usages.put("worldborder", new String[] {"add <distance> [<time>]", "set <distance> [<time>]", "center <pos>", "damage (amount|buffer)", "get", "warning (distance|time)"});
		usages.put("teammsg", new String[] {"<message>"});
		usages.put("say", new String[] {"<message>"});
		usages.put("gamerule", new String[] {"<gamerule> [<value>]"});
		usages.put("setblock", new String[] {"<pos> <block> [destroy|keep|replace]"});
		usages.put("kill", new String[] {"[<targets>]"});
		usages.put("xp", new String[] {"\"(add|set) <targets> <amount> [points|levels]\", \"query <targets> (points|levels)\""});
		usages.put("time", new String[] {"set (day|noon|night|midnight|<time>)", "add <time>", "query (daytime|gametime|day)"});
		usages.put("datapack", new String[] {"enable <name> [after|before|last|first]", "disable <name>", "list [available|enabled]"});
		usages.put("setworldspawn", new String[] {"[<pos>] [<angle>]"});
		usages.put("difficulty", new String[] {"[(peaceful|easy|normal|hard)]"});
		usages.put("fillbiome", new String[] {"<from> <to> <biome> [replace]"});
		usages.put("spawnpoint", new String[] {"[<targets>] [<pos>]"});
		usages.put("banlist", new String[] {"[ips]", "[players]"});
		usages.put("pardon", new String[] {"<targets>"});
		usages.put("pardon-ip", new String[] {"<target>"});
		usages.put("stop", new String[] {""});
		usages.put("save-all", new String[] {"[flush]"});
		usages.put("perf", new String[] {"start", "stop"});
		usages.put("setidletimeout", new String[] {"<minutes>"});
		usages.put("op", new String[] {"<targets>"});
		usages.put("list", new String[] {"[uuids]"});
		usages.put("data", new String[] {"(merge|get|remove|modify) (entity|block|storage)"});
		usages.put("ban", new String[] {"<targets> [<reason>]"});
		usages.put("function", new String[] {"<name>"});
		usages.put("execute", new String[] {"{(if|unless) (block|biome|score|blocks|entity|predicate|data)|(as|at) <targets>|store (result|success)|positioned (<pos>|as)" +
											"|rotated (<rot>|as)|facing (entity|<pos>)|align <axes>|anchored <anchor>|in <dimension>} run <command>"});
		usages.put("playsound", new String[] {"<sound> (master|music|record|weather|block|hostile|neutral|player|ambient|voice)"});
		usages.put("ban-ip", new String[] {"<target> [<reason>]"});
		usages.put("forceload", new String[] {"add <from> [<to>]", "remove (<from>|all)", "query [<pos>]"});
		usages.put("tp", new String[] {"\"<location>\", \"<destination>\", \"<targets> (<location>|<destination>)\""});
		usages.put("tag", new String[] {"<targets> (add|remove|list)"});
		usages.put("loot", new String[] {"replace (entity|block)", "(insert|spawn) <targetPos> (fish|loot|kill|mine)", "give <players> (fish|loot|kill|mine)"});
		usages.put("me", new String[] {"action"});
		usages.put("tm", new String[] {"<message>"});
		usages.put("trigger", new String[] {"<objective> [add|set]"});
		usages.put("save-on", new String[] {""});
		usages.put("save-off", new String[] {""});
		usages.put("experience", new String[] {"(add|set) <targets> <amount> [points|levels]", "query <targets> (points|levels)"});
		usages.put("particle", new String[] {"<name> [<pos>]"});
		usages.put("tell", new String[] {"<targets> <message>"});
		usages.put("msg", new String[] {"<targets> <message>"});
		usages.put("team", new String[] {"list [<team>]", "add <team> [<displayName>]", "(remove|empty|leave) <team>", "join <team> [<members>]", "modify <team> <setting>"});
		usages.put("kick", new String[] {"targets [<reason>]"});
		usages.put("deop", new String[] {"<targets>"});
		usages.put("attribute", new String[] {"<target> <attribute> (get|base|modifier)"});
		usages.put("gamemode", new String[] {"<gamemode> [<target>]"});
		usages.put("scoreboard", new String[] {"objectives (list|add|modify|remove|setdisplay)", "players (list|set|get|add|remove|reset|enable|operation)"});
		usages.put("seed", new String[] {""});
		usages.put("whitelist", new String[] {"(on|off|list|reload)", "(add|remove) <targets>"});
		usages.put("item", new String[] {"(replace|modify) (block|entity)"});
		usages.put("reload", new String[] {""});
		usages.put("advancement", new String[] {"(grant|revoke) <targets> (only|from|until|through|everything)"});
		usages.put("spectate", new String[] {"[<target>] [<player>]"});
		usages.put("enchant", new String[] {"<targets> <enchantment> [<level>]"});
		usages.put("w", new String[] {"clear [<duration>]", "rain [<duration>]", "thunder [<duration>]"});
		usages.put("help", new String[] {"[<command>]"});
		
		return usages;
	}
}