package iieWelcomeKit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagInt;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;
import net.minecraft.server.v1_11_R1.PlayerInventory;

public class Main extends JavaPlugin implements Listener, CommandExecutor
{
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
	
	
	private static NBTTagCompound boxTag()
	{
		NBTTagCompound boxTag = new NBTTagCompound();
		boxTag.set("ench", ench());
		return boxTag;
	}
	private static ItemStack box()
	{
		NBTTagCompound boxNBT = new NBTTagCompound();
		boxNBT.set("Count", new NBTTagInt(1));
		boxNBT.set("id", new NBTTagString("minecraft:silver_shulker_box"));
		boxNBT.set("tag", boxTag());
		return new ItemStack(boxNBT);
	}
	
	
	private static NBTTagCompound bookTag()
	{
		NBTTagCompound bookTag = new NBTTagCompound();
		bookTag.set	("ench", ench());
		bookTag.set	("title", new NBTTagString("§6==Guide Book=="));
		bookTag.set	("author", new NBTTagString("Jujom the Welcomer, crusty old knob, esq."));
		bookTag.set	("generation", new NBTTagInt(2));
		return bookTag;
	}
	private static ItemStack book()
	{
		NBTTagCompound bookNBT = new NBTTagCompound();
		bookNBT.set	("Count", new NBTTagInt(1));
		bookNBT.set	("id", new NBTTagString("minecraft:written_book"));
		bookNBT.set	("tag", bookTag());
		return new ItemStack(bookNBT);
	}
	
	
	private static ItemStack boat()
	{
		NBTTagCompound boatNBT = new NBTTagCompound();
		boatNBT.set	("Count", new NBTTagInt(1));
		boatNBT.set	("id", new NBTTagString("minecraft:birch_boat"));
		return new ItemStack(boatNBT);
	}
	
	
	private static ItemStack bread()
	{
		NBTTagCompound breadNBT = new NBTTagCompound();
		breadNBT.set("Count", new NBTTagInt(8));
		breadNBT.set("id", new NBTTagString("minecraft:bread"));
		return new ItemStack(breadNBT);
	}
	
	
	private static final ItemStack box		= box();		//Serializers.deserialize("H4sIAAAAAAAAADWPwUrEQAyG09Yu3d68eA5zExZZPCgUelFh0RfwoCLTTmzHnc6s08xqWfZ5fA+fzKkg5Cc/JPzJVwIUkGoFZ4O21Hr5xtWn18xkXxvntgnkty5YTkrIWHYZlB1Z8pK1swCQLuGEbNuX0ScpZGZv5j4HbmAJ+U52NBbzELqDoC/2UlRPB8HRikpwr0eMJVHNl3EXGF1UM+EwISnNzq/w/jms15fXA46sjcH5PW075J6iohPHl9V/ojgWsJCBe+fh/CG8u+Fv75FM6waKYa0PI0/ojMKtdc0Kafy4KCBnzYbg9Of7qq43QSvCm4hf1wkUnkZn9qQSSGFxJ4fIFIl+ARctikk6AQAA");
	private static final ItemStack book 	= book();		//Serializers.deserialize("H4sIAAAAAAAAABXMTQqAIBRG0U+tKNfQSpoWtIyweqWkBvlDy89GZ3DhSqAFNzt6ZzxtjzriEIzN9CxBJ3sV1/tlqMc7+cgkRFRnh4r8piUAxiFstr//ZQY4mkk5dVKpH8ZBXsBeAAAA");
	private static final ItemStack boat 	= boat();		//Serializers.deserialize("H4sIAAAAAAAAAONiYOBgYMpMYRDJzcxLTS5KTCuxSsosSs6IT8pPLGFkYHXOL80rYWRiYHNJzE1MT2VgYAAAloMkTTMAAAA=");
	private static final ItemStack bread 	= bread();		//Serializers.deserialize("H4sIAAAAAAAAAONiYOBgYMpMYeDPzcxLTS5KTCuxSipKTUxhZGB1zi/NK+FgYmBzScxNTE9lYGAAAFawFmYuAAAA");
	
