package common.test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import java.util.BitSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import common.crypto.Encryption;
import common.crypto.PRF;

import common.util.Converter;
import common.util.PrintTool;
import common.util.StringXORer;

public class TestBinary {

	public static void main(String[] args) {
		String str = "0101";
		byte[] byte1 = new BigInteger(str, 2).toByteArray();
		System.out.println("byteArray length:" + byte1.length + " byte1[0]:"
				+ byte1[0]);

		String str2 = "00010001";
		byte[] byte2 = new BigInteger(str2, 2).toByteArray();
		byte b2 = new BigInteger(str2, 2).byteValue();

		System.out.println("byteArray2 length:" + byte2.length + " byte2[0]:"
				+ byte2[0] + " b:" + b2);
		for (int mask = 0x01; mask != 0x100; mask <<= 1) {
			boolean value = (byte2[0] & mask) != 0;
			System.out.println("value:" + value);
		}

		String str3 = "00000001";
		byte[] byte3 = new BigInteger(str3, 2).toByteArray();
		System.out.println("byteArray3 length:" + byte3.length + " byte3[0]:"
				+ byte3[0]);

		BitSet bs = new BitSet();
		bs.set(0, true);
		bs.set(1, false);
		bs.set(2, true);
		bs.set(3, true);
		System.out.println("bitSet:" + bs.get(0) + bs.get(1) + bs.get(2)
				+ bs.get(3) + " size:" + bs.size() + " length:" + bs.length());
		byte[] b3 = bs.toByteArray();
		System.out.println("b3:" + b3[0]);

		int int1 = 1001;

		System.out.println("divide:" + int1 / 8 + " mod:" + int1 % 8);

		String plaintext = "foo";
		byte[] binary = Converter.StringToBinaryByte(plaintext);
		byte[] bits = Converter.ConvertByte2BitStore(binary);
		StringBuilder sb1 = new StringBuilder();
		for (int i = 0; i < binary.length; i++) {
			sb1.append(binary[i]);
		}
		StringBuilder sb2 = new StringBuilder();
		for (int i = 0; i < bits.length; i++) {
			sb2.append(bits[i]);
		}
		System.out.println("bytes:" + sb1.toString() + " bits:"
				+ sb2.toString() + " bytes.length:" + binary.length
				+ " bits.length" + bits.length);

		String s = "foo";
		byte[] bytes = s.getBytes();
		byte[] newBytes = new byte[s.getBytes().length];
		StringBuilder sbFoo = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String binaryStringRep = String.format("%8s",
					Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0');
			byte newByte = Byte.parseByte(binaryStringRep, 2);
			newBytes[i] = newByte;
			sbFoo.append(newBytes[i]);
		}

		try {
			String strFoo = new String(newBytes, "UTF-8");
			System.out.println("strFoo:" + strFoo + " binaryFoo:"
					+ sbFoo.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte bb1 = 47;
		byte bb2 = 53;
		byte bb3 = (byte) ((bb1 ^ bb2) & 0x000000ff);
		System.out.println("bb3: " + bb3);

		int id1 = 7;

		byte[] perLshRelatedKeyByte = PRF.HMACSHA1ToByteArray(
				Converter.convertString2BitsStore("lshkey"),
				Converter.convertString2BitsStore("distSSE"));

		String encrypted = Encryption.encryptId(id1, perLshRelatedKeyByte);
		int decrypted = Encryption.decryptId(encrypted, perLshRelatedKeyByte);

		System.out.println("---------encryption:" + encrypted + " decryption:"
				+ decrypted);
		// convert id to bits
		String idStr1 = "7";
		// byte[] idByte1 = Converter.convertString2BitsStore(idStr1);
		byte[] idByte1 = ByteBuffer.allocate(4).putInt(id1).array();
		StringBuilder sb11 = new StringBuilder();
		for (int i = 0; i < idByte1.length; i++) {
			sb11.append(idByte1[i]);
		}

		System.out.println("idByte1:" + sb11.toString());

		// XOR with lsh key
		byte[] eidArray = StringXORer.xorWithKey(perLshRelatedKeyByte, idByte1);
		StringBuilder sb12 = new StringBuilder();
		for (int i = 0; i < eidArray.length; i++) {
			sb12.append(eidArray[i]);
		}
		System.out.println("eidArray after XOR:" + sb12.toString());

		String eidStr = StringXORer.base64Encode(eidArray);

		System.out.println("ID1:" + id1 + " idStr1:" + idStr1 + " eid1:"
				+ eidStr);

		byte[] idByte2 = StringXORer.base64Decode(eidStr, false);
		StringBuilder sb21 = new StringBuilder();
		for (int i = 0; i < idByte2.length; i++) {
			sb21.append(idByte2[i]);
		}
		System.out.println("idByte2 expected equal to eidArray:"
				+ sb21.toString());

		// expect = idByte1
		byte[] idArray = StringXORer.xorWithKey(perLshRelatedKeyByte, idByte2);
		StringBuilder sb22 = new StringBuilder();
		for (int i = 0; i < idArray.length; i++) {
			sb22.append(idArray[i]);
		}
		System.out.println("idArray expected equal to idByte1:"
				+ sb22.toString());

		int id2 = ByteBuffer.wrap(idArray).getInt();

		// int base =2;

		// int id2 = Integer.parseInt(idStr2, base);

		// int id = Integer.parseUnsignedInt(idStr);
		System.out.println("id2:" + id2 + " eid2:" + eidStr);

		String strr1 = "11111";
		byte[] encode1 = StringXORer.base64Decode(strr1, true);
		String decode1 = StringXORer.base64Encode(encode1);
		System.out.println("str1:" + strr1 + " decode1:" + decode1);

		byte[] encode2 = StringXORer.base64Decode(decode1, true);
		String strr2 = StringXORer.base64Encode(encode2);
		System.out.println("str2:" + strr2 + " decode1:" + decode1);

		/*
		 * int seed = 2015;
		 * 
		 * int[][] familyU = new int[4][8]; Random ranSeed = new Random(seed);
		 * for(int i=0; i<4; i++ ){ for(int j=0; j<16/2; j++){ familyU[i][j] =
		 * ranSeed.nextInt(2007);
		 * System.out.println("familyU_"+i+j+":"+familyU[i][j]); } }
		 */
		int int11 = 8;
		int int12 = 128;
		int tmp = int11 ^ int12;
		System.out.println("tmp:" + tmp);

		String str21 = "8";

		byte[] byte21 = Converter.convertString2BitsStore(str21);
		System.out.println("byte21.length:" + byte21.length + " byte21:"
				+ byte21[0]);

		String str22 = "256";
		byte[] byte22 = Converter.convertString2BitsStore(str22);
		byte[] binaryBytes22 = Converter.StringToBinaryByte(str22);
		System.out.println("byte22.length:" + byte22.length + " byte22:"
				+ byte22[0]);
		// System.out.println("binaryBytes22.length:"+binaryBytes22.length+" binaryBytes22:"+binaryBytes22[0]);
		StringBuilder sbB22 = new StringBuilder();
		for (int i = 0; i < binaryBytes22.length; i++) {
			sbB22.append(binaryBytes22[i]);
		}
		System.out.println("binaryBytes22:" + sbB22.toString());

		byte[] byteXOR = StringXORer.xorWithKey(byte21, byte22);
		StringBuilder sb20 = new StringBuilder();
		for (int i = 0; i < byteXOR.length; i++) {
			sb20.append(byteXOR[i]);
		}
		System.out.println("byteXOR:" + sb20.toString());
		// ByteBuffer.wrap(byteXOR);

		byte[] intArray1 = Converter.int2BitwiseArray(8);
		byte[] intArray2 = Converter.int2BitwiseArray(128);
		byte[] xorIntArray = StringXORer.xorWithKey(intArray1, intArray2);
		int intXor = ByteBuffer.wrap(xorIntArray).getInt();
		System.out.println("intXor:" + intXor);
		byte[] xorByte = Converter.ToBinaryByte(xorIntArray);
		StringBuilder sb3 = new StringBuilder();
		for (int i = 0; i < xorByte.length; i++) {
			sb3.append(xorByte[i]);
		}
		System.out.println("byteXOR:" + sb3.toString());

		Map<String, String> unsortMap = new TreeMap<String, String>();
		unsortMap.put("Z", "z");
		unsortMap.put("B", "b");
		unsortMap.put("A", "a");
		unsortMap.put("C", "c");
		unsortMap.put("D", "d");
		unsortMap.put("E", "e");
		unsortMap.put("Y", "y");
		unsortMap.put("N", "n");
		unsortMap.put("J", "j");
		unsortMap.put("M", "m");
		unsortMap.put("F", "f");

		System.out.println("Unsort Map......");
		PrintTool.printMap(unsortMap);

		int id111 = 8;
		byte[] idByte111 = ByteBuffer.allocate(20).putInt(id111).array();
		StringBuilder sb111 = new StringBuilder();
		for (int i = 0; i < idByte111.length; i++) {
			sb111.append(idByte111[i]);
		}

		System.out.println("idByte1:" + sb111.toString());

		long n1 = System.nanoTime();
		Random ran = new Random(2017);
		long n2 = System.nanoTime();
		for (int i = 0; i < 10; i++) {
			// System.out.print(ran.nextInt(200)+";");
			ran.nextInt(200);
		}
		long n3 = System.nanoTime();
		System.out.println("new ran time:" + (n2 - n1) + " next int time:"
				+ (n3 - n2));

		System.out.println();

		for (int i = 0; i < 10; i++) {
			Random ran1 = new Random(2017);
			System.out.print(ran1.nextInt(200) + ";");
		}

	}

}
