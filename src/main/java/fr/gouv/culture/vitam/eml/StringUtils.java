/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
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
package fr.gouv.culture.vitam.eml;

import java.util.HashMap;

/**
 * String utils to unencode HTML string to normal string
 * @author "Frederic Bregier"
 * 
 */
public class StringUtils {

	private StringUtils() {
	}
	
	public static enum CodeList {
		quot("\""),
		amp("&"),
		apos("'"),
		lt("<"),
		gt(">"),
		nbsp(" "),
		iexcl("¡"),
		cent("¢"),
		pound("£"),
		curren("¤"),
		yen("¥"),
		brvbar("¦"),
		sect("§"),
		uml("¨"),
		copy("©"),
		ordf("ª"),
		laquo("«"),
		not("¬"),
		shy(""),
		reg("®"),
		macr("¯"),
		deg("°"),
		plusmn("±"),
		sup2("²"),
		sup3("³"),
		acute("´"),
		micro("µ"),
		para("¶"),
		middot("·"),
		cedil("¸"),
		sup1("¹"),
		ordm("º"),
		raquo("»"),
		frac14("¼"),
		frac12("½"),
		frac34("¾"),
		iquest("¿"),
		Agrave("À"),
		Aacute("Á"),
		Acirc("Â"),
		Atilde("Ã"),
		Auml("Ä"),
		Aring("Å"),
		AElig("Æ"),
		Ccedil("Ç"),
		Egrave("È"),
		Eacute("É"),
		Ecirc("Ê"),
		Euml("Ë"),
		Igrave("Ì"),
		Iacute("Í"),
		Icirc("Î"),
		Iuml("Ï"),
		ETH("Ð"),
		Ntilde("Ñ"),
		Ograve("Ò"),
		Oacute("Ó"),
		Ocirc("Ô"),
		Otilde("Õ"),
		Ouml("Ö"),
		times("×"),
		Oslash("Ø"),
		Ugrave("Ù"),
		Uacute("Ú"),
		Ucirc("Û"),
		Uuml("Ü"),
		Yacute("Ý"),
		THORN("Þ"),
		szlig("ß"),
		agrave("à"),
		aacute("á"),
		acirc("â"),
		atilde("ã"),
		auml("ä"),
		aring("å"),
		aelig("æ"),
		ccedil("ç"),
		egrave("è"),
		eacute("é"),
		ecirc("ê"),
		euml("ë"),
		igrave("ì"),
		iacute("í"),
		icirc("î"),
		iuml("ï"),
		eth("ð"),
		ntilde("ñ"),
		ograve("ò"),
		oacute("ó"),
		ocirc("ô"),
		otilde("õ"),
		ouml("ö"),
		divide("÷"),
		oslash("ø"),
		ugrave("ù"),
		uacute("ú"),
		ucirc("û"),
		uuml("ü"),
		yacute("ý"),
		thorn("þ"),
		yuml("ÿ"),
		OElig("Œ"),
		oelig("œ"),
		Scaron("Š"),
		scaron("š"),
		Yuml("Ÿ"),
		fnof("ƒ"),
		circ("ˆ"),
		tilde("˜"),
		Alpha("Α"),
		Beta("Β"),
		Gamma("Γ"),
		Delta("Δ"),
		Epsilon("Ε"),
		Zeta("Ζ"),
		Eta("Η"),
		Theta("Θ"),
		Iota("Ι"),
		Kappa("Κ"),
		Lambda("Λ"),
		Mu("Μ"),
		Nu("Ν"),
		Xi("Ξ"),
		Omicron("Ο"),
		Pi("Π"),
		Rho("Ρ"),
		Sigma("Σ"),
		Tau("Τ"),
		Upsilon("Υ"),
		Phi("Φ"),
		Chi("Χ"),
		Psi("Ψ"),
		Omega("Ω"),
		alpha("α"),
		beta("β"),
		gamma("γ"),
		delta("δ"),
		epsilon("ε"),
		zeta("ζ"),
		eta("η"),
		theta("θ"),
		iota("ι"),
		kappa("κ"),
		lambda("λ"),
		mu("μ"),
		nu("ν"),
		xi("ξ"),
		omicron("ο"),
		pi("π"),
		rho("ρ"),
		sigmaf("ς"),
		sigma("σ"),
		tau("τ"),
		upsilon("υ"),
		phi("φ"),
		chi("χ"),
		psi("ψ"),
		omega("ω"),
		thetasym("ϑ"),
		upsih("ϒ"),
		piv("ϖ"),
		ensp(" "),
		emsp(" "),
		thinsp(" "),
		ndash("–"),
		mdash("—"),
		lsquo("‘"),
		rsquo("’"),
		sbquo("‚"),
		ldquo("“"),
		rdquo("”"),
		bdquo("„"),
		dagger("†"),
		Dagger("‡"),
		bull("•"),
		hellip("…"),
		permil("‰"),
		prime("′"),
		Prime("″"),
		lsaquo("‹"),
		rsaquo("›"),
		oline("‾"),
		frasl("⁄"),
		euro("€"),
		image("ℑ"),
		weierp("℘"),
		real("ℜ"),
		trade("™"),
		alefsym("ℵ"),
		larr("←"),
		uarr("↑"),
		rarr("→"),
		darr("↓"),
		harr("↔"),
		crarr("↵"),
		lArr("⇐"),
		uArr("⇑"),
		rArr("⇒"),
		dArr("⇓"),
		hArr("⇔"),
		forall("∀"),
		part("∂"),
		exist("∃"),
		empty("∅"),
		nabla("∇"),
		isin("∈"),
		notin("∉"),
		ni("∋"),
		prod("∏"),
		sum("∑"),
		minus("−"),
		lowast("∗"),
		radic("√"),
		prop("∝"),
		infin("∞"),
		ang("∠"),
		and("∧"),
		or("∨"),
		cap("∩"),
		cup("∪"),
//		int("∫"),
		there4("∴"),
		sim("∼"),
		cong("≅"),
		asymp("≈"),
		ne("≠"),
		equiv("≡"),
		le("≤"),
		ge("≥"),
		sub("⊂"),
		sup("⊃"),
		nsub("⊄"),
		sube("⊆"),
		supe("⊇"),
		oplus("⊕"),
		otimes("⊗"),
		perp("⊥"),
		sdot("⋅"),
//		lceil(""),
//		rceil(""),
//		lfloor(""),
//		rfloor(""),
		lang("〈"),
		rang("〉"),
		loz("◊"),
		spades("♠"),
		clubs("♣"),
		hearts("♥"),
		diams("♦");
		
