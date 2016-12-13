package iieWelcomeKit;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;

import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagInt;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.PlayerInventory;

public class SilvShulkerBox implements Listener 
{
	/* BookUpdater's interact listener has a clause that auto-breaks shulker boxes
	 * if the player's hand is empty. This is in BookUpdater to avoid listening 
	 * to the same event a second time here.
	 */
	private static final NBTTagList ench = ench();
	
	private static NBTTagCompound vanishingCurse()
	{
		NBTTagCompound vanishingCurse = new NBTTagCompound();
		
		vanishingCurse.set("id", new NBTTagInt(71));
		vanishingCurse.set("lvl", new NBTTagInt(1));
		
		return vanishingCurse;
	}
	private static NBTTagList ench()
	{
		NBTTagList ench = new NBTTagList();
		ench.add(vanishingCurse());
		return ench;
	}
	
	//----------------------------------------------------------------------
	private static NBTTagCompound addEnch(NBTTagCompound item)
	{
		NBTTagCompound tag = item.getCompound("tag");
		tag.set("ench", ench);
		item.set("tag", tag);
		return item;
	}
	private static void giveEnchantedBox(PlayerInventory inventory, NBTTagCompound box)
	{
		inventory.setItem
		(
				inventory.getFirstEmptySlotIndex(), 
				new ItemStack
				(
						addEnch(box)
						)
				);
	}
	//----------------------------------------------------------------------
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event)
	{
		if (event.isCancelled() || event.getItem().getItemStack().getType() != Material.SILVER_SHULKER_BOX) 
		{	
			return;
		}
		giveEnchantedBox
		(
				((CraftInventoryPlayer) event.getPlayer().getInventory())	
				.getInventory(),
				
				CraftItemStack.asNMSCopy((CraftItemStack) event.getItem().getItemStack())	
				.save(new NBTTagCompound())	
				);
		
		event.getItem().remove();
		event.setCancelled(true);
	}
	@EventHandler
	public void onCraftItem(CraftItemEvent event)
	{
		Inventory inv = event.getInventory();
		if (
				event.getRecipe().getResult().getType() == Material.SILVER_SHULKER_BOX
				||	
				(
						inv.contains(Material.SILVER_SHULKER_BOX)
						&&	
						inv.contains(Material.INK_SACK)
						)
				)
		{
			event.setCancelled(true);
		}
	}
}
