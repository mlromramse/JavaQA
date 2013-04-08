package org.jblooming.waf.html.layout;

import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.utilities.CodeValueList;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Hashtable;
import java.io.File;


/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public class Skin {

  public String name;
  public String css;

  public String imgPath;
  public String imgPathPlus;

  public boolean colorsLoaded = false;
  public String COLOR_TEXT_MAIN;
  public String COLOR_TEXT_LINK;
  public String COLOR_TEXT_TITLE;
  public String COLOR_TEXT_TOOLBAR;
  public String COLOR_TEXT_TITLE01;
  public String COLOR_TEXT_TITLE02;

  public String COLOR_BACKGROUND_MAIN_CONTAINER;
  public String COLOR_BACKGROUND_MAIN;
  public String COLOR_BACKGROUND_TITLE;
  public String COLOR_BACKGROUND_TOOLBAR;
  public String COLOR_BACKGROUND_TITLE01;
  public String COLOR_BACKGROUND_TITLE02;
  public String COLOR_BACKGROUND_CONTENT;
  public String COLOR_WARNING;
  public String COLOR_FEEDBACK;

  public String COLOR_BACKGROUND_MENUBAR;
  public String COLOR_TEXT_MENUBAR;
  public String COLOR_BACKGROUND_MENU;
  public String COLOR_TEXT_MENU;

  public String COLOR_DEPTH;

  public static final String LOAD_COLORS = "LC";

}
