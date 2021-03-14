package net.minecraft.optifine;

import net.minecraft.block.Block;

public class BlockUtils
{
    private static boolean directAccessValid = true;

    public static void setLightOpacity(Block block, int opacity)
    {
        if (directAccessValid)
        {
            try
            {
                block.setLightOpacity(opacity);
                return;
            }
            catch (IllegalAccessError var3)
            {
                directAccessValid = false;
            }
        }
    }
}
