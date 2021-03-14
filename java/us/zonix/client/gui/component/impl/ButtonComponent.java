package us.zonix.client.gui.component.impl;

import us.zonix.client.gui.ModScreen;
import us.zonix.client.gui.component.ILabelledComponent;
import us.zonix.client.util.RenderUtil;

public abstract class ButtonComponent implements ILabelledComponent {

	private String text;

	private int width;
	private int height;

	private int x;
	private int y;

	@java.beans.ConstructorProperties({"text", "width", "height", "x", "y"})
	public ButtonComponent(String text, int width, int height, int x, int y) {
		this.text = text;
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}

	@Override public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override public void draw(int mouseX, int mouseY) {
		boolean hovering = mouseX > this.x && mouseX < this.x + this.width &&
		                   mouseY > this.y && mouseY < this.y + this.height;

		RenderUtil.drawBorderedRoundedRect(this.x, this.y, this.x + this.width, this.y + this.height,
				1.0F, hovering ? ModScreen.HOVER_COLOR : ModScreen.NORMAL_COLOR, 0x1AFFFFFF);
		RenderUtil.drawCenteredString(this.text, this.x + this.width / 2, this.y + this.height / 2, 0xFF000000);
	}

	public String getText() {
		return this.text;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
