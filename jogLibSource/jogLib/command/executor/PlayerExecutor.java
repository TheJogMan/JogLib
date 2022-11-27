package jogLib.command.executor;

import org.bukkit.entity.*;

public class PlayerExecutor extends LivingEntityExecutor
{
	PlayerExecutor(Player player)
	{
		super(player);
	}
	
	@Override
	public Player sender()
	{
		return (Player)sender;
	}
}