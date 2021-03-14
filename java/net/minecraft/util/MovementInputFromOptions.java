package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import us.zonix.client.Client;
import us.zonix.client.module.impl.ToggleSneak;

public class MovementInputFromOptions extends MovementInput {

	public boolean sprintHeldAndReleased;
	private boolean handledSprintPress;
	private boolean handledSneakPress;
	public boolean sprintDoubleTapped;
	public boolean canDoubleTap;
	private boolean wasRiding;
	public boolean isDisabled;
	public boolean sprint;

	public long lastSprintPressed;
	public long lastPressed;

	private GameSettings gameSettings;
	private static final String __OBFID = "CL_00000937";

	public MovementInputFromOptions(GameSettings p_i1237_1_) {
		this.gameSettings = p_i1237_1_;
	}

	public void updatePlayerMoveState() {
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;

		if (this.gameSettings.keyBindForward.getIsKeyPressed()) {
			++this.moveForward;
		}

		if (this.gameSettings.keyBindBack.getIsKeyPressed()) {
			--this.moveForward;
		}

		if (this.gameSettings.keyBindLeft.getIsKeyPressed()) {
			++this.moveStrafe;
		}

		if (this.gameSettings.keyBindRight.getIsKeyPressed()) {
			--this.moveStrafe;
		}

		this.jump = this.gameSettings.keyBindJump.getIsKeyPressed();

		Minecraft mc = Minecraft.getMinecraft();

		if (ToggleSneak.TOGGLE_SNEAK.getValue()) {
			if (this.gameSettings.keyBindSneak.getIsKeyPressed() && !this.handledSneakPress) {
				if (mc.thePlayer.isRiding() || mc.thePlayer.capabilities.isFlying) {
					this.sneak = true;
					this.wasRiding = mc.thePlayer.isRiding();
				} else {
					this.sneak = !this.sneak;
				}
				this.lastPressed = System.currentTimeMillis();
				this.handledSneakPress = true;
			}

			if (!this.gameSettings.keyBindSneak.getIsKeyPressed() && this.handledSneakPress) {
				if (mc.thePlayer.capabilities.isFlying || this.wasRiding) {
					this.sneak = this.wasRiding = false;
				} else if (System.currentTimeMillis() - this.lastPressed > 300L) {
					this.sneak = false;
				}
				this.handledSneakPress = false;
			}
		} else {
			this.sneak = this.gameSettings.keyBindSneak.getIsKeyPressed();
		}

		if (this.sneak) {
			this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
			this.moveForward = (float) ((double) this.moveForward * 0.3D);
		}

		boolean hunger = mc.thePlayer.getFoodStats().getFoodLevel() > 6.0f || mc.thePlayer.capabilities.isFlying;
		boolean sprint = !this.sneak && !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.isRiding() && hunger;

		this.isDisabled = !ToggleSneak.TOGGLE_SPRINT.getValue();
		this.canDoubleTap = ToggleSneak.DOUBLE_TAP_W.getValue();

		if ((sprint || this.isDisabled) && this.gameSettings.keyBindSprint.getIsKeyPressed() &&
		    !this.handledSprintPress && !this.isDisabled) {
			this.sprint = !this.sprint;
			this.lastSprintPressed = System.currentTimeMillis();
			this.handledSprintPress = true;
			this.sprintHeldAndReleased = false;
		}

		if ((sprint || this.isDisabled) &&
		    !this.gameSettings.keyBindSprint.getIsKeyPressed() && this.handledSprintPress) {

			if (System.currentTimeMillis() - this.lastSprintPressed > 300L) {
				this.sprintHeldAndReleased = true;
			}
			this.handledSprintPress = false;
		}

		Client.getInstance().getModuleManager().getModule(ToggleSneak.class)
				.update(this.sprint, this.sprintHeldAndReleased, this.isDisabled, this.sprintDoubleTapped);
	}
}
