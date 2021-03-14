package us.zonix.client.setting.impl;

import lombok.Getter;
import us.zonix.client.setting.ISetting;

public final class FloatSetting implements ISetting<Float> {

	private final String name;
	@Getter private final Float min;
	@Getter private final Float max;

	private Float value;

	public FloatSetting(String name, Float min, Float max, Float value) {
		this.name = name;
		this.min = min;
		this.max = max;
		this.value = value;
	}

	@Override public void setValue(Float value) {
		this.value = Math.max(this.min, Math.min(this.max, value));
	}

	public String getName() {
		return this.name;
	}

	public Float getValue() {
		return this.value;
	}
}
