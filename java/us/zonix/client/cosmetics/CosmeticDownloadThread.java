package us.zonix.client.cosmetics;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CosmeticDownloadThread implements Runnable {

	@java.beans.ConstructorProperties({"uniqueId", "callback"})
	public CosmeticDownloadThread(UUID uniqueId, Callback callback) {
		this.uniqueId = uniqueId;
		this.callback = callback;
	}

	public interface Callback {
		void callback(JsonObject data);
	}

	private final UUID uniqueId;
	private final Callback callback;

	@Override public void run() {
		try {
			URL url = new URL("https://zonix.us/api/client/cosmetics/get/" + this.uniqueId.toString());

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
			this.callback.callback(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
