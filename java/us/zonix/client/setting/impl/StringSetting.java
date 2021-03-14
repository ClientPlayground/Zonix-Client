package us.zonix.client.setting.impl;

import lombok.Getter;
import us.zonix.client.setting.ISetting;

public final class StringSetting implements ISetting<String> {

	@Getter private final String[] options;
	private final String name;
	private String value;

	@Getter private int index;

	public StringSetting(String name, String... options) {
		this.name = name;
		this.options = options;
		this.value = options[0];
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(int index) {
		this.value = this.options[index];
		this.index = index;
	}

	public void setValue(String value) {
		this.value = value;

		for (int i = 0; i < this.options.length; i++) {
			String item = this.options[i];
			if (item.equals(value)) {
				this.index = i;
				break;
			}
		}
	}

}
