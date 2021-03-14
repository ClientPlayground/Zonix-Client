package us.zonix.client.cosmetics;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ResourceLocation;

@Getter
@RequiredArgsConstructor
public class Cape {

	private static final Map<String, ResourceLocation> capeCache = new HashMap<>();

	private final String cape;
	private final int frames;

	private int tick;

	public void tick() {
		this.tick++;
	}

	public ResourceLocation getLocation() {
		int frame = this.tick % (this.frames * 2);
		if (frame >= this.frames) {
			frame = this.frames * 2 - 1 - frame;
		}

		int finalFrame = frame + 1;
		String name = this.cape + "-" + frame;

		return capeCache.computeIfAbsent(name, k ->
				new ResourceLocation("capes/" + this.cape + "/" + finalFrame + ".png"));
	}

}
