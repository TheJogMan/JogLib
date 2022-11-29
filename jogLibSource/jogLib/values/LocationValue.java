package jogLib.values;

import jogLib.command.arguments.*;
import jogLib.command.executor.*;
import jogLib.command.filter.*;
import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.commander.argument.*;
import jogUtil.data.*;
import jogUtil.data.values.*;
import jogUtil.indexable.*;
import jogUtil.richText.*;
import org.bukkit.*;

import java.util.*;

public class LocationValue extends CompoundArgumentValue<Location, Location>
{
	public LocationValue()
	{
		super();
	}
	
	public LocationValue(Location location)
	{
		super(location);
	}
	
	@Override
	public Location emptyValue()
	{
		return new Location(null, 0, 0, 0);
	}
	
	@Override
	public String asString()
	{
		StringBuilder builder = new StringBuilder();
		Location location = get();
		builder.append("{World: ").append(location.getWorld() == null ? "Null" : location.getWorld().getName()).append(",");
		builder.append("X: ").append((new DoubleValue(location.getX())).asString()).append(",");
		builder.append("Y: ").append((new DoubleValue(location.getY())).asString()).append(",");
		builder.append("Z: ").append((new DoubleValue(location.getZ())).asString()).append(",");
		builder.append("Pitch: ").append((new FloatValue(location.getPitch())).asString()).append(",");
		builder.append("Yaw: ").append((new FloatValue(location.getYaw())).asString()).append("}");
		return builder.toString();
	}
	
	@Override
	public byte[] asBytes()
	{
		Location location = get();
		ByteArrayBuilder builder = new ByteArrayBuilder();
		UUID id;
		if (location.getWorld() != null)
			id = location.getWorld().getUID();
		else
			id = new UUID(0, 0);
		builder.add(id);
		builder.add(location.getX());
		builder.add(location.getY());
		builder.add(location.getZ());
		builder.add(location.getPitch());
		builder.add(location.getYaw());
		return builder.toPrimitiveArray();
	}
	
	@Override
	protected Value<Location, Location> makeCopy()
	{
		return new LocationValue(get().clone());
	}
	
	@Override
	protected boolean checkDataEquality(Value<?, ?> value)
	{
		return value instanceof LocationValue && ((LocationValue) value).get().equals(get());
	}
	
	@TypeRegistry.ByteConsumer
	public static Consumer<Value<?, Location>, Byte> getByteConsumer()
	{
		return ((source) ->
		{
			Consumer.ConsumptionResult<Value<?, UUID>, Byte> worldID = UUIDValue.getByteConsumer().consume(source);
			if (!worldID.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start().append("Could not parse world id: ").append(worldID.description()).build());
			
			Consumer.ConsumptionResult<Value<?, Double>, Byte> xCoordinate = DoubleValue.getByteConsumer().consume(source);
			if (!xCoordinate.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start().append("Could not parse x coordinate: ").append(xCoordinate.description()).build());
			
			Consumer.ConsumptionResult<Value<?, Double>, Byte> yCoordinate = DoubleValue.getByteConsumer().consume(source);
			if (!yCoordinate.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start().append("Could not parse y coordinate: ").append(yCoordinate.description()).build());
			
			Consumer.ConsumptionResult<Value<?, Double>, Byte> zCoordinate = DoubleValue.getByteConsumer().consume(source);
			if (!zCoordinate.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start().append("Could not parse z coordinate: ").append(zCoordinate.description()).build());
			
			Consumer.ConsumptionResult<Value<?, Float>, Byte> pitch = FloatValue.getByteConsumer().consume(source);
			if (!pitch.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start().append("Could not parse pitch: ").append(pitch.description()).build());
			
			Consumer.ConsumptionResult<Value<?, Float>, Byte> yaw = FloatValue.getByteConsumer().consume(source);
			if (!yaw.success())
				return new Consumer.ConsumptionResult<>(source, RichStringBuilder.start().append("Could not parse yaw: ").append(yaw.description()).build());
			
			UUID id = (UUID)worldID.value().get();
			World world;
			if (id.getLeastSignificantBits() == 0 && id.getMostSignificantBits() == 0)
				world = null;
			else
			{
				world = Bukkit.getWorld((UUID)worldID.value().get());
				if (world == null)
					return new Consumer.ConsumptionResult<>(source, "There is no world with the id '" + worldID.value().get() + "'");
			}
			
			double x = (double)xCoordinate.value().get();
			double y = (double)yCoordinate.value().get();
			double z = (double)zCoordinate.value().get();
			float yawValue = (float)yaw.value().get();
			float pitchValue = (float)pitch.value().get();
			return new Consumer.ConsumptionResult<>(new LocationValue(new Location(world, x, y, z, yawValue, pitchValue)), source);
		});
	}
	
	@TypeRegistry.ValidationValues
	public static Value<?, Location>[] validationValues()
	{
		return new LocationValue[] {
			new LocationValue(new Location(null, 12, 34.7, 90, .1f, 33f))
		};
	}
	
