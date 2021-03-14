package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.File;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.optifine.CapeUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import us.zonix.client.cosmetics.Cape;
import us.zonix.client.util.NativeUtil;

public abstract class AbstractClientPlayer extends EntityPlayer implements SkinManager.SkinAvailableCallback {
	public static final ResourceLocation locationStevePng = new ResourceLocation("textures/entity/steve.png");

	@Getter @Setter private Cape cape;
	@Getter @Setter private boolean wings;

	private ResourceLocation locationOfCape;
	private ResourceLocation locationSkin;
	private ResourceLocation locationCape;
	private String nameNonControl;

	private static final String __OBFID = "CL_00000935";

	public AbstractClientPlayer(World p_i45074_1_, GameProfile p_i45074_2_) {
		super(p_i45074_1_, p_i45074_2_);
		String var3 = this.getCommandSenderName();

		if (!var3.isEmpty()) {
			SkinManager var4 = Minecraft.getMinecraft().func_152342_ad();
			var4.func_152790_a(p_i45074_2_, this, true);
		}

		this.nameNonControl = p_i45074_2_.getName();
		if (this.nameNonControl != null && !this.nameNonControl.isEmpty()) {
			this.nameNonControl = StringUtils.stripControlCodes(this.nameNonControl);
		}

		CapeUtils.downloadCape(this);
	}

	@Override public void onLivingUpdate() {
		super.onLivingUpdate();

		NativeUtil.a(this.getEntityId());

		if (this.cape != null) {
			this.cape.tick();
		}
	}

	public String getNameClear() {
		return nameNonControl;
	}

	public ResourceLocation getLocationOfCape() {
		return this.locationOfCape;
	}

	public void setLocationOfCape(ResourceLocation locationOfCape) {
		this.locationOfCape = locationOfCape;
	}

	public boolean func_152122_n() {
		return this.locationCape != null || this.locationOfCape != null ||
		       (this.cape != null && this.cape.getLocation() != null);
	}

	public boolean func_152123_o() {
		return this.locationSkin != null;
	}

	public ResourceLocation getLocationSkin() {
		return this.locationSkin == null ? locationStevePng : this.locationSkin;
	}

	public ResourceLocation getLocationCape() {
		return this.locationCape;
	}

	public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation p_110304_0_, String p_110304_1_) {
		TextureManager var2 = Minecraft.getMinecraft().getTextureManager();
		Object var3 = var2.getTexture(p_110304_0_);

		if (var3 == null) {
			var3 = new ThreadDownloadImageData((File) null,
					String.format("http://skins.minecraft.net/MinecraftSkins/%s.png",
							new Object[]{StringUtils.stripControlCodes(p_110304_1_)}), locationStevePng,
					new ImageBufferDownload());
			var2.loadTexture(p_110304_0_, (ITextureObject) var3);
		}

		return (ThreadDownloadImageData) var3;
	}

	public static ResourceLocation getLocationSkin(String p_110311_0_) {
		return new ResourceLocation("skins/" + StringUtils.stripControlCodes(p_110311_0_));
	}

	public void func_152121_a(Type p_152121_1_, ResourceLocation p_152121_2_) {
		switch (AbstractClientPlayer.SwitchType.field_152630_a[p_152121_1_.ordinal()]) {
			case 1:
				this.locationSkin = p_152121_2_;
				break;

			case 2:
				this.locationCape = p_152121_2_;
				break;
		}
	}

	static final class SwitchType {
		static final int[] field_152630_a = new int[Type.values().length];
		private static final String __OBFID = "CL_00001832";

		static {
			try {
				field_152630_a[Type.SKIN.ordinal()] = 1;
			} catch (NoSuchFieldError var2) {
				;
			}

			try {
				field_152630_a[Type.CAPE.ordinal()] = 2;
			} catch (NoSuchFieldError var1) {
				;
			}
		}
	}
}
