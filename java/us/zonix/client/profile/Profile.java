package us.zonix.client.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import lombok.Data;
import net.minecraft.client.Minecraft;
import us.zonix.client.Client;
import us.zonix.client.module.IModule;
import us.zonix.client.setting.ISetting;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.FloatSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.setting.impl.TextSetting;

@Data
public final class Profile {

	private final String name;
	private boolean enabled;

	public void load() {
		File file = new File(Minecraft.getMinecraft().mcDataDir, "Zonix/profiles/" + this.name + ".json");
		if (!file.exists()) {
			throw new RuntimeException("Can't load empty file.");
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			JsonArray array = new JsonParser().parse(reader).getAsJsonArray();

			for (JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();
				String name = object.get("name").getAsString();

				IModule module = Client.getInstance().getModuleManager().getModule(name);
				if (module == null) {
					continue;
				}

				module.setEnabled(object.get("enabled").getAsBoolean());

				module.setX(object.get("x").getAsFloat());
				module.setY(object.get("y").getAsFloat());

				JsonArray settings = object.getAsJsonArray("settings");
				for (JsonElement jsonElement : settings) {
					JsonObject settingObject = jsonElement.getAsJsonObject();
					String settingName = settingObject.get("name").getAsString();

					ISetting setting = module.getSettingMap().get(settingName.toLowerCase());
					if (setting == null) {
						continue;
					}

					JsonElement value = settingObject.get("value");
					if (value == null || value.isJsonNull()) {
						continue;
					}

					if (setting instanceof FloatSetting) {
						((FloatSetting) setting).setValue(value.getAsFloat());
					} else if (setting instanceof ColorSetting) {
						((ColorSetting) setting).setValue(value.getAsInt());

						JsonElement chromaElement = settingObject.get("chroma");
						if (chromaElement != null && !chromaElement.isJsonNull()) {
							((ColorSetting) setting).setChroma(chromaElement.getAsBoolean());
						}
					} else if (setting instanceof BooleanSetting) {
						((BooleanSetting) setting).setValue(value.getAsBoolean());
					} else if (setting instanceof StringSetting) {
						((StringSetting) setting).setValue(value.getAsString());
					} else if (setting instanceof TextSetting) {
						((TextSetting) setting).setValue(value.getAsString());
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error loading profile (" + this.name + ")", e);
		} catch (JsonParseException e) {
			Logger.getGlobal().warning("Error loading profile (" + this.name + ") : " + e.getMessage());
		}
	}

	@SuppressWarnings("Duplicates") public void save() {
		JsonArray mods = new JsonArray();

		for (IModule module : Client.getInstance().getModuleManager().getModules()) {
			JsonObject object = new JsonObject();
			object.addProperty("name", module.getName());
			object.addProperty("x", module.getX());
			object.addProperty("y", module.getY());
			object.addProperty("enabled", module.isEnabled());

			JsonArray settings = new JsonArray();
			for (ISetting setting : module.getSettingMap().values()) {
				JsonObject settingObject = new JsonObject();
				settingObject.addProperty("name", setting.getName().toLowerCase());

				if (setting instanceof FloatSetting) {
					settingObject.addProperty("value", (Number) setting.getValue());
				} else if (setting instanceof ColorSetting) {
					boolean chroma = ((ColorSetting) setting).isChroma();
					settingObject.addProperty("chroma", chroma);

					((ColorSetting) setting).setChroma(false);
					settingObject.addProperty("value", (Number) setting.getValue());
					((ColorSetting) setting).setChroma(chroma);
				} else if (setting instanceof BooleanSetting) {
					settingObject.addProperty("value", (Boolean) setting.getValue());
				} else if (setting instanceof StringSetting || setting instanceof TextSetting) {
					settingObject.addProperty("value", (String) setting.getValue());
				}

				settings.add(settingObject);
			}
			object.add("settings", settings);

			mods.add(object);
		}

		File file = new File(Minecraft.getMinecraft().mcDataDir, "Zonix/profiles/" + this.name + ".json");

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("Error saving profile (" + this.name + ")", e);
			}
		}

		try (PrintWriter writer = new PrintWriter(file)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			writer.println(gson.toJson(mods));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error saving profile (" + this.name + ")", e);
		}
	}

}
