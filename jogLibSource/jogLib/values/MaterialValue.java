package jogLib.values;

import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.data.*;
import jogUtil.data.values.*;
import jogUtil.indexable.*;
import org.bukkit.*;

import java.util.*;

public class MaterialValue extends Value<Material, Material>
{
	boolean mustBeBlock = false;
	
	public MaterialValue()
	{
		super();
	}
	
	public MaterialValue(Material material)
	{
		super(material);
	}
	
	@Override
	public Material emptyValue()
	{
		return Material.AIR;
	}
	
	@Override
	public String asString()
	{
		return convertOut(get().toString());
	}
	
	@Override
	public byte[] asBytes()
	{
		return StringValue.toByteData(convertOut(get().toString()));
	}
	
	@Override
	protected Value<Material, Material> makeCopy()
	{
		return new MaterialValue(get());
	}
	
	@Override
	protected boolean checkDataEquality(Value<?, ?> value)
	{
		return value instanceof MaterialValue materialValue && materialValue.get().equals(get());
	}
	
	@Override
	public void initArgument(Object[] data)
	{
		if (data.length == 1)
			mustBeBlock = (Boolean)data[0];
	}
	
	@Override
	public String defaultName()
	{
		return "Material";
	}
	
	@Override
	public List<String> argumentCompletions(Indexer<Character> source, Executor executor)
	{
		return validOptions(mustBeBlock);
	}
	
	private static List<String> validOptions(boolean mustBeBlock)
	{
		ArrayList<String> options = new ArrayList<>();
		for (Material material : Material.values())
		{
			if (!mustBeBlock || material.isBlock())
				options.add(convertOut(material.toString()));
		}
		return options;
	}
	
	@TypeRegistry.ByteConsumer
	public static Consumer<Value<?, Material>, Byte> getByteConsumer()
	{
		return (source) ->
		{
			Consumer.ConsumptionResult<Value<?, String>, Byte> result = StringValue.getByteConsumer().consume(source);
			if (!result.success())
				return new Consumer.ConsumptionResult<>(source, result.description());
			try
			{
				Material material = Material.valueOf(convertIn((String)result.value().get()));
				return new Consumer.ConsumptionResult<>(new MaterialValue(material), source);
			}
			catch (Exception e)
			{
				return new Consumer.ConsumptionResult<>(source, "Not a valid Material.");
			}
		};
	}
	
	@TypeRegistry.CharacterConsumer
	public static Consumer<Value<?, Material>, Character> getCharacterConsumer(Object[] data)
	{
		boolean mustBeBlock = data.length == 1 ? (Boolean)data[0] : true;
		return (source ->
		{
			int index = source.position();
			for (Material material : Material.values())
			{
				if (mustBeBlock && !material.isBlock())
					continue;
				
				if (StringValue.consumeSequence(source, convertOut(material.toString()), false))
					return new Consumer.ConsumptionResult<>(new MaterialValue(material), source);
			}
			return new Consumer.ConsumptionResult<>(source, "Not a valid Material.");
		});
	}
	
	@TypeRegistry.ValidationValues
	public static Value<?, Material>[] validationValues()
	{
		return new MaterialValue[] {
			new MaterialValue(Material.AIR),
			new MaterialValue(Material.STONE_SLAB)
		};
	}
	
	public static String convertOut(String name)
	{
		StringBuilder newName = new StringBuilder();
		boolean capitalize = false;
		for (int index = 0; index < name.length(); index++)
		{
			char ch = name.charAt(index);
			if (ch == '_') capitalize = true;
			else
			{
				if (!capitalize) ch = Character.toLowerCase(ch);
				newName.append(ch);
				capitalize = false;
			}
		}
		return newName.toString();
	}
	
	public static String convertIn(String name)
	{
		StringBuilder newName = new StringBuilder();
		for (int index = 0; index < name.length(); index++)
		{
			char ch = name.charAt(index);
			if (Character.isUpperCase(ch))
			{
				newName.append("_").append(ch);
			}
			else newName.append(Character.toUpperCase(ch));
		}
		return newName.toString();
	}
}