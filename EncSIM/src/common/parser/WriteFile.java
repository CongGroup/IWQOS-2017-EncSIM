package common.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import com.opencsv.CSVWriter;

import common.util.Config;
import common.util.Constants;


/**
 * file writer
 * @author maggie liu
 *27/4/2017
 *
 */
public class WriteFile {

	/**
	 * Write txt file
	 * 
	 * @param filePath
	 * @param fileName
	 * @param tweets
	 */
	public void writeTxtFile(String filePath, String fileName,
			ArrayList<byte[]> tweets) {

		String directory = filePath + fileName;

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			File file = new File(directory);

			// new file
			if (!file.exists()) {
				file.createNewFile();
			}

			// existing file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			for (int i = 0; i < tweets.size(); i++) {
				String content = new String();
				for (int j = 0; j < tweets.get(i).length; j++) {
					content = content.concat(Byte.toString(tweets.get(i)[j]));
				}
				bw.write(content);
				if (i != tweets.size() - 1) {
					bw.newLine();
				}

			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
	}

	/**
	 * append to exist file
	 * 
	 * @param filePath
	 * @param fileName
	 * @param tweets
	 * @param isLastLine
	 */
	public void appendTxtFile(String filePath, String fileName, byte[] tweets,
			boolean isLastLine) {

		String directory = filePath + fileName;

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			File file = new File(directory);

			// new file
			if (!file.exists()) {
				file.createNewFile();
			}

			// existing file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			String content = new String();
			for (int j = 0; j < tweets.length; j++) {
				content = content.concat(Byte.toString(tweets[j]));
			}

			bw.append(content);

			if (!isLastLine) {
				bw.newLine();
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
	}

	/**
	 * write file in byte stream
	 * 
	 * @param filePath
	 * @param fileName
	 * @param tweets
	 * @param isLastLine
	 */
	public void writeByteFile(String filePath, String fileName, byte[] tweets,
			boolean isLastLine) {

		String directory = filePath + "\\" + fileName;

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(directory, true);
			output.write(tweets);

		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	/**
	 * write dictionary into file
	 * 
	 * @param filePath
	 * @param fileName
	 * @param words
	 * @param isLastLine
	 */
	public void writeDictionary(String filePath, String fileName, String words,
			boolean isLastLine) {

		String directory = filePath + fileName;

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			File file = new File(directory);
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.append(words);
			if (!isLastLine) {
				bw.newLine();
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
	}

	/**
	 * write index into csv file - general
	 * 
	 * @param filePath
	 * @param indexMap
	 */
	public void writeIndex(String filePath, HashMap<String, String> indexMap) {

		String fileName = Config.getSetting(Constants.CONFIG_INDEX_FILE_NAME);
		char separator = Config.getSettingChar(Constants.CONFIG_CSV_SEPARATOR);
		char quotechar = Config.getSettingChar(Constants.CONFIG_CSV_QUOTECHAR);
		this.writeIndex(filePath, fileName, indexMap, separator, quotechar);

	}

	/**
	 * write index into file -xor 
	 * @param filePath
	 * @param indexMap
	 */
	public void writeIndexXOR(String filePath, HashMap<String, String> indexMap) {

		String fileName = Config
				.getSetting(Constants.CONFIG_INDEX_FILE_NAME_XOR);
		char separator = Config.getSettingChar(Constants.CONFIG_CSV_SEPARATOR);
		char quotechar = Config.getSettingChar(Constants.CONFIG_CSV_QUOTECHAR);
		this.writeIndex(filePath, fileName, indexMap, separator, quotechar);

	}

	/**
	 * write precision statistic into file
	 * 
	 * @param filePath
	 * @param indexMap
	 */
	public void writePrecision(String filePath, HashMap<String, String> indexMap) {

		String fileName = Config
				.getSetting(Constants.CONFIG_PRECISION_RECALL_FILE_NAME);
		char separator = Config.getSettingChar(Constants.CONFIG_CSV_SEPARATOR);
		char quotechar = Config.getSettingChar(Constants.CONFIG_CSV_QUOTECHAR);
		this.writeIndex(filePath, fileName, indexMap, separator, quotechar);

	}

	/**
	 * write index into csv file
	 * 
	 * @param filePath
	 * @param fileName
	 * @param indexMap
	 * @param separator
	 * @param quotechar
	 */
	public void writeIndex(String filePath, String fileName,
			HashMap<String, String> indexMap, char separator, char quotechar) {

		String directory = filePath + fileName;

		CSVWriter csvWriter = null;
		FileWriter fw = null;

		try {
			File file = new File(directory);
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile(), true);

			csvWriter = new CSVWriter(fw, separator, quotechar);

			Iterator<Entry<String, String>> it = indexMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) it.next();
				String label = entry.getKey();
				String eid = entry.getValue();
				String[] row = new String[] { label, eid };
				csvWriter.writeNext(row);

			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (csvWriter != null)
					csvWriter.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
	}

}
