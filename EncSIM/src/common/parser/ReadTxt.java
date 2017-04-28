package common.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import redis.clients.jedis.Jedis;

import common.db.Insertion;
import common.lsh.GenAllPairsLsh;
import common.util.Config;
import common.util.Constants;
import dist.index.TokenGeneration;
//import dist.index.TweetParser;

/**
 * 
 * @author maggie liu
 *
 */
public class ReadTxt {

	/**
	 * read txt
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public ArrayList<String> bufferedReadTxt(String filePath, String fileName) {
		ArrayList<String> dic = new ArrayList<String>();

		String directory = filePath + fileName;
		System.out.println("directory:" + directory);
		File fil = new File(directory);
		BufferedReader bf = null;
		FileReader fr = null;

		try {
			fr = new FileReader(fil);
			bf = new BufferedReader(fr);

			String nextLine;
			while ((nextLine = bf.readLine()) != null) {
				dic.add(nextLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bf.close();
				fr.close();

			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}

		return dic;
	}

	/**
	 * Read tweets to db -xor
	 * 
	 * @param filePath
	 * @param fileName
	 * @param m
	 * @param k
	 * @param d
	 * @param ranSeed
	 * @param clientKey
	 * @param jedis
	 */
	public void readToDBXOR(String filePath, String fileName, int m, int k,
			int d, Random ranSeed, byte[] clientKey, Jedis jedis) {
		Insertion inDb = new Insertion();
		
		GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, ranSeed);

		HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

