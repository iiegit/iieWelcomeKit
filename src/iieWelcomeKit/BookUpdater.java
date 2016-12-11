package iieWelcomeKit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import net.minecraft.server.v1_11_R1.IInventory;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

public class BookUpdater implements Listener, CommandExecutor
{
	//================================STATIC================================
	private static final char[] jujom = "Jujom ".toCharArray();//note the space
	//----------------------------------------------------------------------
	private static boolean isJujom(String author)
	{
		for (int i = 0; i < 6; i++)
		{
			if (author.charAt(i) != jujom[i]) return false;
		}
		return true;
	}
	private static boolean authorCheck(BookMeta meta)
	{
		return
					meta.hasAuthor()
				&&	isJujom
					(
							meta.getAuthor()
							);
	}
	private static boolean isGuideBook(ItemStack item)
	{
		return 
					item.getType() == Material.WRITTEN_BOOK
				&& 	item.hasItemMeta()
				&&	authorCheck
					(
							(BookMeta) item.getItemMeta()
							);
	}
	private static void configSetPages(Main main, ItemStack item)
	{
		if (item.getType() == Material.WRITTEN_BOOK && item.hasItemMeta())
		{
			List<String> pages = ((BookMeta) item.getItemMeta()).getPages();
			for (int i = 0; i < pages.size(); i++)
			{
				main.getConfig().set
				(
						"Pages." + String.valueOf(i+1), 
						pages.get(i)
						);
				Bukkit.getLogger().info("updating page " + (i+1) + ": " + pages.get(i));
			}
			main.saveConfig();
		}
		else if (!item.hasItemMeta())
		{
			Bukkit.getLogger().info("held item has no meta");
		}
	}
	
	//===============================INSTANCE===============================
	private final Main main;
	//----------------------------------------------------------------------
	BookUpdater(Main main)
	{
		this.main = main;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBookOpen(PlayerInteractEvent event)
	{
		if (!event.hasItem() || !isGuideBook(event.getItem()))
		{
			return;
		}
		
		PlayerInventory 	playerInventory 	= event.getPlayer().getInventory();
		Inventory 			bukkitInventory 	= playerInventory;
		IInventory 			inventory 			= ((CraftInventory) playerInventory).getInventory();
		
		int slot = playerInventory.getHeldItemSlot();
		NBTTagCompound currentBookNBT = inventory.getItem(slot).save(new NBTTagCompound());
		
		
		bukkitInventory.clear(slot);
		
		Bukkit.getLogger().info("updating book");
		BookWriter.updatePlayerBook
		(
				inventory,
				currentBookNBT,
				slot
				);
	}
	//private static final String iie = "iie";
	public final boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
	{
		if (sender.getName().equals("Iie"))
		{
			Bukkit.getLogger().info("Name '" + sender.getName() + "' did not match 'iie'");
			return false;
		}
		configSetPages
		(
				main,
				Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand()
				);
		BookWriter.setPages
		(
				main.pageList()
				);
		return true;
	}
}
