package jogLib.command.executor;

import org.bukkit.entity.*;

import java.util.*;

public class EntityExecutor extends PhysicalExecutor
{
	EntityExecutor(Entity entity)
	{
		super(entity);
	}
	
	@Override
	public Entity sender()
	{
		return (Entity)sender;
	}
	
	public EntityType getEntityType()
	{
		return sender().getType();
	}
	
	public UUID getUniqueID()
	{
		return sender().getUniqueId();
	}
}