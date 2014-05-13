package org.jblooming.waf.html.button;

import java.io.Serializable;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public interface Button {
  String getId();

  String generateLaunchJs();

  String getLabel();

  String getToolTip();

  void addKeyPressControl(int keyToHandle, String launchedJs, String actionListened);

  String getActionListened();

  void setActionListened(String actionListened);

  int getKeyToHandle();

  void setKeyToHandle(int keyToHandle);

  String getLaunchedJsOnActionListened();

  void setLaunchedJsOnActionListened(String launchedJsOnActionListened);

  void setMainObjectId(Serializable id);

}
