/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Vitam Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Vitam is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Vitam. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fr.gouv.culture.vitam.extract;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import fr.gouv.culture.vitam.droid.DroidFileFormat;
import fr.gouv.culture.vitam.utils.ConfigLoader;
import fr.gouv.culture.vitam.utils.PreferencesResourceBundle;
import fr.gouv.culture.vitam.utils.StaticValues;
import fr.gouv.culture.vitam.utils.VitamArgument.VitamOutputModel;
import fr.gouv.culture.vitam.utils.XmlDom;

/**
 * Extract Information from files
 * 
 * @author "Frederic Bregier"
 * 
 */
public class ExtractInfo {
	private static PreferencesResourceBundle StopWords = null;
	public static HashSet<String> ignoreWords = getHash();
	private static HashSet<String> getHash() {
		if (StopWords == null) {
			StopWords = new PreferencesResourceBundle("resources/stopwords", Locale.getDefault());
		}
		String stopwords = StopWords.get("stopwords");
		String [] list = stopwords.split(",");
		HashSet<String> hash = new HashSet<String>(list.length);
		for (String word : list) {
			hash.add(word);
		}
		return hash;
	}
	/**
	 * Recursive checking
	 * @param file root directory or file
	 * @param basename basename that will be kept in the path
	 * @param root element where the results will be added
	 * @param config
	 * @param writer could be null
	 */
	public static void recursive(File file, String basename, Element root, 
			ConfigLoader config, XMLWriter writer) {
		if (file.isDirectory()) {
			File [] files = file.listFiles();
			if (files != null) {
				for (File file2 : files) {
					recursive(file2, basename, root, config, writer);
				}
			}
		} else {
			String name = file.getAbsolutePath();
			int index = name.indexOf(basename);
			if (index >= 0) {
				name = name.substring(index);
			}
			Element result = exportMetadata(file, name, config, writer, null);
			root.add(result);
		}
	}
	/**
	 * Helper to get from a String a list of words to search in the keywords
	 * @param source
	 * @return the list as space separator
	 */
	public static final String convertToSearchWords(String source) {
		if (source == null) {
			return "";
		}
		String result = source.replaceAll("[\\s\\d\\«\\»\\.\\,\\?\\;\\.\\:\\!\\\"\\'\\(\\[\\`\\)\\]\\=\\{\\}\\<\\>\\~\\’\\°\\‟\\/\\_]", " ");
		result = result.replaceAll("[\\.&&[^\\w]]", " ");
		// remove all at most 2 letters
		result = result.replaceAll("\\b\\S{1,2}\\b", " ");
		// remove all lower case at most 3 letters
		result = result.replaceAll("\\b[a-z]{1,3}\\b", "");
		// remove all special code
		result = result.replaceAll("\\b[\\\\\\•\\+\\-\\=\\*\\%\\€\\$\\§\\²\\-\\/\\&]\\b", "");
		result = result.replaceAll("\\s[\\\\\\•\\+\\-\\=\\*\\%\\€\\$\\§\\²\\-\\/\\&]\\s", " ");
		int len = result.length();
		while (true) {
			result = result.replace("  ", " ");
			if (result.length() == len)
				break;
			len = result.length();
		}
		String [] words = result.split(" ");
		result = null;
		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			word = word.replaceAll("[\\.&&[^\\w]]", "");
			// keep All Upper word with more than 3 chars as is
			if (word.toUpperCase().equals(word)) {
				if (word.length() > 20) {
					word = word.toLowerCase();
				}
			} else {
				word = word.toLowerCase();
			}
			if (word.length() < 3) {
				continue;
			}
			if (ignoreWords.contains(word)) {
				continue;
			}
			// try to prevent binary words
			if (word.matches(".*[aeyuioéèàâêûîôùAEYUIO]{5}.*|.*[a-z&&^aeyuioéèàâêûîôù]{8}.*")) {
				continue;
			}
			// try to go back to single
			if (word.endsWith("s")) {
				String wordwithouts = word.substring(0, word.length()-1);
				builder.append(wordwithouts);
				builder.append(' ');
			} else {
				builder.append(word);
				builder.append(' ');
			}
		}
		words = null;
		return builder.toString().trim();
	}
	
	/**
	 * Export Metadata for one file
	 * @param input file to scan
	 * @param filename filename to show
	 * @param config
	 * @param writer could be null
	 * @param format could be null
	 * @return the base element as result
	 */
	public static Element exportMetadata(File input, String filename, ConfigLoader config,
			XMLWriter writer, DroidFileFormat format) {
		char otherSeperator = File.separatorChar == '/' ? '\\' : '/';
		filename.replace(otherSeperator, File.separatorChar);
		BodyContentHandler handler = new BodyContentHandler(-1);
		Metadata metadata = new Metadata();
		Parser parser = new AutoDetectParser(new DefaultDetector());

		Element root = DocumentFactory.getInstance().createElement("keywords");
		root.addAttribute("filename", filename);

		InputStream inputStream = null;
		try {
			boolean scan = true;
			if (format != null) {
				// check according to format the interest of scanning the document
				String mimeType = format.getMimeType();
				if (mimeType.equalsIgnoreCase(DroidFileFormat.Unknown)) {
					scan = false;
				} else if (mimeType.startsWith("audio") || mimeType.startsWith("image") ||
						mimeType.startsWith("example") || mimeType.startsWith("model") ||
						mimeType.startsWith("video") || mimeType.startsWith("multipart")) {
					scan = false;
				} else if (format.getMimeType().startsWith("text")) {
					scan = true;
				} else {
					// should be an application
					if (! format.getPUID().startsWith(StaticValues.FORMAT_XFMT)) {
						// not x-fmt
						//Should be ok but will depend...
						scan = true;
					} else {
						scan = false;
					}
				}
			}
			System.out.print("Scan " + filename);
			String result = null;
			if (scan) {
				inputStream = new BufferedInputStream(new FileInputStream(input));
				try {
					parser.parse(inputStream, handler, metadata, new ParseContext());
				} catch (NoSuchMethodError e) {
					XmlDom.addDate(config.argument, config, root);
					System.out.println();
					return root;
				}
				result = handler.toString();
				handler = null;
				metadata = null;
				parser = null;
			}
			// Title of file will be considered as Main Keywords
			return exportMetadata(root, result, input.getName(), config, writer);
		} catch (Exception e) {
			XmlDom.addDate(config.argument, config, root);
			System.err.println(StaticValues.LBL.error_error.get() + " Extract: " + e.toString());
			return root;
		} finally {
			close(inputStream);
		}
	}

	/**
	 * Export Metadata for one String
	 * @param root
	 * @param words
	 * @param config
	 * @param writer could be null
	 * @return the base element as result
	 */
	public static Element exportMetadata(Element root, String words, String filename, ConfigLoader config,
			XMLWriter writer) {
		try {
			HashMap<String, Integer> references = new HashMap<String, Integer>();
			int maxRank = 0;
			if (words != null && words.length() > 1) {
				String result = words;
				// remove all non usefull string
				result = result.replaceAll("[\\s\\d\\«\\»\\.\\,\\?\\;\\.\\:\\!\\\"\\'\\(\\[\\`\\)\\]\\=\\{\\}\\<\\>\\~\\’\\°\\‟\\/\\_]", " ");
				result = result.replaceAll("[\\.&&[^\\w]]", " ");
				// remove all at most 2 letters
				result = result.replaceAll("\\b\\S{1,2}\\b", " ");
				// remove all lower case at most 3 letters
				result = result.replaceAll("\\b[a-z]{1,3}\\b", "");
				// remove all special code
				result = result.replaceAll("\\b[\\\\\\•\\+\\-\\=\\*\\%\\€\\$\\§\\²\\-\\/\\&]\\b", "");
				result = result.replaceAll("\\s[\\\\\\•\\+\\-\\=\\*\\%\\€\\$\\§\\²\\-\\/\\&]\\s", " ");
				// remove all extra spaces
				int len = result.length();
				while (true) {
					result = result.replace("  ", " ");
					if (result.length() == len)
						break;
					len = result.length();
				}
				String [] wordLists = result.split(" ");
				result = null;
				for (String word : wordLists) {
					word = word.replaceAll("[\\.&&[^\\w]]", "");
					// keep All Upper word with more than 3 chars as is
					if (word.toUpperCase().equals(word)) {
						if (word.length() > 20) {
							word = word.toLowerCase();
						}
					} else {
						word = word.toLowerCase();
					}
					if (word.length() < 3) {
						continue;
					}
					if (ignoreWords.contains(word)) {
						continue;
					}
					// try to prevent binary words
					if (word.matches(".*[aeyuioAEYUIO]{5}.*|.*[a-z&&^aeyuio]{8}.*")) {
						continue;
					}
					// try to go back to single
					if (word.endsWith("s")) {
						// add potential non plural
						String wordwithouts = word.substring(0, word.length()-1);
						if (references.containsKey(wordwithouts)) {
							Integer value = references.get(wordwithouts);
							Integer valueold = references.remove(word);
							if (valueold != null) {
								value += valueold;
							}
							references.put(wordwithouts, (value+1));
							maxRank = maxRank > value ? maxRank : value + 1;
							// not adding plural
							continue;
						} else {
							references.put(wordwithouts, 1);
							maxRank = maxRank > 1 ? maxRank : 1;
						}
					}
					// try to go back to single
					String wordwiths = word + "s";
					if (references.containsKey(wordwiths)) {
						// remove plural
						Integer value = references.remove(wordwiths);
						Integer valueold = references.get(word);
						if (valueold != null) {
							value += valueold;
						}
						references.put(word, (value+1));
						maxRank = maxRank > value ? maxRank : value + 1;
						continue;
					}
					if (references.containsKey(word)) {
						Integer value = references.get(word);
						references.put(word, (value+1));
						maxRank = maxRank > value ? maxRank : value + 1;
					} else {
						references.put(word, 1);
						maxRank = maxRank > 1 ? maxRank : 1;
					}
				}
			}
			// Title of file will be considered as Main Keywords
			String [] wordLists = convertToSearchWords(filename).split(" ");
			for (String word : wordLists) {
				if (word.trim().length() > 0) {
					references.put(word, maxRank+1);
				}
			}
			System.out.print(" Words found: " + references.size());
			HashMap<Integer, HashSet<String>> rankedValues = new HashMap<Integer, HashSet<String>>();
			TreeSet<Integer> sortRanks = new TreeSet<Integer>();
			for (String word : references.keySet()) {
				Integer rank = references.get(word);
				HashSet<String> wordset = rankedValues.get(rank);
				if (wordset == null) {
					wordset = new HashSet<String>();
				}
				wordset.add(word);
				rankedValues.put(rank, wordset);
				sortRanks.add(rank);
			}
			System.out.print(" Ranks found: " + rankedValues.size());
			Iterator<Integer> iterator = sortRanks.descendingIterator();
			int foundRank = 0;
			int limitword = 0;
			while (iterator.hasNext()) {
				Integer integer = iterator.next();
				// minimum 5 levels + rankLimit
				if (foundRank > 5 && config.rankLimit > integer) {
					break;
				}
				foundRank++;
				Element rankedElement = DocumentFactory.getInstance().createElement("rank");
				rankedElement.addAttribute("occur", integer.toString());
				for (String word : rankedValues.get(integer)) {
					Element wordElement = DocumentFactory.getInstance().createElement("word");
					wordElement.addAttribute("value", word);
					rankedElement.add(wordElement);
					limitword++;
				}
				XmlDom.addElement(writer, config.argument, root, rankedElement);
				if (config.wordLimit > 0 && limitword > config.wordLimit) {
					break;
				}
			}
			System.out.println(" Word kept: " + limitword + " Ranks kept: " + foundRank);
			rankedValues.clear();
			sortRanks.clear();
			rankedValues = null;
			sortRanks = null;
		} catch (Exception e) {
			XmlDom.addDate(config.argument, config, root);
			System.err.println(StaticValues.LBL.error_error.get() + " Extract: " + e.toString());
			return root;
		}
		XmlDom.addDate(config.argument, config, root);
		return root;
	}

	private static void close(Closeable... closeables) {
		for (Closeable c : closeables) {
			if (c != null) {
				try {

					if (c instanceof Flushable) {
						Flushable f = (Flushable) c;
						f.flush();
					}

					c.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}


	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("need file as target");
			return;
		}
		StaticValues.initialize();
		Document document = 
				DocumentFactory.getInstance().createDocument(StaticValues.CURRENT_OUTPUT_ENCODING);
		Element root = DocumentFactory.getInstance().createElement("extractkeywords");
		document.add(root);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(StaticValues.CURRENT_OUTPUT_ENCODING);
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(System.out, format);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return;
		}
		for (String string : args) {
			File file = new File(string);
			recursive(file, string, root, StaticValues.config, writer);
		}
		if (StaticValues.config.argument.outputModel == VitamOutputModel.OneXML) {
			try {
				writer.write(document);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
