package us.zonix.client.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import lombok.Getter;
import net.minecraft.client.Minecraft;

@Getter
public final class ProfileManager {

	public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Map<String, Profile> loadedProfiles = new HashMap<>();

	private Profile activeConfig;

	private String activeConfigName;

	public void loadConfig(String name) {
		Profile profile = this.loadedProfiles.get(name.toLowerCase());
		if (profile == null) {
			Logger.getGlobal().severe("Invalid config supplied when loading profile (" + name + ")");
			return;
		}

		profile.load();

		this.activeConfigName = profile.getName();
		this.activeConfig = profile;

		this.saveSettings();

		Logger.getGlobal().info("Loaded config " + profile.getName());
	}

	public Profile createConfig(String name) {
		Profile profile = new Profile(name);
		profile.save();

		this.activeConfigName = profile.getName();
		this.activeConfig = profile;

		this.saveSettings();

		return this.loadedProfiles.put(name.toLowerCase(), profile);
	}

	public void saveSettings() {
		File dir = new File(Minecraft.getMinecraft().mcDataDir, "Zonix");
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File settings = new File(dir, "settings.json");
		if (!settings.exists()) {
			try {
				settings.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("Error creating settings.json");
			}
		}

		try (PrintWriter writer = new PrintWriter(settings)) {
			JsonObject object = new JsonObject();
			if (this.activeConfig != null) {
				object.addProperty("active-config", this.activeConfig.getName());
			} else {
				object.addProperty("active-config", "default");
			}
			writer.println(PRETTY_GSON.toJson(object));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error writing settings.json");
		}
	}

	public void saveConfig() {
		if (this.activeConfig != null) {
			this.activeConfig.save();
			this.saveSettings();
		} else {
			this.createConfig("default");
		}
	}

	public void load() {
		File dir = new File(Minecraft.getMinecraft().mcDataDir, "Zonix/profiles");
		if (!dir.exists()) {
			dir.mkdirs();
			return;
		}

		File settings = new File(dir.getParentFile(), "settings.json");
		if (!settings.exists()) {
			try {
				settings.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("Error creating settings.json");
			}
		} else {
			try (BufferedReader reader = new BufferedReader(new FileReader(settings))) {
				JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
				this.activeConfigName = object.get("active-config").getAsString();
			} catch (IOException e) {
				throw new RuntimeException("Error reading settings.json");
			} catch (JsonParseException | IllegalStateException e) {
				settings.delete();
				throw new RuntimeException("Error parsing settings.json... Resetting.");
			}
		}

		for (File file : Objects.requireNonNull(dir.listFiles())) {
			if (!file.getName().endsWith(".json")) {
				continue;
			}

			String name = file.getName().replace(".json", "");
			if (name.equalsIgnoreCase("settings")) {
				continue;
			}

			Profile profile = new Profile(name);
			this.loadedProfiles.put(name.toLowerCase(), profile);

			if (name.equalsIgnoreCase(this.activeConfigName)) {
				this.activeConfig = profile;
				profile.load();
			}
		}

		this.saveSettings();
	}

}
