package iieWelcomeKit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_11_R1.IInventory;
import net.minecraft.server.v1_11_R1.ItemStack;

public class Main extends JavaPlugin implements Listener, CommandExecutor
{
	//==================================WELCOME KIT=================================

	private static final ItemStack book		= Serializers.deserialize("H4sIAAAAAAAAADWPwUrEQAyG09Yu3d68eA5zExZZPCgUelFh0RfwoCLTTmzHnc6s08xqWfZ5fA+fzKkg5Cc/JPzJVwIUkGoFZ4O21Hr5xtWn18xkXxvntgnkty5YTkrIWHYZlB1Z8pK1swCQLuGEbNuX0ScpZGZv5j4HbmAJ+U52NBbzELqDoC/2UlRPB8HRikpwr0eMJVHNl3EXGF1UM+EwISnNzq/w/jms15fXA46sjcH5PW075J6iohPHl9V/ojgWsJCBe+fh/CG8u+Fv75FM6waKYa0PI0/ojMKtdc0Kafy4KCBnzYbg9Of7qq43QSvCm4hf1wkUnkZn9qQSSGFxJ4fIFIl+ARctikk6AQAA");
	private static final ItemStack box 		= Serializers.deserialize("H4sIAAAAAAAAABXMTQqAIBRG0U+tKNfQSpoWtIyweqWkBvlDy89GZ3DhSqAFNzt6ZzxtjzriEIzN9CxBJ3sV1/tlqMc7+cgkRFRnh4r8piUAxiFstr//ZQY4mkk5dVKpH8ZBXsBeAAAA");
	private static final ItemStack boat 	= Serializers.deserialize("H4sIAAAAAAAAAONiYOBgYMpMYRDJzcxLTS5KTCuxSsosSs6IT8pPLGFkYHXOL80rYWRiYHNJzE1MT2VgYAAAloMkTTMAAAA=");
	private static final ItemStack bread 	= Serializers.deserialize("H4sIAAAAAAAAAONiYOBgYMpMYeDPzcxLTS5KTCuxSipKTUxhZGB1zi/NK+FgYmBzScxNTE9lYGAAAFawFmYuAAAA");
	
	//==================================INITIALIZE==================================
	
	boolean noKitsGivenYet;
	public void onEnable()
	{
		saveDefaultConfig();
		BookWriter.setPages
		(
				pageList()//see below
				);
		noKitsGivenYet = getConfig().isSet("UUIDs");
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new BookUpdater(this), this);
		getCommand("updateGuideBook").setExecutor(new BookUpdater(this));
	}
	
	//====================================METHODS===================================
	
	List<String> pageList()
	{
		List<String> pages = new ArrayList<String>();
		for (int i = 1; i <= getConfig().getConfigurationSection("Pages").getKeys(false).size(); i++)
		{
			pages.add
			(
					getConfig().getConfigurationSection("Pages").getString(String.valueOf(i))
					.replaceAll(" ", "\\\\\\\\u0020")
					.replaceAll("'", "\\\\\\\\u0027")
					.replaceAll("\n", "\\\\\\\\n")
					);
			Bukkit.getLogger().info(pages.get(i-1));
		}
		return pages;
	}
	private static void giveWelcomeKit(Player player)
	{
		CraftInventory inventory = (CraftInventory) player.getInventory();
		IInventory nms_inventory = inventory.getInventory();
		
		nms_inventory.setItem(inventory.firstEmpty(), box);
		nms_inventory.setItem(inventory.firstEmpty(), book);
		nms_inventory.setItem(inventory.firstEmpty(), boat);
		nms_inventory.setItem(inventory.firstEmpty(), bread);
		Bukkit.getLogger().info("gave welcome kit");
	}
	private static boolean identicalUUIDs(String a, String b)
	{
		for (int i = 0; i < 36; i++)
		{
			if (a.charAt(i) != b.charAt(i))
			{
				Bukkit.getLogger().info("UUIDs do not match: " + a + ", " + b);
				return false;
			}
		}
		Bukkit.getLogger().info("UUIDs match: " + a + ", " + b);
		return true;
	}
	private boolean receivedKit(String uuid)
	{
		if (!noKitsGivenYet)
		{
			for (String key : getConfig().getConfigurationSection("UUIDs").getKeys(false))
			{
				if (identicalUUIDs(uuid,key))
					return true;
			}	
		}
		else
		{
			noKitsGivenYet = false;
		}
		getConfig().set("UUIDs" + uuid, 1);
		saveConfig();
		return false;
	}
	
	//====================================EVENTS====================================
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		if (
					player.getWorld().getName().equals("World")
				&&	receivedKit(player.getUniqueId().toString())
				)
		{
			giveWelcomeKit(player);
		}
		else if (!player.getWorld().getName().equals("World"))
		{
			Bukkit.getLogger().info("Worldname '" + player.getWorld().getName() + "' did not match 'World'");
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if (
					player.getWorld().getName().equals("world")//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ make w uppercase
				&&	receivedKit(player.getUniqueId().toString())
				)
		{
			giveWelcomeKit(player);
		}
		else if (!player.getWorld().getName().equals("World"))
		{
			Bukkit.getLogger().info("Worldname '" + player.getWorld().getName() + "' did not match 'World'");
		}
	}
}
