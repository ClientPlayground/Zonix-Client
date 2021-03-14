package us.zonix.client.module.impl;

import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;

public final class TimeChanger extends AbstractModule {

	public static final StringSetting TIME_SCALE = new StringSetting("Time",
			"Natural", "Night Only", "Day Only");

	public TimeChanger() {
		super("TimeChanger");

		this.addSetting(new LabelSetting("General Settings"));
		this.addSetting(TIME_SCALE);
	}

}
