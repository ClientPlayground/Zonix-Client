package us.zonix.client.module.impl;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Mouse;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.util.RenderUtil;

public final class CPS extends AbstractModule {

	private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw background", true);

	private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", 0xFFFF0000);
	private static final ColorSetting BACKGROUND = new ColorSetting("Background", 0x6F000000);

	private final Set<Long> clicks;

	private boolean buttonDown;

	public CPS() {
		super("CPS");

		this.clicks = new HashSet<>();

		this.x = 4;
		this.y = 76;

		this.addSetting(new LabelSetting("General Settings"));
		this.addSetting(DRAW_BACKGROUND);

		this.addSetting(new LabelSetting("Color Settings"));
		this.addSetting(FOREGROUND);
		this.addSetting(BACKGROUND);
	}

	@Override public void renderReal() {
		boolean buttonDown = Mouse.isButtonDown(0);
		Mouse.poll();

		boolean polledButtonDown = Mouse.isButtonDown(0);
		if (polledButtonDown && buttonDown && !this.buttonDown) {
			this.clicks.add(System.currentTimeMillis());
		}

		this.buttonDown = polledButtonDown;

		this.clicks.removeIf(l -> l + 1000L < System.currentTimeMillis());

		FontRenderer fontRenderer = this.mc.fontRenderer;
		String fps = this.clicks.size() + " CPS";

		int width = 70;
		this.setWidth(width);

		int height = 13;
		this.setHeight(height);

		if (DRAW_BACKGROUND.getValue()) {
			RenderUtil.drawRect(this.x, this.y, this.x + width, this.y + height,
					this.getColorSetting("Background").getValue());
		}

		fontRenderer.drawString(fps, this.x + (width / 2 - fontRenderer.getStringWidth(fps) / 2),
				this.y + 3, this.getColorSetting("Foreground").getValue());
	}

}
