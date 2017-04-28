package common.lsh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import common.crypto.Encryption;
import common.crypto.PRF;
import common.util.Converter;
import common.util.StringXORer;
import common.util.UtilHelper;

public class GenAllPairsLsh {
	// Number of hash tables
	private static int l;

	// Number of composite hash u_a
	private static int m;

	// k-dimensional vector
	private static int k;

	// Number of data dimensions
	private static int d;

	// Hash function family u_1 to u_m
	private static int[][] familyU;

	public GenAllPairsLsh() {

	}

	public GenAllPairsLsh(int numberOfCompositeHashFunctions,
			int kDimensionalVector, int dimension, Random ranSeed) {
		m = numberOfCompositeHashFunctions;
		k = kDimensionalVector;
		d = dimension;

		l = m * (m - 1) / 2;

		familyU = new int[m][k / 2];
		// initialHashFamily(seed);
		genHashFamilyU(ranSeed);
	}

	@Deprecated
	private void initialHashFamily(Random ranSeed) {
		// Random ranSeed = new Random(seed);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < k / 2; j++) {
				familyU[i][j] = ranSeed.nextInt(d);
				System.out.println("familyU_" + i + j + ":" + familyU[i][j]);
			}
		}

	}

	/**
	 * Generate single composite hash function u_a
	 * 
	 * @return
	 */
	private void genHashFunctionU(Random ranSeed, int[][] familyU, int mIndex) {
		int numHash = k / 2;

		for (int i = 0; i < numHash; i++) {

			familyU[mIndex][i] = ranSeed.nextInt(d);
			// System.out.println("compositeU_"+i+":" + familyU[mIndex][i]);
		}

		// return familyU;
	}

	/**
	 * Generate hash function family u_1 to u_m
	 */
	private void genHashFamilyU(Random ranSeed) {

		// Random ranSeed = new Random(seed);
		for (int i = 0; i < m; i++) {
			genHashFunctionU(ranSeed, familyU, i);
			// System.out.println("familyU_"+i+":"+familyU[i]);
		}

		// return familyU;
	}

	/**
	 * Compute u_a(data)
	 * 
	 * @param data
	 * @param mIndex
	 * @return long
	 */

	public static byte[] computeOnePairLsh(byte[] data, int mIndex) {
		// StringBuilder sb = new StringBuilder();
		byte[] hashValueData = new byte[k / 2];

		for (int i = 0; i < k / 2; i++) {
			// hashValueData[i] = data[familyU[mIndex][i]];
			int random = familyU[mIndex][i];
			int indexByte = random / 8;
			int offset = random % 8;
			boolean value = false;

			int counter = 1;
			for (int mask = 0x01; mask != 0x100; mask <<= 1) {
				if (counter == offset) {
					value = (data[indexByte] & mask) != 0;
				}

				counter++;
				// System.out.println("value:"+value);
			}

			if (value) {
				hashValueData[i] = 1;
			} else {
				hashValueData[i] = 0;
			}

			// sb.append(hashValueData[i]);
		}

		// Converter.ConvertByte2BitStore(hashValueData);
		return Converter.ConvertByte2BitStore(hashValueData);
	}

	/**
	 * compute one encrypted lsh
	 * @param data
	 * @param mIndex
	 * @param clientKey
	 * @return
	 */
	public byte[] computeOnePairLshEncrypted(byte[] data, int mIndex,
			byte[] clientKey) {
		// StringBuilder sb = new StringBuilder();
		byte[] hashValueData = new byte[k / 2];

		for (int i = 0; i < k / 2; i++) {
			// hashValueData[i] = data[familyU[mIndex][i]];
			int random = familyU[mIndex][i];
			int indexByte = random / 8;
			int offset = random % 8;
			boolean value = false;

			int counter = 1;
			for (int mask = 0x01; mask != 0x100; mask <<= 1) {
				if (counter == offset) {
					value = (data[indexByte] & mask) != 0;
				}

				counter++;
				// System.out.println("value:"+value);
			}

			if (value) {
				hashValueData[i] = 1;
			} else {
				hashValueData[i] = 0;
			}

			// sb.append(hashValueData[i]);
		}

		// 1. ux
		byte[] ux = Converter.ConvertByte2BitStore(hashValueData);

		// 2. Append with index
		byte[] uxStr = Converter.concatenateByteArrays(ux,
				Converter.int2BitwiseArray(mIndex));

		// 3. Encrypted with PRF
		byte[] uxPrf = PRF.HMACSHA1ToByteArray(uxStr, clientKey);

		// Converter.ConvertByte2BitStore(hashValueData);
		return uxPrf;
	}

	/**
	 * Compute u_1(data) to u_m(data)
	 * 
	 * @param data
	 * @return long[]
	 */
	public static ArrayList<byte[]> computeMPairsLsh(byte[] data) {
		ArrayList<byte[]> mPairsLsh = new ArrayList<byte[]>();
		for (int i = 0; i < m; i++) {
			mPairsLsh.add(GenAllPairsLsh.computeOnePairLsh(data, i));
		}

		return mPairsLsh;
	}

	/**
	 * computer m pairs encrypted lsh
	 * @param data
	 * @param clientKey
	 * @return
	 */
	public ArrayList<byte[]> computeMPairsLshEncrypted(byte[] data,
			byte[] clientKey) {
		ArrayList<byte[]> mPairsLsh = new ArrayList<byte[]>();
		for (int i = 0; i < m; i++) {
			mPairsLsh.add(this.computeOnePairLshEncrypted(data, i, clientKey));
		}

		return mPairsLsh;
	}

	/**
	 * get counter
	 * 
	 * @param counterMap
	 * @param perLshRelatedKeyStr
	 * @return
	 */
	public static byte[] getCounter(HashMap<String, Integer> counterMap,
			String perLshRelatedKeyStr) {
		int counter = 1;
		if (UtilHelper.isEmpty(counterMap)
				|| !counterMap.containsKey(perLshRelatedKeyStr)) {
			counterMap.put(perLshRelatedKeyStr, counter + 1);
		} else {
			counter = counterMap.get(perLshRelatedKeyStr);

			counterMap.put(perLshRelatedKeyStr, counter + 1);
		}

		return Converter.int2BitwiseArray(counter);
	}

	@Deprecated
	public static String getLabelFast(byte[] counter,
			byte[] perLshRelatedKeyByte) {
		
		byte[] labelByte = Converter.concatenateByteArrays(
				perLshRelatedKeyByte, counter);
		String labelStr = StringXORer.base64Encode(labelByte);

		return labelStr;
	}

	/**
	 * get label
	 * 
	 * @param counter
	 * @param perLshRelatedKeyByte
	 * @return
	 */
	public static String getLabel(byte[] counter, byte[] perLshRelatedKeyByte) {
		byte[] labelByte = PRF.HMACSHA1ToByteArray(perLshRelatedKeyByte,
				counter);
		
		String labelStr = StringXORer.base64Encode(labelByte);

		return labelStr;
	}

	/**
	 * Generate l hash tables by combining u_1 to u_m
	 */
	public HashMap<String, String> generateLHashTablesXOR(byte[] data,
			int dataId, byte[] clientKey, HashMap<String, Integer> counterMap,
			HashMap<String, String> indexMap) {

		// 1. Get u_1(data) to u_m(data)
		ArrayList<byte[]> mPairsLsh = this.computeMPairsLshEncrypted(data,
				clientKey);

		for (int x = 0; x < m - 1; x++) {
			for (int y = x + 1; y < m; y++) {

				// 2. get uxPrf
				byte[] uxPrf = mPairsLsh.get(x);
				byte[] uyPrf = mPairsLsh.get(y);

				// 4. XOR

				byte[] perLshRelatedKeyByte = StringXORer.xorFast(uxPrf, uyPrf);
				String perLshRelatedKeyStr = StringXORer
						.base64Encode(perLshRelatedKeyByte);

				// 5. get counter
				byte[] counter = getCounter(counterMap, perLshRelatedKeyStr);

				// 6. Compute hash label

				String labelStr = getLabel(counter, perLshRelatedKeyByte);

				// 7. Encrypted id

				String eidStr = Encryption.encryptId(dataId,
						perLshRelatedKeyByte);

				indexMap.put(labelStr, eidStr);

			}

		}

		return indexMap;
	}

	/**
	 * Generate L hash tables - plaintext
	 * 
	 * @param data
	 * @param dataId
	 * @param counterMap
	 * @param indexMap
	 * @return
	 */
	public HashMap<String, String> generateLHashTablesPlaintext(byte[] data,
			int dataId, HashMap<String, Integer> counterMap,
			HashMap<String, String> indexMap) {

		// 1. Get u_1(data) to u_m(data)
		ArrayList<byte[]> mPairsLsh = GenAllPairsLsh.computeMPairsLsh(data);

		for (int x = 0; x < m - 1; x++) {
			for (int y = x + 1; y < m; y++) {

				// 1. Get u_1(data) to u_m(data)

				String ux = StringXORer.base64Encode(mPairsLsh.get(x));
				String uy = StringXORer.base64Encode(mPairsLsh.get(y));

				// 2. Append with index
				String perLshRelatedKeyStr = ux + uy;

				// 5. get counter
				int counter = 1;
				if (UtilHelper.isEmpty(counterMap)
						|| !counterMap.containsKey(perLshRelatedKeyStr)) {
					counterMap.put(perLshRelatedKeyStr, counter + 1);
				} else {
					counter = counterMap.get(perLshRelatedKeyStr);

					counterMap.put(perLshRelatedKeyStr, counter + 1);
				}

				// 6. Compute hash label

				String labelStr = perLshRelatedKeyStr
						+ Converter.int2BinaryString(counter);

				indexMap.put(labelStr, Integer.toString(dataId));

			}

		}

		return indexMap;
	}

	/**
	 * Generate L hash tables - general
	 * 
	 * @param data
	 * @param dataId
	 * @param clientKey
	 * @param counterMap
	 * @param indexMap
	 * @return
	 */
	public HashMap<String, String> generateLHashTablesGeneral(byte[] data,
			int dataId, byte[] clientKey, HashMap<String, Integer> counterMap,
			HashMap<String, String> indexMap) {
		// long[] lKeys = new long[l];

		// 1. Get u_1(data) to u_m(data)
		ArrayList<byte[]> mPairsLsh = GenAllPairsLsh.computeMPairsLsh(data);

		for (int x = 0; x < m - 1; x++) {
			for (int y = x + 1; y < m; y++) {

				// 1. Get u_1(data) to u_m(data)
				byte[] ux = mPairsLsh.get(x);
				byte[] uy = mPairsLsh.get(y);

				// 3. Encrypted with PRF
				byte[] uxPrf = PRF.HMACSHA1ToByteArray(ux, clientKey);
				byte[] uyPrf = PRF.HMACSHA1ToByteArray(uy, clientKey);

				// 4. concatenate

				byte[] perLshRelatedKeyByte = PRF.HMACSHA1ToByteArray(uxPrf,
						uyPrf); // 20 Bytes
				String perLshRelatedKeyStr = StringXORer
						.base64Encode(perLshRelatedKeyByte);

				// 5. get counter
				byte[] counter = getCounter(counterMap, perLshRelatedKeyStr);

				// 6. Compute hash label

				String labelStr = getLabel(counter, perLshRelatedKeyByte);

				// 7. Encrypted id

				String eidStr = Encryption.encryptId(dataId,
						perLshRelatedKeyByte);

				indexMap.put(labelStr, eidStr);

			}

		}

		return indexMap;
	}

	/**
	 * Generate L hash tables - baseline
	 * 
	 * @param data
	 * @param dataId
	 * @param clientKey
	 * @param counterMap
	 * @param indexMap
	 * @return
	 */
	public HashMap<String, String> generateLHashTablesLshSSE(byte[] data,
			int dataId, byte[] clientKey, HashMap<String, Integer> counterMap,
			HashMap<String, String> indexMap) {

		// 1. Get u_1(data) to u_m(data)
		ArrayList<byte[]> mPairsLsh = GenAllPairsLsh.computeMPairsLsh(data);

		for (int index = 0; index < m; index++) {
			byte[] gi = mPairsLsh.get(index);
			byte[] giStr = Converter.concatenateByteArrays(gi,
					Converter.int2BitwiseArray(index));
			byte[] perLshRelatedKeyByte = PRF.HMACSHA1ToByteArray(giStr,
					clientKey);
			String perLshRelatedKeyStr = StringXORer
					.base64Encode(perLshRelatedKeyByte);
			byte[] counter = getCounter(counterMap, perLshRelatedKeyStr);
			String labelStr = getLabel(counter, perLshRelatedKeyByte);
			String eidStr = Encryption.encryptId(dataId, perLshRelatedKeyByte);

			indexMap.put(labelStr, eidStr);
		}

		return indexMap;
	}

}
