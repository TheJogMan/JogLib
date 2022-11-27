package jogLib.command.executor;

import org.bukkit.command.*;

public class ConsoleExecutor extends PluginExecutor
{
	ConsoleExecutor(ConsoleCommandSender sender)
	{
		super(sender);
	}
	
	@Override
	public ConsoleCommandSender sender()
	{
		return (ConsoleCommandSender)sender;
	}
}