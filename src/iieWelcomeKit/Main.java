package iieWelcomeKit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener 
{	
	//==============================WELCOME KIT=============================
	
	private static final ItemStack box 		= new ItemStack(Material.SILVER_SHULKER_BOX);
	private static final ItemStack book 	= new ItemStack(Material.WRITTEN_BOOK);
	private static final ItemStack boat 	= new ItemStack(Material.BOAT_BIRCH);
	private static final ItemStack bread 	= new ItemStack(Material.BREAD, 8);	
	//set attributes
	static
	{
		box.addEnchantment(Enchantment.VANISHING_CURSE, 1);
		book.addEnchantment(Enchantment.VANISHING_CURSE, 1);
		boat.addEnchantment(Enchantment.VANISHING_CURSE, 1);
		bread.addEnchantment(Enchantment.VANISHING_CURSE, 1);
		
		box.getItemMeta().setDisplayName("Travel Box");
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setDisplayName("==Guide Book==");
		bookMeta.setTitle("An Introduction");
		bookMeta.setAuthor("Jujom the Welcomer, crusty old knob, esq.");
		bookMeta.setPages
		(
				Arrays.asList
				(
						"Welcome to the land, newcomer.\n\n"
						+ "second line",
						
						"second page"
						)
				);
	}
	
	//===========================STATIC METHODS=============================
	
	private static void giveWelcomeKit(Inventory inventory)
	{
		inventory.addItem(box);
		inventory.addItem(book);
		inventory.addItem(boat);
		inventory.addItem(bread);
	}
	private static String serialize()
	{
		ByteArrayOutputStream 	output = new ByteArrayOutputStream();
		ObjectOutputStream 		source;
		try 
		{
			source = new ObjectOutputStream(output);
			source.writeObject(book);
			source.close();
			
			return Base64.getEncoder().encodeToString(output.toByteArray()); 
		} 
		catch (IOException e)
		{ 
			return null;
		}
	}
	private static boolean updateBook(FileConfiguration config)
	{
		String currentBook = serialize();
		if (!config.contains("Book"))
		{
			config.set("Book", currentBook);
			return false;
		}
		if (Objects.equals(currentBook, config.getString("Book")))
		{
			return false;
		}
		config.set("Book", currentBook);
		return true;
	}
	
	//===========================INSTANCE METHODS===========================
	
	public void onEnable()
	{
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		if (updateBook(getConfig()))
		{
			getServer().getPluginManager().registerEvents(new BookUpdater(book), this);
		}
	}
	
	private void evaluate(Player player)
	{
		String uuid = player.getUniqueId().toString();
		if (
				Objects.equals(player.getWorld().getName().toLowerCase(), "world")
				|| getConfig().contains(uuid)
				)
		{
			return;
		}
		giveWelcomeKit(player.getInventory());
		getConfig().set("", uuid);
	}
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event)
	{
		evaluate(event.getPlayer());
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		evaluate(event.getPlayer());
	}
}
