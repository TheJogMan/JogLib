package jogLib.values;

import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.data.*;
import jogUtil.data.values.*;
import jogUtil.indexable.*;
import org.bukkit.*;
import org.bukkit.plugin.*;

import java.util.*;

public class NamespacedKeyValue extends Value<NamespacedKey, NamespacedKey>
{
	public NamespacedKeyValue()
	{
		super();
	}
	
	public NamespacedKeyValue(NamespacedKey key)
	{
		super(key);
	}
	
	@Override
	public NamespacedKey emptyValue()
	{
		return Material.VOID_AIR.getKey();
	}
	
	@Override
	public String asString()
	{
		return (new StringValue(get().toString())).toString();
	}
	
	@Override
	public byte[] asBytes()
	{
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.add(get().getNamespace());
		builder.add(get().getKey());
		return builder.toPrimitiveArray();
	}
	
	@Override
	protected Value<NamespacedKey, NamespacedKey> makeCopy()
	{
		NamespacedKey key = get();
		if (key.getNamespace().equals(NamespacedKey.MINECRAFT))
			key = NamespacedKey.minecraft(key.getKey());
		else
			key = new NamespacedKey(Bukkit.getPluginManager().getPlugin(key.getNamespace()), key.getKey());
		return new NamespacedKeyValue(key);
	}
	
	@Override
	protected boolean checkDataEquality(Value<?, ?> value)
	{
		if (value instanceof NamespacedKeyValue)
			return ((NamespacedKeyValue)value).get().equals(get());
		else
			return false;
	}
	
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
	
	@TypeRegistry.CharacterConsumer
	public static Consumer<Value<?, NamespacedKey>, Character> getCharacterConsumer()
	{
		return ((source) ->
		{
			Consumer.ConsumptionResult<String, Character> result = StringValue.primitiveCharacterConsume(source);
			if (!result.success())
				return new Consumer.ConsumptionResult<>(source, result.description());
			else
			{
				NamespacedKey key = NamespacedKey.fromString(result.value());
				if (key == null)
					return new Consumer.ConsumptionResult<>(source, "Invalid format.");
				else
					return new Consumer.ConsumptionResult<>(new NamespacedKeyValue(key), source);
			}
		});
	}
	
	@TypeRegistry.ByteConsumer
	public static Consumer<Value<?, NamespacedKey>, Byte> getByteConsumer()
	{
		return ((source) ->
		{
			Consumer.ConsumptionResult<String, Byte> namespaceResult = StringValue.primitiveByteConsume(source);
			if (!namespaceResult.success())
				return new Consumer.ConsumptionResult<>(source, "Could not parse namespace: " + namespaceResult.description());
			Consumer.ConsumptionResult<String, Byte> keyResult = StringValue.primitiveByteConsume(source);
			if (!keyResult.success())
				return new Consumer.ConsumptionResult<>(source, "Could not parse key: " + keyResult.description());
			String namespace = namespaceResult.value();
			if (namespace.equals(NamespacedKey.MINECRAFT))
				return new Consumer.ConsumptionResult<>(new NamespacedKeyValue(NamespacedKey.minecraft(keyResult.value())), source);
			else
			{
				Plugin plugin = getPlugin(namespace);
				if (plugin == null)
					return new Consumer.ConsumptionResult<>(source, "There is no currently loaded plugin named " + namespace);
				else
					return new Consumer.ConsumptionResult<>(new NamespacedKeyValue(new NamespacedKey(plugin, keyResult.value())), source);
			}
		});
	}
	
	private static Plugin getPlugin(String name)
	{
		Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
		for (Plugin plugin : plugins)
		{
			if (plugin.getName().toLowerCase().equals(name))
				return plugin;
		}
		return null;
	}
	
	@TypeRegistry.ValidationValues
	public static Value<?, NamespacedKey>[] validationValues()
	{
		return new NamespacedKeyValue[] {
				new NamespacedKeyValue(Material.AIR.getKey()),
				new NamespacedKeyValue(Material.STONE_SLAB.getKey())
		};
	}
}