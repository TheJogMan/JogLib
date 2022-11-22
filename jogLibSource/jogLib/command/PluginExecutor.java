package jogLib.command;

import jogUtil.*;
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
	
	public CommandSender sender()
	{
		return sender;
	}
	
	public static class PluginExecutorFilter implements ExecutorFilter.Filter
	{
		@Override
		public Result canExecute(Executor executor)
		{
			if (executor instanceof PluginExecutor)
				return new Result();
			else
				return new Result("Not a plugin executor.");
		}
	}
}