package us.zonix.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.util.font.ZFontRenderer;

@SuppressWarnings("Duplicates") public final class RenderUtil {

	public static void drawCircle(double x, double y, double r) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Tessellator tes = Tessellator.instance;
		tes.startDrawing(GL11.GL_TRIANGLE_FAN);
		tes.addVertex(x, y, 0.0F);

		double end = Math.PI * 2.0;
		double increment = end / 30.0F;
		for (double theta = -increment; theta < end; theta += increment) {
			tes.addVertex(x + (r * Math.cos(-theta)), y + (r * Math.sin(-theta)), 0.0F);
		}
		tes.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawSquareTexture(ResourceLocation resourceLocation, float size, float x, float y) {
		float height = size * 2;
		float width = size * 2;

		float u = 0;
		float v = 0;

		GL11.glEnable(GL11.GL_BLEND);

		Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2d(u / size, v / size);
			GL11.glVertex2d(x, y);
			GL11.glTexCoord2d(u / size, (v + size) / size);
			GL11.glVertex2d(x, y + height);
			GL11.glTexCoord2d((u + size) / size, (v + size) / size);
			GL11.glVertex2d(x + width, y + height);
			GL11.glTexCoord2d((u + size) / size, v / size);
			GL11.glVertex2d(x + width, y);
		}
		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawTexturedModalRect(float x, float y, int width, int height, int u, int v) {
		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawingQuads();

		float scaleX = 0.00390625F;
		float scaleY = 0.00390625F;

		tessellator.addVertexWithUV((double) (x + 0.0F), (double) (y + (float) height), 0.0D,
				(double) ((float) (u) * scaleX), (double) ((float) (v + height) * scaleY));

		tessellator.addVertexWithUV((double) (x + (float) width), (double) (y + (float) height), 0.0D,
				(double) ((float) (u + width) * scaleX), (double) ((float) (v + height) * scaleY));

		tessellator.addVertexWithUV((double) (x + (float) width), (double) (y + 0.0F), 0.0D,
				(double) ((float) (u + width) * scaleX), (double) ((float) (v) * scaleY));

		tessellator.addVertexWithUV((double) (x + 0.0F), (double) (y + 0.0F), 0.0D,
				(double) ((float) (u) * scaleX), (double) ((float) (v) * scaleY));

		tessellator.draw();
	}

	public static void startScissorBox(float minY, float maxY, float minX, float maxX) {
		GL11.glPushMatrix();

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		float width = maxX - minX;
		float height = maxY - minY;

		Minecraft mc = Minecraft.getMinecraft();

		float scale = new ScaledResolution(mc).getScaleFactor();

		GL11.glScissor((int) (minX * scale), (int) (mc.displayHeight - (minY + height) * scale),
				(int) (width * scale), (int) (height * scale));
	}

