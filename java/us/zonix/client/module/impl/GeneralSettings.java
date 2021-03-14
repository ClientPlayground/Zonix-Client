package us.zonix.client.module.impl;

import lombok.Getter;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;

public final class GeneralSettings extends AbstractModule {

	@Getter private static final GeneralSettings instance = new GeneralSettings();

	private GeneralSettings() {
		super("General");

		this.addSetting(new BooleanSetting("Show HUD in \'Focus Mode\'"));
		this.addSetting(new BooleanSetting("Show Hotbar in \'Focus Mode\'"));
	}

}
