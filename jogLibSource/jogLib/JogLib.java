package jogLib;

import org.bukkit.plugin.java.*;

public class JogLib extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		jogUtil.data.TypeRegistry.defaultValueStatus();
	}
}