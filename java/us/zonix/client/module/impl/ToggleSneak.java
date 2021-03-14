package us.zonix.client.module.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.FloatSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.TextSetting;
import us.zonix.client.util.RenderUtil;

public final class ToggleSneak extends AbstractModule {

	public static final BooleanSetting TOGGLE_SPRINT = new BooleanSetting("Toggle Sprint", true) {
		@Override public void setValue(Boolean value) {
			super.setValue(value);

			if (Minecraft.getMinecraft().thePlayer != null) {
				Minecraft.getMinecraft().thePlayer.sprintDisabledTick = true;
			}
		}
	};

	public static final BooleanSetting DOUBLE_TAP_W = new BooleanSetting("Double Tap W", false);
	public static final BooleanSetting TOGGLE_SNEAK = new BooleanSetting("Toggle Sneak", true);
	public static final BooleanSetting FLY_BOOST = new BooleanSetting("Fly Boost", true);

	public static final FloatSetting FLY_BOOST_AMOUNT = new FloatSetting("Boost Amount", 1.0F, 10.0F, 10.0F);

	private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", 0xFFFF0000);
	private static final ColorSetting BACKGROUND = new ColorSetting("Background", 0x6F000000);

	private final TextSetting vanillaSprintText = new TextSetting("Vanilla Sprint Text", "[Sprinting (Vanilla)]");
	private final TextSetting toggleSprintText = new TextSetting("Toggle Sprint Text", "[Sprinting (Toggled)]");
	private final TextSetting toggleSneakText = new TextSetting("Toggle Sneak Text", "[Sneaking (Toggled)]");
	private final TextSetting heldSprintText = new TextSetting("Held Sprint Text", "[Sprinting (Key Held)]");
	private final TextSetting heldSneakText = new TextSetting("Held Sneak Text", "[Sneaking (Key Held)]");
	private final TextSetting flyBoostText = new TextSetting("Fly Boost Text", "[Flying (10x boost)]");
	private final TextSetting dismountingText = new TextSetting("Dismounting Text", "[Dismounting]");
	private final TextSetting descendingText = new TextSetting("Descending Text", "[Descending]");
	private final TextSetting ridingText = new TextSetting("Riding Text", "[Riding]");
	private final TextSetting flyText = new TextSetting("Fly Text", "[Flying]");

	private String hudText;

	public ToggleSneak() {
		super("ToggleSneak");

		this.y = 4;

		this.addSetting(new LabelSetting("General Settings"));
		this.addSetting(new BooleanSetting("Hide from HUD", false));
		this.addSetting(new BooleanSetting("Draw Background", false));
		this.addSetting(TOGGLE_SPRINT);
		this.addSetting(TOGGLE_SNEAK);
		this.addSetting(DOUBLE_TAP_W);
		this.addSetting(FLY_BOOST);
		this.addSetting(FLY_BOOST_AMOUNT);

		this.addSetting(new LabelSetting("Color Settings"));
		this.addSetting(FOREGROUND);
		this.addSetting(BACKGROUND);

		this.addSetting(new LabelSetting("Text Settings"));
		this.addSetting(this.toggleSprintText);
		this.addSetting(this.toggleSneakText);
		this.addSetting(this.heldSprintText);
		this.addSetting(this.heldSneakText);
		this.addSetting(this.vanillaSprintText);
		this.addSetting(this.flyBoostText);
		this.addSetting(this.flyText);
		this.addSetting(this.ridingText);
		this.addSetting(this.descendingText);
		this.addSetting(this.dismountingText);
	}

	@Override
	public void renderPreview() {
		if (this.hudText != null && !this.hudText.isEmpty()) {
			this.renderReal();
			return;
		}

		this.hudText = this.toggleSprintText.getValue().replace("&", "\u00a7");

		if (this.getBooleanSetting("Draw Background").getValue()) {
			this.hudText = this.hudText.replace("[", "").replace("]", "");
		}

		this.setWidth(this.mc.fontRenderer.getStringWidth(this.hudText) + 3);
		this.setHeight(this.mc.fontRenderer.FONT_HEIGHT + 3);

		if (this.getBooleanSetting("Draw Background").getValue()) {
			this.setHeight(this.getHeight() + 2);
			this.setWidth(this.getWidth() + 2);
		}

		this.renderReal();

		this.hudText = null;
	}

	@Override
	public void renderReal() {
		if (this.getBooleanSetting("Hide from HUD").getValue()) {
			return;
		}

		if (this.hudText == null || this.hudText.isEmpty()) {
			return;
		}

		FontRenderer fontRenderer = this.mc.fontRenderer;

		if (this.getBooleanSetting("Draw Background").getValue()) {
			RenderUtil.drawRect(this.x, this.y, this.x + this.getWidth(),
					this.y + this.getHeight(), BACKGROUND.getValue());

			fontRenderer.drawString(this.hudText, this.x + 2, this.y + 2, FOREGROUND.getValue());
			return;
		}

		fontRenderer.drawStringWithShadow(this.hudText, this.getX() + 2,
				this.getY() + 2, FOREGROUND.getValue());
	}

	public void update(boolean sprint, boolean sprintHeldAndReleased, boolean isDisabled, boolean sprintDoubleTapped) {
		boolean isFlying = this.mc.thePlayer.capabilities.isFlying;
		boolean isRiding = this.mc.thePlayer.isRiding();

		boolean isHoldingSprint = this.mc.gameSettings.keyBindSprint.getIsKeyPressed();
		boolean isHoldingSneak = this.mc.gameSettings.keyBindSneak.getIsKeyPressed();

		this.hudText = "";

		if (isFlying) {
			if (isHoldingSprint && this.mc.thePlayer.capabilities.isCreativeMode && FLY_BOOST.getValue()) {
				this.hudText += this.flyBoostText.getValue().replace("{BOOST}",
						String.format("%.1f", FLY_BOOST_AMOUNT.getValue())) + " ";
			} else {
				this.hudText += this.flyText.getValue() + " ";
			}
		}

		if (isRiding) {
			this.hudText += this.ridingText.getValue() + " ";
		}

		if (this.mc.thePlayer.movementInput.sneak) {
			if (isFlying) {
				this.hudText += this.descendingText.getValue() + "  " + EnumChatFormatting.RESET;
			} else if (isRiding) {
				this.hudText += this.dismountingText.getValue() + "  " + EnumChatFormatting.RESET;
			} else if (isHoldingSneak) {
				this.hudText += this.heldSneakText.getValue();
			} else {
				this.hudText += this.toggleSneakText.getValue();
			}
		} else if (sprint && !isFlying && !isRiding) {
			boolean isVanilla = sprintHeldAndReleased || isDisabled || sprintDoubleTapped;

			if (isHoldingSprint) {
				this.hudText += this.heldSprintText.getValue();
			} else if (isVanilla) {
				this.hudText += this.vanillaSprintText.getValue();
			} else {
				this.hudText += this.toggleSprintText.getValue();
			}
		}

		if (this.hudText.isEmpty()) {
			this.setHeight(0);
			this.setWidth(0);
		} else {
			this.hudText = this.hudText.replace("&", "\u00a7");
			this.setWidth(this.mc.fontRenderer.getStringWidth(this.hudText) + 3);
			this.setHeight(this.mc.fontRenderer.FONT_HEIGHT + 3);
		}
	}

}
