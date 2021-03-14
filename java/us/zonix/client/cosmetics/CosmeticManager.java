package us.zonix.client.cosmetics;

import com.google.gson.JsonElement;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("JavaReflectionMemberAccess")
public class CosmeticManager {

	private void giveCape(UUID uuid, String cape, int frames) {
		if (Minecraft.getMinecraft().theWorld == null) {
			return;
		}

		EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(uuid);
		if (!(player instanceof AbstractClientPlayer)) {
			return;
		}

		AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
		if (cape.contains("anim")) {
			clientPlayer.setCape(new Cape(cape, frames));
		}

		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

		ResourceLocation resourceLocation = new ResourceLocation("capes/" + cape);
		CapeImageBuffer buffer = new CapeImageBuffer(clientPlayer, resourceLocation);

		ThreadDownloadImageData textureCape = new ThreadDownloadImageData(null,
				"https://zonix.us/api/client/cape/download/" + uuid.toString(), null, buffer);
		textureManager.loadTexture(resourceLocation, textureCape);
	}

	private void setCape(AbstractClientPlayer player, ResourceLocation location) {
		if (location == null) {
			return;
		}

		player.func_152121_a(MinecraftProfileTexture.Type.CAPE, location);
	}

	public void onSpawn(final UUID uuid) {
		new Thread(new CosmeticDownloadThread(uuid, object -> {
			String cape = object.get("cape").getAsString();
			if (!cape.equals("none")) {
				int frames = 11;
				JsonElement element = object.get("frames");
				if (element != null) {
					frames = element.getAsInt();
				}
				this.giveCape(uuid, cape, frames);
			}

			boolean wings = object.get("wings").getAsBoolean();
			if (wings) {
				EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(uuid);
				if (!(player instanceof AbstractClientPlayer)) {
					return;
				}
				((AbstractClientPlayer) player).setWings(true);
			}
		})).start();
	}

	public void onDestroy(UUID uuid) {

	}

	private static BufferedImage parseCape(BufferedImage img) {
		int imageHeight = 32;
		int imageWidth = 64;

		int srcHeight = img.getHeight();
		int srcWidth = img.getWidth();

		while (imageWidth < srcWidth || imageHeight < srcHeight) {
			imageHeight *= 2;
			imageWidth *= 2;
		}

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, 2);
		Graphics graphics = image.getGraphics();

		graphics.drawImage(img, 0, 0, null);
		graphics.dispose();

		return image;
	}

	private class CapeImageBuffer implements IImageBuffer {

		private final WeakReference<AbstractClientPlayer> playerWeakReference;
		private final ResourceLocation location;

		CapeImageBuffer(AbstractClientPlayer player, ResourceLocation location) {
			this.playerWeakReference = new WeakReference<>(player);
			this.location = location;
		}

		@Override public BufferedImage parseUserSkin(BufferedImage bufferedImage) {
			return parseCape(bufferedImage);
		}

		@Override public void func_152634_a() {
			setCape(this.playerWeakReference.get(), this.location);
		}

	}

}