		String result;
		private CodeList(String result) {
			this.result = result;
		}

	}

	public static enum FilteredCodeList {
		quot(" "),
		amp(" "),
		apos(" "),
		lt(" "),
		gt(" "),
		nbsp(" "),
		iexcl(" "),
		cent(" "),
		pound(" "),
		curren(" "),
		yen(" "),
		brvbar(" "),
		sect(" "),
		uml(" "),
		copy(" "),
		ordf(" "),
		laquo(" "),
		not(" "),
		shy(" "),
		reg(" "),
		macr(" "),
		deg(" "),
		plusmn(" "),
		sup2(" "),
		sup3(" "),
		acute(" "),
		micro(" "),
		para(" "),
		middot(" "),
		cedil(" "),
		sup1(" "),
		ordm(" "),
		raquo(" "),
		frac14(" "),
		frac12(" "),
		frac34(" "),
		iquest(" "),
		Agrave("À"),
		Aacute("Á"),
		Acirc("Â"),
		Atilde("Ã"),
		Auml("Ä"),
		Aring("Å"),
		AElig("Æ"),
		Ccedil("Ç"),
		Egrave("È"),
		Eacute("É"),
		Ecirc("Ê"),
		Euml("Ë"),
		Igrave("Ì"),
		Iacute("Í"),
		Icirc("Î"),
		Iuml("Ï"),
		ETH(" "),
		Ntilde("Ñ"),
		Ograve("Ò"),
		Oacute("Ó"),
		Ocirc("Ô"),
		Otilde("Õ"),
		Ouml("Ö"),
		times("×"),
		Oslash("Ø"),
		Ugrave("Ù"),
		Uacute("Ú"),
		Ucirc("Û"),
		Uuml("Ü"),
		Yacute("Ý"),
		THORN(" "),
		szlig("ß"),
		agrave("à"),
		aacute("á"),
		acirc("â"),
		atilde("ã"),
		auml("ä"),
		aring("å"),
		aelig("æ"),
		ccedil("ç"),
		egrave("è"),
		eacute("é"),
		ecirc("ê"),
		euml("ë"),
		igrave("ì"),
		iacute("í"),
		icirc("î"),
		iuml("ï"),
		eth("ð"),
		ntilde("ñ"),
		ograve("ò"),
		oacute("ó"),
		ocirc("ô"),
		otilde("õ"),
		ouml("ö"),
		divide("÷"),
		oslash("ø"),
		ugrave("ù"),
		uacute("ú"),
		ucirc("û"),
		uuml("ü"),
		yacute("ý"),
		thorn(" "),
		yuml("ÿ"),
		OElig("Œ"),
		oelig("œ"),
		Scaron("Š"),
		scaron("š"),
		Yuml("Ÿ"),
		fnof(" "),
		circ(" "),
		tilde(" "),
		Alpha("Α"),
		Beta("Β"),
		Gamma("Γ"),
		Delta("Δ"),
		Epsilon("Ε"),
		Zeta("Ζ"),
		Eta("Η"),
		Theta("Θ"),
		Iota("Ι"),
		Kappa("Κ"),
		Lambda("Λ"),
		Mu("Μ"),
		Nu("Ν"),
		Xi("Ξ"),
		Omicron("Ο"),
		Pi("Π"),
		Rho("Ρ"),
		Sigma("Σ"),
		Tau("Τ"),
		Upsilon("Υ"),
		Phi("Φ"),
		Chi("Χ"),
		Psi("Ψ"),
		Omega("Ω"),
		alpha("α"),
		beta("β"),
		gamma("γ"),
		delta("δ"),
		epsilon("ε"),
		zeta("ζ"),
		eta("η"),
		theta("θ"),
		iota("ι"),
		kappa("κ"),
		lambda("λ"),
		mu("μ"),
		nu("ν"),
		xi("ξ"),
		omicron("ο"),
		pi("π"),
		rho("ρ"),
		sigmaf("ς"),
		sigma("σ"),
		tau("τ"),
		upsilon("υ"),
		phi("φ"),
		chi("χ"),
		psi("ψ"),
		omega("ω"),
		thetasym("ϑ"),
		upsih("ϒ"),
		piv("ϖ"),
		ensp(" "),
		emsp(" "),
		thinsp(" "),
		ndash("–"),
		mdash("—"),
		lsquo(" "),
		rsquo(" "),
		sbquo(" "),
		ldquo(" "),
		rdquo(" "),
		bdquo(" "),
		dagger(" "),
		Dagger(" "),
		bull(" "),
		hellip(" "),
		permil(" "),
		prime(" "),
		Prime(" "),
		lsaquo(" "),
		rsaquo(" "),
		oline(" "),
		frasl(" "),
		euro("€"),
		image(" "),
		weierp(" "),
		real(" "),
		trade(" "),
		alefsym(" "),
		larr(" "),
		uarr(" "),
		rarr(" "),
		darr(" "),
		harr(" "),
		crarr(" "),
		lArr(" "),
		uArr(" "),
		rArr(" "),
		dArr(" "),
		hArr(" "),
		forall(" "),
		part(" "),
		exist(" "),
		empty(" "),
		nabla(" "),
		isin(" "),
		notin(" "),
		ni(" "),
		prod(" "),
		sum(" "),
		minus("-"),
		lowast(" "),
		radic(" "),
		prop(" "),
		infin(" "),
		ang(" "),
		and(" "),
		or(" "),
		cap(" "),
		cup(" "),
//		int("∫"),
		there4(" "),
		sim(" "),
		cong(" "),
		asymp(" "),
		ne(" "),
		equiv(" "),
		le(" "),
		ge(" "),
		sub(" "),
		sup(" "),
		nsub(" "),
		sube(" "),
		supe(" "),
		oplus(" "),
		otimes(" "),
		perp(" "),
		sdot(" "),
//		lceil(""),
//		rceil(""),
//		lfloor(""),
//		rfloor(""),
		lang(" "),
		rang(" "),
		loz(" "),
		spades(" "),
		clubs(" "),
		hearts(" "),
		diams(" ");
		
