package com.sysystem.ecommerce.service;

import static com.ibm.icu.lang.UCharacter.EastAsianWidth.*;

import java.util.stream.Collectors;

import com.ibm.icu.lang.CharacterProperties;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.util.CodePointMap;

/**
 * CleanseString Class 全角英数字を半角へ変更するサービスクラス
 * 
 * @author NyiNyiHtun
 */
public class CleanseString {

	private static CodePointMap eastAsianWidthMap = CharacterProperties.getIntPropertyMap(UProperty.EAST_ASIAN_WIDTH);

	public static String apply(String text) {

		String cleansedText = "";
		cleansedText = text.chars().map(CleanseString::convertToHalfWidth).mapToObj(c -> String.valueOf((char) c))
				.collect(Collectors.joining());

		return cleansedText;
	}

	public static char convertToHalfWidth(int character) {

		int unicodeWidth = eastAsianWidthMap.get(character);
		if (unicodeWidth != NARROW && unicodeWidth != HALFWIDTH) {
			// cleanse
			// check if character code is in full width number/alphabet code range
			if ((character >= 65296 && character <= 65305) || (character >= 65313 && character <= 65338)
					|| (character >= 65345 && character <= 65370)) {
				character = character - 65248;
			}
		}
		return (char) character;
	}

}
