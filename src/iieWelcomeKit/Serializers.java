package iieWelcomeKit;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NBTCompressedStreamTools;

public class Serializers 
{
	static ItemStack deserialize(String itemStackString)
	{
		try
		{
			return new ItemStack
					(
							NBTCompressedStreamTools.a
							(
									new ByteArrayInputStream
									(
											Base64.decodeBase64(itemStackString)
											)
									)
							);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
	}	
}
