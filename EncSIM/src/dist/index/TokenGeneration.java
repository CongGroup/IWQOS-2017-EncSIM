package dist.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import common.crypto.PRF;
import common.lsh.GenAllPairsLsh;
import common.parser.Remover;
import common.parser.Tokenizer;

import common.util.Constants;
import common.util.Converter;

import common.util.StringXORer;
import dist.model.TokenTuple;

/**
 * Token generator
 * 
 * @author maggie liu
 * 
 */
public class TokenGeneration {

	/**
	 * Generate search tokens - xor way
	 * 
	 * @param singleTweetByte
	 * @param clientKey
	 * @param m
	 * @param k
	 * @param d
	 * @param ranSeed
	 * @param dictionary
	 * @return
	 */
	public HashMap<Integer, TokenTuple> generateSearchTokensXOR(
			byte[] singleTweetByte, byte[] clientKey, int m, int k, int d,
			Random ranSeed, ArrayList<String> dictionary) {
		HashMap<Integer, TokenTuple> tokenMap = new HashMap<Integer, TokenTuple>();

		// compute lsh value
		long lsh1 = System.nanoTime();
		GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, ranSeed);
		long lsh2 = System.nanoTime();
		System.out.println("Initial class. nano:" + (lsh2 - lsh1) + " milli:"
				+ (lsh2 - lsh1) / Constants.NANO_TO_MILLI);

		long t1 = System.nanoTime();
		ArrayList<byte[]> mPairsLsh = genLsh.computeMPairsLshEncrypted(
				singleTweetByte, clientKey);
		long t2 = System.nanoTime();
		long dur = t2 - t1;
		System.out.println("genLsh. nano:" + dur + " milli:" + dur
				/ Constants.NANO_TO_MILLI);

		// Random ran = new Random(seed);
		int[] rArray = new int[m];
		for (int i = 0; i < m; i++) {
			rArray[i] = ranSeed.nextInt();
		}

		for (int x = 0; x < m; x++) {
			// 1. Get u_1(data) to u_m(data)
			byte[] uxPrf = mPairsLsh.get(x);

			int r = rArray[x];
			byte[] rB = Converter.int2BitwiseArray(r);

			// 5. XOR with random
			byte[] t = StringXORer.xorWithKey(rB, uxPrf);

			// 6. XOR two random
			byte[] rXOR;
			if (x == m - 1) {
				rXOR = rB;
			} else {
				int rXX = rArray[x + 1];
				byte[] rXXB = Converter.int2BitwiseArray(rXX);
				rXOR = StringXORer.xorFast(rB, rXXB);
			}

			// 6. store into map
			TokenTuple tuple = new TokenTuple();
			tuple.setToken(t);
			tuple.setRandom(rXOR);
			tokenMap.put(x, tuple);

		}

