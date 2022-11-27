package jogLib.command.executor;

import org.bukkit.command.*;

public class RemoteConsoleExecutor extends PluginExecutor
{
	RemoteConsoleExecutor(RemoteConsoleCommandSender sender)
	{
		super(sender);
	}
	
	@Override
	public RemoteConsoleCommandSender sender()
	{
		return (RemoteConsoleCommandSender)sender;
	}
}