package dist.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

import redis.clients.jedis.Jedis;
import common.db.ConnectRedis;
import common.db.Retrieval;
import common.lsh.ComputeHammingDistance;
import common.parser.ReadTxt;
import common.util.Config;
import common.util.Constants;
import common.util.Converter;
import common.util.PrintTool;
import dist.index.PlaintextIdRecovery;
import dist.index.TokenGeneration;
import dist.index.TokenRecovery;
import dist.model.TokenTuple;

public class IndexSearch {

	public static void main(String[] args) {
		String filePath = "";
		String dictionaryName = "";
		String tweetFileName = "";
		String query = "";
		if (args.length >= 1) {

			query = args[0];
		} else {

			query = Config.getSetting(Constants.CONFIG_QUERY);
		}
		filePath = Config.getSetting(Constants.CONFIG_FILE_PATH);
		tweetFileName = Config.getSetting(Constants.CONFIG_TWEET_FILE_NAME);
		dictionaryName = Config.getSetting(Constants.CONFIG_DICTIONARY_NAME);

		int m = Config.getSettingInt(Constants.CONFIG_LSH_M);
		int k = Config.getSettingInt(Constants.CONFIG_LSH_K);
		int d = Config.getSettingInt(Constants.CONFIG_LSH_D);
		int r = Config.getSettingInt(Constants.CONFIG_LSH_RADIUS);
		String ip = Config.getSetting(Constants.CONFIG_DB_IP);

		int seed = Config.getSettingInt(Constants.CONFIG_LSH_SEED);
		Random ranSeed = new Random(seed);
		String clientKeyStr = Config
				.getSetting(Constants.CONFIG_LSH_CLIENT_KEY);

		byte[] clientKey = Converter.convertString2BitsStore(clientKeyStr);

		TokenGeneration tg = new TokenGeneration();
		ReadTxt rt = new ReadTxt();
		TokenRecovery tr = new TokenRecovery();
		ConnectRedis cr = new ConnectRedis();
		Retrieval retrieval = new Retrieval();
		PlaintextIdRecovery pr = new PlaintextIdRecovery();

		long nanoToken1 = System.nanoTime();
		// 4. connect db
		Jedis jedis = cr.connectDb(ip);

		// 1. load dictionary
		ArrayList<String> dic = rt.bufferedReadTxt(filePath, dictionaryName);

		// 2. gen token map

		byte[] singleTweetByte = tg.parseQuery(query, d, dic);

		HashMap<Integer, TokenTuple> tokenMap = tg.generateSearchTokensXOR(
				singleTweetByte, clientKey, m, k, d, ranSeed, dic);
		long nanoToken2 = System.nanoTime();
		long nanoToken = nanoToken2 - nanoToken1;
		System.out.println("Token generation time:" + (double) nanoToken
				/ Constants.NANO_TO_MILLI + " nano:" + nanoToken);
		System.out.println("tokenMap size:" + tokenMap.keySet().size());

		// 3. gen perLshRealtedKey
		long ldSearch1 = System.nanoTime();
		ArrayList<byte[]> perLshRelatedList = tr.recoverPerLshKeyXOR(tokenMap);
		System.out.println("perLshRelatedKeyList.size:"
				+ perLshRelatedList.size());

		// 5. retrieve from db
		HashMap<String, String> resMap = retrieval.retrieveFromDbXOR(jedis,
				perLshRelatedList);

		System.out.println("resMap.size:" + resMap.keySet().size());

		// 6. decryption and get id list
		ArrayList<String> idList = pr.recoverId(resMap, perLshRelatedList);
		long ldSearch2 = System.nanoTime();
		long nanoSearch = ldSearch2 - ldSearch1;
		double millisecondSearch = (double) nanoSearch
				/ Constants.NANO_TO_MILLI;
		System.out.println("Search time:" + millisecondSearch);
		System.out.println("idList.size:" + idList.size());

		// compute distance
		boolean isCheckAll = true;
		// 1. get all tweets in byte array
		HashMap<Integer, byte[]> tesMapByte = rt.readToByteById(filePath,
				tweetFileName, d, idList, isCheckAll);
		System.out.println("tesMapByte:" + tesMapByte.keySet().size());

		// 2. compute distance for all tweets within r
		TreeMap<Integer, ArrayList<String>> distanceMap = ComputeHammingDistance
				.sortResHammingDistanceWithinR(singleTweetByte, tesMapByte, r);
		PrintTool.printMap(distanceMap);

		// 3. get results within r from all tweets with in r
		TreeMap<Integer, ArrayList<String>> distanceMapLSH = ComputeHammingDistance
				.falsePositiveFilterWithinR(distanceMap, idList);

		distanceMapLSH.clear();
		System.gc();

		// 4. distance no filter
		TreeMap<Integer, ArrayList<String>> distanceMapAll = ComputeHammingDistance
				.sortResHammingDistance(singleTweetByte, tesMapByte);
		TreeMap<Integer, ArrayList<String>> distanceMapLSHAll = ComputeHammingDistance
				.falsePositiveFilterWithinR(distanceMapAll, idList);

		// precision
		int topK = Config.getSettingInt(Constants.CONFIG_LSH_TOP_K);
		ComputeHammingDistance.computePrecision(distanceMapAll,
				distanceMapLSHAll, topK);

	}
}
