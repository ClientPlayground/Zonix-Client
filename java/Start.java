import java.util.Arrays;
import net.minecraft.client.main.Main;

public class Start {

	public static void main(String[] args) {
		try {
			Main.main(concat(new String[]{
					"--version", "mcp",
					"--assetIndex", "1.7.10",
					"--userProperties", "{}",
			"--username", "JanTheLama",
				"--uuid", "",
				"--accessToken", ""
			}, args));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

}
