package dist.main;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;

import redis.clients.jedis.Jedis;
import common.db.ConnectRedis;
import common.db.Retrieval;
import common.parser.ReadTxt;
import common.util.Converter;
import dist.index.PlaintextIdRecovery;
import dist.index.TokenGeneration;
import dist.index.TokenRecovery;

public class GeneralLshSearch {
	public static void main(String[] args) {
		String filePath = "";
		String dictionaryName = "";
		String query = "";
		if (args.length >= 3) {
			filePath = args[0];
			dictionaryName = args[1];
			query = args[2];
		} else {
			filePath = "F:/LSH/dataset";
			dictionaryName = "dictionary.txt";
			query = "Fuck this economy";
		}
		int m = 10;
		int k = 10;
		int d = 2077;
		String ip = "localhost";

		int seed = 2017;
		Random ranSeed = new Random(seed);
		String clientKeyStr = "distSSE";
		byte[] clientKey = Converter.convertString2BitsStore(clientKeyStr);

		TokenGeneration tg = new TokenGeneration();
		ReadTxt rt = new ReadTxt();
		TokenRecovery tr = new TokenRecovery();
		ConnectRedis cr = new ConnectRedis();
		Retrieval retrieval = new Retrieval();
		PlaintextIdRecovery pr = new PlaintextIdRecovery();

		// 1. load dictionary
		ArrayList<String> dic = rt.bufferedReadTxt(filePath, dictionaryName);

		// 2. gen token map
		long localDateToken1 = System.nanoTime();
		ArrayList<byte[]> tokenList = tg.generateSearchTokensGeneral(query,
				clientKey, m, k, d, ranSeed, dic);
		long localDateToken2 = System.nanoTime();

		long nanoSecondsToken = localDateToken2 - localDateToken1;
		System.out.println("tokenMap size:" + tokenList.size()
				+ " duration in nano seconds:" + nanoSecondsToken);

		// 3. gen perLshRealtedKey
		ArrayList<byte[]> perLshRelatedList = tr
				.recoverPerLshKeyGeneral(tokenList);
		System.out.println("perLshRelatedKeyList.size:"
				+ perLshRelatedList.size());

		// 4. connect db
		Jedis jedis = cr.connectDb(ip);

		// 5. retrieve from db
		HashMap<String, String> resMap = retrieval.retrieveFromDbGeneral(jedis,
				perLshRelatedList);
		System.out.println("resMap.size:" + resMap.keySet().size());

		// 6. decryption and get id list
		ArrayList<String> idList = pr.recoverId(resMap, perLshRelatedList);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < idList.size(); i++) {
			sb.append(idList.get(i));
			sb.append(";");
		}

		System.out.println("id list:" + sb.toString());

	}

}
