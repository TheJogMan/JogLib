package jogLib;

import jogLib.customContent.*;
import jogLib.values.*;
import jogUtil.data.*;
import org.bukkit.*;
import org.bukkit.plugin.java.*;

public class JogLib extends JavaPlugin
{
	public static final PluginConsole commandConsole = new PluginConsole();
	static JogLib jogLib;
	
	public JogLib()
	{
		jogLib = this;
	}
	
	public static JogLib jogLib()
	{
		return jogLib;
	}
	
	@Override
	public void onEnable()
	{
		TypeRegistry.RegistrationQueue.start()
			.add("NamespacedKey", NamespacedKeyValue.class)
			.add("Location", LocationValue.class)
			.add("Material", MaterialValue.class)
			.add("BlockData", BlockDataValue.class)
			.add("Vector", VectorValue.class)
			.add("FluidCollisionMode", FluidCollisionModeValue.class)
		.process();
		
		Bukkit.getPluginManager().registerEvents(new PluginConsole.CommandEventListener(), this);
		
		ContentManager.init(this);
	}
}