package net.minecraft.optifine;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class EntityUtils
{
    private static boolean directEntityAge = true;
    private static boolean directDespawnEntity = true;

    public static int getEntityAge(EntityLivingBase elb)
    {
        return elb.entityAge;
    }

    public static void setEntityAge(EntityLivingBase elb, int age)
    {
        elb.entityAge = age;
    }

    public static void despawnEntity(EntityLiving el)
    {
        el.despawnEntity();
    }
}
