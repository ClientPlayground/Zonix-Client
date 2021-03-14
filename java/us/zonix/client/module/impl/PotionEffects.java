package us.zonix.client.module.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.util.RenderUtil;

public final class PotionEffects extends AbstractModule {

	private static final ResourceLocation INVENTORY_RESOURCE =
			new ResourceLocation("textures/gui/container/inventory.png");

	public static final BooleanSetting SHOW_IN_INVENTORY = new BooleanSetting("Show in inventory", true);

	private List<PotionEffect> potionEffects = new ArrayList<>();

	public PotionEffects() {
		super("Potion Status");

		this.x = 4;

		this.y = new ScaledResolution(this.mc).getScaledHeight() / 2 - this.getHeight();

		this.addSetting(new LabelSetting("General Settings"));
		this.addSetting(new BooleanSetting("Show effect icon", true));
		this.addSetting(new BooleanSetting("Show effect name", true));
		this.addSetting(new BooleanSetting("Show duration", true));
		this.addSetting(SHOW_IN_INVENTORY);

		this.addSetting(new LabelSetting("Color Settings"));
		this.addSetting(new ColorSetting("Effect Color", 0xFFFF0000));
		this.addSetting(new ColorSetting("Duration Color", 0xFFFF0000));
	}

	@Override public void renderReal() {
		if (this.mc.thePlayer == null) {
			return;
		}

		this.potionEffects.clear();

		//noinspection unchecked
		Collection<PotionEffect> potionEffects = this.mc.thePlayer.getActivePotionEffects();
		this.render(potionEffects);
	}

	@Override public void renderPreview() {
		if (this.mc.thePlayer == null) {
			return;
		}

		if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
			this.renderReal();
			return;
		}

		if (this.potionEffects.isEmpty()) {
			this.potionEffects.add(new PotionEffect(Potion.damageBoost.getId(), 20 * 30, 1));
			this.potionEffects.add(new PotionEffect(Potion.moveSpeed.getId(), 20 * 90, 1));
			this.potionEffects.add(new PotionEffect(Potion.fireResistance.getId(), 20 * 60 * 7));
		}

		this.potionEffects.removeIf(potionEffect -> potionEffect.getDuration() <= 0);

		this.render(this.potionEffects);
	}

	private void render(Collection<PotionEffect> potionEffects) {
		int height = 0;
		int maxWidth = 0;

		for (PotionEffect effect : potionEffects) {
			if (this.getBooleanSetting("Show effect name").getValue()) {
				String label = StatCollector.translateToLocal(effect.getEffectName()) +
				               this.getAmplifierNumerals(effect.getAmplifier());

				int width = this.mc.fontRenderer.getStringWidth(label) + 24;

				this.mc.fontRenderer.drawStringWithShadow(label, this.getX() + 22,
						this.getY() + height + 2,
						this.getColorSetting("Effect Color").getValue());

				if (width > maxWidth) {
					maxWidth = width;
				}
			}

			if (this.getBooleanSetting("Show duration").getValue()) {
				String duration = Potion.getDurationString(effect);
				int width = this.mc.fontRenderer.getStringWidth(duration) + 24;

				this.mc.fontRenderer.drawStringWithShadow(duration, this.getX() + 22,
						this.getY() + height + 12,
						this.getColorSetting("Duration Color").getValue());

				if (width > maxWidth) {
					maxWidth = width;
				}
			}

			if (this.getBooleanSetting("Show effect icon").getValue()) {
				Potion potion = Potion.potionTypes[effect.getPotionID()];
				if (potion.hasStatusIcon()) {
					GL11.glColor4f(1F, 1F, 1F, 1F);

					this.mc.getTextureManager().bindTexture(INVENTORY_RESOURCE);

					int index = potion.getStatusIconIndex();

					RenderUtil.drawTexturedRect(this.getX(), this.getY() + height + 2, index % 8 * 18,
							198 + index / 8 * 18, 18, 18);
				}
			}

			height += 24;
		}

		this.setWidth(maxWidth);
		this.setHeight(height);
	}

	private String getAmplifierNumerals(int amplifier) {
		switch (amplifier) {
			case 0:
				return " I";
			case 1:
				return " II";
			case 2:
				return " III";
			case 3:
				return " IV";
			case 4:
				return " V";
			case 5:
				return " VI";
			case 6:
				return " VII";
			case 7:
				return " VIII";
			case 8:
				return " IX";
			case 9:
				return " X";
			default:
				if (amplifier < 1) {
					return "";
				}
				return " " + amplifier + 1;
		}
	}

}
