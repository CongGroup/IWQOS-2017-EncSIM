package common.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class Converter {

	/**
	 * 
	 * @param bi
	 * @param length
	 *            number of byte
	 * @return
	 */
	public static String bigInteger2String(BigInteger bi, int length) {

		String ZERO = "00000000";
		byte[] data = bi.toByteArray();

		StringBuffer sb = new StringBuffer();

		if (data.length > length) {

			byte[] temp = new byte[length];
			System.arraycopy(data, data.length - length, temp, 0, length);

			data = temp;
		} else if (data.length < length) {
			int paddingNum = length - data.length;

			for (int i = 0; i < paddingNum; i++) {
				sb.append(ZERO);
			}
		}

		for (int i = 0; i < data.length; i++) {
			String s = Integer.toBinaryString(data[i]);
			if (s.length() > 8) {
				s = s.substring(s.length() - 8);
			} else if (s.length() < 8) {
				s = ZERO.substring(s.length()) + s;
			}

			sb.append(s);
		}

		// System.out.println(sb.toString());
		return sb.toString();
	}

	public static String bytes2Hex(byte[] bts) {

		StringBuilder des = new StringBuilder();
		String tmp;

		for (byte bt : bts) {

			tmp = (Integer.toHexString(bt & 0xFF));

			if (tmp.length() == 1) {

				des.append('0');
			}
			des.append(tmp.toUpperCase());
		}
		return des.toString();
	}

	private static ByteBuffer buffer = ByteBuffer.allocate(8);

	public static byte[] longToBytes(long x) {
		buffer.clear();
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {

		buffer.clear();
		buffer.put(bytes, 0, 8);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	public static long bytesToUnsignedInt(byte[] bytes) {

		ByteBuffer buffer = ByteBuffer.allocate(8);

		buffer.clear();
		buffer.put(bytes, 0, 4);
		buffer.flip();
		return buffer.getInt() & 0x0FFFFFFFFl;
	}

	/**
	 * @param data
	 *            the original data
	 * @return long
	 */
	public static long flod256Bytes(byte[] data) {

		long result = 0;

		if (data.length == 32) {

			byte[][] bb = new byte[4][8];

			for (int i = 0; i < data.length; ++i) {

				bb[i / 8][i % 8] = data[i];
			}

			result = bytesToLong(bb[0]) ^ bytesToLong(bb[1])
					^ bytesToLong(bb[2]) ^ bytesToLong(bb[3]);
		}

		return result;
	}

	public static int mapIndex(int id, int loopSize) {

		if (id % loopSize == 0) {
			return loopSize - 1;
		} else {
			return id % loopSize - 1;
		}

	}

	public static String long2String(long value) {
		return Long.toString(value);
	}

	public static String appendLongString(long value1, String value2) {
		String value1Str = Converter.long2String(value1);
		StringBuilder sb = new StringBuilder();
		sb.append(value1Str);
		sb.append(value2);

		return sb.toString();
	}

	public static String appendStringLong(String value1, long value2) {
		String value2Str = Converter.long2String(value2);
		StringBuilder sb = new StringBuilder();
		sb.append(value1);
		sb.append(value2Str);

		return sb.toString();
	}

	public static String int2BinaryString(int value) {
		String s = Integer.toBinaryString(value);
		int length = s.length();
		int offset = 32 - length;
		for (int i = 0; i < offset; i++) {
			s = "0" + s;
		}
		/*
		 * if (s.length() % 4 != 0) {
		 * 
		 * if (s.length() % 4 == 3) { s = "0" + s; } else if (s.length() % 4 ==
		 * 2) { s = "00" + s; } else if (s.length() % 4 == 1) { s = "000" + s; }
		 * 
		 * }
		 */
		return s;
	}

	public static String appendLongInt(long value1, int value2) {
		String value1Str = Converter.long2String(value1);
		String value2Str = Converter.int2BinaryString(value2);
		StringBuilder sb = new StringBuilder();
		sb.append(value1Str);
		sb.append(value2Str);
		return sb.toString();

	}

	public static String appendStringInt(String value1, int value2) {
		// String value1Str = Converter.long2String(value1);
		String value2Str = Converter.int2BinaryString(value2);
		StringBuilder sb = new StringBuilder();
		sb.append(value1);
		sb.append(value2Str);
		return sb.toString();

	}

	public static String convertByteArrayToString(byte[] array) {
		String str = new String();
		if (array == null) {
			return null;
		} else {

			for (int i = 0; i < array.length; i++) {
				str += (char) array[i];
			}
			return str;
		}

	}

	public static String capitalize(final String str, final char... delimiters) {
		final int delimLen = delimiters == null ? -1 : delimiters.length;
		if (UtilHelper.isEmptyString(str) || delimLen == 0) {
			return str;
		}
		final char[] buffer = str.toCharArray();

		for (int i = 0; i < buffer.length; i++) {
			final char ch = buffer[i];

			buffer[i] = Character.toTitleCase(ch);

		}
		return new String(buffer);
	}

	public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	public static String ToBinaryString(byte[] bytes) {

		// byte[] bytes = asciiString.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
			// binary.append(' ');
		}
		return binary.toString();
	}

	public static byte[] ToBinaryByte(byte[] bytes) {

		// byte[] bytes = asciiString.getBytes();
		// StringBuilder binary = new StringBuilder();
		int size = bytes.length * 8;
		byte[] res = new byte[size];
		int counter = 0;
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				res[counter] = (byte) ((val & 128) == 0 ? 0 : 1);
				val <<= 1;

				counter++;
			}
			// binary.append(' ');
		}
		return res;
	}

	public static byte[] StringToBinaryByte(String s) {
		byte[] bytes = s.getBytes();

		return ToBinaryByte(bytes);
	}

	public static byte[] prependZeroTo8(byte[] s) {
		int size = s.length;
		int divide = s.length / 8;
		int mod = s.length % 8;
		int zeroNum = 0;
		if (mod != 0) {
			size = 8 * (divide + 1);
			zeroNum = 8 - mod;
		}
		byte[] newBytes = new byte[size];

		if (zeroNum != 0) {
			for (int index = 0; index < zeroNum; index++) {
				// System.out.println("s.length:"+size+" zeroNum:"+zeroNum+" mod:"+mod+" index:"+index);
				newBytes[index] = 0;
			}
			int counter = 0;
			for (int index = zeroNum; index < size; index++) {
				newBytes[index] = s[counter];
				counter++;
			}
		} else {
			newBytes = s;
		}

		return newBytes;
	}

	public static byte[] ConvertByte2BitStore(byte[] bytes) {
		byte[] newBytes = prependZeroTo8(bytes);

		/*
		 * int divide = bytes.length/8; int mod = bytes.length % 8; int size =
		 * divide; if(mod != 0){ size++; }
		 */
		byte[] bits = new byte[newBytes.length / 8];

		for (int i = 0; i < newBytes.length; i++) {
			int indexBit = i % 8;
			int index = i / 8;
			boolean isContain = true;
			byte b = newBytes[i];

			if (b == 0) {
				isContain = false;
			} else {
				isContain = true;
			}

			if (isContain) {
				bits[index] = (byte) (bits[index] | (1 << indexBit));

			} else {
				bits[index] = (byte) (bits[index] & ~(1 << indexBit));

			}

		}

		return bits;

	}

	public static byte[] convertString2BitsStore(String s) {
		byte[] bytes = Converter.StringToBinaryByte(s);
		byte[] newBytes = Converter.ConvertByte2BitStore(bytes);
		return newBytes;
	}

	public static byte[] int2BitwiseArray(int value) {

		return ByteBuffer.allocate(4).putInt(value).array();
	}

}
