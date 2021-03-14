package us.zonix.client.gui.component;

public interface IComponent {

	int getX();

	int getY();

	void setPosition(int x, int y);

	int getWidth();

	int getHeight();

	void setWidth(int width);

	void setHeight(int height);

	/*
	Events
	 */

	void onOpen();

	void tick();

	void draw(int mouseX, int mouseY);

	void onClick(int mouseX, int mouseY, int button);

	default void onMouseEvent() {

	}

	default void onMouseRelease() {

	}

	default void onKeyPress(int code, char c) {

	}

}
