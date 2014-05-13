package org.jblooming.utilities;

import org.jblooming.waf.settings.ApplicationState;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * @author pietro polsinelli info@twproject.com
 */
public class NumberUtilities {

  static String[] units = // Units 0 to 9
          {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

  static String[] tens = // Tens 0 to 90
          {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};

  static String[] hundreds = // Hundreds 0 to 900
          {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};

  static String[] thousands = // Thousands 0 to 3000
          {"", "M", "MM", "MMM"};
  public static String DEFAULT_CURRENCY_FORMAT = "###,##0.00";

  public static int DEFAULT_DECIMAL_PLACES = 2;

  /**
   * @param n
   */
  public static String intToRoman(int n) {
    return thousands[(n / 1000)] +
            hundreds[(n / 100) % 10] +
            tens[(n / 10) % 10] +
            units[(n) % 10];
  }


  /**
   * Returns the integer value of the given string of Roman numerals.
   *
   * @param roman the string of Roman numbers
   * @return the integer value
   */
  public static int romanToInt(String roman) throws NumberFormatException {
    int value = 0;
    if (roman != null) {
      for (int p = 0; p < roman.length(); p++) {
        char c = roman.charAt(p);
        if (c == 'X') {
          value += 10;
        } else if (c == 'V') {
          value += 5;
        } else if (c == 'I') {
          if (p + 1 < roman.length()) {
            char p1 = roman.charAt(p + 1);
            if (p1 == 'V') {
              value += 4;
              p++;
            } else if (p1 == 'X') {
              value += 9;
              p++;
            } else {
              value += 1;
            }
          } else {
            value += 1;
          }
        } else
          throw new NumberFormatException("Not a roman character: " + p);
      }
    }
    return value;
  }

  public static String currency(Number amount) {
    DecimalFormat format = new DecimalFormat(DEFAULT_CURRENCY_FORMAT);//,dfs);
    return format.format(amount);
  }

  public static String decimal(Number amount) {
    return decimal(amount, DEFAULT_DECIMAL_PLACES);
  }

  public static String decimal(Number amount, int decimalPlaces) {

    NumberFormat numberInstance = NumberFormat.getNumberInstance(ApplicationState.SYSTEM_LOCALE);
    numberInstance.setMaximumFractionDigits(decimalPlaces);
    numberInstance.setMinimumFractionDigits(decimalPlaces);
    return numberInstance.format(amount);
  }

  public static String decimalNoGrouping(Number amount) {
    return decimalNoGrouping(amount, DEFAULT_DECIMAL_PLACES);
  }

  public static String decimalNoGrouping(Number amount, int decimalPlaces) {
    NumberFormat numberInstance = NumberFormat.getNumberInstance(ApplicationState.SYSTEM_LOCALE);
    numberInstance.setGroupingUsed(false);
    numberInstance.setMaximumFractionDigits(decimalPlaces);
    numberInstance.setMinimumFractionDigits(decimalPlaces);
    return numberInstance.format(amount);
  }

  public static String padd(String original, int lenght, String padder) {
    if (original.length() < lenght) original = padder + original;
    if (original.length() < lenght)
      return padd(original, lenght, padder);
    else
      return original;
  }

  public static double parseCurrency(String currency) throws ParseException {
    DecimalFormat format = new DecimalFormat(DEFAULT_CURRENCY_FORMAT);
    double v = format.parse(currency).doubleValue();
    return v;
  }
}
