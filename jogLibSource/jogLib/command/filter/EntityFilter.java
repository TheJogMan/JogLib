package jogLib.command.filter;

import jogLib.command.executor.*;
import jogUtil.*;
import jogUtil.commander.*;
import org.bukkit.entity.*;

public class EntityFilter implements ExecutorFilter.Filter
{
	public EntityFilter()
	{
		this(new EntityType[0], FilterType.BLACKLIST);
	}
	
	public EntityFilter(EntityType type)
	{
		this(new EntityType[] {type});
	}
	
	public EntityFilter(EntityType type, FilterType filter)
	{
		this(new EntityType[] {type}, filter);
	}
	
	public EntityFilter(EntityType[] types)
	{
		this(types, FilterType.WHITELIST);
	}
	
	public EntityFilter(EntityType[] types, FilterType filter)
	{
		this.types = types;
		this.type = filter;
	}
	
	final EntityType[] types;
	final FilterType type;
	
	@Override
	public Result canExecute(Executor executor)
	{
		if (executor instanceof EntityExecutor)
			return new Result(type.filterer.canExecute(((EntityExecutor)executor).getEntityType(), types));
		else
			return new Result("You must be an Entity.");
	}
	
	public enum FilterType
	{
		WHITELIST((type, types) ->
		{
			for (EntityType entityType : types)
			{
				if (type.equals(entityType))
					return true;
			}
			return false;
		}),
		BLACKLIST((type, types) ->
		{
			for (EntityType entityType : types)
			{
				if (type.equals(entityType))
					return false;
			}
			return true;
		});
		
		final Filterer filterer;
		
		FilterType(Filterer filterer)
		{
			this.filterer = filterer;
		}
		
		interface Filterer
		{
			boolean canExecute(EntityType type, EntityType[] types);
		}
	}
}