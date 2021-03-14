package us.zonix.client.module.impl;

import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.util.RenderUtil;

public final class ReachDisplay extends AbstractModule {

	private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw background", true);

	private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", 0xFFFF0000);
	private static final ColorSetting BACKGROUND = new ColorSetting("Background", 0x6F000000);

	public ReachDisplay() {
		super("Reach Display");

		this.setEnabled(false);

		this.addSetting(new LabelSetting("General Settings"));
		this.addSetting(DRAW_BACKGROUND);

		this.addSetting(new LabelSetting("Color Settings"));
		this.addSetting(FOREGROUND);
		this.addSetting(BACKGROUND);
	}

	@Override public void renderReal() {
		String text = String.format("%.2f blocks", this.mc.entityRenderer.lastRange);
		if (this.mc.entityRenderer.lastAttackTime + 2000L < System.currentTimeMillis()) {
			text = "0.0 blocks";
		}

		float width = this.mc.fontRenderer.getStringWidth(text) + 4;
		if (width < 70.0F) {
			width = 70.0F;
		}

		this.setWidth((int) width);
		this.setHeight(13);

		if (DRAW_BACKGROUND.getValue()) {
			RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(),
					this.getY() + this.getHeight(), BACKGROUND.getValue());
		}

		this.mc.fontRenderer.drawString(text,
				this.getX() + this.getWidth() / 2 - this.mc.fontRenderer.getStringWidth(text) / 2,
				this.y + 3, FOREGROUND.getValue());
	}

}
