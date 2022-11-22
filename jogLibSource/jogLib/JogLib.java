package jogLib;

import jogLib.command.*;
import jogUtil.*;
import jogUtil.data.values.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.*;
import org.bukkit.plugin.java.*;

public class JogLib extends JavaPlugin
{
	public static PluginConsole commandConsole = new PluginConsole("PluginConsole", "Commands.");
	static JogLib jogLib;
	
	public JogLib()
	{
		jogLib = this;
	}
	
	@Override
	public void onEnable()
	{
		jogUtil.data.TypeRegistry.defaultValueStatus();
		
		Bukkit.getPluginManager().registerEvents(new CommandEventListener(commandConsole), this);
	}
	
	public static ReturnResult<Boolean> executeCommand(String command, PluginExecutor executor)
	{
		if (executor == null)
			return new ReturnResult<>("Must provide an executor");
		
		if (command == null || command.length() == 0)
			return new ReturnResult<>("Command string empty or null");
		
		if (command.charAt(0) == commandConsole.prefix)
			command = command.substring(1);
		
		return commandConsole.interpret(StringValue.indexer(command), executor);
	}
	
	private static class CommandEventListener implements Listener
	{
		PluginConsole console;
		
		CommandEventListener(PluginConsole console)
		{
			this.console = console;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onServerCommand(ServerCommandEvent event)
		{
			executeCommand(event.getCommand(), new PluginExecutor(event.getSender()));
			event.setCancelled(true);
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
		{
			executeCommand(event.getMessage(), new PluginExecutor(event.getPlayer()));
			event.setCancelled(true);
		}
	}
}