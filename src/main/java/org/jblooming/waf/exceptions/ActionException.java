package org.jblooming.waf.exceptions;


import org.jblooming.waf.view.PageState;

public class ActionException extends Exception {

  public PageState view;

  public ActionException() {
    super();
  }

  public ActionException(String s) {
    super(s);
  }

  public ActionException(Exception e) {
    super(e);
  }

  public ActionException(String s, PageState view) {
    super(s);
    this.view = view;
  }

  public ActionException(Exception e, PageState view) {
    super(e);
    this.view = view;
  }

}
