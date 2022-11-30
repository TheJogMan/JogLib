package jogLib;

import jogLib.command.filter.*;
import jogUtil.*;
import jogUtil.commander.command.*;
import jogUtil.data.*;
import jogUtil.richText.*;

import java.io.*;

public class PluginConfig extends Config
{
	private final Category configCategory;
	
	PluginConfig(JogPlugin plugin)
	{
		super(new File(plugin.getDataFolder() + "/config.txt"), plugin.getName(), "Configuration options for " + plugin.getName());
		configCategory = new Category(rootConfigCategory, plugin.getName(), "Configuration options for " + plugin.getName());
	}
	
	@Override
	public <Type> Setting<Type> createSetting(String name, TypeRegistry.RegisteredType type, Value<Type, Type> defaultValue, String description)
	{
		return createSetting(name, type, defaultValue, new RichString(description), new Object[0]);
	}
	
	@Override
	public <Type> Setting<Type> createSetting(String name, TypeRegistry.RegisteredType type, Value<Type, Type> defaultValue, String description, Object[] argumentData)
	{
		return createSetting(name, type, defaultValue, new RichString(description));
	}
	
	@Override
	public <Type> Setting<Type> createSetting(String name, TypeRegistry.RegisteredType type, Value<Type, Type> defaultValue, RichString description)
	{
		return createSetting(name, type, defaultValue, description, new Object[0]);
	}
	
	@Override
	public <Type> Setting<Type> createSetting(String name, TypeRegistry.RegisteredType type, Value<Type, Type> defaultValue, RichString description, Object[] argumentData)
	{
		Setting<Type> setting = super.createSetting(name, type, defaultValue, description, argumentData);
		setting.addCommands(configCategory);
		return setting;
	}
	
	private static final ConfigCategory rootConfigCategory = new ConfigCategory();
	
	static class ConfigCategory extends Category
	{
		ConfigCategory()
		{
			super(JogLib.commandConsole, "Config", "Configuration options for JogLib plugins.");
			addFilter(new OperatorFilter());
		}
	}
}