	public static void endScissorBox() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();
	}

	public static void drawTexture(ResourceLocation resourceLocation, float size, float x, float y) {
		GL11.glPushMatrix();
		{
			float squareSize = size * 2.0F;

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			bindTexture(resourceLocation);

			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glTexCoord2d(0, 0);
				GL11.glVertex2d(x, y);

				GL11.glTexCoord2d(0, 1);
				GL11.glVertex2d(x, y + squareSize);

				GL11.glTexCoord2d(1, 1);
				GL11.glVertex2d(x + squareSize, y + squareSize);

				GL11.glTexCoord2d(1, 0);
				GL11.glVertex2d(x + squareSize, y);
			}
			GL11.glEnd();

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glPopMatrix();
	}

	public static void drawSquareTexture(ResourceLocation resourceLocation,
	                                     float width, float height,
	                                     float x, float y) {
		float size = width / 2.0F;

		float u = 0;
		float v = 0;

		GL11.glEnable(3042);

		Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);

		GL11.glBegin(7);
		{
			GL11.glTexCoord2d(u / size, v / size);
			GL11.glVertex2d(x, y);
			GL11.glTexCoord2d(u / size, (v + size) / size);
			GL11.glVertex2d(x, y + height);
			GL11.glTexCoord2d((u + size) / size, (v + size) / size);
			GL11.glVertex2d(x + width, y + height);
			GL11.glTexCoord2d((u + size) / size, v / size);
			GL11.glVertex2d(x + width, y);
		}
		GL11.glEnd();

		GL11.glDisable(3042);
	}

	public static void scaleAtPoint(float centerX, float centerY, float scale) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

		GL11.glTranslatef(centerX, centerY, 0.0F);
		GL11.glScalef(scale, scale, 0.0F);
		GL11.glTranslatef(centerX * -1.0F, centerY * -1.0F, 0.0F);
	}

	public static void drawCenteredString(String text, int x, int y, int color) {
		ZFontRenderer fontRenderer = Client.getInstance().getRegularFontRenderer();
		drawCenteredString(fontRenderer, text, x, y, color);
	}

	public static void drawCenteredStringWithIcon(
			ResourceLocation resourceLocation, float width,
			ZFontRenderer fontRenderer, String text, int x, int y, int color
	) {
		int textX = (int) (x + width * 1.5F + 1.5F);
		drawCenteredString(fontRenderer, text, textX, y + fontRenderer.getHeight(), color);

		GL11.glPushMatrix();
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);

			float iconX = x - width * 3.0F - 4.5F;
			drawSquareTexture(resourceLocation, width, iconX, y + width / 4.0F);
		}
		GL11.glPopMatrix();
	}

	public static void drawCenteredString(ZFontRenderer fontRenderer, String text, float x, float y, int color) {
		drawCenteredString(fontRenderer, text, x, y, color, true);
	}

	public static void drawCenteredString(ZFontRenderer fontRenderer, String text,
	                                      float x, float y, int color, boolean shadow) {
		int width = fontRenderer.getStringWidth(text);
		int height = fontRenderer.getHeight();

		float dX = x - width / 2;
		float dY = y - height / 2;

		if (shadow) {
			fontRenderer.drawStringWithShadow(text, dX, dY, color);
		} else {
			fontRenderer.drawString(text, dX, dY, color);
		}
	}

	public static void drawSmallString(String text, float x, float y, int color) {
		ZFontRenderer fontRenderer = Client.getInstance().getSmallFontRenderer();
		fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color);
	}

	public static void drawString(String text, float x, float y, int color) {
		ZFontRenderer fontRenderer = Client.getInstance().getRegularFontRenderer();
		fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color);
	}

	public static void drawString(ZFontRenderer fontRenderer, String text,
	                              float x, float y, int color, boolean shadow) {
		if (shadow) {
			fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color);
		} else {
			fontRenderer.drawString(text, (int) x, (int) y, color);
		}
	}

	public static void drawString(ZFontRenderer fontRenderer, String text, float x, float y, int color) {
		fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color);
	}

	public static void drawString(String text, int x, int y, int color) {
		Client.getInstance().getRegularFontRenderer().drawStringWithShadow(text, x, y, color);
	}

	public static void bindTexture(ResourceLocation resourceLocation) {
		ITextureObject texture = Minecraft.getMinecraft().renderEngine.getTexture(resourceLocation);
		if (texture == null) {
			texture = new SimpleTexture(resourceLocation);
			Minecraft.getMinecraft().renderEngine.loadTexture(resourceLocation, texture);
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());
	}

	public static void drawTexture(ResourceLocation resourceLocation, float x, float y, float width, float height) {
		float size = width / 2;
		float u = 0;
		float v = 0;

		GL11.glEnable(GL11.GL_BLEND);

		bindTexture(resourceLocation);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2d(u / size, v / size);
			GL11.glVertex2d(x, y);
			GL11.glTexCoord2d(u / size, (v + size) / size);
			GL11.glVertex2d(x, y + height);
			GL11.glTexCoord2d((u + size) / size, (v + size) / size);
			GL11.glVertex2d(x + width, y + height);
			GL11.glTexCoord2d((u + size) / size, v / size);
			GL11.glVertex2d(x + width, y);
		}
		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawBorderedRoundedRect(float x, float y, float x1, float y1,
	                                           float borderSize, int borderC, int insideC) {
		drawRoundedRect(x, y, x1, y1, borderSize, borderC);
		drawRoundedRect(x + 0.5F, y + 0.5F, x1 - 0.5F, y1 - 0.5F, borderSize, insideC);
	}

	public static void drawBorderedRoundedRect(float x, float y, float x1, float y1,
	                                           float radius, float borderSize, int borderC, int insideC) {
		drawRoundedRect(x, y, x1, y1, radius, borderC);
		drawRoundedRect(x + borderSize, y + borderSize, x1 - borderSize, y1 - borderSize, radius, insideC);
	}

	public static void drawTexturedRect(float x, float y, int width, int height, int u, int v) {
		float scale = 0.00390625F;

		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawingQuads();

		tessellator.addVertexWithUV((double) x, (double) (y + v), 0.0D,
				(double) (width * scale), (double) ((height + (float) v) * scale));

		tessellator.addVertexWithUV((double) (x + u), (double) (y + v), 0.0D,
				(double) ((width + (float) u) * scale), (double) ((height + (float) v) * scale));

		tessellator.addVertexWithUV((double) (x + u), (double) y, 0.0D,
				(double) ((width + (float) u) * scale), (double) (height * scale));

		tessellator.addVertexWithUV((double) x, (double) y, 0.0D,
				(double) (width * scale), (double) (height * scale));

		tessellator.draw();
	}

	public static void setColor(int color) {
		float r = (float) (color >> 24 & 255) / 255.0F;
		float g = (float) (color >> 16 & 255) / 255.0F;
		float b = (float) (color >> 8 & 255) / 255.0F;
		float a = (float) (color & 255) / 255.0F;

		GL11.glColor4f(r, g, b, a);
	}

	public static void drawRect(float minX, float minY, float maxX, float maxY, int color) {
		float bounds;
		if (minX < maxX) {
			bounds = minX;
			minX = maxX;
			maxX = bounds;
		}

		if (minY < maxY) {
			bounds = minY;
			minY = maxY;
			maxY = bounds;
		}

		float r = (float) (color >> 24 & 255) / 255.0F;
		float g = (float) (color >> 16 & 255) / 255.0F;
		float b = (float) (color >> 8 & 255) / 255.0F;
		float a = (float) (color & 255) / 255.0F;

		Tessellator tessellator = Tessellator.instance;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(g, b, a, r);

		tessellator.startDrawingQuads();

		tessellator.addVertex((double) minX, (double) maxY, 0.0D);
		tessellator.addVertex((double) maxX, (double) maxY, 0.0D);
		tessellator.addVertex((double) maxX, (double) minY, 0.0D);
		tessellator.addVertex((double) minX, (double) minY, 0.0D);

		tessellator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawBorderedRect(float x, float y, float x2, float y2, float border, int bColor, int color) {
		drawRect(x + border, y + border, x2 - border, y2 - border, color);

		drawRect(x, y + border, x + border, y2 - border, bColor);
		drawRect(x2 - border, y + border, x2, y2 - border, bColor);

		drawRect(x, y, x2, y + border, bColor);
		drawRect(x, y2 - border, x2, y2, bColor);
	}

	public static void drawHollowRect(float x, float y, float x2, float y2, float border, int bColor) {
		drawRect(x, y + border, x + border, y2 - border, bColor);
		drawRect(x2 - border, y + border, x2, y2 - border, bColor);

		drawRect(x, y, x2, y + border, bColor);
		drawRect(x, y2 - border, x2, y2, bColor);
	}

	public static void drawBorderedRect(int x, int y, int x2, int y2, int border, int bColor, int color) {
		Gui.drawRect(x + border, y + border, x2 - border, y2 - border, color);

		Gui.drawRect(x, y + border, x + border, y2 - border, bColor);
		Gui.drawRect(x2 - border, y + border, x2, y2 - border, bColor);

		Gui.drawRect(x, y, x2, y + border, bColor);
		Gui.drawRect(x, y2 - border, x2, y2, bColor);
	}

	public static void drawRoundedRect(double x, double y, double x1, double y1, double radius, int color) {
		float f = (color >> 24 & 0xFF) / 255.0F;
		float f1 = (color >> 16 & 0xFF) / 255.0F;
		float f2 = (color >> 8 & 0xFF) / 255.0F;
		float f3 = (color & 0xFF) / 255.0F;

		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);

		x *= 2;
		y *= 2;
		x1 *= 2;
		y1 *= 2;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		GL11.glBegin(GL11.GL_POLYGON);

		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x + radius + +(Math.sin((i * Math.PI / 180)) * (radius * -1)),
					y + radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
		}

		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x + radius + (Math.sin((i * Math.PI / 180)) * (radius * -1)),
					y1 - radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
		}

		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius),
					y1 - radius + (Math.cos((i * Math.PI / 180)) * radius));
		}

		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius),
					y + radius + (Math.cos((i * Math.PI / 180)) * radius));
		}

		GL11.glEnd();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glScaled(2, 2, 2);

		GL11.glPopAttrib();
	}

	public static void drawRoundedTexturedRect(ResourceLocation resourceLocation,
	                                           double x, double y, double x1, double y1, double radius, int color) {
		float f = (color >> 24 & 0xFF) / 255.0F;
		float f1 = (color >> 16 & 0xFF) / 255.0F;
		float f2 = (color >> 8 & 0xFF) / 255.0F;
		float f3 = (color & 0xFF) / 255.0F;

		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);

		x *= 2;
		y *= 2;
		x1 *= 2;
		y1 *= 2;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(f1, f2, f3, f);
		bindTexture(resourceLocation);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		GL11.glBegin(GL11.GL_POLYGON);

		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x + radius + +(Math.sin((i * Math.PI / 180)) * (radius * -1)),
					y + radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
		}

		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x + radius + (Math.sin((i * Math.PI / 180)) * (radius * -1)),
					y1 - radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
		}

		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius),
					y1 - radius + (Math.cos((i * Math.PI / 180)) * radius));
		}

		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius),
					y + radius + (Math.cos((i * Math.PI / 180)) * radius));
		}

		GL11.glEnd();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glScaled(2, 2, 2);

		GL11.glPopAttrib();
	}

}
