package iieWelcomeKit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.PlayerInventory;

public class Main extends JavaPlugin implements Listener, CommandExecutor
{
	private static final ItemStack book		= Serializers.deserialize("H4sIAAAAAAAAADWPwUrEQAyG09Yu3d68eA5zExZZPCgUelFh0RfwoCLTTmzHnc6s08xqWfZ5fA+fzKkg5Cc/JPzJVwIUkGoFZ4O21Hr5xtWn18xkXxvntgnkty5YTkrIWHYZlB1Z8pK1swCQLuGEbNuX0ScpZGZv5j4HbmAJ+U52NBbzELqDoC/2UlRPB8HRikpwr0eMJVHNl3EXGF1UM+EwISnNzq/w/jms15fXA46sjcH5PW075J6iohPHl9V/ojgWsJCBe+fh/CG8u+Fv75FM6waKYa0PI0/ojMKtdc0Kafy4KCBnzYbg9Of7qq43QSvCm4hf1wkUnkZn9qQSSGFxJ4fIFIl+ARctikk6AQAA");
	private static final ItemStack box 		= Serializers.deserialize("H4sIAAAAAAAAABXMTQqAIBRG0U+tKNfQSpoWtIyweqWkBvlDy89GZ3DhSqAFNzt6ZzxtjzriEIzN9CxBJ3sV1/tlqMc7+cgkRFRnh4r8piUAxiFstr//ZQY4mkk5dVKpH8ZBXsBeAAAA");
	private static final ItemStack boat 	= Serializers.deserialize("H4sIAAAAAAAAAONiYOBgYMpMYRDJzcxLTS5KTCuxSsosSs6IT8pPLGFkYHXOL80rYWRiYHNJzE1MT2VgYAAAloMkTTMAAAA=");
	private static final ItemStack bread 	= Serializers.deserialize("H4sIAAAAAAAAAONiYOBgYMpMYeDPzcxLTS5KTCuxSipKTUxhZGB1zi/NK+FgYmBzScxNTE9lYGAAAFawFmYuAAAA");
	
	private static final char[] world = "World".toCharArray();
	private static final int worldNameLength = world.length;
	
	boolean noKitsGivenYet;
	//=====================================INIT=====================================
	List<String> pageListFromConfig()
	{
		List<String> pages = new ArrayList<String>();
		for (String key : getConfig().getConfigurationSection("Pages").getKeys(false))
		{
			pages.add
			(
					getConfig().getConfigurationSection("Pages").getString(String.valueOf(key))
					);
		}
		return pages;
	}
	public void onEnable()
	{
		saveDefaultConfig();
		noKitsGivenYet = !getConfig().contains("UUIDs");
		Book.setPages
		(
				pageListFromConfig()
				);
		
		getServer().getPluginManager().registerEvents	(this					, this);
		getServer().getPluginManager().registerEvents	(new SilvShulkerBox()	, this);
		getServer().getPluginManager().registerEvents	(new BookUpdater(this)	, this);
		getCommand("updateGuideBook").setExecutor		(new BookUpdater(this));
	}
	
	//====================================METHODS===================================
	
	static boolean invHasRoom(PlayerInventory inventory)
	{
		int empty = 0;
		for (int i = 0; i < 36 && empty < 4; i++)
		{
			if (inventory.getItem(i).isEmpty()) empty++;
		}
		if (empty == 4) 
		{
			inventory.setItem(inventory.getFirstEmptySlotIndex(), box);
			inventory.setItem(inventory.getFirstEmptySlotIndex(), book);
			inventory.setItem(inventory.getFirstEmptySlotIndex(), boat);
			inventory.setItem(inventory.getFirstEmptySlotIndex(), bread);
			return true;
		}
		return false;
	}
	private boolean giveWelcomeKit(Player player)
	{
		if (invHasRoom(((CraftInventoryPlayer) player.getInventory()).getInventory())) return true;
		else
		{
			player.sendMessage("§2====================================================");
			player.sendMessage("§6Greetings, " + player.getDisplayName() + ", welcome back...");
			player.sendMessage("§6I'm trying to give you your belated welcome kit, but your pack is full.");
			player.sendMessage("§6...if you clear up some space for four more things I'll give you your gifts.");
			player.sendMessage("§2====================================================");
			
			ListenForPlayerHasMadeEnoughSpace.addPlayer(player.getName(), this);
			return false;
		}
		
	}
	private static boolean isWorld(String worldname)
	{
		if (worldname.length() != worldNameLength) return false;
		for (int i = 0; i < worldNameLength; i++)
		{
			if (worldname.charAt(i) != world[i]) return false;
		}
		return true;
	}
	private static boolean identicalUUIDs(String a, String b)
	{
		for (int i = 0; i < 36; i++)
		{
			if (a.charAt(i) != b.charAt(i)) return false;
		}
		return true;
	}
	private boolean receivedKit(String uuid)
	{
		if (!noKitsGivenYet)
		{
			for (String key : getConfig().getConfigurationSection("UUIDs").getKeys(false))
			{
				if (identicalUUIDs(uuid,key)) return true;
			}	
		}
		else
		{
			noKitsGivenYet = false;
		}
		return false;
	}
	
	
	//====================================EVENTS====================================
	
	void kitEvent(Player player)
	{
		String uuid;
		if (
					isWorld(player.getWorld().getName())
					
				&&	!receivedKit(uuid = player.getUniqueId().toString())
				
				&&	giveWelcomeKit(player)
				)
		{
			getConfig().set("UUIDs." + uuid, 1);
			saveConfig();
		}
	}
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event)
	{
		kitEvent(event.getPlayer());
		
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		kitEvent(event.getPlayer());
	}
}
