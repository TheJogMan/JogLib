package jogLib;

import org.bukkit.plugin.java.*;

public class JogLib extends JavaPlugin
{
	public static PluginConsole commandConsole = new PluginConsole("PluginConsole", "Plugin commands.");
	static JogLib jogLib;
	
	public JogLib()
	{
		jogLib = this;
	}
	
	@Override
	public void onEnable()
	{
		jogUtil.data.TypeRegistry.defaultValueStatus();
	}
}