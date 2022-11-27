package jogLib.command.arguments;

import jogLib.command.executor.*;
import jogLib.command.filter.*;
import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.commander.argument.*;
import jogUtil.data.*;
import jogUtil.data.values.*;
import jogUtil.indexable.*;
import jogUtil.richText.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

import java.util.*;

public class CoordinateArgument extends PlainArgument<Double>
{
	CoordinateAxis axis;
	
	@Override
	public void initArgument(Object[] data)
	{
		if (data.length == 0 || !(data[0] instanceof CoordinateAxis))
			throw new RuntimeException("You must specify a CoordinateAxis");
		axis = (CoordinateAxis)data[0];
		addFilter(new PhysicalExecutorFilter());
	}
	
	@Override
	public String defaultName()
	{
		return axis.name();
	}
	
	@Override
	public List<String> argumentCompletions(Indexer<Character> source, Executor executor)
	{
		ArrayList<String> completions = new ArrayList<>();
		if (source.atEnd())
			completions.add("~");
		else if (((PhysicalExecutor)executor).sender() instanceof LivingEntity entity)
		{
			Block block = entity.getTargetBlockExact(5);
			if (block != null)
				completions.add(axis.getFromBlock.get(block) + "");
		}
		return completions;
	}
	
	@Override
	public ReturnResult<Double> interpretArgument(Indexer<Character> source, Executor executor)
	{
		if (source.atEnd())
			return new ReturnResult<>("Argument is empty");
		
		boolean relative = false;
		if (source.get() == '~')
		{
			relative = true;
			source.next();
			if (source.atEnd() || source.get() == ' ')
				return new ReturnResult<>(axis.getRelative.get((PhysicalExecutor)executor, 0));
		}
		Consumer.ConsumptionResult<Value<?, Double>, Character> result = DoubleValue.getCharacterConsumer().consume(source);
		if (!result.success())
			return new ReturnResult<>(RichStringBuilder.start().append("Could not parse double: ").append(result.description()).build());
		if (relative)
			return new ReturnResult<>(axis.getRelative.get((PhysicalExecutor)executor, (Double)result.value().get()));
		else
			return new ReturnResult<>((Double)result.value().get());
	}
	
	public enum CoordinateAxis
	{
		X((executor, offset) ->
		{
			return executor.getLocation().getX() + offset;
		},
		(block) ->
		{
			return block.getX();
		}),
		
		Y((executor, offset) ->
		{
			return executor.getLocation().getY() + offset;
		},
		(block) ->
		{
			return block.getY();
		}),
		
		Z((executor, offset) ->
		{
			return executor.getLocation().getZ() + offset;
		},
		(block) ->
		{
			return block.getZ();
		});
		
		final GetRelative getRelative;
		final GetFromBlock getFromBlock;
		
		CoordinateAxis(GetRelative getRelative, GetFromBlock getFromBlock)
		{
			this.getRelative = getRelative;
			this.getFromBlock = getFromBlock;
		}
		
		interface GetRelative
		{
			double get(PhysicalExecutor executor, double offset);
		}
		
		interface GetFromBlock
		{
			double get(Block block);
		}
	}
}