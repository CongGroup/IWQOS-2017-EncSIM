package common.crypto;

import java.nio.ByteBuffer;

import common.util.StringXORer;

/**
 * Encrypt and decrypt ID
 * @author maggie liu
 *
 */
public class Encryption {

	/**
	 * Encrypt ID
	 * 
	 * @param id1
	 * @param perLshRelatedKeyByte
	 * @return
	 */
	public static String encryptId(int id1, byte[] perLshRelatedKeyByte) {

		byte[] idByte1 = ByteBuffer.allocate(4).putInt(id1).array();

		// XOR with lsh key
		byte[] eidArray = StringXORer.xorWithKey(perLshRelatedKeyByte, idByte1);

		String eidStr = StringXORer.base64Encode(eidArray);

		return eidStr;

	}

	/**
	 * Decrypt ID
	 * 
	 * @param eidStr
	 * @param perLshRelatedKeyByte
	 * @return
	 */
	public static int decryptId(String eidStr, byte[] perLshRelatedKeyByte) {

		byte[] idByte2 = StringXORer.base64Decode(eidStr, false);

		byte[] idArray = StringXORer.xorWithKey(perLshRelatedKeyByte, idByte2);

		byte[] arrayInt = new byte[4];
		arrayInt[0] = idArray[16];
		arrayInt[1] = idArray[17];
		arrayInt[2] = idArray[18];
		arrayInt[3] = idArray[19];

		int id2 = ByteBuffer.wrap(arrayInt).getInt();

		return id2;

	}

}
