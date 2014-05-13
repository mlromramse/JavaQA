package org.jblooming.waf.html.container;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.ontology.LoggableIdentifiableSupport;
import org.jblooming.waf.html.button.ButtonSupport;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.core.JspIncluder;
import org.jblooming.waf.html.display.Img;

import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.LinkedList;


public class ButtonBar extends JspHelper {
  public LinkedList<JspIncluder> buttonList = new LinkedList<JspIncluder>();
  public String align = "right";
  public String buttonAreaHtmlClass = "buttonArea";
  public String spacing = "2";
  public String padding = "0";
  public LoggableIdentifiableSupport loggableIdentifiableSupport = null;

  private static final int LEFT = 0;
  private static final int RIGHT = 1;


  public ButtonBar() {
    super("/commons/layout/partButtonBar.jsp");
  }

  public void addButton(JspIncluder button) {
    addToRight(button);
  }


  public Separator addSeparator(int width) {
    Separator sep = new Separator();
    sep.width = width;
    buttonList.add(sep);
    return sep;
  }

  public void addLabel(String label) {
    Label l = new Label();
    l.label = label;
    buttonList.add(l);
  }

  public class Label extends JspHelper {
    public String label;

    public void toHtml(PageContext pageContext) {
      try {
        pageContext.getOut().write(label);
      } catch (IOException e) {
        throw new PlatformRuntimeException(e);
      }
    }
  }

  public static class Separator extends JspHelper {
    public int width = 5;
    public int height = 1;

    public void toHtml(PageContext pageContext) {
      Img.imgSpacer(width, height, pageContext);
    }
  }

  public void insertToTheLeftOf(String buttonLabel, JspIncluder whatToInsert) {
    insertNextTo(buttonLabel, whatToInsert, LEFT);
  }

  public void insertToTheRightOf(String buttonLabel, JspIncluder whatToInsert) {
    insertNextTo(buttonLabel, whatToInsert, RIGHT);
  }


  private void insertNextTo(String buttonLabel, JspIncluder whatToInsert, int where) {
    int pos = 0;
    for (JspIncluder jspi : buttonList) {
      if (jspi instanceof ButtonSupport) {
        ButtonSupport but = (ButtonSupport) jspi;
        if (buttonLabel.equalsIgnoreCase(but.label)) {
          buttonList.add(pos + where, whatToInsert);
        }
      }
      pos++;
    }
  }

  public void addToLeft(JspIncluder button) {
    buttonList.add(0, button);
  }

  public void addToRight(JspIncluder button) {
    buttonList.add(button);
  }


}

