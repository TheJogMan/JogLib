package jogLib.values;

import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.data.*;
import jogUtil.data.values.*;
import jogUtil.indexable.*;
import jogUtil.richText.*;
import org.bukkit.util.Vector;

import java.util.*;

public class VectorValue extends Value<Vector, Vector>
{
	public VectorValue()
	{
		super();
	}
	
	public VectorValue(Vector vector)
	{
		super(vector);
	}
	
	@Override
	public Vector emptyValue()
	{
		return new Vector(0, 0, 0);
	}
	
	@Override
	public String asString()
	{
		return toString(get());
	}
	
	public static String toString(Vector vector)
	{
		return "{X: " + vector.getX() + ", Y: " + vector.getY() + ", Z: " + vector.getZ() + "}";
	}
	
	public static RichString toRichString(Vector vector)
	{
		RichStringBuilder builder = RichStringBuilder.start();
		builder.style(builder.style().color(RichColor.ORANGE));
		builder.append("{", builder.style().color(RichColor.AQUA));
		builder.append("X: ");
		builder.append(vector.getX() + "", builder.style().color(RichColor.WHITE));
		builder.append(", ", builder.style().color(RichColor.AQUA));
		builder.append("Y: ");
		builder.append(vector.getY() + "", builder.style().color(RichColor.WHITE));
		builder.append(", ", builder.style().color(RichColor.AQUA));
		builder.append("Z: ");
		builder.append(vector.getZ() + "", builder.style().color(RichColor.WHITE));
		builder.append("}", builder.style().color(RichColor.AQUA));
		return builder.build();
	}
	
	public static byte[] toBytes(Vector vector)
	{
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.add(vector.getX());
		builder.add(vector.getY());
		builder.add(vector.getZ());
		return builder.toPrimitiveArray();
	}
	
	@Override
	public byte[] asBytes()
	{
		return toBytes(get());
	}
	
	@Override
	protected Value<Vector, Vector> makeCopy()
	{
		return new VectorValue(get().clone());
	}
	
	@Override
	protected boolean checkDataEquality(Value<?, ?> value)
	{
		return value instanceof VectorValue other && other.get().equals(get());
	}
	
	@Override
	public void initArgument(Object[] data)
	{
	
	}
	
	@Override
	public String defaultName()
	{
		return "Vector";
	}
	
	@Override
	public List<String> argumentCompletions(Indexer<Character> source, Executor executor, Object[] data)
	{
		return null;
	}
	
	@TypeRegistry.ByteConsumer
	public static Consumer<Value<?, Vector>, Byte> getByteConsumer()
	{
		return ((source) ->
		{
			Consumer<Value<?, Double>, Byte> doubleConsumer = DoubleValue.getByteConsumer();
			
			Consumer.ConsumptionResult<Value<?, Double>, Byte> xResult = doubleConsumer.consume(source);
			if (!xResult.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start("Could not parse X: ").append(xResult.description()).build());
			
			Consumer.ConsumptionResult<Value<?, Double>, Byte> yResult = doubleConsumer.consume(source);
			if (!yResult.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start("Could not parse Y: ").append(yResult.description()).build());
			
			Consumer.ConsumptionResult<Value<?, Double>, Byte> zResult = doubleConsumer.consume(source);
			if (!zResult.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start("Could not parse Z: ").append(zResult.description()).build());
			
			return new Consumer.ConsumptionResult<>(new VectorValue(new Vector((double)xResult.value().get(), (double)yResult.value().get(), (double)zResult.value().get())), source);
		});
	}
	
	@TypeRegistry.CharacterConsumer
	public static Consumer<Value<?, Vector>, Character> getCharacterConsumer()
	{
		return ((source) ->
		{
			Consumer<Value<?, Double>, Character> doubleConsumer = DoubleValue.getCharacterConsumer();
			source.pushFilterState();
			source.addFilter(new Indexer.ExclusionFilter<>(' '));
			if (!StringValue.consumeSequence(source, "{x:", false))
				return new Consumer.ConsumptionResult<>(source, "Must begin with '{X:'");
			
			Consumer.ConsumptionResult<Value<?, Double>, Character> xResult = doubleConsumer.consume(source);
			if (!xResult.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start("Could not parse X: ").append(xResult.description()).build());
			
			if (!StringValue.consumeSequence(source, ",y:", false))
				return new Consumer.ConsumptionResult<>(source, "X coordinate must be followed by ',y:'");
			
			Consumer.ConsumptionResult<Value<?, Double>, Character> yResult = doubleConsumer.consume(source);
			if (!yResult.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start("Could not parse Y: ").append(yResult.description()).build());
			
			if (!StringValue.consumeSequence(source, ",z:", false))
				return new Consumer.ConsumptionResult<>(source, "Y coordinate must be followed by ',z:'");
			
			Consumer.ConsumptionResult<Value<?, Double>, Character> zResult = doubleConsumer.consume(source);
			if (!zResult.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start("Could not parse Z: ").append(zResult.description()).build());
			
			source.popFilterState();
			source.skip(new Character[]{' '});
			if (source.atEnd() || source.next() != '}')
				return new Consumer.ConsumptionResult<>(source, "Must end with '}'");
			
			return new Consumer.ConsumptionResult<>(new VectorValue(new Vector((double)xResult.value().get(), (double)yResult.value().get(), (double)zResult.value().get())), source);
		});
	}
	
	@TypeRegistry.ValidationValues
	public static Value<?, Vector>[] validationValues()
	{
		return new VectorValue[] {
			new VectorValue(new Vector(0, 0, 0)),
			new VectorValue(new Vector(1, 2, 3)),
			new VectorValue(new Vector(-4, 2.3, -8.7))
		};
	}
}