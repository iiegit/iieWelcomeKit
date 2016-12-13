package iieWelcomeKit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookUpdater implements Listener, CommandExecutor //too many lines----!
{
	private final Main main;
	BookUpdater(Main main)
	{
		this.main = main;
	}
	
	//==========================CHECK IF GUIDEBOOK==========================
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
		return meta.hasAuthor() && isJujom(meta.getAuthor());
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

	//==========================UPDATE SERVER COPY==========================
	
	private void setConfigPages(List<String> pages)
	{
		for (int i = 0; i < pages.size(); i++)
		{
			main.getConfig().set
			(
					"Pages." + String.valueOf(i+1), 
					pages.get(i)
					);
		}
		main.saveConfig();
	}
	private boolean validSourceBook(ItemStack item, Material type)
	{
		return
				(
							type == Material.BOOK_AND_QUILL
						||	type == Material.WRITTEN_BOOK
						)
				&&	item.hasItemMeta();
	}
	private boolean updateServerCopyFromItem(ItemStack item)
	{
		if (validSourceBook(item, item.getType()))
		{	
			setConfigPages(((BookMeta) item.getItemMeta()).getPages());
			Book.setPages(main.pageListFromConfig());
			
			return true;
		}
		return false;
	}
	//================================BUKKIT================================
	
	
	//----------------------------BOOK READ EVENT---------------------------
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBookOpen(PlayerInteractEvent event)
	{
		if (event.hasItem())
		{
			if (isGuideBook(event.getItem()))
			{
				Book.updateHeldBook
				(
						(
								(CraftInventoryPlayer) event.getPlayer().getInventory()
								)
						.getInventory()
						);
			}
		}
		else if 
		(
					event.getAction() == Action.LEFT_CLICK_BLOCK 
				&& 	event.getClickedBlock().getType().name().endsWith("_BOX")
				)
		{
			event.getClickedBlock().breakNaturally();
		}
		//bare-handed punching a shulker box instantly breaks it;
		//in here to avoid listening to this event twice
	}
	
	//--------------------------------COMMAND-------------------------------
	
	public boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
	{
		return
					sender.getName().equals("iie")
				&&	updateServerCopyFromItem
					(
							Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand()
							);
	}
}
