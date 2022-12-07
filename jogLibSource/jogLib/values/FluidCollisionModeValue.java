package jogLib.values;

import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.data.*;
import jogUtil.data.values.*;
import jogUtil.indexable.*;
import org.bukkit.*;

import java.util.*;

public class FluidCollisionModeValue extends Value<FluidCollisionMode, FluidCollisionMode>
{
	public FluidCollisionModeValue()
	{
		super();
	}
	
	public FluidCollisionModeValue(FluidCollisionMode mode)
	{
		super(mode);
	}
	
	@Override
	public FluidCollisionMode emptyValue()
	{
		return FluidCollisionMode.NEVER;
	}
	
	@Override
	public String asString()
	{
		return MaterialValue.convertOut(get().toString());
	}
	
	@Override
	public byte[] asBytes()
	{
		return StringValue.toByteData(get().toString());
	}
	
	@Override
	protected Value<FluidCollisionMode, FluidCollisionMode> makeCopy()
	{
		return new FluidCollisionModeValue(get());
	}
	
	@Override
	protected boolean checkDataEquality(Value<?, ?> value)
	{
		return value instanceof FluidCollisionModeValue other && other.get().equals(get());
	}
	
	@Override
	public void initArgument(Object[] data)
	{
	
	}
	
	@Override
	public String defaultName()
	{
		return "Fluid Collision Mode";
	}
	
	@Override
	public List<String> argumentCompletions(Indexer<Character> source, Executor executor)
	{
		ArrayList<String> completions = new ArrayList<>();
		for (FluidCollisionMode mode : FluidCollisionMode.values())
		{
			completions.add(MaterialValue.convertOut(mode.toString()));
		}
		return completions;
	}
	
	@TypeRegistry.ByteConsumer
	public static Consumer<Value<?, FluidCollisionMode>, Byte> getByteConsumer()
	{
		return (source ->
		{
			Consumer.ConsumptionResult<Value<?, String>, Byte> result = StringValue.getByteConsumer().consume(source);
			if (!result.success())
				return new Consumer.ConsumptionResult<>(source, result.description());
			String mode = (String)result.value().get();
			
			try
			{
				return new Consumer.ConsumptionResult<>(new FluidCollisionModeValue(FluidCollisionMode.valueOf(mode)), source);
			}
			catch (Exception e)
			{
				return new Consumer.ConsumptionResult<>(source, "Not a valid Fluid Collision Mode");
			}
		});
	}
	
	@TypeRegistry.CharacterConsumer
	public static Consumer<Value<?, FluidCollisionMode>, Character> getCharacterConsumer()
	{
		return (source ->
		{
			int index = source.position();
			for (FluidCollisionMode mode : FluidCollisionMode.values())
			{
				source.setPosition(index);
				if (StringValue.consumeSequence(source, MaterialValue.convertOut(mode.toString()), false))
				{
					return new Consumer.ConsumptionResult<>(new FluidCollisionModeValue(mode), source);
				}
			}
			source.setPosition(index);
			return new Consumer.ConsumptionResult<>(source, "Not a valid Fluid Collision Mode");
		});
	}
	
	@TypeRegistry.ValidationValues
	public static Value<?, FluidCollisionMode>[] validationValues()
	{
		return new FluidCollisionModeValue[] {
			new FluidCollisionModeValue(FluidCollisionMode.ALWAYS),
			new FluidCollisionModeValue(FluidCollisionMode.SOURCE_ONLY)
		};
	}
}