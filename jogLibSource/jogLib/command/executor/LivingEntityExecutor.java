package jogLib.command.executor;

import org.bukkit.*;
import org.bukkit.entity.*;

public class LivingEntityExecutor extends EntityExecutor
{
	LivingEntityExecutor(LivingEntity entity)
	{
		super(entity);
	}
	
	@Override
	public LivingEntity sender()
	{
		return (LivingEntity)sender;
	}
	
	public Location getEyeLocation()
	{
		return sender().getEyeLocation();
	}
}