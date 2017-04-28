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


public class LshSSESearch {
	public static void main(String[] args) {
		String filePath = "";
		String dictionaryName = "";
		String query = "";
		String tweetFileName = "";
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

		int l = m * (m - 1) / 2;
		int k2 = k * 2;
		byte[] clientKey = Converter.convertString2BitsStore(clientKeyStr);

		TokenGeneration tg = new TokenGeneration();
		ReadTxt rt = new ReadTxt();

		ConnectRedis cr = new ConnectRedis();
		Retrieval retrieval = new Retrieval();
		PlaintextIdRecovery pr = new PlaintextIdRecovery();

		// 1. load dictionary
		ArrayList<String> dic = rt.bufferedReadTxt(filePath, dictionaryName);

		// 2. gen token map
		ArrayList<byte[]> tokenList = new ArrayList<byte[]>();
		byte[] singleTweetByte = tg.parseQuery(query, d, dic);
		long d1Token = System.nanoTime();

		tokenList = tg.generateSearchTokensLshSSE(singleTweetByte, clientKey,
				l, k2, d, ranSeed, dic);

		long d2Token = System.nanoTime();
		long nanoToken = d2Token - d1Token;
		double millisecondToken = (double) nanoToken / Constants.NANO_TO_MILLI;
		dic.clear();
		System.gc();
		System.out.println("tokenMap size:" + tokenList.size());
		System.out.println(" duration in millisecond:" + millisecondToken
				+ " nano:" + nanoToken);

		// 4. connect db
		Jedis jedis = cr.connectDb(ip);

		long ldSearch1 = System.nanoTime();
		// 5. retrieve from db
		HashMap<String, String> resMap = retrieval.retrieveFromDbGeneral(jedis,
				tokenList);
		System.out.println("resMap.size:" + resMap.keySet().size());

		// 6. decryption and get id list
		ArrayList<String> idList = pr.recoverId(resMap, tokenList);
		long ldSearch2 = System.nanoTime();
		long nanoSearch = ldSearch2 - ldSearch1;
		double millisecondSearch = (double) nanoSearch
				/ Constants.NANO_TO_MILLI;
		System.out.println("SearchTime. nano:" + nanoSearch + " millisecond:"
				+ millisecondSearch);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < idList.size(); i++) {
			sb.append(idList.get(i));
			sb.append(";");
		}

		System.out.println("Number of id:" + idList.size() + "id list:"
				+ sb.toString());

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
		PrintTool.printMap(distanceMapLSH);

		// precision
		int topK = Config.getSettingInt(Constants.CONFIG_LSH_TOP_K);
		ComputeHammingDistance.computePrecision(distanceMap, distanceMapLSH,
				topK);

	}
}
