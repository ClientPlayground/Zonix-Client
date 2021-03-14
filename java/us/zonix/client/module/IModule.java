package us.zonix.client.module;

import java.util.List;
import java.util.Map;
import us.zonix.client.setting.ISetting;

public interface IModule {

	Map<String, ISetting> getSettingMap();

	List<ISetting> getSortedSettings();

	String getName();

	boolean isEnabled();

	void setEnabled(boolean enabled);

	int getWidth();
	int getHeight();

	float getX();
	float getY();

	void setX(float x);
	void setY(float y);

	/*
	Events
	 */

	default void renderPreview() {
		this.renderReal();
	}

	default void renderReal() {

	}

	default void onPostPlayerUpdate() {

	}

	default void onPrePlayerUpdate() {

	}

}
