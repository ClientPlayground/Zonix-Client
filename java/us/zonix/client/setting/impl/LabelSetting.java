package us.zonix.client.setting.impl;

import us.zonix.client.setting.ISetting;

public final class LabelSetting implements ISetting<String> {

	private final String name;
	private final String value;

	public LabelSetting(String name) {
		this.name = this.value = name;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {

	}

}
