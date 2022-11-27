package jogLib.customContent;

import org.bukkit.persistence.*;

public class CustomObject
{
	PersistentDataHolder object;
	
	CustomObject(PersistentDataHolder object)
	{
		this.object = object;
	}
	
	public CustomObjectType<? extends PersistentDataHolder> getType()
	{
		return ContentManager.getCustomObjectType(object);
	}
}