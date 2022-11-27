package jogLib.command.arguments;

import jogUtil.*;
import jogUtil.commander.*;
import jogUtil.commander.argument.*;
import jogUtil.data.values.*;
import jogUtil.indexable.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;

public class PlayerArgument extends PlainArgument<Player>
{
	
	@Override
	public void initArgument(Object[] data)
	{
	
	}
	
	@Override
	public String defaultName()
	{
		return "Player";
	}
	
	@Override
	public List<String> argumentCompletions(Indexer<Character> source, Executor executor)
	{
		String token = StringValue.consumeString(source, ' ').toLowerCase();
		ArrayList<String> completions = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (player.getName().toLowerCase().startsWith(token))
				completions.add(player.getName());
		}
		return completions;
	}
	
	@Override
	public ReturnResult<Player> interpretArgument(Indexer<Character> source, Executor executor)
	{
		String name = StringValue.consumeString(source, ' ');
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (player.getName().equals(name))
				return new ReturnResult<>(player);
		}
		return new ReturnResult<>("There is no player with that name.");
	}
}