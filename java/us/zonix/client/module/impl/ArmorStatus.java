package us.zonix.client.module.impl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.util.HUDElement;

public final class ArmorStatus extends AbstractModule {

	private static final LabelSetting GENERAL_LABEL = new LabelSetting("General Settings");

	public static final StringSetting DAMAGE_THRESHOLD_TYPE = new StringSetting("Damage Type",
			"value", "percent");
	public static final StringSetting DAMAGE_DISPLAY_TYPE = new StringSetting("Damage Display Type",
			"value", "percent", "none");
	private static final StringSetting LIST_MODE = new StringSetting("List Mode",
			"vertical", "horizontal");

	public static final BooleanSetting DAMAGE_OVERLAY = new BooleanSetting("Damage Overlay", true);
	public static final BooleanSetting ARMOR_DAMAGE = new BooleanSetting("Armor Damage", true);
	public static final BooleanSetting ITEM_DAMAGE = new BooleanSetting("Item Damage", true);
	public static final BooleanSetting MAX_DAMAGE = new BooleanSetting("Max Damage", false);
	public static final BooleanSetting ITEM_COUNT = new BooleanSetting("Item Count", true);
	public static final BooleanSetting ITEM_NAME = new BooleanSetting("Item Name", false);
	public static final BooleanSetting HELD_ITEM = new BooleanSetting("Held item", true);

	private static final LabelSetting COLOR_LABEL = new LabelSetting("Color Settings");

	private static final ColorSetting FULL_COLOR = new ColorSetting("100% Color", 0xFFFFFFFF);
	private static final ColorSetting EIGHTY_COLOR = new ColorSetting("80% Color", 0xFFAAAAAA);
	private static final ColorSetting SIXTY_COLOR = new ColorSetting("60% Color", 0xFFFFFF55);
	private static final ColorSetting FORTY_COLOR = new ColorSetting("40% Color", 0xFFFFAA00);
	private static final ColorSetting QUARTER_COLOR = new ColorSetting("25% Color", 0xFFFF5555);
	private static final ColorSetting TEN_COLOR = new ColorSetting("10% Color", 0xAA0000);

	public static final RenderItem ITEM_RENDERER = new RenderItem();

	private final List<HUDElement> elements = new ArrayList<>();

	public ArmorStatus() {
		super("Armor Status");

		this.addSetting(GENERAL_LABEL);
		{
			this.addSetting(DAMAGE_THRESHOLD_TYPE);
			this.addSetting(DAMAGE_DISPLAY_TYPE);
			this.addSetting(LIST_MODE);

			this.addSetting(DAMAGE_OVERLAY);
			this.addSetting(ARMOR_DAMAGE);
			this.addSetting(ITEM_DAMAGE);
			this.addSetting(MAX_DAMAGE);
			this.addSetting(ITEM_COUNT);
			this.addSetting(ITEM_NAME);
		}

		this.addSetting(COLOR_LABEL);
		{
			this.addSetting(FULL_COLOR);
			this.addSetting(EIGHTY_COLOR);
			this.addSetting(SIXTY_COLOR);
			this.addSetting(FORTY_COLOR);
			this.addSetting(QUARTER_COLOR);
			this.addSetting(TEN_COLOR);
		}
	}

	@Override public void renderPreview() {
		this.elements.clear();

		ItemStack[] armor = new ItemStack[]{
				new ItemStack(Item.getItemById(310)),
				new ItemStack(Item.getItemById(311)),
				new ItemStack(Item.getItemById(312)),
				new ItemStack(Item.getItemById(313))
		};

		for (int i = 0; i < 4; i++) {
			ItemStack itemStack = this.mc.thePlayer.inventory.armorInventory[i];
			if (itemStack == null) {
				itemStack = armor[i];
			}

			this.elements.add(new HUDElement(itemStack, 16, 16, 2, true));
		}

		if (HELD_ITEM.getValue()) {
			if (this.mc.thePlayer.getCurrentEquippedItem() == null) {
				this.elements.add(new HUDElement(new ItemStack(Item.getItemById(276)), 16, 16, 2, false));
			} else {
				this.elements.add(new HUDElement(this.mc.thePlayer.getCurrentEquippedItem(), 16, 16, 2, false));
			}
		}

		GL11.glPushMatrix();
		{
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			this.displayArmorStatus();
		}
		GL11.glPopMatrix();
	}

	@Override public void renderReal() {
		this.getHUDElements();

		if (!this.elements.isEmpty()) {
			GL11.glPushMatrix();
			{
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				this.displayArmorStatus();
			}
			GL11.glPopMatrix();
		}
	}

	private void getHUDElements() {
		this.elements.clear();

		for (int i = 3; i >= -1; i--) {
			ItemStack itemStack = null;
			if ((i == -1) && HELD_ITEM.getValue()) {
				itemStack = this.mc.thePlayer.getCurrentEquippedItem();
			} else if (i != -1) {
				itemStack = this.mc.thePlayer.inventory.armorInventory[i];
			}

			if (itemStack != null) {
				this.elements.add(new HUDElement(itemStack, 16, 16, 2, i > -1));
			}
		}
	}

	public static int getColorCode(int percentage) {
		if (percentage <= 10) {
			return TEN_COLOR.getValue();
		}
		if (percentage <= 25) {
			return QUARTER_COLOR.getValue();
		}
		if (percentage <= 40) {
			return FORTY_COLOR.getValue();
		}
		if (percentage <= 60) {
			return SIXTY_COLOR.getValue();
		}
		if (percentage <= 80) {
			return EIGHTY_COLOR.getValue();
		}
		return FULL_COLOR.getValue();
	}

	private void displayArmorStatus() {
		if (this.elements.size() > 0) {
			int yOffset = ITEM_NAME.getValue() ? 18 : 16;

			if (LIST_MODE.getValue().equalsIgnoreCase("vertical")) {
				int yBase = 0;
				int heWidth = 0;

				for (HUDElement e : elements) {
					yBase += yOffset;
					if (e.width() > heWidth) {
						heWidth = e.width();
					}
				}

				this.setHeight(yBase);
				this.setWidth(heWidth);

				for (HUDElement e : elements) {
					e.renderToHud(this.getX(), this.getY() - yBase + this.getHeight());
					yBase -= yOffset;
					if (e.width() > heWidth) {
						heWidth = e.width();
					}
				}
			} else if (LIST_MODE.getValue().equalsIgnoreCase("horizontal")) {
				int yBase = 0;
				int prevX = 0;
				int heHeight = 0;

				for (HUDElement e : elements) {
					e.renderToHud(this.getX() + prevX, this.getY() + yBase);
					prevX += e.width();
					if (e.height() > heHeight) {
						heHeight += e.height();
					}
				}

				this.setHeight(heHeight);
				this.setWidth(prevX);
			}
		}
	}

}
