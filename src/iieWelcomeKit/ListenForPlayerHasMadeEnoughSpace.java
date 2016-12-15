package iieWelcomeKit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ListenForPlayerHasMadeEnoughSpace implements Listener 
{
	private static 			Listener 		listener 	= null;
	private static volatile boolean 		listening 	= false;
	private static volatile List<String> 	players 	= new ArrayList<String>();
	//----------------------------------------------------------------------
	static void addPlayer(String name, Main main)
	{
		if (!listening)
		{
			listening = true;
			main.getServer().getPluginManager().registerEvents
			(
					listener = new ListenForPlayerHasMadeEnoughSpace(main), 
					main
					);
			players = new ArrayList<String>();
			players.add(name);
		}
	}
	//----------------------------------------------------------------------
	private final Main main;
	private ListenForPlayerHasMadeEnoughSpace(Main main)
	{
		this.main = main;
	}
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		HumanEntity player = event.getPlayer();
		String name = player.getName();
		if (
					players.contains(name)
				&&	main.giveWelcomeKit
					(
							((CraftInventoryPlayer) player.getInventory()).getInventory(),
							
							player.getUniqueId().toString()
							)
				)
		{
			players.remove(name);
			if (players.isEmpty())
			{
				InventoryCloseEvent.getHandlerList().unregister(listener);
				listening = false;
			}
			player.sendMessage("§6...Alrighty, that should do it. Enjoy your gifts. The bread has gone a bit stale.");
		}
	}
}
