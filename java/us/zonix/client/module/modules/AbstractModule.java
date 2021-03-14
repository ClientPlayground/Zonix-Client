package us.zonix.client.module.modules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import us.zonix.client.module.IModule;
import us.zonix.client.setting.ISetting;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.FloatSetting;

@Getter
@Setter
public abstract class AbstractModule implements IModule {

	private final Map<String, ISetting> settingMap = new HashMap<>();
	private final List<ISetting> sortedSettings = new LinkedList<>();

	protected final Minecraft mc = Minecraft.getMinecraft();

	private final String name;

	protected float x = 2.0F;
	protected float y = 2.0F;

	private int height;
	private int width;

	protected AbstractModule(String name) {
		this.name = name;

		this.addSetting(new BooleanSetting("Enabled", true));
	}

	@Override public boolean isEnabled() {
		return this.getBooleanSetting("Enabled").getValue();
	}

	@Override public void setEnabled(boolean enabled) {
		this.getBooleanSetting("Enabled").setValue(enabled);
	}

	protected <T extends ISetting> void addSetting(T t) {
		this.settingMap.put(t.getName().toLowerCase(), t);

		this.sortedSettings.add(t);
	}

	@SuppressWarnings("unchecked") private <T extends ISetting> T getSetting(String name) {
		return (T) this.settingMap.get(name.toLowerCase());
	}

	protected <T extends BooleanSetting> T getBooleanSetting(String name) {
		return this.getSetting(name);
	}

	protected <T extends FloatSetting> T getFloatSetting(String name) {
		return this.getSetting(name);
	}

	protected <T extends ColorSetting> T getColorSetting(String name) {
		return this.getSetting(name);
	}

}
