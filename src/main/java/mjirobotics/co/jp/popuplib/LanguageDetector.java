package mjirobotics.co.jp.popuplib;

import android.util.Log;
import java.util.ArrayList;

/**
 * Created by Andy on 15/08/2017
 */
public class LanguageDetector {

    public enum CharType {TYPE_HIRAGANA, TYPE_KATAKANA, TYPE_CHINESE, TYPE_KOREAN, TYPE_OTHER}

    /**
     * Check if a character is hiragana
     * @param c char
     * @return Returns true if a character is hiragana
     * https://stackoverflow.com/questions/3826918/how-to-classify-japanese-characters-as-either-kanji-or-kana
     */
    public static boolean isHiragana(final char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return Character.UnicodeBlock.HIRAGANA.equals(block);
    }

    /**
     * Check if a character is katakana
     * @param c char
     * @return true if a character is katakana
     */
    public static boolean isKatakana(final char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (Character.UnicodeBlock.KATAKANA.equals(block)
                || Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS.equals(block));
    }

    /**
     * Check if a char is Japanese
     * @param c char
     * @return true if a language is Japanese
     */
    public static boolean isJapanese(final char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block)
                || Character.UnicodeBlock.HIRAGANA.equals(block)
                || Character.UnicodeBlock.KATAKANA.equals(block)
                || Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS.equals(block)
                || Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS.equals(block)
                || Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION.equals(block));
    }

    /**
     * Check if a language is Korean
     * @param c char
     * @return true if a language is Korean
     */
    public static boolean isKoreanHangul(final char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (Character.UnicodeBlock.HANGUL_JAMO.equals(block) ||
                Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_A.equals(block) || // api 19
                Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_B.equals(block) || // api 19
                Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO.equals(block) ||
                Character.UnicodeBlock.HANGUL_SYLLABLES.equals(block));
    }

    /**
     * Check if a language is Englsih
     * @param c char
     * @return true if a language is English
     */
    public static boolean isEnglish(final char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return Character.UnicodeBlock.BASIC_LATIN.equals(block);
    }

    /**
     * Check for punctuation
     * @param c char
     * @return true if punctuation
     */
    public static boolean isPunctuation(final char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return Character.UnicodeBlock.GENERAL_PUNCTUATION.equals(block);
    }

    /**
     * Check for end mark char
     * @param c char
     * @return true end mark char
     */
    public static boolean isEndMark(final char c) {
        return c == '？' || c == '?' || c == '。';
    }

    /**
     * Returns true if a character is one of Chinese-Japanese-Korean characters.
     * @param c the character to be tested
     * @return true if CJK, false otherwise
     */
    public static boolean isCharCJK(final char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        if ((Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block))
                || (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(block))
                || (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B.equals(block))
                || (Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS.equals(block))
                || (Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(block))
                || (Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT.equals(block))
                || (Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION.equals(block))
                || (Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS.equals(block))) {
            return true;
        }
        return false;
    }

    /**
     * Returns type based on languages alphabet
     * @param c char
     * @return CharType
     */
    public static CharType getCharType(final char c) {
        final CharType charType;
        if(isHiragana(c) == true) {
            charType = CharType.TYPE_HIRAGANA;
        } else if(isKatakana(c) == true) {
            charType = CharType.TYPE_KATAKANA;
        } else if(isKoreanHangul(c) == true){
            charType = CharType.TYPE_KOREAN;
        } else if(isCharCJK(c) == true){
            charType = CharType.TYPE_CHINESE;
        } else {
            charType = CharType.TYPE_OTHER;
        }
        return charType;
    }
}
