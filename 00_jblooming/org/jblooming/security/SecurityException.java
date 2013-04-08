package org.jblooming.security;

import org.jblooming.waf.constants.SecurityConstants;


public class SecurityException extends Exception {

  public Permission p;

  public SecurityException() {
    super();
  }

  public SecurityException(String s) {
    super(s);
  }

  public SecurityException(String s, Permission p) {

    super(s+" "+p.name);
    this.p = p;
  }


  public static void throwException(boolean havePermission,Permission p) throws SecurityException {
    if (!havePermission)
      throw new SecurityException(SecurityConstants.I18N_PERMISSION_LACKING,p);
  }

}
