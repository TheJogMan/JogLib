package jogLib.command.executor;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

public abstract class PhysicalExecutor extends PluginExecutor
{
	PhysicalExecutor(Entity entity)
	{
		super(entity);
	}
	
	PhysicalExecutor(BlockCommandSender block)
	{
		super(block);
	}
	
	public Location getLocation()
	{
		if (sender instanceof Entity entity)
			return entity.getLocation();
		else
			return ((BlockCommandSender)sender).getBlock().getLocation();
	}
}