		String directory = filePath + fileName;
		File fil = new File(directory);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fil);

			
			int sizeB = 0;
			int divide = d / 8;
			int mod = d % 8;
			if (mod == 0) {
				sizeB = divide;
			} else {
				sizeB = divide + 1;
			}

			int idIndex = 0;

			int progressBar = 0;
			long nano = 0;
			long msecond = 0;

			boolean isContinue = true;

			
			while (isContinue) {
				HashMap<String, String> indexMap = new HashMap<String, String>();
				
				
				byte[] singleTweet = new byte[sizeB];

				// 1. read tweet file
				int stop = fis.read(singleTweet);
				if (progressBar == Config
						.getSettingInt(Constants.CONFIG_PROGRESS_BAR)) {
					
					System.out
							.println("progress:"
									+ idIndex
									/ Config.getSettingInt(Constants.CONFIG_PROGRESS_BAR)
									+ "%");
					progressBar = 0;
					/*
					 * if(idIndex/1000 == 50){ break; }
					 */
				}

				if (idIndex == Config.getSettingInt(Constants.CONFIG_LINE_NUM)
						|| stop == -1) {
					isContinue = false;
					break;
				}

				long nano1 = System.nanoTime();
				long d1 = System.currentTimeMillis();
				
				// 2. generate LSH value
				genLsh.generateLHashTablesXOR(singleTweet, idIndex, clientKey,
						counterMap, indexMap);

				long nano2 = System.nanoTime();
				long d2 = System.currentTimeMillis();

				
				nano += nano2 - nano1;
				msecond = msecond + (d2 - d1);
			
				idIndex++;
				progressBar++;

				// 3. append index map to db
				inDb.insertToDb(jedis, indexMap);

				

			}

			System.out.println("Setup. nano:" + nano + " millisecond:"
					+ msecond);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

	}

	/**
	 * read tweets to db - plaintext
	 * @param filePath
	 * @param fileName
	 * @param m
	 * @param k
	 * @param d
	 * @param ranSeed
	 * @param jedis
	 */
	public void readToDBPlaintext(String filePath, String fileName, int m,
			int k, int d, Random ranSeed, Jedis jedis) {
		Insertion inDb = new Insertion();
		GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, ranSeed);

		HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

		String directory = filePath + fileName;
		File fil = new File(directory);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fil);
			int sizeB = 0;
			int divide = d / 8;
			int mod = d % 8;
			if (mod == 0) {
				sizeB = divide;
			} else {
				sizeB = divide + 1;
			}

			int i = 0;
			int progressBar = 0;
			boolean isContinue = true;
			//1. read file
			while (isContinue) {
				HashMap<String, String> indexMap = new HashMap<String, String>();
				
				byte[] singleTweet = new byte[sizeB];

				int stop = fis.read(singleTweet);
				
				if (i == Config.getSettingInt(Constants.CONFIG_LINE_NUM)
						|| stop == -1) {
					isContinue = false;
					break;
				}

				if (progressBar == Config
						.getSettingInt(Constants.CONFIG_PROGRESS_BAR)) {
					
					System.out
							.println("progress:"
									+ i
									/ Config.getSettingInt(Constants.CONFIG_PROGRESS_BAR)
									+ "%");
					progressBar = 0;
					

				}

				// 2. generate LSH value
				genLsh.generateLHashTablesPlaintext(singleTweet, i, counterMap,
						indexMap);

				// 3. append index map to db
				inDb.insertToDb(jedis, indexMap);

				progressBar++;
				i++;

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Read to db - general
	 * @param filePath
	 * @param fileName
	 * @param m
	 * @param k
	 * @param d
	 * @param ranSeed
	 * @param clientKey
	 * @param jedis
	 */
	public void readToDBGeneral(String filePath, String fileName, int m, int k,
			int d, Random ranSeed, byte[] clientKey, Jedis jedis) {
		Insertion inDb = new Insertion();
		GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, ranSeed);

		HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

		String directory = filePath + fileName;
		File fil = new File(directory);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fil);
			int sizeB = 0;
			int divide = d / 8;
			int mod = d % 8;
			if (mod == 0) {
				sizeB = divide;
			} else {
				sizeB = divide + 1;
			}

			int idIndex = 0;

			boolean isContinue = true;
			while (isContinue) {
				HashMap<String, String> indexMap = new HashMap<String, String>();
				
				byte[] singleTweet = new byte[sizeB];
				int stop = fis.read(singleTweet);
				if (stop == -1) {
					isContinue = false;
					break;
				}

				// 2. generate LSH value
				genLsh.generateLHashTablesGeneral(singleTweet, idIndex,
						clientKey, counterMap, indexMap);

				// 3. append index map to db
				inDb.insertToDb(jedis, indexMap);

				idIndex++;

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

	}

	/**
	 * read file and convert to byte
	 * @param filePath
	 * @param fileName
	 * @param d
	 * @param idList
	 * @param isCheckAll
	 * @return
	 */
	public HashMap<Integer, byte[]> readToByteById(String filePath,
			String fileName, int d, ArrayList<String> idList, boolean isCheckAll) {
		
		HashMap<Integer, byte[]> map = new HashMap<Integer, byte[]>();
		

		String directory = filePath + fileName;
		
		File fil = new File(directory);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fil);

			
			int sizeB = 0;
			int divide = d / 8;
			int mod = d % 8;
			if (mod == 0) {
				sizeB = divide;
			} else {
				sizeB = divide + 1;
			}

			int idIndex = 0;

			boolean isContinue = true;
			while (isContinue) {
				

				byte[] singleTweet = new byte[sizeB];
				int stop = fis.read(singleTweet);
				
				if (stop == -1) {
					isContinue = false;
					break;
				}

				if (isCheckAll) {
					map.put(idIndex, singleTweet);
				} else {
					if (idList.contains(Integer.toString(idIndex))) {

						// add to map
						map.put(idIndex, singleTweet);

					}
				}

				idIndex++;

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

		return map;
	}

	/**
	 * Read source tweet to key-value store -baseline
	 * @param filePath
	 * @param fileName
	 * @param l
	 * @param k2
	 * @param d
	 * @param ranSeed
	 * @param clientKey
	 * @param jedis
	 */
	public void readToDBLshSSE(String filePath, String fileName, int l, int k2,
			int d, Random ranSeed, byte[] clientKey, Jedis jedis) {
		Insertion inDb = new Insertion();
		
		GenAllPairsLsh genLsh = new GenAllPairsLsh(l, k2, d, ranSeed);

		HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

		String directory = filePath + fileName;
		File fil = new File(directory);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fil);

			
			int sizeB = 0;
			int divide = d / 8;
			int mod = d % 8;
			if (mod == 0) {
				sizeB = divide;
			} else {
				sizeB = divide + 1;
			}

			int idIndex = 0;

			int progressBar = 0;
			long nano = 0;
			long msecond = 0;
			long mmsecond = 0;

			boolean isContinue = true;

			
			while (isContinue) {
				HashMap<String, String> indexMap = new HashMap<String, String>();
				// read sizeB bytes each time
				
				byte[] singleTweet = new byte[sizeB];
				int stop = fis.read(singleTweet);
				
				if (idIndex == Config.getSettingInt(Constants.CONFIG_LINE_NUM)
						|| stop == -1) {
					isContinue = false;
					break;
				}

				if (progressBar == Config
						.getSettingInt(Constants.CONFIG_PROGRESS_BAR)) {
					

					System.out
							.println("progress:"
									+ idIndex
									/ Config.getSettingInt(Constants.CONFIG_PROGRESS_BAR)
									+ "%");
					progressBar = 0;
					/*
					 * if(idIndex/1000 == 50){ break; }
					 */

				}

				long d1 = System.nanoTime();
				long dd1 = System.currentTimeMillis();
				// 2. generate LSH value
				genLsh.generateLHashTablesLshSSE(singleTweet, idIndex,
						clientKey, counterMap, indexMap);

				long d2 = System.nanoTime();
				long dd2 = System.currentTimeMillis();

				msecond += d2 - d1;
				mmsecond += dd2 - dd1;
				idIndex++;
				progressBar++;

				// 3. append index map to db
				 inDb.insertToDb(jedis, indexMap);

				

			}
			

			System.out.println("IndexSetupTime. Nano:" + nano + " Millisecond:"
					+ (double) msecond / Constants.NANO_TO_MILLI + " 2:"
					+ mmsecond);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	
	/**
	 * Read tweet vectors and convert to index - xor
	 * @param filePath
	 * @param insertFileName
	 * @param dictionaryName
	 * @param m
	 * @param k
	 * @param d
	 * @param ranSeed
	 * @param clientKey
	 * @param jedis
	 */
	public void readParseInsertXOR(String filePath, String insertFileName,
			String dictionaryName, int m, int k, int d, Random ranSeed,
			byte[] clientKey, Jedis jedis) {
		Insertion inDb = new Insertion();
		// ConnectRedis conn = new ConnectRedis();
		GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, ranSeed);

		HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

		String directory = filePath + insertFileName;

		ReadCsv reader = new ReadCsv();
		ArrayList<String> tweets = new ArrayList<String>();
		// 1. load dictionary
		ArrayList<String> dic = this.bufferedReadTxt(filePath, dictionaryName);

		// 2. read tweets
		tweets = reader.readCsvFileTweetVector(directory,
				Config.getSettingChar(Constants.CONFIG_CSV_SEPARATOR));

		//TweetParser tweetP = new TweetParser();
		TokenGeneration tg = new TokenGeneration();

		int idIndex = 0;

		long nano = 0;
		long msecond = 0;

		
		for (int i = 0; i < tweets.size(); i++) {

			long nano1 = System.nanoTime();
			long d1 = System.currentTimeMillis();

			byte[] singleTweet = tg.parseQuery(tweets.get(i), d, dic);

			HashMap<String, String> indexMap = new HashMap<String, String>();

			if (idIndex == Config.getSettingInt(Constants.CONFIG_LINE_NUM)) {

				break;
			}

			// 2. generate LSH value
			genLsh.generateLHashTablesXOR(singleTweet, idIndex, clientKey,
					counterMap, indexMap);

			long nano2 = System.nanoTime();
			long d2 = System.currentTimeMillis();

			
			nano += nano2 - nano1;
			msecond = msecond + (d2 - d1);
			

			// 3. append index map to db
			inDb.insertToDb(jedis, indexMap);

			idIndex++;

		}

		

		System.out.println("Setup. nano:" + nano + " millisecond:" + msecond);

	}
	
	/**
	 * Read tweet vectors and convert to index - baseline
	 * 
	 * @param filePath
	 * @param insertFileName
	 * @param dictionaryName
	 * @param m
	 * @param k
	 * @param d
	 * @param ranSeed
	 * @param clientKey
	 * @param jedis
	 */
	public void readParseInsertLshSSE(String filePath, String insertFileName,
			String dictionaryName, int m, int k, int d, Random ranSeed,
			byte[] clientKey, Jedis jedis) {
		Insertion inDb = new Insertion();
	
		GenAllPairsLsh genLsh = new GenAllPairsLsh(m, k, d, ranSeed);

		HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

		String directory = filePath + insertFileName;

		ReadCsv reader = new ReadCsv();
		ArrayList<String> tweets = new ArrayList<String>();
		// 1. load dictionary
		ArrayList<String> dic = this.bufferedReadTxt(filePath, dictionaryName);

		// 2. read tweets
		tweets = reader.readCsvFileTweetVector(directory,
				Config.getSettingChar(Constants.CONFIG_CSV_SEPARATOR));

		
		TokenGeneration tg = new TokenGeneration();

		int idIndex = 0;

		long nano = 0;
		long msecond = 0;

		
		for (int i = 0; i < tweets.size(); i++) {

			long nano1 = System.nanoTime();
			long d1 = System.currentTimeMillis();

			byte[] singleTweet = tg.parseQuery(tweets.get(i), d, dic);

			HashMap<String, String> indexMap = new HashMap<String, String>();

			if (idIndex == Config.getSettingInt(Constants.CONFIG_LINE_NUM)) {

				break;
			}

			// 2. generate LSH value
			genLsh.generateLHashTablesLshSSE(singleTweet, idIndex, clientKey,
					counterMap, indexMap);

			long nano2 = System.nanoTime();
			long d2 = System.currentTimeMillis();

			
			nano += nano2 - nano1;
			msecond = msecond + (d2 - d1);
			

			// 3. append index map to db
			inDb.insertToDb(jedis, indexMap);

			idIndex++;

		}

		

		System.out.println("Setup. nano:" + nano + " millisecond:" + msecond);

	}
	
	
	

}