		return tokenMap;
	}

	/**
	 * Parse query
	 * 
	 * @param query
	 * @param d
	 * @param dictionary
	 * @return
	 */
	public byte[] parseQuery(String query, int d, ArrayList<String> dictionary) {
		Tokenizer spliter = new Tokenizer();
		Remover remover = new Remover();
		TweetParser tp = new TweetParser();

		// 1.split tweet into words
		String[] words = spliter.splitTweetToWords(query);

		// 2. remove meaningless words
		ArrayList<String> clearWords = remover.removeMeaninglessWords(words);

		int dimension = d;
		int divide = dimension / 8;
		int mod = dimension % 8;
		int size = 0;
		if (mod == 0) {
			size = divide;
		} else {
			size = divide + 1;
		}

		byte[] singleTweetByte = new byte[size];

		// 3. compare dictionary
		for (int i = 0; i < dictionary.size(); i++) {

			String dicStr = dictionary.get(i);
			boolean isContain = tp.compareWithDictionary(dicStr, clearWords);

			int indexB = i / 8;
			int indexBit = i % 8;

			if (isContain) {
				singleTweetByte[indexB] = (byte) (singleTweetByte[indexB] | (1 << indexBit));

			} else {
				singleTweetByte[indexB] = (byte) (singleTweetByte[indexB] & ~(1 << indexBit));

			}
		}

		return singleTweetByte;
	}

	/**
	 * Generate token - plaintext
	 * 
	 * @param singleTweetByte
	 * @param m
	 * @param k
	 * @param d
	 * @param ranSeed
	 * @param dictionary
	 * @return
	 */
	public ArrayList<String> generateSearchTokensPlaintext(
			byte[] singleTweetByte, int m, int k, int d, Random ranSeed,
			ArrayList<String> dictionary) {
		ArrayList<String> tokenMap = new ArrayList<String>();
		GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, ranSeed);

		// 1. parse query point to byte[]
		// byte[] singleTweetByte = parseQuery(query, d, dictionary);

		// 2. compute lsh value

		ArrayList<byte[]> mPairsLsh = genLsh.computeMPairsLsh(singleTweetByte);
		// Random ran = new Random();
		for (int x = 0; x < m - 1; x++) {
			for (int y = x + 1; y < m; y++) {
				// 1. Get u_1(data) to u_m(data)
				String ux = StringXORer.base64Encode(mPairsLsh.get(x));
				String uy = StringXORer.base64Encode(mPairsLsh.get(y));
				

				String perLshRelatedKeyStr = ux + uy;

				// 6. store into map
				tokenMap.add(perLshRelatedKeyStr);
			}

		}

		return tokenMap;
	}

	/**
	 * Generate search token - general way
	 * @param query
	 * @param clientKey
	 * @param m
	 * @param k
	 * @param d
	 * @param ranSeed
	 * @param dictionary
	 * @return
	 */
	public ArrayList<byte[]> generateSearchTokensGeneral(String query,
			byte[] clientKey, int m, int k, int d, Random ranSeed,
			ArrayList<String> dictionary) {
		ArrayList<byte[]> tokenMap = new ArrayList<byte[]>();
		GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, ranSeed);

		// 1. parse query point to byte[]
		byte[] singleTweetByte = parseQuery(query, d, dictionary);

		// 2. compute lsh value

		ArrayList<byte[]> mPairsLsh = genLsh.computeMPairsLsh(singleTweetByte);

		for (int x = 0; x < m; x++) {
			// 1. Get u_1(data) to u_m(data)
			byte[] ux = mPairsLsh.get(x);

			// 3. Encrypted with PRF
			byte[] uxPrf = PRF.HMACSHA1ToByteArray(ux, clientKey);

			

			// 6. store into map
			tokenMap.add(uxPrf);

		}

		return tokenMap;
	}

	/**
	 * Generate search token - baseline
	 * 
	 * @param singleTweetByte
	 * @param clientKey
	 * @param l
	 * @param k2
	 * @param d
	 * @param ranSeed
	 * @param dictionary
	 * @return
	 */
	public ArrayList<byte[]> generateSearchTokensLshSSE(byte[] singleTweetByte,
			byte[] clientKey, int l, int k2, int d, Random ranSeed,
			ArrayList<String> dictionary) {
		ArrayList<byte[]> tokenMap = new ArrayList<byte[]>();

		long lsh1 = System.nanoTime();
		GenAllPairsLsh genLsh = new GenAllPairsLsh(l, k2, d, ranSeed);
		long lsh2 = System.nanoTime();
		System.out.println("Initial class. nano:" + (lsh2 - lsh1) + " milli:"
				+ (lsh2 - lsh1) / Constants.NANO_TO_MILLI);

		// 2. compute lsh value
		long fun1 = System.nanoTime();
		ArrayList<byte[]> mPairsLsh = genLsh.computeMPairsLshEncrypted(
				singleTweetByte, clientKey);
		long fun2 = System.nanoTime();
		System.out.println("genLsh. nano:" + (fun2 - fun1) + " milli:"
				+ (fun2 - fun1) / Constants.NANO_TO_MILLI);

		for (int x = 0; x < l; x++) {
			// 1. Get u_1(data) to u_m(data)
			byte[] ux = mPairsLsh.get(x);
			
			tokenMap.add(ux);


		}

		return tokenMap;
	}

}
