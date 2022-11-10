package jogLib.command;

import jogUtil.commander.*;
import jogUtil.richText.*;
import org.bukkit.command.*;

public class PluginExecutor extends Executor
{
	final CommandSender sender;
	
	public PluginExecutor(CommandSender sender)
	{
		this.sender = sender;
	}
	
	@Override
	public void respond(RichString message)
	{
		sender.sendMessage(message.encode(EncodingType.CODED));
	}
}