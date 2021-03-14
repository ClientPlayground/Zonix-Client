package us.zonix.client.util;

import net.minecraft.client.Minecraft;

public final class NativeUtil {

	private static final boolean ANTI_CHEAT = false;

	private static native int d0();
	public static int d() {
		if (ANTI_CHEAT) {
			return d0();
		}
		return 1;
	}

	private static native void z0(String s1, String s2);
	public static void z(String uuid, String username) {
		if (ANTI_CHEAT) {
			z0(uuid, username);
		}
	}

	private static native void a0();
	public static void a() {
		if (ANTI_CHEAT) {
			a0();
		}
	}

	private static native void a0(int i);
	public static void a(int i) {
		if (ANTI_CHEAT) {
			a0(i);
		}
	}

	private static native void b0(int i);
	public static void b(int i) {
		if (ANTI_CHEAT) {
			b0(i);
		}
	}

	private static native void a0(int i, boolean b);
	public static void a(int i, boolean b) {
		if (ANTI_CHEAT) {
			a0(i, b);
		}
	}

	private static native void a0(double d, double d1, double d2);
	public static void a(double d, double d1, double d2) {
		if (ANTI_CHEAT) {
			a0(d, d1, d2);
		}
	}

	public static boolean e() {
		return Minecraft.getMinecraft().theWorld == null;
	}

	public static String f() {
		return Minecraft.getMinecraft().func_147104_D() == null ? null : Minecraft.getMinecraft().func_147104_D().serverIP;
	}

}
