package jogLib;

import jogLib.customContent.*;
import jogLib.values.*;
import jogUtil.*;
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
		registerValues();
		
		Bukkit.getPluginManager().registerEvents(new PluginConsole.CommandEventListener(), this);
		
		ContentManager.init(this);
	}
	
	private static ReturnResult<Result[]> registerValues()
	{
		Object[][] typeClasses = {
				{"NamespacedKey", NamespacedKeyValue.class},
				{"Location", LocationValue.class},
				{"Material", MaterialValue.class},
				{"BlockData", BlockDataValue.class},
				{"Vector", VectorValue.class},
				{"FluidCollisionMode", FluidCollisionModeValue.class}
		};
		
		Result[] registrationResults = new Result[typeClasses.length];
		for (int index = 0; index < typeClasses.length; index++)
		{
			Class<? extends Value<?, ?>> typeClass = (Class<? extends Value<?, ?>>)typeClasses[index][1];
			String name = (String)typeClasses[index][0];
			Result result = TypeRegistry.register(name, typeClass);
			if (!result.success())
				throw new RuntimeException("Could not register value: " + result.description());
			registrationResults[index] = result;
		}
		
		return new ReturnResult<>(registrationResults);
	}
}