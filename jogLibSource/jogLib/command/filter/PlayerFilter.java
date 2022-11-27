package jogLib.command.filter;

import jogLib.command.executor.*;
import jogUtil.*;
import jogUtil.commander.*;

public class PlayerFilter implements ExecutorFilter.Filter
{
	@Override
	public Result canExecute(Executor executor)
	{
		return new Result("You must be a Player.", executor instanceof PlayerExecutor);
	}
}