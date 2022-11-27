package jogLib.command.executor;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.*;

public class BlockExecutor extends PhysicalExecutor
{
	BlockExecutor(BlockCommandSender block)
	{
		super(block);
	}
	
	public Material getMaterial()
	{
		return getBlock().getType();
	}
	
	public Block getBlock()
	{
		return sender().getBlock();
	}
	
	@Override
	public BlockCommandSender sender()
	{
		return (BlockCommandSender)sender;
	}
}