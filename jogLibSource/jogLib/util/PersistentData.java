package jogLib.util;

import jogUtil.*;
import jogUtil.data.*;
import org.bukkit.*;
import org.bukkit.persistence.*;

public class PersistentData
{
	public static void setValue(PersistentDataHolder holder, NamespacedKey key, Value<?, ?> value)
	{
		holder.getPersistentDataContainer().set(key, new ValueDataType(value.type()), value);
	}
	
	public static <Type> Value<Type, ?> getValue(PersistentDataHolder holder, NamespacedKey key, Value<Type, ?> defaultValue)
	{
		Value<Type, ?> value = (Value<Type, ?>) holder.getPersistentDataContainer().get(key, new ValueDataType(defaultValue.type()));
		if (value == null)
			value = defaultValue;
		return value;
	}
	
	private static class ValueDataType implements PersistentDataType<byte[], Value<?, ?>>
	{
		TypeRegistry.RegisteredType type;
		
		ValueDataType(TypeRegistry.RegisteredType type)
		{
			this.type = type;
		}
		
		@Override
		public Value<?, ?> fromPrimitive(byte[] arg0, PersistentDataAdapterContext arg1)
		{
			Consumer.ConsumptionResult<Value<?, ?>, Byte> result = type.byteConsumer().consume(ByteArrayBuilder.indexer(arg0));
			if (result.success())
				return result.value();
			else
				return null;
		}
		
		@Override
		public Class<Value<?, ?>> getComplexType()
		{
			return (Class<Value<?, ?>>)type.typeClass();
		}
		
		@Override
		public Class<byte[]> getPrimitiveType()
		{
			return byte[].class;
		}
		
		@Override
		public byte[] toPrimitive(Value<?, ?> arg0, PersistentDataAdapterContext arg1)
		{
			return arg0.asBytes();
		}
	}
}