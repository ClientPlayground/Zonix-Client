package us.zonix.client.setting.impl;

import lombok.Data;
import us.zonix.client.setting.ISetting;

@Data
public final class TextSetting implements ISetting<String> {

	private final String name;
	private final String defaultValue;

	private String value;

	private boolean editing;
	private boolean valued;
	private long valueFlipTime;

	public TextSetting(String name, String value) {
		this.name = name;

		this.defaultValue = this.value = value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
