package org.jblooming.utilities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class HTMLEncoderOld {

  static String schr(int i) {
    return new String(new char[]{(char) i});
  }

  static String[] HTMLEncodingArray = {
    "&nbsp;", schr(1), schr(2), schr(3), schr(4), schr(5), schr(6), schr(7),
    schr(8), schr(9), schr(10), schr(11), schr(12), schr(13), schr(14), schr(15),
    schr(16), schr(17), schr(18), schr(19), schr(20), schr(21), schr(22), schr(23),
    schr(24), schr(25), schr(26), schr(27), schr(28), schr(29), schr(30), schr(31),
    schr(32), schr(33), "&quot;", schr(35), schr(36), schr(37), "&amp;", "&#39;",
    schr(40), schr(41), schr(42), schr(43), schr(44), schr(45), schr(46), schr(47),
    schr(48), schr(49), schr(50), schr(51), schr(52), schr(53), schr(54), schr(55),
    schr(56), schr(57), schr(58), schr(59), "&lt;", schr(61), "&gt;", schr(63),
    schr(64), schr(65), schr(66), schr(67), schr(68), schr(69), schr(70), schr(71),
    schr(72), schr(73), schr(74), schr(75), schr(76), schr(77), schr(78), schr(79),
    schr(80), schr(81), schr(82), schr(83), schr(84), schr(85), schr(86), schr(87),
    schr(88), schr(89), schr(90), schr(91), schr(92), schr(93), schr(94), schr(95),
    schr(96), schr(97), schr(98), schr(99), schr(100), schr(101), schr(102), schr(103),
    schr(104), schr(105), schr(106), schr(107), schr(108), schr(109), schr(110), schr(111),
    schr(112), schr(113), schr(114), schr(115), schr(116), schr(117), schr(118), schr(119),
    schr(120), schr(121), schr(122), schr(123), schr(124), schr(125), schr(126), schr(127),
    "&#8364;", schr(129), "&#8218;", "&#402;", "&#8222;", "&#8230;", "&#8224;", "&#8225;",
    "&#710;", "&#8240;", "&#352;", "&#8249;", "&#338;", schr(141), "&#381;", schr(143),
    schr(144), "&#8216;", "&#8217;", "&#8220;", "&#8221;", "&#8226;", "&#8211;", "&#8212;",
    "&#732;", "&#8482;", "&#353;", "&#8250;", "&#339;", schr(157), "&#382;", "&#376;",
    "&#160;", "&#161;", "&#162;", "&#163;", "&#164;", "&#165;", "&#166;", "&#167;",
    "&#168;", "&#169;", "&#170;", "&#171;", "&#172;", "&#173;", "&#174;", "&#175;",
    "&#176;", "&#177;", "&#178;", "&#179;", "&#180;", "&#181;", "&#182;", "&#183;",
    "&#184;", "&#185;", "&#186;", "&#187;", "&#188;", "&#189;", "&#190;", "&#191;",
    "&#192;", "&#193;", "&#194;", "&#195;", "&#196;", "&#197;", "&#198;", "&#199;",
    "&#200;", "&#201;", "&#202;", "&#203;", "&#204;", "&#205;", "&#206;", "&#207;",
    "&#208;", "&#209;", "&#210;", "&#211;", "&#212;", "&#213;", "&#214;", "&#215;",
    "&#216;", "&#217;", "&#218;", "&#219;", "&#220;", "&#221;", "&#222;", "&#223;",
    "&#224;", "&#225;", "&#226;", "&#227;", "&#228;", "&#229;", "&#230;", "&#231;",
    "&#232;", "&#233;", "&#234;", "&#235;", "&#236;", "&#237;", "&#238;", "&#239;",
    "&#240;", "&#241;", "&#242;", "&#243;", "&#244;", "&#245;", "&#246;", "&#247;",
    "&#248;", "&#249;", "&#250;", "&#251;", "&#252;", "&#253;", "&#254;", "&#255;"
  };
  static boolean[] isHTMLEncoded = {
    true, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, true, false, false, false, true, true,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, true, false, true, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false,
    true, false, true, true, true, true, true, true,
    true, true, true, true, true, false, true, false,
    false, true, true, true, true, true, true, true,
    true, true, true, true, true, false, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true,
    true, true, true, true, true, true, true, true
  };


  public static boolean isEncoded(char c) {
    if (c < 0 || c >= isHTMLEncoded.length)
      return true;
    return isHTMLEncoded[c];
  }

  public static String encode(char c) {
    if (c < 0)
      return "";
    else if (c < HTMLEncodingArray.length)
      return HTMLEncodingArray[c];
    else
      return "&" + ((int) c) + ';';
  }

  private static void encode(char c, StringBuffer sb) {
    if (c < 0)
      return;
    else if (c < HTMLEncodingArray.length)
      if (isEncoded(c))
        sb.append(HTMLEncodingArray[c]);
      else
        sb.append(c);
    else
      sb.append('&').append((int) c).append(';');
  }


  private static final String encode(String s, int start) {
    StringBuffer sb = new StringBuffer();
    if (start > 0)
      sb.append(s.substring(0, start));
    for (int i = start; i < s.length(); ++i)
      encode(s.charAt(i), sb);
    return sb.toString();
  }

  /**
   * @param s
   * @return
   * @deprecated the url escape sequences are '&lt;percent-sign&gt;xx'. No zeros are allowed before '&lt;percent-sign&gt;'
   */
  public static String mailtoEncode(String s) {
    char[] ch = s.toCharArray();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ch.length; ++i) {
      char c = ch[i];
      if (Character.isLetterOrDigit(c)) {
        sb.append(c);
      } else {
        String value = Integer.toHexString(c);
        for (int j = 0; j < (2 - value.length()); ++j)
          sb.append('0');
        if (value.length() > 2)
          value = value.substring(0, 2);
        sb.append('%').append(value);
      }
    }
    return sb.toString();
  }

  public static String urlEncode(String s) {
    if (s == null)
      return "";
    char[] ch = s.toCharArray();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ch.length; ++i) {
      char c = ch[i];
      if (Character.isLetterOrDigit(c)) {
        sb.append(c);
      } else {
        String value = Integer.toHexString(c);
        while (value.length() < 2)
          value = '0' + value;
        if (value.length() > 2)
          value = value.substring(0, 2);
        sb.append('%').append(value);
      }
    }
    return sb.toString();
  }

  /**
   * @param input
   * @return
   * @deprecated Be careful with this method: its use for sticky notes leaded to malfunctions.
   *             The solution was {@link JSP#encode(String) encode(String input)}.
   */
  public static String htmlFormattedString(String input) {

    if (input == null)
      return "";

    StringBuffer inputBuff = new StringBuffer(input);
    StringBuffer outputBuff = new StringBuffer(512);

    if (inputBuff != null && inputBuff.length() > 0) {
      int i = 0;
      while (i < inputBuff.length()) {
        //collects first occurrences of characters to be encoded
        Collection ints = new HashSet();

        int nextOccurenceOfSingleApex = (inputBuff.indexOf("'", i) > -1 ? inputBuff.indexOf("'", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfSingleApex));
        int nextOccurenceOfDoubleApex = (inputBuff.indexOf("\"", i) > -1 ? inputBuff.indexOf("\"", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfDoubleApex));
        int nextOccurenceOfCR = (inputBuff.indexOf("\r\n", i) > -1 ? inputBuff.indexOf("\r\n", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfCR));
        int nextOccurenceOfWordFirstSingleApex = (inputBuff.indexOf(schr(8216), i) > -1 ? inputBuff.indexOf(schr(8216), i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordFirstSingleApex));
        int nextOccurenceOfWordSecondSingleApex = (inputBuff.indexOf(schr(8217), i) > -1 ? inputBuff.indexOf(schr(8217), i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordSecondSingleApex));
        int nextOccurenceOfWordFirstDoubleApex = (inputBuff.indexOf(schr(8220), i) > -1 ? inputBuff.indexOf(schr(8220), i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordFirstDoubleApex));
        int nextOccurenceOfWordSecondDoubleApex = (inputBuff.indexOf(schr(8221), i) > -1 ? inputBuff.indexOf(schr(8221), i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordSecondDoubleApex));

        int cursor = getMinValue(ints);

        if (cursor == Integer.MAX_VALUE) {
          outputBuff.append(inputBuff.substring(i));
          break;

        } else {
          outputBuff.append(inputBuff.substring(i, cursor));
          i = cursor;
          if (nextOccurenceOfSingleApex == cursor)
            outputBuff.append("&rsquo;");
          else if (nextOccurenceOfDoubleApex == cursor)
            outputBuff.append("&quot;");
          else if (nextOccurenceOfWordFirstDoubleApex == cursor)
            outputBuff.append("&#8220;");
          else if (nextOccurenceOfWordSecondDoubleApex == cursor)
            outputBuff.append("&#8221;");
          else if (nextOccurenceOfWordFirstSingleApex == cursor)
            outputBuff.append("&#8216;");
          else if (nextOccurenceOfWordSecondSingleApex == cursor)
            outputBuff.append("&#8217;");
          else if (nextOccurenceOfCR == cursor) {
            outputBuff.append("<br>");
            i++;
          }
          i++;
        }
      }
    }
    return outputBuff.toString();
  }

  public static String jsFormattedString(String input) {
    if (input == null)
      return "";

    StringBuffer inputBuff = new StringBuffer(input);
    StringBuffer outputBuff = new StringBuffer(512);

    if (inputBuff != null && inputBuff.length() > 0) {
      int i = 0;
      while (i < inputBuff.length()) {
        //collects first occurrences of characters to be encoded
        Collection ints = new HashSet();

        int nextOccurenceOfSingleApex = (inputBuff.indexOf("'", i) > -1 ? inputBuff.indexOf("'", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfSingleApex));
        int nextOccurenceOfDoubleApex = (inputBuff.indexOf("\"", i) > -1 ? inputBuff.indexOf("\"", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfDoubleApex));

        int cursor = getMinValue(ints);

        if (cursor == Integer.MAX_VALUE) {
          outputBuff.append(inputBuff.substring(i));
          break;

        } else {
          outputBuff.append(inputBuff.substring(i, cursor));
          i = cursor;
          if (nextOccurenceOfSingleApex == cursor)
            outputBuff.append('\'');
          else if (nextOccurenceOfDoubleApex == cursor)
            outputBuff.append('"');
          i++;
        }
      }
    }
    return outputBuff.toString();
  }

  /**
   * input is a html encoded string; returns a decoded string
   *
   * @param input
   */
  public static String textareaFormattedString(String input) {

    if (input == null)
      return "";

    StringBuffer inputBuff = new StringBuffer(input);
    StringBuffer outputBuff = new StringBuffer(512);
    int i = 0;

    if (inputBuff != null && inputBuff.length() > 0) {

      while (i < inputBuff.length()) {
        //collects first occurrences of characters to be encoded
        Collection ints = new HashSet();

        int nextOccurenceOfWordFirstDoubleApex = (inputBuff.indexOf("&#8220;", i) > -1 ? inputBuff.indexOf("&#8220;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordFirstDoubleApex));
        int nextOccurenceOfWordSecondDoubleApex = (inputBuff.indexOf("&#8221;", i) > -1 ? inputBuff.indexOf("&#8221;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordSecondDoubleApex));
        int nextOccurenceOfCR = (inputBuff.indexOf("<br>", i) > -1 ? inputBuff.indexOf("<br>", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfCR));
        int nextOccurenceOfSingleApex = (inputBuff.indexOf("&rsquo;", i) > -1 ? inputBuff.indexOf("&rsquo;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfSingleApex));
        int nextOccurenceOfAccent = (inputBuff.toString().indexOf("&#39;", i) > -1 ? inputBuff.toString().indexOf("&#39;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfAccent));
        int nextOccurenceOfDoubleApex = (inputBuff.indexOf("&quot;", i) > -1 ? inputBuff.indexOf("&quot;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfDoubleApex));
        int nextOccurenceOfWordFirstSingleApex = (inputBuff.indexOf("&#8216;", i) > -1 ? inputBuff.indexOf("&#8216;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordFirstSingleApex));
        int nextOccurenceOfWordSecondSingleApex = (inputBuff.indexOf("&#8217;", i) > -1 ? inputBuff.indexOf("&#8217;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordSecondSingleApex));
        int nextOccurenceOfEgrave = (inputBuff.toString().indexOf("&#232;", i) > -1 ? inputBuff.toString().indexOf("&#232;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfEgrave));
        int nextOccurenceOfEacute = (inputBuff.toString().indexOf("&#233;", i) > -1 ? inputBuff.toString().indexOf("&#233;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfEacute));
        int nextOccurenceOfAgrave = (inputBuff.toString().indexOf("&#224;", i) > -1 ? inputBuff.toString().indexOf("&#224;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfAgrave));
        int nextOccurenceOfOgrave = (inputBuff.toString().indexOf("&#242;", i) > -1 ? inputBuff.toString().indexOf("&#242;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfOgrave));
        int nextOccurenceOfUgrave = (inputBuff.toString().indexOf("&#249;", i) > -1 ? inputBuff.toString().indexOf("&#249;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfUgrave));
        int nextOccurenceOfIgrave = (inputBuff.toString().indexOf("&#236;", i) > -1 ? inputBuff.toString().indexOf("&#236;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfIgrave));

        int cursor = getMinValue(ints);

        if (cursor == Integer.MAX_VALUE) {
          outputBuff.append(inputBuff.substring(i));
          break;

        } else {
          outputBuff.append(inputBuff.substring(i, cursor));
          i = cursor;
          if (nextOccurenceOfWordFirstDoubleApex == cursor) {
            outputBuff.append('"');
            i = i + 7;
          } else if (nextOccurenceOfWordSecondDoubleApex == cursor) {
            outputBuff.append('"');
            i = i + 7;
          } else if (nextOccurenceOfCR == cursor) {
            outputBuff.append("\r\n");
            i = i + 4;

          } else if (nextOccurenceOfSingleApex == cursor) {
            outputBuff.append('\'');
            i = i + 7;

          } else if (nextOccurenceOfAccent == cursor) {
            outputBuff.append('\'');
            i = i + 5;

          }   /*
          else if (nextOccurenceOfDoubleApex == cursor) {
            outputBuff.append('\"');
            i = i + 6;
          } else if (nextOccurenceOfWordFirstSingleApex == cursor) {
            outputBuff.append('\'');
            i = i + 7;
          } else if (nextOccurenceOfWordSecondSingleApex == cursor) {
            outputBuff.append('\'');
            i = i + 7;
          } else if (nextOccurenceOfEgrave == cursor) { // &egrave; &#232;
            outputBuff.append('�');
            i = i + 6;
          } else if (nextOccurenceOfEacute == cursor) {
            outputBuff.append('�');
            i = i + 6;
          } else if (nextOccurenceOfAgrave == cursor) {
            outputBuff.append('�');
            i = i + 6;
          } else if (nextOccurenceOfOgrave == cursor) {
            outputBuff.append('�');
            i = i + 6;
          } else if (nextOccurenceOfUgrave == cursor) {
            outputBuff.append('�');
            i = i + 6;
          } else if (nextOccurenceOfIgrave == cursor) {
            outputBuff.append('�');
            i = i + 6;
          }
            */
        }
      }
    }
    return outputBuff.toString();
  }

  /**
   * input is a html encoded string; returns a decoded rtf string
   *
   * @param input
   */
  public static String rtfFormattedString(String input) {

    if (input == null)
      return "";

    StringBuffer inputBuff = new StringBuffer(input);
    StringBuffer outputBuff = new StringBuffer(512);
    int i = 0;

    if (inputBuff != null && inputBuff.length() > 0) {

      while (i < inputBuff.length()) {
        //collects first occurrences of characters to be encoded
        Collection ints = new HashSet();

        int nextOccurenceOfWordFirstDoubleApex = (inputBuff.indexOf("&#8220;", i) > -1 ? inputBuff.indexOf("&#8220;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordFirstDoubleApex));
        int nextOccurenceOfWordSecondDoubleApex = (inputBuff.indexOf("&#8221;", i) > -1 ? inputBuff.indexOf("&#8221;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordSecondDoubleApex));
        int nextOccurenceOfCR = (inputBuff.indexOf("<br>", i) > -1 ? inputBuff.indexOf("<br>", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfCR));
        int nextOccurenceOfSingleApex = (inputBuff.indexOf("&rsquo;", i) > -1 ? inputBuff.indexOf("&rsquo;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfSingleApex));
        int nextOccurenceOfDoubleApex = (inputBuff.indexOf("&quot;", i) > -1 ? inputBuff.indexOf("&quot;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfDoubleApex));
        int nextOccurenceOfWordFirstSingleApex = (inputBuff.indexOf("&#8216;", i) > -1 ? inputBuff.indexOf("&#8216;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordFirstSingleApex));
        int nextOccurenceOfWordSecondSingleApex = (inputBuff.indexOf("&#8217;", i) > -1 ? inputBuff.indexOf("&#8217;", i) : Integer.MAX_VALUE);
        ints.add(new Integer(nextOccurenceOfWordSecondSingleApex));

        int cursor = getMinValue(ints);

        if (cursor == Integer.MAX_VALUE) {
          outputBuff.append(inputBuff.substring(i));
          break;

        } else {
          outputBuff.append(inputBuff.substring(i, cursor));
          i = cursor;
          if (nextOccurenceOfWordFirstDoubleApex == cursor) {
            outputBuff.append('\"');
            i = i + 7;
          } else if (nextOccurenceOfWordSecondDoubleApex == cursor) {
            outputBuff.append('\"');
            i = i + 7;
          } else if (nextOccurenceOfCR == cursor) {
            outputBuff.append("\r\n");
            i = i + 4;
          } else if (nextOccurenceOfSingleApex == cursor) {
            outputBuff.append('\'');
            i = i + 7;
          } else if (nextOccurenceOfDoubleApex == cursor) {
            outputBuff.append('\"');
            i = i + 6;
          } else if (nextOccurenceOfWordFirstSingleApex == cursor) {
            outputBuff.append('\'');
            i = i + 7;
          } else if (nextOccurenceOfWordSecondSingleApex == cursor) {
            outputBuff.append('\'');
            i = i + 7;
          }
        }
      }
    }
    return outputBuff.toString();
  }

  public static int getMinValue(Collection ints) {

    int result = Integer.MAX_VALUE;

    for (Iterator iterator = ints.iterator(); iterator.hasNext();) {
      int i = ((Integer) iterator.next()).intValue();
      if (i < result)
        result = i;
    }
    return result;
  }

  public static final String removeBadCharsFromJSConstant(String source) {
    if (source != null)
      return StringUtilities.replaceAllNoRegex(source, new String[]{"\"", "'"}, new String[]{"\\\"", "\\'"});
    else
      return "";
  }

  public static String stripHtmlTag(String html) {
    return html.replaceAll("</?[\\width]+[^<>]*>", "");
  }
}
