package com.QA.waf;

import org.jblooming.waf.ScreenArea;
import org.jblooming.waf.ScreenBasic;

public class QAScreenApp extends ScreenBasic {

  public boolean hasRightColumn = true;

  public QAScreenApp(){
    super();
  }

  public QAScreenApp(ScreenArea body){
    super(body);
    initialize(body);
  }

 public void initialize(ScreenArea body) {
    setBody(body);
    urlToInclude = "/applications/QA/screens/mpScreenApp.jsp";
  }
}