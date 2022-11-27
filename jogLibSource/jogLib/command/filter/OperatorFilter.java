package jogLib.command.filter;

import jogLib.command.executor.*;
import jogUtil.*;
import jogUtil.commander.*;

public class OperatorFilter implements ExecutorFilter.Filter
{
	@Override
	public Result canExecute(Executor executor)
	{
		return new Result("You must be an Operator.", (executor instanceof PluginExecutor) && ((PluginExecutor)executor).isOp());
	}
}