		String result;
		private FilteredCodeList(String result) {
			this.result = result;
		}

	}

	private static HashMap<String, String> htmlEntities;
	private static HashMap<String, String> htmlFilteredEntities;
	static {
		htmlEntities = new HashMap<String, String>();
		for (CodeList code : CodeList.values()) {
			htmlEntities.put("&"+code.name()+";", code.result);
		}
		htmlFilteredEntities = new HashMap<String, String>();
		for (FilteredCodeList code : FilteredCodeList.values()) {
			htmlFilteredEntities.put("&"+code.name()+";", code.result);
		}
	}

	public static final String unescapeHTML(String source, boolean filtered, boolean cleaned) {
		int i, j;

		StringBuilder builder = new StringBuilder();
		boolean continueLoop;
		int skip = 0;
		HashMap<String, String> filter = htmlEntities;
		if (filtered) {
			filter = htmlFilteredEntities;
		}
		do {
			continueLoop = false;
			i = source.indexOf("&", skip);
			if (i > -1) {
				j = source.indexOf(";", i+1);
				if (j > i) {
					String entityToLookFor = source.substring(i, j + 1);
					String value = filter.get(entityToLookFor);
					if (skip < i) {
						builder.append(source.substring(skip, i));
					}
					if (value != null) {
						builder.append(value);
					}
					skip = j + 1;
					continueLoop = true;
				} else {
					builder.append(source.substring(skip));
				}
			} else {
				builder.append(source.substring(skip));
			}
		} while (continueLoop);
		if (cleaned) {
			return cleanString(builder.toString());
		} else {
			return cleanSpaces(builder.toString());
		}
	}

