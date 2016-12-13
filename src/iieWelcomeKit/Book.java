package iieWelcomeKit;

import java.util.List;

import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagInt;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;
import net.minecraft.server.v1_11_R1.PlayerInventory;

public class Book 
{
	private static NBTTagCompound tag = createTag();
	
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
	private static NBTTagCompound createTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		tag.set	("ench"			, ench()																);
		tag.set	("title"		, new NBTTagString	("§6==Guide Book==")								);
		tag.set	("author"		, new NBTTagString	("Jujom the Welcomer, crusty old knob, esq.")		);
		tag.set	("generation"	, new NBTTagInt		(2)													);
		
		return tag;
	}
	
	//----------------------------------------------------------------------
	private static NBTTagList createPages(List<String> pageList)
	{
		NBTTagList pages = new NBTTagList();
		for (String page : pageList)
		{
			pages.add
			(
					new NBTTagString("{\"text\":\"" + page + "\"}")
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
	
	//----------------------------------------------------------------------
	private static NBTTagCompound setTag(NBTTagCompound item)
	{
		item.set("tag", tag);
		return item;
	}
	//----------------------------------------------------------------------
	
	static void updateHeldBook(PlayerInventory inventory)
	{
		inventory.setItem
		(
				inventory.itemInHandIndex, 
				new ItemStack
				(
						setTag
						(
								inventory.getItem(inventory.itemInHandIndex).save(new NBTTagCompound())
								)
						)
				);
	}
}
