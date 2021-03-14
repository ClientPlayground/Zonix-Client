package net.minecraft.client.gui;

import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.gui.MainMenuScreen;
import us.zonix.client.module.impl.FPSBoost;
import us.zonix.client.util.RenderUtil;

import java.awt.*;

public class GuiIngameMenu extends GuiScreen {

	private final ResourceLocation usersIcon = new ResourceLocation("icon/users.png");

	private int field_146445_a;
	private int field_146444_f;
	private static final String __OBFID = "CL_00000703";

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		if (FPSBoost.MENU_BLUR.getValue()) {
			this.mc.entityRenderer.setBlur(true);
		}

		this.escapeButtons[0] = new EscapeButton("BACK TO GAME") {
			@Override protected void onClick(int mouseX, int mouseY) {
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
			}
		};
		this.escapeButtons[1] = new EscapeButton("OPTIONS") {
			@Override protected void onClick(int mouseX, int mouseY) {
				mc.displayGuiScreen(new GuiOptions(GuiIngameMenu.this, mc.gameSettings));
			}
		};
		this.escapeButtons[2] = new EscapeButton("SERVER SELECTOR") {
			@Override protected void onClick(int mouseX, int mouseY) {
				mc.displayGuiScreen(new GuiMultiplayer(GuiIngameMenu.this));
			}
		};
		this.escapeButtons[3] = new EscapeButton("OPEN TO LAN") {
			@Override protected void onClick(int mouseX, int mouseY) {
				mc.displayGuiScreen(new GuiShareToLan(GuiIngameMenu.this));
			}
		};
		this.escapeButtons[4] = new EscapeButton("DISCONNECT") {
			@Override protected void onClick(int mouseX, int mouseY) {
				if (mc.theWorld != null) {
					mc.theWorld.sendQuittingDisconnectingPacket();
				}
				mc.loadWorld(null);
				mc.displayGuiScreen(new GuiMainMenu());
			}
		};

