package us.zonix.client.module.impl;

import com.thevoxelbox.voxelmap.VoxelMap;
import us.zonix.client.module.modules.AbstractModule;

public final class MiniMap extends AbstractModule {

	private final VoxelMap voxelMap;

	public MiniMap() {
		super("Minimap");

		this.voxelMap = new VoxelMap(true, true);
	}

	@Override public void renderReal() {
		if (this.mc.currentScreen == null) {
			this.voxelMap.onTickInGame(this.mc);
		}
	}

}
