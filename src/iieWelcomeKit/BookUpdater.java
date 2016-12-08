package iieWelcomeKit;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BookUpdater implements Listener{
	
	private final ItemStack book;
	BookUpdater(ItemStack book)
	{
		this.book = book;
	}
	
	
	@EventHandler
	public void onBookClick(InventoryClickEvent event)
	{
		ItemStack clicked = event.getCurrentItem();
		if (clicked != null 
				&& clicked.getType() == Material.WRITTEN_BOOK)
		{
			String display = clicked.getItemMeta().getDisplayName();
			if (display == "==Guide Book==")
			{
				event.setCurrentItem(book);
			}
		}
	}
}