		this.field_146445_a = 0;
		this.buttonList.clear();
		//		byte var1 = -16;
		//		boolean var2 = true;
		//		this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + var1,
		//				I18n.format("menu.returnToMenu", new Object[0])));
		//
		//		if (!this.mc.isIntegratedServerRunning()) {
		//			((GuiButton) this.buttonList.get(0)).displayString = I18n.format("menu.disconnect", new Object[0]);
		//		}
		//
		//		this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + var1,
		//				I18n.format("menu.returnToGame", new Object[0])));
		//		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + var1, 98, 20,
		//				I18n.format("menu.options", new Object[0])));
		//		GuiButton var3;
		//		this.buttonList.add(var3 = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + var1, 98, 20,
		//				I18n.format("menu.shareToLan", new Object[0])));
		//		this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + var1, 98, 20,
		//				I18n.format("gui.achievements", new Object[0])));
		//		this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + var1, 98, 20,
		//				I18n.format("gui.stats", new Object[0])));
		//		var3.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
	}

	protected void actionPerformed(GuiButton p_146284_1_) {
		switch (p_146284_1_.id) {
			case 0:
				this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
				break;
			case 1:
				p_146284_1_.enabled = false;
				this.mc.theWorld.sendQuittingDisconnectingPacket();
				this.mc.loadWorld(null);
				this.mc.displayGuiScreen(new GuiMainMenu());
			case 2:
			case 3:
			default:
				break;
			case 4:
				this.mc.displayGuiScreen(null);
				this.mc.setIngameFocus();
				break;
			case 5:
				this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.func_146107_m()));
				break;
			case 6:
				this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.func_146107_m()));
				break;
			case 7:
				this.mc.displayGuiScreen(new GuiShareToLan(this));
				break;
		}
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		super.updateScreen();
		++this.field_146444_f;
	}

	private abstract class EscapeButton {
		private final String text;

		EscapeButton(String text) {
			this.text = text;
		}

		protected abstract void onClick(int mouseX, int mouseY);
	}

	private final EscapeButton[] escapeButtons = new EscapeButton[5];

	@Override protected void mouseClicked(int mouseX, int mouseY, int button) {
		ScaledResolution resolution = new ScaledResolution(this.mc);

		float buttonWidth = 40.0F;
		float startX = resolution.getScaledWidth() - 10.0F;

		if (mouseX >= startX - buttonWidth && mouseX <= startX && mouseY >= 11.5F && mouseY <= 31.0F) {
			this.mc.shutdown();
			return;
		}

		// Center
		float boxWidth = 225.0F;
		float boxHeight = 150.0F;
		float minX = resolution.getScaledWidth() / 2 - boxWidth / 2 + 10.0F;
		float minY = resolution.getScaledHeight() / 2 - boxHeight / 2 + 10.0F;

		float buttonHeight = 25.0F;

		for (EscapeButton escapeButton : this.escapeButtons) {
			if (escapeButton.text.equals("OPEN TO LAN") && !this.mc.isSingleplayer()) {
				continue;
			} else if (escapeButton.text.equals("SERVER SELECTOR") && this.mc.isSingleplayer()) {
				continue;
			}

			if (mouseX >= minX && mouseX <= minX + boxWidth - 20.0F &&
			    mouseY >= minY && mouseY <= minY + buttonHeight) {
				escapeButton.onClick(mouseX, mouseY);
				return;
			}

			minY += buttonHeight + 10.0F;
		}

	}

	@Override public void onGuiClosed() {
		this.mc.entityRenderer.setBlur(false);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//		drawDefaultBackground();

		ScaledResolution resolution = new ScaledResolution(this.mc);

		// Header
		RenderUtil.drawRect(0.0F, 0.0F, resolution.getScaledWidth(), 40.0F, new Color(25, 25, 25, 125).getRGB());
		RenderUtil.drawString(Client.getInstance().getLargeBoldFontRenderer(),
				"Zonix Client", 10.0F, 12.0F, 0xFFFFFFFF);

		float buttonWidth = 40.0F;
		float startX = resolution.getScaledWidth() - 10.0F;

		RenderUtil.drawBorderedRect(startX - buttonWidth, 11.5F, startX, 31.0F,
				1.0F, new Color(15, 15, 15, 190).getRGB(), new Color(22, 22, 22, 140).getRGB());

		RenderUtil.drawCenteredString(Client.getInstance().getRegularFontRenderer(),
				"Exit", (int) (startX - buttonWidth / 2.0F), 21, 0xFFFFFFFF);

		// Center
		float boxWidth = 225.0F;
		float boxHeight = 150.0F;
		float minX = resolution.getScaledWidth() / 2 - boxWidth / 2;
		float minY = resolution.getScaledHeight() / 2 - boxHeight / 2;

		RenderUtil.drawRect(minX, minY, minX + boxWidth, minY + boxHeight, 0x77161313);

		minX += 10.0F;
		minY += 10.0F;

		float buttonHeight = 25.0F;

		for (EscapeButton button : this.escapeButtons) {
			if (button.text.equals("OPEN TO LAN") && !this.mc.isSingleplayer()) {
				continue;
			} else if (button.text.equals("SERVER SELECTOR") && this.mc.isSingleplayer()) {
				continue;
			}

			String text;

			switch (button.text) {
				case "OPEN TO LAN":
					text = "Open to LAN";
					break;
				case "SERVER SELECTOR":
					text = "Server Selector";
					break;
				case "DISCONNECT":
					text = "Disconnect";
					break;
				case "BACK TO GAME":
					text = "Back to Game";
					break;
				case "OPTIONS":
					text = "Options";
					break;
				default:
					continue;
			}

			RenderUtil.drawBorderedRect(minX, minY, minX + boxWidth - 20.0F, minY + buttonHeight,
					1.0F, new Color(16, 16, 16, 180).getRGB(), new Color(23, 23, 23, 160).getRGB());

			RenderUtil.drawCenteredString(Client.getInstance().getRegularFontRenderer(), text,
					(int) (minX + (boxWidth - 20.0F) / 2), (int) (minY + buttonHeight / 2) + 1, 0xFFFFFFFF);

			minY += buttonHeight + 10.0F;
		}
	}

}
