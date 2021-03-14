package net.minecraft.optifine;

import java.io.File;
import net.minecraft.client.resources.AbstractResourcePack;

public class ResourceUtils
{
    private static boolean directAccessValid = true;

    public static File getResourcePackFile(AbstractResourcePack arp)
    {
        return arp.resourcePackFile;
    }
}
