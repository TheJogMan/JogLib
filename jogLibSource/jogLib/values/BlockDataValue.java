package jogLib.values;

import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.data.*;
import jogUtil.data.values.*;
import jogUtil.indexable.*;
import jogUtil.richText.*;
import org.bukkit.*;
import org.bukkit.block.data.*;

import java.util.*;

public class BlockDataValue extends Value<BlockData, BlockData>
{
	public BlockDataValue()
	{
		super();
	}
	
	public BlockDataValue(BlockData data)
	{
		super(data);
	}
	
	@Override
	public BlockData emptyValue()
	{
		return Bukkit.createBlockData(Material.AIR);
	}
	
	@Override
	public String asString()
	{
		return toString(get());
	}
	
	public static String toString(BlockData data)
	{
		String value = data.getAsString();
		if (value.charAt(value.length() - 1) != ']')
			value += "[]";
		return value;
	}
	
	public static byte[] toByteData(BlockData data)
	{
		return StringValue.toByteData(toString(data));
	}
	
	@Override
	public byte[] asBytes()
	{
		return toByteData(get());
	}
	
	@Override
	protected Value<BlockData, BlockData> makeCopy()
	{
		return new BlockDataValue(get().clone());
	}
	
	@Override
	protected boolean checkDataEquality(Value<?, ?> value)
	{
		return value instanceof BlockDataValue && get() != null && get().matches(((BlockDataValue) value).get());
	}
	
	@Override
	public void initArgument(Object[] data)
	{
	
	}
	
	@Override
	public String defaultName()
	{
		return "BlockData";
	}
	
	@Override
	public List<String> argumentCompletions(Indexer<Character> source, Executor executor)
	{
		return null;
	}
	
	@TypeRegistry.ByteConsumer
	public static Consumer<Value<?, BlockData>, Byte> getByteConsumer()
	{
		return ((source) ->
		{
			Consumer.ConsumptionResult<String, Byte> result = StringValue.primitiveByteConsume(source);
			if (!result.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start().append("Could not parse data: ").append(result.description()).build());
			try
			{
				return new Consumer.ConsumptionResult<>(new BlockDataValue(Bukkit.createBlockData(result.value())), source);
			}
			catch (IllegalArgumentException exception)
			{
				return new Consumer.ConsumptionResult<>(source, "Could not create blockdata: " + exception.getMessage());
			}
		});
	}
	
	@TypeRegistry.CharacterConsumer
	public static Consumer<Value<?, BlockData>, Character> getCharacterConsumer()
	{
		return ((source) ->
		{
			StringBuilder builder = new StringBuilder();
			String result = StringValue.consumeString(source, '[');
			builder.append(result).append('[');
			source.next();
			int depth = 1;
			while (!source.atEnd() && depth > 0)
			{
				char ch = source.next();
				if (ch == '[')
					depth++;
				else if (ch == ']')
					depth--;
				builder.append(ch);
			}
			if (depth > 0)
				return new Consumer.ConsumptionResult<>(source, "Could not parse data: unbalanced brackets.");
			
			String value = builder.toString();
			if (value.endsWith("[]"))
				value = value.substring(0, value.length() - 2);
			try
			{
				return new Consumer.ConsumptionResult<>(new BlockDataValue(Bukkit.createBlockData(value)), source);
			}
			catch (IllegalArgumentException exception)
			{
				return new Consumer.ConsumptionResult<>(source, "Could not create blockdata: " + exception.getMessage());
			}
		});
	}
	
	@TypeRegistry.ValidationValues
	public static Value<?, BlockData>[] validationValues()
	{
		return new BlockDataValue[] {
			new BlockDataValue(Bukkit.createBlockData(Material.AIR)),
			new BlockDataValue(Bukkit.createBlockData(Material.STONE_SLAB))
		};
	}
}