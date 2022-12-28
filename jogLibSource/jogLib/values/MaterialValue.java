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
	InterpretationConfig config = new InterpretationConfig();
	
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
		return config.boundingFormat.pack(convertOut(get().toString()));
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
		config = new InterpretationConfig(data);
	}
	
	@Override
	public String defaultName()
	{
		return "Material";
	}
	
	@Override
	public List<String> argumentCompletions(Indexer<Character> source, Executor executor, Object[] data)
	{
		return validOptions(config);
	}
	
	private static List<String> validOptions(InterpretationConfig config)
	{
		ArrayList<String> options = new ArrayList<>();
		for (Material material : Material.values())
		{
			if (!config.mustBeBlock || material.isBlock())
			{
				String materialName = convertOut(material.toString());
				if (!config.boundingFormat.equals(BoundingFormat.SPACE_TERMINATED))
					materialName = config.boundingFormat.pack(materialName);
				options.add(materialName);
			}
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
		InterpretationConfig config = new InterpretationConfig(data);
		return (source ->
		{
			Consumer.ConsumptionResult<String, Character> result = config.boundingFormat.consume(source);
			if (!result.success())
				return new Consumer.ConsumptionResult<>(source, result.description());
			String materialName = convertIn(config.boundingFormat.unpack(result.value()));
			Material material;
			try
			{
				material = Material.valueOf(materialName);
			}
			catch (Exception e)
			{
				return new Consumer.ConsumptionResult<>(source, "Not a valid Material.");
			}
			
			if (config.mustBeBlock && !material.isBlock())
				return new Consumer.ConsumptionResult<>(source, "Must be a block.");
			
			return new Consumer.ConsumptionResult<>(new MaterialValue(material), source);
		});
	}
	
	private static class InterpretationConfig
	{
		boolean mustBeBlock = false;
		BoundingFormat boundingFormat = BoundingFormat.QUOTES;
		
		InterpretationConfig()
		{
		
		}
		
		InterpretationConfig(Object[] data)
		{
			load(data);
		}
		
		void load(Object[] data)
		{
			if (data.length > 0 && data[0] instanceof Boolean mustBeBlock)
				this.mustBeBlock = mustBeBlock;
			if (data.length > 1 && data[1] instanceof BoundingFormat boundingFormat)
				this.boundingFormat = boundingFormat;
		}
	}
		
	public enum BoundingFormat
	{
		QUOTES(
		input ->
		{
			return StringValue.pack(input);
		},
		input ->
		{
			return StringValue.unpack(input);
		},
		source ->
		{
			if (source.atEnd() || source.next() != '"')
				return new Consumer.ConsumptionResult<>(source, "Must begin with '\"'");
			String result = StringValue.consumeString(source, '"');
			if (source.atEnd() || source.next() != '"')
				return new Consumer.ConsumptionResult<>(source, "Must end with '\"'");
			return new Consumer.ConsumptionResult<>('"' + result + '"', source);
		}),
		SPACE_TERMINATED(
		input ->
		{
			return input + ' ';
		},
		input ->
		{
			return input.substring(0, input.length() - 1);
		},
		source ->
		{
			return new Consumer.ConsumptionResult<>(StringValue.consumeString(source, ' ') + ' ', source);
		});
		
		final Converter packer;
		final Converter unpacker;
		final Consumer<String, Character> consumer;
		
		BoundingFormat(Converter packer, Converter unpacker, Consumer<String, Character> consumer)
		{
			this.packer = packer;
			this.unpacker = unpacker;
			this.consumer = consumer;
		}
		
		String pack(String input)
		{
			if (input == null)
				return null;
			
			return packer.convert(input);
		}
		
		String unpack(String input)
		{
			if (input == null)
				return null;
			
			return unpacker.convert(input);
		}
		
		Consumer.ConsumptionResult<String, Character> consume(Indexer<Character> source)
		{
			return consumer.consume(source);
		}
		
		interface Converter
		{
			String convert(String input);
		}
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
		if (name == null)
			return null;
		
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
		if (name == null)
			return null;
		
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