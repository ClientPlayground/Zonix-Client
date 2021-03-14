package com.thevoxelbox.voxelmap.forge;

public class VoxelMapMod {
	public static final String NAME = "VoxelMap No Radar";
	public static final String MODID = "voxelmap";
	public static final String VERSION = "1.7.10";
	public static final boolean CANBEDEACTIVATED = true;
	static final boolean runningForge = classExists("net.minecraftforge.common.MinecraftForge");
	public static VoxelMapMod instance;
	private boolean isEnabled = true;

	public VoxelMapMod() {
		instance = this;
	}

	private static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException exception) {
		}
		return false;
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void setEnabled(boolean bool) {
		this.isEnabled = bool;
	}
}
