package jogLib.command.filter;

import jogLib.command.executor.*;
import jogUtil.*;
import jogUtil.commander.*;

public class PhysicalExecutorFilter implements ExecutorFilter.Filter
{
	@Override
	public Result canExecute(Executor executor)
	{
		return new Result(executor instanceof PhysicalExecutor);
	}
}