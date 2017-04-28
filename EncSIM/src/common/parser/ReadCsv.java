package common.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.opencsv.CSVReader;

import common.util.Config;
import common.util.Constants;

/**
 * 
 * @author maggie liu
 * 27/4/2017
 *
 */
public class ReadCsv {

	/**
	 * Read Tweets CSV file
	 * 
	 * @param directory
	 * @param delimiter
	 * @return
	 */
	public ArrayList<String> readCsvFileTweetVector(String directory,
			char delimiter) {

		ArrayList<String> textList = new ArrayList<String>();
		File fil = new File(directory);
		FileInputStream fis = null;

		BufferedReader fr = null;
		CSVReader reader = null;
		try {

			fis = new FileInputStream(fil);

			fr = new BufferedReader(new InputStreamReader(fis));

			reader = new CSVReader(fr, delimiter, '"');
			String[] nextLine;
			int lineNum = 1;

			while ((nextLine = reader.readNext()) != null) {
				int length = nextLine.length;
				String twitterText = nextLine[length - 1];
				textList.add(twitterText);

				if (length != 6) {
					System.out.println("LineNum:" + (lineNum) + " twitterText:"
							+ twitterText + " lineLength:" + length);
					break;
				}

				if (lineNum == Config.getSettingInt(Constants.CONFIG_CANDIDATE)) {
					break;
				}

				lineNum++;
			}

			System.out.println("LineNum:" + lineNum);
			return textList;

		} catch (IOException e) {

			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
				fr.close();
				fis.close();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

	/**
	 * Read index file
	 * 
	 * @param filePath
	 * @return
	 */
	public HashMap<String, String> readCsvFileIndex(String filePath) {
		String fileName = Config.getSetting(Constants.CONFIG_INDEX_FILE_NAME);
		char separator = Config.getSettingChar(Constants.CONFIG_CSV_SEPARATOR);
		char quotechar = Config.getSettingChar(Constants.CONFIG_CSV_QUOTECHAR);
		return this.readCsvFileIndex(filePath, fileName, separator, quotechar);
	}

	/**
	 * Read index csv file
	 * 
	 * @param filePath
	 * @param fileName
	 * @param delimiter
	 * @param quotechar
	 * @return
	 */
	public HashMap<String, String> readCsvFileIndex(String filePath,
			String fileName, char delimiter, char quotechar) {

		HashMap<String, String> resultIndexMap = new HashMap<String, String>();
		String directory = filePath + fileName;

		File fil = new File(directory);
		FileInputStream fis = null;

		BufferedReader fr = null;
		CSVReader reader = null;
		try {

			fis = new FileInputStream(fil);

			fr = new BufferedReader(new InputStreamReader(fis));

			reader = new CSVReader(fr, delimiter, quotechar);
			String[] nextLine;
			int lineNum = 1;

			while ((nextLine = reader.readNext()) != null) {
				int length = nextLine.length;

				if (length != 2) {
					System.out
							.println("ERROR || LineNum:" + (lineNum)
									+ " label:" + nextLine[0] + " lineLength:"
									+ length);
					break;
				}

				String key = nextLine[0];
				String value = nextLine[1];

				resultIndexMap.put(key, value);

				lineNum++;
			}

			System.out.println("LineNum:" + lineNum);
			return resultIndexMap;

		} catch (IOException e) {

			e.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (fr != null) {
					fr.close();
				}
				if (fis != null) {
					fis.close();
				}

			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

}
