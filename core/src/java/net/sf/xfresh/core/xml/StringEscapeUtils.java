package net.sf.xfresh.core.xml;

/**
 * Author: Alexander Astakhov (alalast@yandex.ru)
 * Date: 02.01.11 1:38
 */
class StringEscapeUtils {

    private static final double GROW_FACTOR = 1.1;

    /**
     * I have to do it myself because jakarta-commons-lang does something like this
     * <pre>
     * if (ch > 0x7F) {
     *     int intValue = ch;
     *     buf.append("&#");
     *     buf.append(intValue);
     *     buf.append(';');
     * }</pre>
     * It works but increases documents size for "non-latin" documents even if they use native encoding.
     */
    public static String escapeXml(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder((int) (str.length() * GROW_FACTOR));

        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);

            //Filter restricted chars: [#x1-#x8] | [#xB-#xC] | [#xE-#x1F] | [#x7F-#x84] | [#x86-#x9F]
            if ((0x1 <= c && c <= 0x8) || (0xB <= c && c <= 0xC) || (0xE <= c && c <= 0x1F) || (0x7F <= c && c <= 0x84) || (0x86 <= c && c <= 0x9F)) {
                continue;
            }
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }
}

