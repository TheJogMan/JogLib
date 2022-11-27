package jogLib.command.executor;

import jogUtil.commander.*;
import jogUtil.richText.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.permissions.*;
import org.bukkit.plugin.*;

import java.util.*;

public class PluginExecutor extends Executor implements CommandSender
{
	final CommandSender sender;
	
	protected PluginExecutor(CommandSender sender)
	{
		this.sender = sender;
	}
	
	@Override
	public void respond(RichString message)
	{
		sender.sendMessage(message.encode(EncodingType.CODED));
	}
	
	public CommandSender sender()
	{
		return sender;
	}
	
	@Override
	public boolean isOp()
	{
		return sender.isOp();
	}
	
	@Override
	public void setOp(boolean value)
	{
		sender.setOp(value);
	}
	
	@Override
	public void sendMessage(String message)
	{
		sender.sendMessage(message);
	}
	
	@Override
	public void sendMessage(String... messages)
	{
		sender.sendMessage(messages);
	}
	
	@Override
	public void sendMessage(UUID sender, String message)
	{
		this.sender.sendMessage(sender, message);
	}
	
	@Override
	public void sendMessage(UUID sender, String... messages)
	{
		this.sender.sendMessage(sender, messages);
	}
	
	@Override
	public Server getServer()
	{
		return sender.getServer();
	}
	
	@Override
	public String getName()
	{
		return sender.getName();
	}
	
	@Override
	public Spigot spigot()
	{
		return sender.spigot();
	}
	
	@Override
	public boolean isPermissionSet(String name)
	{
		return sender.isPermissionSet(name);
	}
	
	@Override
	public boolean isPermissionSet(Permission perm)
	{
		return sender.isPermissionSet(perm);
	}
	
	@Override
	public boolean hasPermission(String name)
	{
		return sender.hasPermission(name);
	}
	
	@Override
	public boolean hasPermission(Permission perm)
	{
		return sender.hasPermission(perm);
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
	{
		return sender.addAttachment(plugin, name, value);
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin)
	{
		return sender.addAttachment(plugin);
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
	{
		return sender.addAttachment(plugin, name, value, ticks);
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks)
	{
		return sender.addAttachment(plugin, ticks);
	}
	
	@Override
	public void removeAttachment(PermissionAttachment attachment)
	{
		sender.removeAttachment(attachment);
	}
	
	@Override
	public void recalculatePermissions()
	{
		sender.recalculatePermissions();
	}
	
	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		return sender.getEffectivePermissions();
	}
	
	public static PluginExecutor convert(CommandSender commandSender)
	{
		if (commandSender instanceof Player)
			return new PlayerExecutor((Player)commandSender);
		else if (commandSender instanceof LivingEntity)
			return new LivingEntityExecutor((LivingEntity)commandSender);
		else if (commandSender instanceof Entity)
			return new EntityExecutor((Entity)commandSender);
		else if (commandSender instanceof BlockCommandSender)
			return new BlockExecutor((BlockCommandSender)commandSender);
		else if (commandSender instanceof ConsoleCommandSender)
			return new ConsoleExecutor((ConsoleCommandSender)commandSender);
		else if (commandSender instanceof ProxiedCommandSender)
			return new ProxiedExecutor((ProxiedCommandSender)commandSender);
		else if (commandSender instanceof RemoteConsoleCommandSender)
			return new RemoteConsoleExecutor((RemoteConsoleCommandSender)commandSender);
		else
			return new PluginExecutor(commandSender);
	}
}