	public static final String selectChevron(String source) {
		String value = source;
		int pos = value.indexOf('<');
		if (pos >= 0) {
			int pos2 = value.indexOf('>');
			if (pos2 > 0) {
				value = value.substring(pos+1, pos2);
			}
		}
		return value;
	}

	public static final String removeChevron(String source) {
		return source.replaceAll("[\\<\\>]", "");
	}

	public static final String cleanSpaces(String source) {
		String result = source.trim();
		int len = result.length();
		while (true) {
			result = result.replace("  ", " ");
			if (result.length() == len)
				break;
			len = result.length();
		}
		return result;
	}
	public static final String cleanString(String source) {
		if (source == null || source.length() <= 1) {
			return "";
		}
		String result = source;
		result = result.replaceAll("[\\s\\d\\«\\»\\.\\,\\?\\;\\.\\:\\!\\\"\\'\\(\\[\\`\\)\\]\\=\\{\\}\\<\\>\\~\\’\\°\\‟\\/\\_]", " ");
		result = result.replaceAll("[\\.&&[^\\w]]", " ");
		// remove all at most 2 letters
		result = result.replaceAll("\\b\\S{1,2}\\b", " ");
		// remove all lower case at most 3 letters
		result = result.replaceAll("\\b[a-z]{1,3}\\b", "");
		// remove all special code
		result = result.replaceAll("\\b[\\\\\\•\\+\\-\\=\\*\\%\\€\\$\\§\\²\\-\\/\\&]\\b", "");
		result = result.replaceAll("\\s[\\\\\\•\\+\\-\\=\\*\\%\\€\\$\\§\\²\\-\\/\\&]\\s", " ");
		return cleanSpaces(result);
	}

	public static void main(String args[]) throws Exception {
		String test = "&copy; 2007  R&eacute;el Test &lt;www.test.com&gt; test@test.com";
		System.out.println(test + "\n-->\n" + unescapeHTML(test, true, false));
		System.out.println(test + "\n-->\n" + unescapeHTML(test, true, true));
		System.out.println(test + "\n-->\n" + unescapeHTML(test, false, false));

		/*
		 * output ((Windows DOS Shell): &copy; 2007 R&eacute;al Gagnon &lt;www.rgagnon.com&gt; --> ©
		 * 2007 Réal Gagnon <www.rgagnon.com>
		 */
	}
}
