package common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.BitSet;

public class StringXORer {

	public String encode(String s, String key) {
		return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
	}

	public static byte[] xorWithKey(byte[] a, byte[] key) {

		if (a.length >= key.length) {
			byte[] out = new byte[a.length];

			byte[] keyAimed = new byte[a.length];
			for (int i = 0; i < a.length - key.length; i++) {
				keyAimed[i] = 0;
			}
			int j = 0;
			for (int i = a.length - key.length; i < a.length; i++) {
				keyAimed[i] = key[j];
				j++;
			}

			for (int i = 0; i < a.length; i++) {
				out[i] = (byte) ((a[i] ^ keyAimed[i]) & 0x000000ff);
			}
			return out;
		} else {
			byte[] out = new byte[key.length];

			byte[] aAimed = new byte[key.length];
			for (int i = 0; i < key.length - a.length; i++) {
				aAimed[i] = 0;
			}
			int j = 0;
			for (int i = key.length - a.length; i < key.length; i++) {
				aAimed[i] = a[j];
				j++;
			}

			for (int i = 0; i < key.length; i++) {
				out[i] = (byte) ((key[i] ^ aAimed[i]) & 0x000000ff);
			}

			return out;
		}

	}

	public static byte[] xorFast(byte[] a, byte[] key) {

		if (a.length != key.length) {
			System.out.println("Wrong length.");
		}
		byte[] out = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			out[i] = (byte) ((a[i] ^ key[i]));
		}
		return out;

	}

	public static byte[] xorFast4Byte(byte[] a, byte[] key) {

		byte[] out = new byte[4];
		for (int i = 0; i < 4; i++) {
			out[i] = (byte) ((a[i] ^ key[i]));
		}
		return out;

	}

	public static byte[] andFast(byte[] a, byte[] key) {

		if (a.length != key.length) {
			System.out.println("Wrong length.");
		}
		byte[] out = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			out[i] = (byte) ((a[i] & key[i]));
		}
		return out;

	}

	public static byte[] base64Decode(String s, boolean isPadding) {
		try {
			BASE64Decoder d = new BASE64Decoder();
			if (isPadding && s.length() % 4 != 0) {

				if (s.length() % 4 == 3) {
					s = "0" + s;
				} else if (s.length() % 4 == 2) {
					s = "00" + s;
				} else if (s.length() % 4 == 1) {
					s = "000" + s;
				}

			}

			return d.decodeBuffer(s);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String base64Encode(byte[] bytes) {
		BASE64Encoder enc = new BASE64Encoder();
		return enc.encode(bytes).replaceAll("\\s", "");

	}
}
