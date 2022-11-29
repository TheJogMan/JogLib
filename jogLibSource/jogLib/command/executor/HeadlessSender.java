package jogLib.command.executor;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.permissions.*;
import org.bukkit.plugin.*;

import java.io.*;
import java.util.*;

public class HeadlessSender implements CommandSender, Iterable<PermissionAttachmentInfo>
{
	boolean op;
	final String name;
	final PrintStream printStream;
	final ArrayList<PermissionAttachment> attachments = new ArrayList<>();
	
	public HeadlessSender(boolean op, String name, PrintStream printStream)
	{
		this.op = op;
		this.name = name;
		this.printStream = printStream;
	}
	
	public HeadlessSender(boolean op, String name)
	{
		this(op, name, null);
	}
	
	public HeadlessSender(String name)
	{
		this(false, name, null);
	}
	
	public HeadlessSender()
	{
		this(false, "HeadlessCommandSender", null);
	}
	
	@Override
	public void sendMessage(String message)
	{
		if (printStream != null)
			printStream.println(message);
	}
	
	@Override
	public void sendMessage(String... messages)
	{
		for (String message : messages)
			sendMessage(message);
	}
	
	@Override
	public void sendMessage(UUID sender, String message)
	{
		sendMessage(message);
	}
	
	@Override
	public void sendMessage(UUID sender, String... messages)
	{
		sendMessage(messages);
	}
	
	@Override
	public Server getServer()
	{
		return Bukkit.getServer();
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public CommandSender.Spigot spigot()
	{
		return new CommandSender.Spigot();
	}
	
	@Override
	public boolean isPermissionSet(String name)
	{
		for (PermissionAttachmentInfo info : this)
		{
			if (info.getPermission().equals(name))
				return info.getValue();
		}
		return false;
	}
	
	@Override
	public boolean isPermissionSet(Permission perm)
	{
		for (PermissionAttachmentInfo info : this)
		{
			if (info.getPermission().equals(perm.getName()))
				return info.getValue();
		}
		return false;
	}
	
	@Override
	public boolean hasPermission(String name)
	{
		for (PermissionAttachmentInfo permissionAttachmentInfo : this)
		{
			if (permissionAttachmentInfo.getPermission().equals(name))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean hasPermission(Permission perm)
	{
		for (PermissionAttachmentInfo permissionAttachmentInfo : this)
		{
			if (permissionAttachmentInfo.getPermission().equals(perm.getName()))
				return true;
		}
		return false;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
	{
		PermissionAttachment attachment = new PermissionAttachment(plugin, this);
		attachments.add(attachment);
		attachment.setPermission(name, value);
		return attachment;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin)
	{
		PermissionAttachment attachment = new PermissionAttachment(plugin, this);
		attachments.add(attachment);
		return attachment;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
	{
		PermissionAttachment attachment = new PermissionAttachment(plugin, this);
		attachments.add(attachment);
		attachment.setPermission(name, value);
		Bukkit.getScheduler().runTaskLater(plugin, attachment::remove, ticks);
		return attachment;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks)
	{
		PermissionAttachment attachment = new PermissionAttachment(plugin, this);
		attachments.add(attachment);
		Bukkit.getScheduler().runTaskLater(plugin, attachment::remove, ticks);
		return attachment;
	}
	
	@Override
	public void removeAttachment(PermissionAttachment attachment)
	{
		attachments.remove(attachment);
	}
	
	@Override
	public void recalculatePermissions()
	{
	
	}
	
	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		Set<PermissionAttachmentInfo> info = new HashSet<>();
		this.forEach(info::add);
		return info;
	}
	
	@Override
	public boolean isOp()
	{
		return op;
	}
	
	@Override
	public void setOp(boolean value)
	{
		op = value;
	}
	
	public static class PermissionIterator implements Iterator<PermissionAttachmentInfo>
	{
		final Iterator<PermissionAttachment> attachmentIterator;
		Iterator<Map.Entry<String, Boolean>> permissionIterator;
		PermissionAttachment attachment;
		final HeadlessSender sender;
		
		public PermissionIterator(HeadlessSender sender)
		{
			this.sender = sender;
			attachmentIterator = sender.attachments.iterator();
		}
		
		@Override
		public boolean hasNext()
		{
			return (permissionIterator == null || !permissionIterator.hasNext()) || !attachmentIterator.hasNext();
		}
		
		@Override
		public PermissionAttachmentInfo next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			if (permissionIterator == null || !permissionIterator.hasNext())
			{
				attachment = attachmentIterator.next();
				permissionIterator = attachment.getPermissions().entrySet().iterator();
			}
			Map.Entry<String, Boolean> permission = permissionIterator.next();
			return new PermissionAttachmentInfo(sender, permission.getKey(), attachment, permission.getValue());
		}
	}
	
	@Override
	public Iterator<PermissionAttachmentInfo> iterator()
	{
		return new PermissionIterator(this);
	}
}