	//====================================ENABLE====================================
	boolean noKitsGivenYet;
	//------------------------------------------------------------------------------
	List<String> pageListFromConfig()
	{
		Bukkit.getLogger().info("page list from config");
		List<String> pages = new ArrayList<String>();
		for (String key : getConfig().getConfigurationSection("Pages").getKeys(false))
		{
			Bukkit.getLogger().info(getConfig().getString("Pages" + String.valueOf(key)));
			pages.add
			(
					getConfig().getString("Pages." + String.valueOf(key))
					);
		}
		return pages;
	}
	public void onEnable()
	{
		saveDefaultConfig();
		Book.setPages(pageListFromConfig());
		noKitsGivenYet = !getConfig().contains("UUIDs");		
		
		getServer().getPluginManager().registerEvents	(this					, this);
		getServer().getPluginManager().registerEvents	(new SilvShulkerBox()	, this);
		getServer().getPluginManager().registerEvents	(new BookUpdater(this)	, this);
		
		getCommand("updateGuideBook").setExecutor		(new BookUpdater(this));
		getCommand("giveWelcomeKit").setExecutor		(this);
	}
	
	//====================================METHODS===================================
	
	boolean giveWelcomeKit(PlayerInventory inventory, String uuid)
	{
		//check if inventory has room
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
			inventory.update();
			
			getConfig().set("UUIDs." + uuid, 1);
			saveConfig();
			noKitsGivenYet = false;
			
			return true;
		}
		return false;
	}
	
	
	private static boolean identicalUUIDs(String a, String b)
	{
		for (int i = 0; i < 36; i++)
		{
			if (a.charAt(i) != b.charAt(i)) return false;
		}
		return true;
	}
	private boolean alreadyHasKit(String uuid)
	{
		if (!noKitsGivenYet)
		{
			for (String key : getConfig().getConfigurationSection("UUIDs").getKeys(false))
			{
				if (identicalUUIDs(uuid,key)) return true;
			}	
		}
		return false;
	}
	
	
	private static final char[] world = "World".toCharArray();
	private static final int worldlength = world.length;
	private static boolean isWorld(String playerworld)
	{
		if (playerworld.length() != worldlength) return false;
		for (int i = 1; i < worldlength; i++)
		{
			if (playerworld.charAt(i) != world[i]) return false;
		}
		return true;
	}
	
	
	boolean kitEvent(Player player)
	{
		if (!isWorld(player.getWorld().getName())) 
		{
			return false;
		}
		
		String uuid = player.getUniqueId().toString();
		
		if(alreadyHasKit(uuid)) 
		{
			return false;
		}
		
		if(giveWelcomeKit(((CraftInventoryPlayer) player.getInventory()).getInventory(),	uuid))
		{
			player.sendMessage("§4====================================================");
			player.sendMessage("§cSalutations, friend. §6I've given you a small travel kit,");
			player.sendMessage("§6free of charge - consider it a gift.");
			player.sendMessage("§6Welcome to the continent, " + player.getDisplayName().replaceAll("§.", "§2") + ".");
			player.sendMessage("§4====================================================");
			return true;
		}
		player.sendMessage("§2====================================================");
		player.sendMessage("§cGreetings, §6" + player.getDisplayName().replaceAll("§.", "") + ", welcome back...");
		player.sendMessage("§6I'm trying to give you your belated welcome kit, but your pack is full.");
		player.sendMessage("§6...if you clear up some space for four more things I'll give you your gifts.");
		player.sendMessage("§2====================================================");
		
		ListenForPlayerHasMadeEnoughSpace.addPlayer(player.getName(), this);
		return false;
	}
	
	//====================================EVENTS====================================
	
	private final Runnable kitEventRunnable(Player player)
	{
		return new Runnable() { public void run() { kitEvent(player); } };
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent event)
	{
		Bukkit.getScheduler().runTaskLater(this, kitEventRunnable(event.getPlayer()), 60);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event)
	{
		Bukkit.getScheduler().runTaskLater(this, kitEventRunnable(event.getPlayer()), 60);
	}
	
	//===================================COMMAND====================================
	
	public boolean onCommand(CommandSender sender, Command label, String command, String[] args) 
	{
		if (args.length > 0 && sender.getName().equals("iie"))
		{
			Player player = Bukkit.getPlayer(args[0]);
			if (player != null)
			{
				getConfig().set("UUIDs." + player.getUniqueId().toString(), null);
				kitEvent(player);
				
				sender.sendMessage("giving welcome kit to '" + args[0] + "'");
				return true;
			}
			sender.sendMessage("null player");
			return false;
		}
		sender.sendMessage("/giveWelcomeKit <playername> ...and be iie when you press enter");
		return false;
	}
}
