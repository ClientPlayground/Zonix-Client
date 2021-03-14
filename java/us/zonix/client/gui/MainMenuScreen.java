package us.zonix.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import us.zonix.client.Client;
import us.zonix.client.util.RenderUtil;
import us.zonix.client.util.font.ZFontRenderer;

import java.awt.*;

public final class MainMenuScreen extends GuiScreen {

	public static final ResourceLocation closeIcon = new ResourceLocation("icon/close.png");

	private static final ResourceLocation[] panoramaBackground =
			new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"),
					new ResourceLocation("textures/gui/title/background/panorama_1.png"),
					new ResourceLocation("textures/gui/title/background/panorama_2.png"),
					new ResourceLocation("textures/gui/title/background/panorama_3.png"),
					new ResourceLocation("textures/gui/title/background/panorama_4.png"),
					new ResourceLocation("textures/gui/title/background/panorama_5.png")};
	private static final ResourceLocation panoramaBlur;

	private static final ResourceLocation languageIcon = new ResourceLocation("icon/language.png");
	private static final ResourceLocation settingsIcon = new ResourceLocation("icon/settings.png");
	private static final ResourceLocation zonixLogo = new ResourceLocation("zonix.png");

	private static final float logoSize = 80.0F;
	private static final float logoMargin = 12.0F;

	private static final float centerWidth = 200.0F;
	private static final float centerHeight = 180.0F;

	private static final float centerButtonWidth = 150.0F;
	private static final float centerButtonHeight = 30.0F;

	static {
		DynamicTexture viewportTexture = new DynamicTexture(256, 256);
		panoramaBlur = Minecraft.getMinecraft().getTextureManager()
				.getDynamicTextureLocation("background", viewportTexture);
	}

	@Override public void initGui() {
		Keyboard.enableRepeatEvents(true);
	}

	@Override protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (button != 0)
			return;

		ScaledResolution resolution = new ScaledResolution(this.mc);

		float buttonPadding = 5.0F;
		float iconWidth = 9.0F;
		float startX = resolution.getScaledWidth() - 10.0F;

		ZFontRenderer medium = Client.getInstance().getMediumFontRenderer();

		for (int i = 0; i < 3; i++) {
			String text;

			switch (i) {
				case 0:
					text = "Exit";
					break;
				case 1:
					text = "Settings";
					break;
				case 2:
					text = "Language";
					break;
				default:
					continue;
			}

			float stringWidth = medium.getStringWidth(text);
			float buttonWidth = stringWidth + (buttonPadding * 2) + (iconWidth * 2);

			boolean hovering = mouseX > startX - buttonWidth && mouseX < startX && mouseY > 7.5F && mouseY < 35.0F;

			if (hovering) {
				switch (i) {
					case 0:
						this.mc.shutdown();
						break;
					case 1:
						this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
						break;
					case 2:
						this.mc.displayGuiScreen(new GuiLanguage(this,
								this.mc.gameSettings, this.mc.getLanguageManager()));
						break;
				}
				return;
			}

			startX -= buttonWidth + 10.0F;
		}

		float minX = resolution.getScaledWidth() / 2 - (centerButtonWidth / 2);
		float minY = resolution.getScaledHeight() / 2 - centerHeight / 2 + logoSize + logoMargin + 8;

		for (int i = 0; i < 2; i++) {
			if (mouseX >= minX && mouseX <= minX + centerButtonWidth && mouseY >= minY && mouseY <= minY + centerButtonHeight) {
				switch (i) {
					case 0:
						this.mc.displayGuiScreen(new GuiSelectWorld(this));
						break;
					case 1:
						this.mc.displayGuiScreen(new GuiMultiplayer(this));
						break;
				}
				return;
			}

			minY += (centerButtonHeight + 5);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		this.renderSkybox(partialTicks);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		ScaledResolution resolution = new ScaledResolution(this.mc);

//		RenderUtil.drawTexture(panoramaBackground, 0, 0, resolution.getScaledWidth(), resolution.getScaledHeight());

		RenderUtil.drawRect(0.0F, 0.0F, resolution.getScaledWidth(), resolution.getScaledHeight(), new Color(155, 155, 155, 50).getRGB());

		// Header
		RenderUtil.drawRect(0.0F, 0.0F, resolution.getScaledWidth(), 40.0F, new Color(25, 25, 25, 125).getRGB());
		RenderUtil.drawString(Client.getInstance().getLargeBoldFontRenderer(),
				"Zonix Client", 10.0F, 12.0F, 0xFFFFFFFF);

		float buttonPadding = 5.0F;
		float iconWidth = 9.0F;
		float startX = resolution.getScaledWidth() - 10.0F;

		ZFontRenderer medium = Client.getInstance().getMediumFontRenderer();

		for (int i = 0; i < 3; i++) {
			String text;
			ResourceLocation icon;

			switch (i) {
				case 0:
					text = "Exit";
					icon = closeIcon;
					break;
				case 1:
					text = "Settings";
					icon = settingsIcon;
					break;
				case 2:
					text = "Language";
					icon = languageIcon;
					break;
				default:
					continue;
			}

			float stringWidth = medium.getStringWidth(text);
			float buttonWidth = stringWidth + (buttonPadding * 2) + (iconWidth * 2);

			boolean hovering = mouseX > startX - buttonWidth && mouseX < startX && mouseY > 7.5F && mouseY < 35.0F;

			RenderUtil.drawBorderedRect(startX - buttonWidth, 9.5F, startX, 33.0F,
					1.0F, new Color(15, 15, 15, 190).getRGB(), new Color(22, 22, 22, 140).getRGB());

			RenderUtil.drawCenteredString(medium, text, (int) (startX - buttonWidth / 2.0F + iconWidth),
					21, hovering ? 0xFFFFFFFF : 0xA9FFFFFF, false);

			RenderUtil.drawSquareTexture(icon, iconWidth, startX - buttonWidth - buttonPadding + iconWidth, 12.0F);

			startX -= buttonWidth + 10.0F;
		}

		// Center
		float minX = resolution.getScaledWidth() / 2 - centerWidth / 2;
		float minY = resolution.getScaledHeight() / 2 - centerHeight / 2;

		RenderUtil.drawBorderedRect(minX, minY, minX + centerWidth, minY + centerHeight, 1.0F, new Color(11, 11, 11, 130).getRGB(), new Color(25, 25, 25, 100).getRGB());

		GL11.glPushMatrix();
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderUtil.drawSquareTexture(zonixLogo, logoSize, logoSize,
					resolution.getScaledWidth() / 2 - (logoSize / 2), minY + logoMargin);
		}
		GL11.glPopMatrix();

		String[] strings = new String[]{"Singleplayer", "Multiplayer"};

		minX = resolution.getScaledWidth() / 2 - (centerButtonWidth / 2);
		minY += centerHeight - 80.0F;

		for (int i = 0; i < 2; i++) {
			RenderUtil.drawBorderedRect(minX, minY, minX + centerButtonWidth, minY + centerButtonHeight, 1.0F, new Color(11, 11, 11, 180).getRGB(), new Color(23, 23, 23, 150).getRGB());

			boolean hovering = mouseX >= minX && mouseX <= minX + centerButtonWidth &&
			                   mouseY >= minY && mouseY <= minY + centerButtonHeight;

			RenderUtil.drawCenteredString(Client.getInstance().getMediumFontRenderer(), strings[i],
					(int) (minX + (centerButtonWidth / 2)), (int) (minY + (centerButtonHeight / 2)), hovering ? 0xFFFFFFFF : 0xA9FFFFFF, false);

			minY += (centerButtonHeight + 5);
		}

		// Footer
		RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), "Minecraft 1.7.10 (zonix-1.2)", 10.0F,
				resolution.getScaledHeight() - 12.0F, 0xFFFFFFFF, false);

		String notice = "Zonix, LLC is not affiliated with Mojang AB.";

		RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), notice,
				resolution.getScaledWidth() - 10.0F -
				Client.getInstance().getSmallFontRenderer().getStringWidth(notice),
				resolution.getScaledHeight() - 12.0F, 0xFFFFFFFF, false);
	}

	/*
	Minecraft's gay ass panorama shit
	 */
	private int panoramaTimer;

	@Override public void updateScreen() {
		this.panoramaTimer++;
	}

	private void renderSkybox(float partialTicks) {
		this.mc.getFramebuffer().unbindFramebuffer();
		GL11.glViewport(0, 0, 256, 256);

		this.drawPanorama(partialTicks);

		this.rotateAndBlurSkybox();
		this.rotateAndBlurSkybox();
		this.rotateAndBlurSkybox();
		this.rotateAndBlurSkybox();
		this.rotateAndBlurSkybox();
		this.rotateAndBlurSkybox();
		this.rotateAndBlurSkybox();

		this.mc.getFramebuffer().bindFramebuffer(true);

		GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);

		Tessellator var4 = Tessellator.instance;
		var4.startDrawingQuads();
		float var5 = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
		float var6 = (float) this.height * var5 / 256.0F;
		float var7 = (float) this.width * var5 / 256.0F;

		var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);

		int var8 = this.width;
		int var9 = this.height;

		var4.addVertexWithUV(0.0D, (double) var9, (double) this.zLevel,
				(double) (0.5F - var6), (double) (0.5F + var7));

		var4.addVertexWithUV((double) var8, (double) var9, (double) this.zLevel,
				(double) (0.5F - var6), (double) (0.5F - var7));

		var4.addVertexWithUV((double) var8, 0.0D, (double) this.zLevel,
				(double) (0.5F + var6), (double) (0.5F - var7));

		var4.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel,
				(double) (0.5F + var6), (double) (0.5F + var7));

		var4.draw();
	}

	private void drawPanorama(float partialTicks) {
		Tessellator var4 = Tessellator.instance;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		byte var5 = 8;

		for (int var6 = 0; var6 < var5 * var5; ++var6) {
			GL11.glPushMatrix();
			float var7 = ((float) (var6 % var5) / (float) var5 - 0.5F) / 64.0F;
			float var8 = ((float) (var6 / var5) / (float) var5 - 0.5F) / 64.0F;
			float var9 = 0.0F;
			GL11.glTranslatef(var7, var8, var9);
			GL11.glRotatef(MathHelper.sin(((float) this.panoramaTimer + partialTicks) / 400.0F) *
					25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-((float) this.panoramaTimer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int var10 = 0; var10 < 6; ++var10) {
				GL11.glPushMatrix();

				if (var10 == 1) {
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 2) {
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 3) {
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 4) {
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (var10 == 5) {
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				this.mc.getTextureManager().bindTexture(this.panoramaBackground[var10]);
				var4.startDrawingQuads();
				var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
				float var11 = 0.0F;
				var4.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double) (0.0F + var11), (double) (0.0F + var11));
				var4.addVertexWithUV(1.0D, -1.0D, 1.0D, (double) (1.0F - var11), (double) (0.0F + var11));
				var4.addVertexWithUV(1.0D, 1.0D, 1.0D, (double) (1.0F - var11), (double) (1.0F - var11));
				var4.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double) (0.0F + var11), (double) (1.0F - var11));
				var4.draw();
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glColorMask(true, true, true, false);
		}

		var4.setTranslation(0.0D, 0.0D, 0.0D);
		GL11.glColorMask(true, true, true, true);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@SuppressWarnings("Duplicates") private void rotateAndBlurSkybox() {
		this.mc.getTextureManager().bindTexture(this.panoramaBlur);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GL11.glEnable(GL11.GL_BLEND);

		OpenGlHelper.glBlendFunc(770, 771, 1, 0);

		GL11.glColorMask(true, true, true, false);

		Tessellator var2 = Tessellator.instance;
		var2.startDrawingQuads();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		byte var3 = 3;

		for (int var4 = 0; var4 < var3; ++var4) {
			var2.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (var4 + 1));
			int var5 = this.width;
			int var6 = this.height;
			float var7 = (float) (var4 - var3 / 2) / 256.0F;
			var2.addVertexWithUV((double) var5, (double) var6, (double) this.zLevel, (double) (0.0F + var7), 1.0D);
			var2.addVertexWithUV((double) var5, 0.0D, (double) this.zLevel, (double) (1.0F + var7), 1.0D);
			var2.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (1.0F + var7), 0.0D);
			var2.addVertexWithUV(0.0D, (double) var6, (double) this.zLevel, (double) (0.0F + var7), 0.0D);
		}

		var2.draw();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColorMask(true, true, true, true);
	}

}
