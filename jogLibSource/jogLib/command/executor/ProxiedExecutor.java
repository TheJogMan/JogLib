package jogLib.command.executor;

import org.bukkit.command.*;

public class ProxiedExecutor extends PluginExecutor
{
	ProxiedExecutor(ProxiedCommandSender sender)
	{
		super(sender);
	}
	
	@Override
	public ProxiedCommandSender sender()
	{
		return (ProxiedCommandSender)sender;
	}
}