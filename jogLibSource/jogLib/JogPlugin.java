package jogLib;

import org.bukkit.plugin.java.*;

public abstract class JogPlugin extends JavaPlugin
{
	public final PluginConfig config = new PluginConfig(this);
	
	public JogPlugin()
	{
		getDataFolder().mkdir();
	}
}