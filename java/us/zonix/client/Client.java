package us.zonix.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import us.zonix.client.cosmetics.CosmeticManager;
import us.zonix.client.module.ModuleManager;
import us.zonix.client.profile.ProfileManager;
import us.zonix.client.social.PartyManager;
import us.zonix.client.social.friend.FriendManager;
import us.zonix.client.util.NativeUtil;
import us.zonix.client.util.font.ZFontRenderer;

@Getter
public final class Client {

	@Getter private static Client instance;

	private final ProfileManager profileManager;
	private final FriendManager friendManager;
	private final ModuleManager moduleManager;
	private final PartyManager partyManager;
	private final CosmeticManager capeManager;

	// Font shit
	// TODO: Find a better way to change font size fucking kill me
	private final ZFontRenderer hugeBoldFontRenderer;
	private final ZFontRenderer hugeFontRenderer;
	private final ZFontRenderer largeBoldFontRenderer;
	private final ZFontRenderer largeFontRenderer;
	private final ZFontRenderer mediumBoldFontRenderer;
	private final ZFontRenderer mediumFontRenderer;
	private final ZFontRenderer regularBoldFontRenderer;
	private final ZFontRenderer regularFontRenderer;
	private final ZFontRenderer regularMediumBoldFontRenderer;
	private final ZFontRenderer regularMediumFontRenderer;
	private final ZFontRenderer smallFontRenderer;
	private final ZFontRenderer smallBoldFontRenderer;
	private final ZFontRenderer tinyFontRenderer;
	private final ZFontRenderer tinyBoldFontRenderer;

	public Client() {
		instance = this;

		try {
			URL url = new URL("http://www.zonix.us/api/client/authentication/valid");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line = reader.readLine();

				if (line == null || line.isEmpty()) {
					System.exit(0);
					throw new RuntimeException("Unauthorised"); // give it a sec, starting up
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.loadLibrary("zonix-natives");

		NativeUtil.z(Minecraft.getMinecraft().getSession().getPlayerID(),
				Minecraft.getMinecraft().getSession().getUsername());

		this.profileManager = new ProfileManager();
		this.friendManager = new FriendManager();

		this.moduleManager = new ModuleManager();
		this.partyManager = new PartyManager();
		this.capeManager = new CosmeticManager();

		this.profileManager.load();

		Runtime.getRuntime().addShutdownHook(new Thread(this.profileManager::saveConfig));

		// Fonts
		this.hugeBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Bold.ttf"), 40.0F);
		this.hugeFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Regular.ttf"), 40.0F);
		this.largeBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansProBold.ttf"), 36.0F);
		this.largeFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Regular.ttf"), 36.0F);
		this.mediumBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Bold.ttf"), 28.0F);
		this.mediumFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Regular.ttf"), 28.0F);
		this.regularMediumBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Bold.ttf"), 24.0F);
		this.regularMediumFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Regular.ttf"), 24.0F);
		this.regularBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Bold.ttf"), 20.0F);
		this.regularFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Regular.ttf"), 20.0F);
		this.smallBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Bold.ttf"), 16.0F);
		this.smallFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Regular.ttf"), 16.0F);
		this.tinyBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Bold.ttf"), 12.0F);
		this.tinyFontRenderer = new ZFontRenderer(new ResourceLocation("font/SourceSansPro-Regular.ttf"), 12.0F);
	}

	public ProfileManager getProfileManager() {
		return this.profileManager;
	}

	public FriendManager getFriendManager() {
		return this.friendManager;
	}

	public ModuleManager getModuleManager() {
		return this.moduleManager;
	}

	public PartyManager getPartyManager() {
		return this.partyManager;
	}

	public CosmeticManager getCapeManager() {
		return this.capeManager;
	}

	public ZFontRenderer getHugeFontRenderer() {
		return this.hugeFontRenderer;
	}

	public ZFontRenderer getHugeBoldFontRenderer() {
		return this.hugeBoldFontRenderer;
	}

	public ZFontRenderer getLargeFontRenderer() {
		return this.largeFontRenderer;
	}

	public ZFontRenderer getLargeBoldFontRenderer() {
		return this.largeBoldFontRenderer;
	}

	public ZFontRenderer getMediumFontRenderer() {
		return this.mediumFontRenderer;
	}

	public ZFontRenderer getMediumBoldFontRenderer() {
		return this.mediumBoldFontRenderer;
	}

	public ZFontRenderer getRegularMediumFontRenderer() {
		return this.regularMediumFontRenderer;
	}

	public ZFontRenderer getRegularMediumBoldFontRenderer() {
		return this.regularMediumBoldFontRenderer;
	}

	public ZFontRenderer getRegularFontRenderer() {
		return this.regularFontRenderer;
	}

	public ZFontRenderer getRegularBoldFontRenderer() {
		return this.regularBoldFontRenderer;
	}

	public ZFontRenderer getSmallFontRenderer() {
		return this.smallFontRenderer;
	}

	public ZFontRenderer getSmallBoldFontRenderer() {
		return this.smallBoldFontRenderer;
	}

	public ZFontRenderer getTinyFontRenderer() {
		return this.tinyFontRenderer;
	}

	public ZFontRenderer getTinyBoldFontRenderer() {
		return this.tinyBoldFontRenderer;
	}

	public static Client getInstance() {
		return instance;
	}
}
