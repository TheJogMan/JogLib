package jogLib.command.filter;

import jogLib.command.executor.*;
import jogUtil.*;
import jogUtil.commander.*;

public class PluginExecutorFilter implements ExecutorFilter.Filter
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