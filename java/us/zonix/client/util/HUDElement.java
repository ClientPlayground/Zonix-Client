package us.zonix.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.module.impl.ArmorStatus;
import us.zonix.client.util.font.ZFontRenderer;

public class HUDElement {
	public final ItemStack itemStack;
	public final int iconW;
	public final int iconH;
	public final int padW;
	private int elementW;
	private int elementH;
	private String itemName = "";
	private int itemNameW;
	private String itemDamage = "";
	private int itemDamageW;
	private final boolean isArmor;
	private Minecraft mc = Minecraft.getMinecraft();
	private int color;

	public HUDElement(ItemStack itemStack, int iconW, int iconH, int padW, boolean isArmor) {
		this.itemStack = itemStack;
		this.iconW = iconW;
		this.iconH = iconH;
		this.padW = padW;
		this.isArmor = isArmor;

		initSize();
	}

	public int width() {
		return elementW;
	}

	public int height() {
		return elementH;
	}

	private void initSize() {
		elementH = ArmorStatus.ITEM_NAME.getValue() ? Math.max(
				Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 2, iconH) :
				Math.max(mc.fontRenderer.FONT_HEIGHT, iconH);

		if (itemStack != null) {
			int damage;
			int maxDamage;

			if (((isArmor && ArmorStatus.ARMOR_DAMAGE.getValue()) ||
			     (!isArmor && ArmorStatus.ITEM_DAMAGE.getValue())) && itemStack.isItemStackDamageable()) {
				maxDamage = itemStack.getMaxDamage() + 1;
				damage = maxDamage - itemStack.getItemDamageForDisplay();

				this.color = ArmorStatus.getColorCode(ArmorStatus.DAMAGE_THRESHOLD_TYPE.getValue()
						.equalsIgnoreCase("percent") ? damage * 100 / maxDamage : damage);
				if (ArmorStatus.DAMAGE_DISPLAY_TYPE.getValue().equalsIgnoreCase("value")) {
					itemDamage = damage + (ArmorStatus.MAX_DAMAGE.getValue() ? "/" + maxDamage : "");
				} else if (ArmorStatus.DAMAGE_DISPLAY_TYPE.getValue().equalsIgnoreCase("percent")) {
					itemDamage = (damage * 100 / maxDamage) + "%";
				}
			}

			itemDamageW = mc.fontRenderer.getStringWidth(StringUtils.stripCtrl(itemDamage));
			elementW = padW + iconW + padW + itemDamageW;

			if (ArmorStatus.ITEM_NAME.getValue()) {
				itemName = itemStack.getDisplayName();
				elementW = padW + iconW + padW +
				           Math.max(mc.fontRenderer.getStringWidth(StringUtils.stripCtrl(itemName)), itemDamageW);
			}

			itemNameW = mc.fontRenderer.getStringWidth(StringUtils.stripCtrl(itemName));
		}
	}

	public void renderToHud(float x, float y) {
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826);
		RenderHelper.enableStandardItemLighting();
		RenderHelper.enableGUIStandardItemLighting();
		ArmorStatus.ITEM_RENDERER.zLevel = -10F;

		ArmorStatus.ITEM_RENDERER.renderItemAndEffectIntoGUI(mc.fontRenderer,
				mc.getTextureManager(), itemStack, (int) x, (int) y);

		HUDUtils.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, (int) x, (int) y,
				ArmorStatus.DAMAGE_OVERLAY.getValue(), ArmorStatus.ITEM_COUNT.getValue());

		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(32826);
		GL11.glDisable(GL11.GL_BLEND);

		ZFontRenderer fontRenderer = Client.getInstance().getRegularFontRenderer();
		fontRenderer.drawString(itemName + "\247r", x + iconW + padW, y, this.color);
		fontRenderer.drawString(itemDamage + "\247r", x + iconW + padW,
				y + (ArmorStatus.ITEM_NAME.getValue() ? elementH / 2 : elementH / 4), this.color);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
}