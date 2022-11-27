package jogLib.command.filter;

import jogLib.command.executor.*;
import jogUtil.*;
import jogUtil.commander.*;
import org.bukkit.permissions.*;

public class PermissionFilter extends PluginExecutorFilter
{
	String permission;
	String deniedMessage;
	
	public PermissionFilter(String permission, String deniedMessage)
	{
		this.permission = permission;
		this.deniedMessage = deniedMessage;
	}
	
	@Override
	public Result canExecute(Executor executor)
	{
		Result result = super.canExecute(executor);
		if (permission == null)
			return new Result();
		Permissible permissible = ((PluginExecutor)executor).sender();
		if (permissible.hasPermission(permission))
			return new Result();
		else
			return new Result(deniedMessage);
	}
}