	@TypeRegistry.ArgumentList
	public static AdaptiveArgumentList argumentList(Object[] initData)
	{
		AdaptiveArgumentList list = new AdaptiveArgumentList();
		list.addArgument(InternalLocationArgument.class);
		list.addList();
		list.addArgument(1, CoordinateArgument.class, new Object[] {CoordinateArgument.CoordinateAxis.X});
		list.addArgument(1, CoordinateArgument.class, new Object[] {CoordinateArgument.CoordinateAxis.Y});
		list.addArgument(1, CoordinateArgument.class, new Object[] {CoordinateArgument.CoordinateAxis.Z});
		list.addFilterToList(1, new PhysicalExecutorFilter());
		return list;
	}
	
	@TypeRegistry.BuildValue
	public static Location valueBuilder(AdaptiveInterpretation result, Executor executor)
	{
		if (result.listNumber() == 0)
			return (Location)result.value()[0];
		else
			return new Location(((PhysicalExecutor)executor).getLocation().getWorld(), (Double)result.value()[0], (Double)result.value()[1], (Double)result.value()[2]);
	}
	
	public static class InternalLocationArgument extends PlainArgument<Location>
	{
		@Override
		public void initArgument(Object[] data)
		{
		
		}
		
		@Override
		public String defaultName()
		{
			return null;
		}
		
		@Override
		public List<String> argumentCompletions(Indexer<Character> source, Executor executor)
		{
			return null;
		}
		
		@Override
		public ReturnResult<Location> interpretArgument(Indexer<Character> source, Executor executor)
		{
			source.pushFilterState();
			source.addFilter(new Indexer.ExclusionFilter<>(Data.formattingCharacters));
			if (!StringValue.consumeSequence(source, "{World:"))
				return new ReturnResult<>("Invalid format. Must begin with '{World:'");
			String worldName = StringValue.consumeString(source, ',');
			if (source.next() != ',')
				return new ReturnResult<>("Invalid format, expected ',' after world name.");
			
			if (!StringValue.consumeSequence(source, "X:"))
				return new ReturnResult<>("Invalid format. X coordinate must begin with 'X:'");
			Consumer.ConsumptionResult<Value<?, Double>, Character> xCoordinate = DoubleValue.getCharacterConsumer().consume(source);
			if (!xCoordinate.success())
				return new ReturnResult<>(RichStringBuilder.start().append("Could not parse X Coordinate: ").append(xCoordinate.description()).build());
			if (source.next() != ',')
				return new ReturnResult<>("Invalid format, expected ',' after x coordinate.");
			
			if (!StringValue.consumeSequence(source, "Y:"))
				return new ReturnResult<>("Invalid format. Y coordinate must begin with 'Y:'");
			Consumer.ConsumptionResult<Value<?, Double>, Character> yCoordinate = DoubleValue.getCharacterConsumer().consume(source);
			if (!yCoordinate.success())
				return new ReturnResult<>(RichStringBuilder.start().append("Could not parse Y Coordinate: ").append(yCoordinate.description()).build());
			if (source.next() != ',')
				return new ReturnResult<>("Invalid format, expected ',' after y coordinate.");
			
			if (!StringValue.consumeSequence(source, "Z:"))
				return new ReturnResult<>("Invalid format. Z coordinate must begin with 'Z:'");
			Consumer.ConsumptionResult<Value<?, Double>, Character> zCoordinate = DoubleValue.getCharacterConsumer().consume(source);
			if (!zCoordinate.success())
				return new ReturnResult<>(RichStringBuilder.start().append("Could not parse Z Coordinate: ").append(zCoordinate.description()).build());
			if (source.next() != ',')
				return new ReturnResult<>("Invalid format, expected ',' after z coordinate.");
			
			if (!StringValue.consumeSequence(source, "Pitch:"))
				return new ReturnResult<>("Invalid format. Pitch must begin with 'Pitch:'");
			Consumer.ConsumptionResult<Value<?, Float>, Character> pitch = FloatValue.getCharacterConsumer().consume(source);
			if (!pitch.success())
				return new ReturnResult<>(RichStringBuilder.start().append("Could not parse Pitch: ").append(pitch.description()).build());
			if (source.next() != ',')
				return new ReturnResult<>("Invalid format, expected ',' after Pitch.");
			
			if (!StringValue.consumeSequence(source, "Yaw:"))
				return new ReturnResult<>("Invalid format. Yaw must begin with 'Yaw:'");
			Consumer.ConsumptionResult<Value<?, Float>, Character> yaw = FloatValue.getCharacterConsumer().consume(source);
			if (!yaw.success())
				return new ReturnResult<>(RichStringBuilder.start().append("Could not parse Yaw: ").append(yaw.description()).build());
			
			/*
			we want to make sure we aren't consuming any white space after our closing bracket,
			so we remove the filter before checking for it, and we will manually skip past any remaining
			formatting characters
			 */
			source.popFilterState();
			source.skip(Data.formattingCharacters);
			if (source.next() != '}')
				return new ReturnResult<>("Invalid format, expected '}' after Yaw.");
			
			World world;
			if (worldName.equalsIgnoreCase("null"))
				world = null;
			else
			{
				world = Bukkit.getWorld(worldName);
				if (world == null)
					return new ReturnResult<>("There is no world named '" + worldName + "'");
			}
			double x = (double)xCoordinate.value().get();
			double y = (double)yCoordinate.value().get();
			double z = (double)zCoordinate.value().get();
			float yawValue = (float)yaw.value().get();
			float pitchValue = (float)pitch.value().get();
			return new ReturnResult<>(new Location(world, x, y, z, yawValue, pitchValue));
		}
	}
}