package us.zonix.client.module.impl;

import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.FloatSetting;
import us.zonix.client.setting.impl.LabelSetting;

public final class FPSBoost extends AbstractModule {

	public static FloatSetting CHUNK_LOADING = new FloatSetting("Chunk Loading", 1.0F, 100.0F, 100.0F);

	public static BooleanSetting LIGHTING = new BooleanSetting("Lighting Updates", true);
	public static BooleanSetting CHAT_SHADOW = new BooleanSetting("Chat Shadow", false);
	public static BooleanSetting CLEAR_GLASS = new BooleanSetting("Clear Glass", false);
	public static BooleanSetting SHINY_POTS = new BooleanSetting("Shiny Pots", false);
	public static BooleanSetting ITEM_GLINT = new BooleanSetting("Item Glint", true);
	public static BooleanSetting FAST_CHAT = new BooleanSetting("Fast Chat", false);
	public static BooleanSetting WEATHER = new BooleanSetting("Weather", true);
	public static BooleanSetting MENU_BLUR = new BooleanSetting("Menu Blur", true);

	public FPSBoost() {
		super("FPS Boost");

		this.addSetting(new LabelSetting("General Settings"));

		this.addSetting(CHUNK_LOADING);
		this.addSetting(CLEAR_GLASS);
		this.addSetting(CHAT_SHADOW);
		this.addSetting(ITEM_GLINT);
		this.addSetting(SHINY_POTS);
		this.addSetting(FAST_CHAT);
		this.addSetting(LIGHTING);
		this.addSetting(WEATHER);
		this.addSetting(MENU_BLUR);
	}

	@Override public void renderReal() {

	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(true);
	}

}
