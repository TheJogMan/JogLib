package jogLib.customContent;

import org.bukkit.*;
import org.bukkit.persistence.*;

public abstract class CustomObjectType<HolderType extends PersistentDataHolder>
{
	NamespacedKey id;
	
	public CustomObjectType(NamespacedKey key)
	{
		if (ContentManager.customObjectTypes.containsKey(key))
			throw new RuntimeException("A custom object has already been registered as " + key.toString() + ", you can not register two objects with the same ID.");
		ContentManager.customObjectTypes.put(key, this);
		id = key;
	}
	
	public NamespacedKey id()
	{
		return id;
	}
	
	public HolderType create()
	{
		HolderType object = createObject();
		ContentManager.makeCustomObject(object, id);
		return object;
	}
	
	abstract CustomObject getObject(PersistentDataHolder holder);
	protected abstract HolderType createObject();
}