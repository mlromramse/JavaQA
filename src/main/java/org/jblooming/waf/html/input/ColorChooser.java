package org.jblooming.waf.html.input;

import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.layout.HtmlColors;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 *         Date: 16-feb-2006 : 15.05.54
 */
public class ColorChooser extends JspHelper {

  public final static String COLOR_PREFIX = "CCCP";
  public final static String COLOR_POPUP_PREFIX = "CCCPP";

  public String fieldName;
  public String label;
  public String separator = "";
  public int fieldSize = 3;
  public String onSelectScript=""; // "color" variable is available for usage

  public int colorSquareSize = 20;

  /**
   * if null uses no color
   */
  public String colorSquareBorderColor = "#000000";

  public int howManyColumnsInPopup = 1;


  /**
   * list of color like "#ff6655"
   */
  public List<String> colors = new ArrayList<String>();

  public ColorChooser(String fieldName) {
    this.urlToInclude = "/commons/layout/partColorChooser.jsp";
    this.fieldName = fieldName;

  }


  public void fillColorLighter() {
    int val = 50;
    howManyColumnsInPopup = 6;
    String yellow = "#ffff00";
    String red = "#ff0000";
    String green = "#00ff00";
    String blue = "#0000ff";
    String violet = "#ff00ff";
    String black = "#000000";
    for (int i = 1; i <= 5; i++) {
      colors.add(yellow);
      colors.add(red);
      colors.add(green);
      colors.add(blue);
      colors.add(violet);
      colors.add(black);

      yellow = HtmlColors.modifyLight(yellow, val);
      red = HtmlColors.modifyLight(red, val);
      green = HtmlColors.modifyLight(green, val);
      blue = HtmlColors.modifyLight(blue, val);
      violet = HtmlColors.modifyLight(violet, val);
      black = HtmlColors.modifyLight(black, val);


    }
  }


  public void fillColorLighter(int variations, String... colorSeeds) {
    int diff = 240 / variations;
    howManyColumnsInPopup = colorSeeds.length;
    for (int i = 0; i < variations; i++) {
      for (String color : colorSeeds) {
        colors.add(HtmlColors.modifyLight(color, i * diff));
      }
    }
  }


  public void fillColorGradients(int variations) {
    int st = 255 / (variations-1);
    howManyColumnsInPopup=(variations)*(int)(Math.sqrt(variations));
    for (int r = 0; r <= 255; r += st)
      for (int g = 0; g <= 255; g += st)
        for (int b = 0; b <= 255; b += st) {
          colors.add(HtmlColors.colorToStringHex(new HtmlColors.Color(r,g,b)));
        }

  }
}
