package iieWelcomeKit;

import java.util.List;

import net.minecraft.server.v1_11_R1.IInventory;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagInt;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;

public class BookWriter 
{
	private static NBTTagCompound tag = createTag();
	private static NBTTagCompound createTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound ench = new NBTTagCompound();
		
		ench.set("id", new NBTTagInt(71));
		ench.set("lvl", new NBTTagInt(1));
		
		tag.set("title", 		new NBTTagString("§6==Guide Book=="));
		tag.set("author", 		new NBTTagString("Jujom the Welcomer, crusty old knob, esq."));
		tag.set("generation", 	new NBTTagInt(2));
		tag.set("ench", 		ench);
		
		return tag;
	}
	private static NBTTagList createPages(List<String> pageList)
	{
		NBTTagList pages = new NBTTagList();
		for (String page : pageList)
		{
			pages.add
			(
					new NBTTagString(page)
					);
		}
		return pages;
	}
	static void setPages(List<String> pageList)
	{
		tag.set
		(
				"pages",
				createPages(pageList)
				);
	}
	static void updatePlayerBook(IInventory inventory, NBTTagCompound item, int slot)
	{
		item.set("tag", tag);
		inventory.setItem(slot, new ItemStack(item));
	}
}
