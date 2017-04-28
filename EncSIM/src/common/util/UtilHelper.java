package common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public final class UtilHelper {

	//private static final Log log = LogFactory.getLog(UtilHelper.class);

	/**
	 * Add quote to string for SQL statement composition.
	 * 
	 * @param str
	 *            String value
	 * @return String
	 */
	public static String sqlString(String str) {
		if ((str == null) || (str.trim() == "")) {
			return "NULL";
		} else {
			return "'" + str.replaceAll("'", "''") + "'";
		}
	}

	public static String sqlWildcardString(String str) {
		String wildcard = "%";
		if ((str == null) || (str.trim() == "")) {
			return null;
		} else {
			return wildcard + str + wildcard;
		}
	}

	/**
	 * Determine if input string variable is empty or null
	 * 
	 * @param str
	 *            String value
	 * @return {@code true} if input is empty string, {@code false} if not.
	 */
	public static boolean isEmptyString(String str) {
		if ((str == null) || (str.trim().equalsIgnoreCase("")))
			return true;
		else
			return false;
	}

	/**
	 * Return an empty string object if the input string is null or empty
	 * 
	 * @param str
	 *            String value
	 * @return an empty string if input string is empty or null, else return the
	 *         original string
	 */
	public static String emptyString(String str) {
		if ((str == null) || (str.trim().equalsIgnoreCase("")))
			return "";
		else
			return str;
	}

	/**
	 * Encode HTML entity characters such as "<", ">", "&", etc.
	 * 
	 * @param s
	 *            String
	 * @return Encoded string
	 */
	public static String HTMLEncode(String s) {

		char c[] = { '<', '>', '&', '\"' };
		String expansion[] = { "&lt;", "&gt;", "&amp;", "&quot;" };

		StringBuffer st = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			boolean copy = true;
			char ch = s.charAt(i);
			for (int j = 0; j < c.length; j++) {
				if (c[j] == ch) {
					st.append(expansion[j]);
					copy = false;
					break;
				}
			}
			if (copy)
				st.append(ch);
		}
		return st.toString();
	}

	/**
	 * Pad leading zeros to the given string to output at the given length.
	 * 
	 * @param s
	 *            String to be padded
	 * @param length
	 *            Length of output string
	 * @return Left zero padded string
	 */
	public static String lpadZero(String s, int len) {
		String rtn = "";

		if (!isEmptyString(s)) {
			rtn = s;
			if (rtn.length() != len) {
				int offset = len - rtn.length();
				for (int i = 0; i < offset; i++) {
					rtn = "0" + rtn;
				}
			}
		}
		return rtn;
	}

	/**
	 * Pad leading zeros to the given integer to output at the given length.
	 * 
	 * @param n
	 *            An integer
	 * @param len
	 *            Length of output string
	 * @return Left zero padded string
	 */
	public static String lpadZero(int n, int len) {
		return lpadZero(n + "", len);
	}

	/**
	 * Return the extension part of the given file name.
	 * 
	 * @param filename
	 *            String contains the file name
	 * @return Extension of the input file name
	 */
	public static String getFileExtension(String filename) {
		String rtn = "";
		if (!isEmptyString(filename)) {
			int pos = filename.lastIndexOf(".");
			rtn = filename.substring(pos + 1, filename.length());
		}
		return rtn;
	}

	/**
	 * Return the extension part of the given file name.
	 * 
	 * @param fpath
	 *            String contains the file name
	 * @return Extension of the input file name
	 */
	// public static String getFilenameFromPath(String fpath) {
	// String rtn = "";
	// if (!isEmptyString(fpath)) {
	// int pos = fpath.lastIndexOf(Constants.FILE_SEPARATOR);
	// rtn = fpath.substring(pos+1, fpath.length());
	// }
	// return rtn;
	// }

	/**
	 * Return current date time in formatted string
	 * 
	 * @param fmt
	 *            Date format string
	 * @return Current date time in formatted string
	 */
	public static String getCurDateTime(String fmt) {
		return (new SimpleDateFormat(fmt).format(new java.util.Date()));
	}

	/**
	 * Return current date time in formatted string
	 * 
	 * @return Current date time in yyyyMMddHHmmss format
	 */
	public static String getCurDateTime() {
		return getCurDateTime("yyyyMMddHHmmss");
	}

	/**
	 * Format date in dd/mm/yyyy format.
	 * 
	 * @param date
	 *            Date value to format
	 * @return Date string in dd/mm/yyyy format
	 */
	public static String formatDate(Date date) {
		return formatDate(date, "dd/MM/yyyy");
	}

	/**
	 * Get Current date in dd MMMM, yyyy format.
	 * 
	 * @return Date string in dd MMMM, yyyy format
	 */
	public static String currentDate() {
		Date date = new Date();
		DateFormat dateformat = new SimpleDateFormat("dd MMMM, yyyy");
		return dateformat.format(date);
	}

	/**
	 * Format date in specific format. If no format specified then "dd/MM/yyyy"
	 * will be used.
	 * 
	 * @param date
	 *            Date value to format
	 * @param fmt
	 *            Date format
	 * @return Date string
	 */
	public static String formatDate(Date date, String fmt) {
		if (isEmptyString(fmt))
			fmt = "dd/MM/yyyy";

		if (date == null) {
			return "";
		} else {
			return new SimpleDateFormat(fmt).format(date);
		}
	}

	/**
	 * parse Date of the given format
	 * 
	 * @param date
	 * @param fmt
	 * @return
	 */
	public static Date parseDate(String date, String fmt) {
		if (isEmptyString(fmt))
			fmt = "dd/MM/yyyy";

		if (date != null) {
			if (!date.isEmpty()) {
				try {
					return new SimpleDateFormat(fmt).parse(date);
				} catch (ParseException e) {
					//log.error(e.getMessage(), e);

				}
			}
		}
		return null;
	}

	/**
	 * parse as SQL Date of the given format
	 * 
	 * @param date
	 * @param fmt
	 * @return
	 */
	public static java.sql.Date parseSQLDate(String date, String fmt) {
		if (date == null)
			return null;
		if (date.isEmpty())
			return null;
		return new java.sql.Date(parseDate(date, fmt).getTime());
	}

	/**
	 * parse as Timestamp of the given format
	 * 
	 * @param date
	 * @param fmt
	 * @return
	 */
	public static Timestamp parseTimestamp(String date, String fmt) {
		if (isEmptyString(fmt))
			fmt = "dd/MM/yyyy hh:mm";

		if (date != null) {
			try {
				return new Timestamp(new SimpleDateFormat(fmt).parse(date).getTime());
			} catch (ParseException e) {
				//log.error(e.getMessage(), e);

			}
		}
		return null;
	}

	/**
	 * Format number currency
	 * 
	 * @param d
	 *            Number to be formatted in {@code double} type.
	 * @return Formatter currency #,##0.00
	 */
	public static String formatCurrency(double d) {
		return new DecimalFormat("#,##0.00;(#,##0.00)").format(d);
	}

	/**
	 * Format number currency
	 * 
	 * @param d
	 *            String
	 * @return Formatter currency #,##0.00, return empty String while d = 0 or
	 *         0.00
	 */
	public static String formatCurrency(String d) {
		if (d.equalsIgnoreCase("") || d.equalsIgnoreCase(null) || d.equalsIgnoreCase("0") || d.equalsIgnoreCase("0.00")) {
			return "";
		} else {
			return new DecimalFormat("#,##0.00;(#,##0.00)").format(Double.parseDouble(d));
		}
	}

	/**
	 * Delete file from specific location.
	 * 
	 * @param path
	 *            Full path of the file to be deleted.
	 */
	public static void deleteFile(String path) {

		try {
			@SuppressWarnings("unused")
			boolean rtn = false;
			File file = new File(path);
			rtn = file.delete();
			file = null;
		} catch (Exception e) {
			/* Ignore */
		}
	}

	/**
	 * Return local phone number if the input area code is null or empty
	 * 
	 * @param str1
	 *            String area code
	 * @param str2
	 *            String local phone number
	 * @return an empty string if input string is empty or null, else return the
	 *         xxx-xxxxxxxx
	 */
	public static String areaPhoneNumber(String str1, String str2) {
		if ((str1 == null) || (str1.trim().equalsIgnoreCase(""))) {
			if ((str2 == null) || (str2.trim().equalsIgnoreCase(""))) {
				return "";
			} else {
				return str2.trim();
			}
		} else {
			return str1.trim() + "-" + str2.trim();
		}
	}

	/**
	 * Format number currency
	 * 
	 * @param s
	 *            Number to be formatted in {@code double} type.
	 * @return Formatter currency #,##0
	 */
	public static String formatNumber(double s) {
		return new DecimalFormat("#,##0;(#,##0)").format(s);
	}

	/**
	 * Format number currency
	 * 
	 * @param s
	 *            Number to be formatted in {@code double} type.
	 * @return Formatter currency #,##0, return empty String while s = 0 or 0.00
	 */
	public static String formatNumber(String s) {
		if (s.equalsIgnoreCase("") || s.equalsIgnoreCase(null) || s.equalsIgnoreCase("0") || s.equalsIgnoreCase("0.00")) {
			return "";
		} else {
			return new DecimalFormat("#,##0;(#,##0)").format(Double.parseDouble(s));
		}

	}

	/**
	 * Split String currency
	 * 
	 * @param splitString
	 *            String currency
	 * @return long currency decimal place 0000.(xx)
	 */
	public static long decimalPlace(String splitString) {
		String[] s = splitString.split("\\.");
		return Long.parseLong(s[1]);

	}

	/**
	 * Split String currency
	 * 
	 * @param splitString
	 *            String currency // * @return long currency value (xxxx).00
	 */
	public static long numberOfCurrency(String splitString) {
		String[] s = splitString.split("\\.");
		return Long.parseLong(s[0]);

	}

	

	/*
	 * Date Time
	 * ----------------------------------------------------------------
	 * ----------------
	 */
	public static Timestamp getSQLTimestamp() {

		java.util.Date today = new java.util.Date();

		Timestamp sqlToday = new java.sql.Timestamp(today.getTime());

		return sqlToday;
	}

	/*
	 * De-Duplicate List
	 * --------------------------------------------------------
	 * ------------------------
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection Union(Collection coll1, Collection coll2) {
		Set union = new HashSet(coll1);
		union.addAll(new HashSet(coll2));
		return new ArrayList(union);
	}

	@SuppressWarnings("rawtypes")
	public static ArrayList GetUniqueValues(Collection values) {
		return (ArrayList) Union(values, values);
	}

	/*
	 * MD5 Encrypt
	 * --------------------------------------------------------------
	 * ------------------
	 */
	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buf.append((char) ('0' + halfbyte));
				} else {
					buf.append((char) ('a' + (halfbyte - 10)));
					halfbyte = data[i] & 0x0F;
				}
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	

	public static String int2digit(int num) {
		if ((num >= 0) && (num < 10)) {
			return "0" + String.valueOf(num);
		} else {
			return String.valueOf(num);
		}
	}

	public static String int3digit(int num) {
		if ((num >= 0) && (num < 10)) {
			return "00" + String.valueOf(num);
		} else if ((num >= 10) && (num < 100)) {
			return "0" + String.valueOf(num);
		} else {
			return String.valueOf(num);
		}
	}

	public static String int4digit(int num) {
		if ((num >= 0) && (num < 10)) {
			return "000" + String.valueOf(num);
		} else if ((num >= 10) && (num < 100)) {
			return "00" + String.valueOf(num);
		} else if ((num >= 100) && (num < 1000)) {
			return "0" + String.valueOf(num);
		} else {
			return String.valueOf(num);
		}
	}

	public static int GetYear() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int year = cal.get(java.util.Calendar.YEAR);
		return year;
	}

	public static int GetYear2digit() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int year = cal.get(java.util.Calendar.YEAR);

		return (year % 100);
	}

	public static int GetYear1digit() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int year = cal.get(java.util.Calendar.YEAR);

		return (year % 10);
	}

	public static int GetMonth() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int month = cal.get(java.util.Calendar.MONTH) + 1;

		return month;
	}

	public static int GetDay() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int day = cal.get(java.util.Calendar.DAY_OF_MONTH);

		return day;
	}

	public static String GetMonthStr(int num) {
		// java.util.Calendar cal= java.util.Calendar.getInstance();
		// int month=cal.get( java.util.Calendar.MONTH) + 1;
		String month = new String();
		if (num == 1) {
			month = "Jan";
		}
		if (num == 2) {
			month = "Feb";
		}
		if (num == 3) {
			month = "Mar";
		}
		if (num == 4) {
			month = "Apr";
		}
		if (num == 5) {
			month = "May";
		}
		if (num == 6) {
			month = "Jun";
		}
		if (num == 7) {
			month = "Jul";
		}
		if (num == 8) {
			month = "Aug";
		}
		if (num == 9) {
			month = "Sep";
		}
		if (num == 10) {
			month = "Oct";
		}
		if (num == 11) {
			month = "Nov";
		}
		if (num == 12) {
			month = "Dec";
		}

		return month;
	}

	public static String checkDecimal(String num) {
		if (num == null) {
			return "0";
		} else {
			if (num.isEmpty()) {
				return "0";
			} else {
				if ((Pattern.matches("[0-9]+", num)) || (Pattern.matches("[0-9]+\\.[0-9]+", num))) {
					return num;
				} else {
					return "0";
				}
			}
		}
	}

	public static boolean checkNum(String num) {
		if (Pattern.matches("[0-9]+", num)) {
			return true;
		} else {
			return false;
		}
	}

	// public static Date ymdToDate(String y, String m, String d) {
	// Date date = null;
	// if (checkNum(y) && checkNum(m) && checkNum(d)) {
	// date = new Date((Integer.parseInt(y) - 1900), (Integer.parseInt(m) - 1),
	// (Integer.parseInt(d)));
	// }
	// return date;
	// }

	public static Double checkDouble(Double num) {
		if (num != null) {
			return num;
		} else {
			return 0.00;
		}
	}

	public static void delete(String fileName) {
		try {
			// Construct a File object for the file to be deleted.
			File target = new File(fileName);

			if (!target.exists()) {
				System.err.println("File " + fileName + " not present to begin with!");
				return;
			}

			// Quick, now, delete it immediately:
			if (target.delete())
				System.err.println("** Deleted " + fileName + " **");
			else
				System.err.println("Failed to delete " + fileName);
		} catch (SecurityException e) {
			System.err.println("Unable to delete " + fileName + "(" + e.getMessage() + ")");
		}
	}

	public static void copy(String from_name, String to_name) throws IOException {
		File from_file = new File(from_name);
		File to_file = new File(to_name);
		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(from_file);
			to = new FileOutputStream(to_file);
			byte[] buffer = new byte[4096];
			int bytes_read;
			while ((bytes_read = from.read(buffer)) != -1)
				to.write(buffer, 0, bytes_read);
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					;
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					;
				}
		}
	}

	public static String trimRight(String s) {
		String _s = s;

		while (_s.charAt(_s.length() - 1) == ' ') {
			_s = _s.substring(0, _s.length() - 1);
		}

		return _s;
	}

	/**
	 * Return if the given timestamp is empty or null
	 * 
	 * @param tmp
	 * @return @return <code>true</code> if the timestamp is null or empty,
	 *         <code>false</code> otherwise
	 */
	public static boolean isEmpty(Timestamp tmp) {
		if (tmp == null) {
			return true;
		} else {
			if (tmp.toString().trim().equalsIgnoreCase("")) {
				return true;
			} else {
				return true;
			}
		}

	}

	public static double translateX(double imageWidth, double imageHeight, double angle) {
		double resultX = 0;
		angle = angle % 360;
		if (angle <= 90) {
			resultX = imageHeight * Math.cos(Math.toRadians(90 - angle));
		} else if (angle <= 180) {
			resultX = imageHeight * Math.cos(Math.toRadians(angle - 90)) + imageWidth * Math.cos(Math.toRadians(180 - angle));
		} else if (angle <= 270) {
			resultX = imageWidth * Math.sin(Math.toRadians(270 - angle));
		} else if (angle <= 360) {
			resultX = 0;
		}
		return resultX;
	}

	public static double translateY(double imageWidth, double imageHeight, double angle) {
		double resultY = 0;
		angle = angle % 360;
		if (angle <= 90) {
			resultY = 0;
		} else if (angle <= 180) {
			resultY = imageHeight * Math.sin(Math.toRadians(angle - 90));
		} else if (angle <= 270) {
			resultY = imageHeight * Math.cos(Math.toRadians(angle - 180)) + imageWidth * Math.cos(Math.toRadians(270 - angle));
		} else if (angle <= 360) {
			resultY = imageWidth * Math.sin(Math.toRadians(360 - angle));
		}
		return resultY;
	}

	public static String deadline() {

		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, 7);
		String deadline = String.valueOf(today.get(today.YEAR)) + "-" + String.valueOf(today.get(today.MONTH) + 1) + "-" + String.valueOf(today.get(today.DATE));
		return deadline;

	}

	public static double roundFourDecimals(double d) {
		DecimalFormat fourDForm = new DecimalFormat("#.####");
		return Double.valueOf(fourDForm.format(d));
	}

	/**
	 * Check if the password obey the password policy: <li>Simple: At least 6
	 * characters</li> <li>Complex: At least 6 characters, at least a digit, an
	 * upper-case characters, a lower-case character and a special character.</li>
	 * 
	 * 
	 * @param password
	 *            the given password string
	 * @param isComplex
	 *            flag specify where to apply complex policy
	 * @return <code>true</code> if the password obey the policy,
	 *         <code>false</code> otherwise
	 */
	// public static boolean checkPassword(String password, boolean isComplex) {
	// if (isComplex) {
	// Matcher matcher = Constants.PASSWORD_COMPLEX_PATTERN.matcher(password);
	// if (matcher == null)
	// return false;
	// return matcher.find();
	// }
	// return password.length() >= Constants.MIN_PASSWORD_LENGTH;
	// }

	/**
	 * A simple email validation
	 * 
	 * @param email
	 * @return
	 */
	public static boolean simpleEmailCheck(String email) {

		// FIXME: a better validation can be done using
		// org.apache.commons.validator.EmailValidator

		// return EmailValidator.getInstance().isValid(email);

		// Set the email pattern string
		Pattern p = Pattern.compile("^[^@]+@[^@]+\\.[a-z]+$");

		// Match the given string with the pattern
		Matcher m = p.matcher(email);

		// check whether match is found
		boolean matchFound = m.matches();

		StringTokenizer st = new StringTokenizer(email, ".");
		String lastToken = null;
		while (st.hasMoreTokens()) {
			lastToken = st.nextToken();
		}

		// FIXME: this checking originally validate the country code
		// but this checking is removed at the moment cause
		// it does not match that of the javascript email checking

		return matchFound && lastToken.length() >= 1 // 2 should be used for
														// country code mapping
				&& email.length() - 1 != lastToken.length();
	}

	public static String checkTimestamp(String date, String hour, String min) {
		if ((date != null) && (hour != null) && (min != null)) {
			if (isTimeStampValid(date + " " + hour + ":" + min + ":00")) {
				return date + " " + hour + ":" + min + ":00";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * Check if the input string is in yyyy-MM-dd HH:mm:ss format
	 * 
	 * @param inputString
	 * @return {@code true} if input in yyyy-MM-dd HH:mm:ss format,
	 *         {@code false} if not.
	 */
	public static boolean isTimeStampValid(String inputString) {
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			format.parse(inputString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * Check if the given list is null or an empty list
	 * 
	 * @param list
	 * @return <code>true</code> if the list is null or empty,
	 *         <code>false</code> otherwise
	 */
	public static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	
	/**
	 * Convert array to string, ignoring empty items default separator: comma
	 * (,)
	 * 
	 * @param strArray
	 * @return
	 */
	public static String convertArrayToString(List<String> strArray) {
		return convertArrayToString(strArray, ",");
	}

	/**
	 * Convert array to string with user-defined separator, ignoring empty items
	 * 
	 * @param strArray
	 * @return
	 */
	public static String convertArrayToString(List<String> strArray, String separator) {
		// log.info("******************** " + strArray);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strArray.size(); i++) {
			if (!UtilHelper.isEmptyString(strArray.get(i))) {
				builder.append(strArray.get(i));
				if (i < strArray.size() - 1)
					builder.append(separator).append(" ");
			}
		}
		if (builder.length() >= 2 && builder.substring(builder.length() - 2).equals(separator + " "))
			builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}

	/**
	 * Convert array to string with user-defined separator, make empty as dash
	 * 
	 * @param strArray
	 * @return
	 */
	public static String convertArrayToStringWithEmpty(List<String> strArray, String separator, String asEmpty) {
		//log.info("******************** " + strArray);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strArray.size(); i++) {
			if (!UtilHelper.isEmptyString(strArray.get(i))) {
				builder.append(strArray.get(i));
			} else {
				builder.append(asEmpty);
			}
			if (i < strArray.size() - 1)
				builder.append(separator).append(" ");
		}
		if (builder.length() >= 2 && builder.substring(builder.length() - 2).equals(separator + " "))
			builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}

	/**
	 * Convert array to string with user-defined separator, ignoring empty items
	 * 
	 * @param strArray
	 * @return
	 */
	/*public static String convertArrayToStringEscapeHtml(List<String> strArray, String separator) {
		//log.info("******************** " + strArray);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strArray.size(); i++) {
			if (!UtilHelper.isEmptyString(strArray.get(i))) {
				builder.append(escapeHtml(strArray.get(i)));
				if (i < strArray.size() - 1)
					builder.append(separator).append(" ");
			}
		}
		if (builder.length() >= 2 && builder.substring(builder.length() - 2).equals(separator + " "))
			builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}*/

	public static String convertArrayToStringinExcel(List<String> strArray) {
		if (strArray == null || strArray.size() == 0)
			return "-";

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strArray.size(); i++) {
			if (!UtilHelper.isEmptyString(strArray.get(i))) {
				builder.append(strArray.get(i));
				if (i < strArray.size() - 1)
					builder.append("\n");
			}
		}
		if (builder.length() >= 2 && builder.substring(builder.length() - 1).equals("\n"))
			builder.delete(builder.length() - 1, builder.length());
		return builder.toString();
	}

	/**
	 * If the input string is null, return empty. Otherwise, return the Original
	 * String
	 * 
	 * @param str
	 * @return
	 */
	public static String nullAsEmpty(String str) {
		if (str == null)
			return "";
		return str;
	}

	/**
	 * Check if the map is null or empty
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * Clear the time field of calendar
	 * 
	 * @param cal
	 */
	public static void clearTime(Calendar cal) {
		// Set time fields to zero
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

	}

	/**
	 * Add given number of days
	 * 
	 * @param date
	 * @param numDay
	 * @return
	 */
	public static Date addDay(Date date, int numDay) {
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(date);
		UtilHelper.clearTime(endCal);
		endCal.add(Calendar.DATE, 1);
		return endCal.getTime();
	}

	/**
	 * Get the last path without suffix. e.g., www.abcde.com/kgh/cde/user.do
	 * will return user
	 * 
	 * @param requestUri
	 * @return
	 */
	public static String getLastPathWithoutSuffix(String requestUri) {
		// String requestUri = httpRequest.getRequestURI();
		if (requestUri == null)
			return null;
		int posSlash = requestUri.lastIndexOf("/");
		String lastPath = requestUri;
		if (posSlash >= 0) {
			lastPath = requestUri.substring(posSlash + 1);
		}
		String lastPathWithoutSuffix = lastPath;
		int posDot = lastPath.lastIndexOf(".");
		if (posDot >= 0)
			lastPathWithoutSuffix = lastPath.substring(0, posDot);
		return lastPathWithoutSuffix;
	}

	/*public static String escapeHtml(String data) {
		return StringEscapeUtils.escapeHtml(data);
	}*/

	public static boolean checkPasswordComplex(String password, int length, int lengthNum, int lengthUpper, int lengthLower, int lengthSpecial) {
		boolean check = true;
		Matcher matcher = null;
		// (?=.*[\\d])(?=.*[a-z])(?=.*[A-Z])(?=.*[!~@#$%^&+=_])

		String patternReg = null;
		Pattern pattern = null;
		String result = "";

		patternReg = "^.*(?=.{" + length + ",}).*$";
		pattern = Pattern.compile(patternReg);
		matcher = pattern.matcher(password);
		if (!matcher.find()) {
			check = false;
			result+="length ";
		}

		patternReg = "^.*((.*[A-Z].*){" + lengthUpper + ",}).*$";
		pattern = Pattern.compile(patternReg);
		matcher = pattern.matcher(password);
		if (!matcher.find()) {
			check = false;
			result+="lengthUpper ";
		}
		patternReg = "^.*((.*[a-z].*){" + lengthLower + ",}).*$";
		pattern = Pattern.compile(patternReg);
		System.out.println(password);
		matcher = pattern.matcher(password);
		if (!matcher.find()) {
			check = false;
			result+="lengthLower ";
		}
		patternReg = "^.*((.*[0-9].*){" + lengthNum + ",}).*$";
		pattern = Pattern.compile(patternReg);
		matcher = pattern.matcher(password);
		if (!matcher.find()) {
			check = false;
			result+="lengthNum ";
		}
		patternReg = "^.*((.*[\\W].*){" + lengthSpecial + ",}).*$";
		pattern = Pattern.compile(patternReg);
		matcher = pattern.matcher(password);
		if (!matcher.find()) {
			check = false;
			result+="lengthSpecial ";
		}
		return check;
	}

	public static String randomPassword(int length, int lengthNum, int lengthUpper, int lengthLower, int lengthSpecial) {

		String[] NUMS = "0123456789".split("");
		String[] UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXTZ".split("");
		String[] LOWER = "abcdefghiklmnopqrstuvwxyz".split("");
		String[] SPECIAL = "~!@#$%^&*()_+".split("");

		String s = "";

		Random generator = new Random();
		for (int i = 0; i < lengthNum; i++) {
			s += NUMS[generator.nextInt(NUMS.length - 1) + 1];
		}
		for (int i = 0; i < lengthUpper; i++) {
			s += UPPER[generator.nextInt(UPPER.length - 1) + 1];
		}
		for (int i = 0; i < lengthLower; i++) {
			s += LOWER[generator.nextInt(LOWER.length - 1) + 1];
		}
		for (int i = 0; i < lengthSpecial; i++) {
			s += SPECIAL[generator.nextInt(SPECIAL.length - 1) + 1];
		}

		while (s.length() < length) {
			s += NUMS[generator.nextInt(NUMS.length - 1) + 1];
		}

		List list = new ArrayList();
		Collections.shuffle(list);
		String[] array = s.split("");
		Collections.shuffle(Arrays.asList(array));

		String result = "";
		for (String tmp : array) {
			result += tmp;
		}

		return result;

	}

	/*public static boolean sendPassword(User user, String pw, String smtpHost, String from, String subject, String msgText) throws GenericException {

		Properties params = new Configuration("mail.properties").getConfigurations();

		Mailer mailer = new Mailer();
		params.put("to", user.getEmail());
		params.put("from", from);
		params.put("subject", subject);
		params.put("msgText", msgText + "\n\n" + "Login: " + user.getLogin() + "\nPassword: " + pw);
		if (!isEmptyString(smtpHost)) {
			params.put("smtp.host", smtpHost);
		}
		return mailer.sendTextMsg(params);

	}*/

	/*public static String getIP(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}*/

	public static boolean isEmptyString(List<String> paramList) {
		boolean check = false;
		for (String str : paramList) {
			if ((str == null) || (str.trim().equalsIgnoreCase("")))
				check = true;
		}
		return check;
	}

	public static Calendar inputToCalendar(String inputDate, String hour) {
		try {
			Calendar cal = Calendar.getInstance();
			String[] startDate = inputDate.split("-");
			cal.set(Integer.valueOf(startDate[0]), Integer.valueOf(startDate[1]) - 1, Integer.valueOf(startDate[2]), Integer.valueOf(hour), 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal;
		} catch (Exception e) {
			return null;
		}
	}

	public static String calendarToDatetimeStr(Calendar cal) {
		String strdate = null;
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (cal != null) {
			strdate = format.format(cal.getTime());
			System.out.println(strdate);
		}
		return strdate;
	}

	public static String calendarToDateStr(Calendar cal) {
		String strdate = null;
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
		if (cal != null) {
			strdate = format.format(cal.getTime());
			System.out.println(strdate);
		}
		return strdate;
	}
}