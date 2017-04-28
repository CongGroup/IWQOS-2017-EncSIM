package dist.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import redis.clients.jedis.Jedis;
import common.db.ConnectRedis;
import common.db.Retrieval;
import common.lsh.ComputeHammingDistance;
import common.lsh.GenAllPairsLsh;
import common.parser.ReadTxt;
import common.util.Config;
import common.util.Constants;
import common.util.PrintTool;
import dist.index.PlaintextIdRecovery;
import dist.index.TokenGeneration;
import dist.index.TokenRecovery;

public class PlaintextSearch {
	public static void main(String[] args){
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
		dictionaryName = Config
				.getSetting(Constants.CONFIG_DICTIONARY_NAME);
		/*
		 * int m = 2; int k = 10; int d = 62; String ip = "localhost";
		 * 
		 * int seed = 2017; String clientKeyStr = "distSSE";
		 */
		int m = Config.getSettingInt(Constants.CONFIG_LSH_M);
		int k = Config.getSettingInt(Constants.CONFIG_LSH_K);
		int d = Config.getSettingInt(Constants.CONFIG_LSH_D);
		int r = Config.getSettingInt(Constants.CONFIG_LSH_RADIUS);
		String ip = Config.getSetting(Constants.CONFIG_DB_IP);

		int seed = Config.getSettingInt(Constants.CONFIG_LSH_SEED);
		Random ranSeed = new Random(seed);
		String clientKeyStr = Config
				.getSetting(Constants.CONFIG_LSH_CLIENT_KEY);
		
		TokenGeneration tg = new TokenGeneration();
		ReadTxt rt = new ReadTxt();
		TokenRecovery tr = new TokenRecovery();
		ConnectRedis cr = new ConnectRedis();
		Retrieval retrieval = new Retrieval();
		PlaintextIdRecovery pr = new PlaintextIdRecovery();
		//GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, seed);
		
		long d1Token = System.nanoTime();
		
		// 1. load dictionary
		ArrayList<String> dic = rt.bufferedReadTxt(filePath, dictionaryName);
		
		// 2. gen token map 
		
		// parse query point to byte[]
		byte[] singleTweetByte = tg.parseQuery(query, d, dic);
		
		
		ArrayList<String> perLshRelatedList = tg.generateSearchTokensPlaintext(singleTweetByte, m, k, d, ranSeed, dic);
		
		long d2Token = System.nanoTime();
		long nanoToken = d2Token-d1Token;
		double millisecondToken = (double)nanoToken/Constants.NANO_TO_MILLI;
		dic.clear();
		System.gc();
		System.out.println(" duration in millisecond:"+millisecondToken+" nano:"+nanoToken);
		System.out.println("tokenMap size:"+perLshRelatedList.size());
		
		
		// 4. connect db
		Jedis jedis = cr.connectDb(ip);
		
		// 5. retrieve from db
		long ldSearch1 = System.nanoTime();
		HashMap<String, String> resMap = retrieval.retrieveFromDbPlaintext(jedis, perLshRelatedList);
		System.out.println("resMap.size:"+resMap.keySet().size());
		long ldSearch2 =System.nanoTime();
		long nanoSearch = ldSearch2- ldSearch1;
		double millisecondSearch = (double)nanoSearch/Constants.NANO_TO_MILLI;
		System.out.println("SearchTime. nano:"+nanoSearch+" millisecond:"+millisecondSearch);
		System.exit(0);
		ArrayList<String> idList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, String>> it = resMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> entry = (Entry<String, String>)it.next();
			entry.getKey();
			String value = entry.getValue();
			if(!idList.contains(value)){
				idList.add(value);
				sb.append(value);
				sb.append(";\t");
			}
			
		}
		
		System.out.println("id list:"+ sb.toString());
		System.out.println("total:"+idList.size());
		
		
		
		//compute distance
		
		//1. get result tweets in byte array
		HashMap<Integer, byte[]> tesMapByte = rt.readToByteById(filePath, tweetFileName, d, idList, false);
		System.out.println("tesMapByte:"+tesMapByte.keySet().size());
		
		// 2. compute distance
		TreeMap<Integer, ArrayList<String>> distanceMap = ComputeHammingDistance.sortResHammingDistance(singleTweetByte, tesMapByte);
		PrintTool.printMap(distanceMap);
		
		
		
	}

}
