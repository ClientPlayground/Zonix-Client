package us.zonix.client.module.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.util.HUDUtils;
import us.zonix.client.util.RenderUtil;
import us.zonix.client.util.font.ZFontRenderer;

public final class DirectionHUD extends AbstractModule {

	private static final ResourceLocation CARET_DOWN = new ResourceLocation("icon/caret-down.png");
	private static final ResourceLocation COMPASS = new ResourceLocation("compass.png");

	private static final Map<Integer, String> directionMap = new HashMap<>();

	private static final StringSetting DISPLAY_TYPE = new StringSetting("Display Type",
			"Zonix", "Zonix", "Classic", "Classic - Dark");

	private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw Background");

	private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", 0xFFFFFFFF);
	private static final ColorSetting BACKGROUND = new ColorSetting("Background", 0x6F000000);

	static {
		directionMap.put(0, "S");
		directionMap.put(45, "SW");
		directionMap.put(90, "W");
		directionMap.put(135, "NW");
		directionMap.put(180, "N");
		directionMap.put(225, "NE");
		directionMap.put(270, "E");
		directionMap.put(315, "SE");
	}

	public DirectionHUD() {
		super("Direction HUD");

		this.addSetting(new LabelSetting("General Settings"));
		this.addSetting(DRAW_BACKGROUND);
		this.addSetting(DISPLAY_TYPE);

		this.addSetting(new LabelSetting("Color Settings"));
		this.addSetting(FOREGROUND);
		this.addSetting(BACKGROUND);
	}

	@Override public void renderReal() {
		switch (DISPLAY_TYPE.getIndex()) {
			case 0:
				this.renderZonix();
				break;
			case 1:
				this.renderClassic(false);
				break;
			case 2:
				this.renderClassic(true);
				break;
		}
	}

	private void renderZonix() {
		int hex = FOREGROUND.getValue();
		Color color = new Color(FOREGROUND.getValue());
		int opaque = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0xA9).getRGB();

		float direction = this.mc.thePlayer.rotationYaw;
		while (direction > 360.0F) {
			direction -= 360.0F;
		}

		while (direction < 0.0F) {
			direction += 360.0F;
		}

		GL11.glPushMatrix();
		{
			float y = this.y + 17.5F;

			GL11.glPushMatrix();
			{
				RenderUtil.startScissorBox(this.y, y + 25.0F, this.x, this.x + 300.0F);

				if (DRAW_BACKGROUND.getValue()) {
					RenderUtil.drawRect(this.x, this.y, this.x + 300.0F, this.y + 30.0F, BACKGROUND.getValue());
				}

				GL11.glTranslatef(-direction * 2 + 150.0F, 0.0F, 0.0F);

				float steps = 15.0F;

				List<Integer> directions = new LinkedList<>();
				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 360; j += steps) {
						directions.add(j);
					}
				}

				float x = this.x - (directions.size() / 2 * steps);
				for (Integer j : directions) {
					String mapped = directionMap.get(j);
					if (mapped != null) {
						ZFontRenderer fontRenderer = Client.getInstance().getLargeBoldFontRenderer();
						if (mapped.length() == 2) {
							fontRenderer = Client.getInstance().getRegularFontRenderer();
						}

						RenderUtil.drawCenteredString(fontRenderer, mapped, x, y + 5.0F, hex, false);
					} else {
						RenderUtil.drawRect(x, y + 1.5F, x + 1.0F, y + 5.0F, opaque);

						RenderUtil.drawCenteredString(Client.getInstance().getTinyFontRenderer(),
								String.valueOf(j), x, y + 10.0F, hex, false);
					}

					x += steps * 2;
				}

				RenderUtil.endScissorBox();
			}
			GL11.glPopMatrix();

			float width = 7.5F;

			GL11.glPushMatrix();

			color = new Color(hex);
			GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F,
					color.getBlue() / 255.0F, color.getAlpha() / 255.0F);

			RenderUtil.drawSquareTexture(CARET_DOWN, width, width + 5.0F,
					this.x + 300.0F / 2 - width / 2.0F, this.y + 5.0F);

			RenderUtil.drawCenteredString(Client.getInstance().getSmallFontRenderer(), String.valueOf((int) direction),
					this.x + 300.0F / 2, this.y + Client.getInstance().getSmallFontRenderer().getHeight(), hex, false);

			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		this.setHeight(35);
		this.setWidth(300);
	}

	private void renderClassic(boolean dark) {
		int direction = MathHelper.floor_double(this.mc.thePlayer.rotationYaw * 256.0F / 360.0F + 0.5D) & 0xFF;

		GL11.glPushMatrix();
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			RenderUtil.bindTexture(COMPASS);
			if (direction < 128) {
				HUDUtils.drawTexturedModalRect((int) this.getX(), (int) this.getY(),
						direction, dark ? 0 : 24, 65, 12, -100);
			} else {
				HUDUtils.drawTexturedModalRect((int) this.getX(), (int) this.getY(),
						direction - 128, 12 + (dark ? 0 : 24), 65, 12, -100);
			}

			RenderUtil.drawString(EnumChatFormatting.RED + "|",
					this.getX() + 32, this.getY() + 1, 16777215);

			RenderUtil.drawString(EnumChatFormatting.RED + "|" + EnumChatFormatting.RESET,
					this.getX() + 32, this.getY() + 5, 16777215);
		}
		GL11.glPopMatrix();

		this.setHeight(12);
		this.setWidth(65);
	}

}
