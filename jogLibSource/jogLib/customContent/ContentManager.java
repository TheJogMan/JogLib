package jogLib.customContent;

import jogLib.*;
import jogLib.values.*;
import jogUtil.*;
import jogUtil.data.*;
import jogUtil.data.values.*;
import org.bukkit.*;
import org.bukkit.persistence.*;
import org.bukkit.plugin.*;

import java.util.*;

public class ContentManager
{
	static HashMap<NamespacedKey, CustomObjectType<?>> customObjectTypes;
	static final CustomDataType customDataType = new CustomDataType();
	static final NamespacedKey customDataKey = new NamespacedKey(JogLib.jogLib(), "CustomData");
	static final NamespacedKey invalidKey = new NamespacedKey(JogLib.jogLib(), "Invalid");
	
	public static void init(Plugin plugin)
	{
		if (customObjectTypes == null)
		{
			customObjectTypes = new HashMap<>();
			
			CustomItemType.CustomItemManager.init(plugin);
		}
	}
	
	public static List<CustomObjectType<?>> getTypesOf(Class<CustomObjectType<?>> superClass)
	{
		ArrayList<CustomObjectType<?>> list = new ArrayList<>();
		customObjectTypes.values().forEach(type ->
		{
			if (superClass.isAssignableFrom(type.getClass()))
				list.add(type);
		});
		return list;
	}
	
	static Data getObjectData(PersistentDataHolder holder)
	{
		return ((DataValue)getCustomData(holder).get("CustomObjectData", new DataValue())).get();
	}
	
	static void setObjectData(PersistentDataHolder holder, Data objectData)
	{
		Data data = getCustomData(holder);
		data.put("CustomObjectData", new DataValue(objectData));
		setCustomData(holder, data);
	}
	
	static void makeCustomObject(PersistentDataHolder holder, NamespacedKey type)
	{
		Data data = getCustomData(holder);
		data.put("CustomObjectType", new NamespacedKeyValue(type));
		setCustomData(holder, data);
	}
	
	static void setCustomData(PersistentDataHolder holder, Data data)
	{
		holder.getPersistentDataContainer().set(customDataKey, customDataType, new DataValue(data));
	}
	
	static Data getCustomData(PersistentDataHolder holder)
	{
		return holder.getPersistentDataContainer().getOrDefault(customDataKey, customDataType, new DataValue()).get();
	}
	
	public static boolean isCustomObject(PersistentDataHolder holder)
	{
		return getCustomData(holder).has("CustomObjectType");
	}
	
	public static CustomObject getCustomObject(PersistentDataHolder holder)
	{
		CustomObjectType<? extends PersistentDataHolder> type = getCustomObjectType(holder);
		if (type == null)
			return null;
		else
			return type.getObject(holder);
	}
	
	public static CustomObjectType<? extends PersistentDataHolder> getCustomObjectType(PersistentDataHolder holder)
	{
		NamespacedKey key = ((NamespacedKeyValue)getCustomData(holder).get("CustomObjectType", new NamespacedKeyValue(invalidKey))).get();
		return customObjectTypes.get(key);
	}
	
	static class CustomDataType implements PersistentDataType<byte[], DataValue>
	{
		@Override
		public DataValue fromPrimitive(byte[] arg0, PersistentDataAdapterContext arg1)
		{
			return new DataValue(Data.fromBytes(ByteArrayBuilder.indexer(arg0)));
		}
		
		@Override
		public Class<DataValue> getComplexType()
		{
			return DataValue.class;
		}
		
		@Override
		public Class<byte[]> getPrimitiveType()
		{
			return byte[].class;
		}
		
		@Override
		public byte[] toPrimitive(DataValue arg0, PersistentDataAdapterContext arg1)
		{
			return arg0.asBytes();
		}
	}
}