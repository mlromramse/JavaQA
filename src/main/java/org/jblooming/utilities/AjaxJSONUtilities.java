package org.jblooming.utilities;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Federico Soldani - fsoldani@open-lab.com
 *         Date: 27-nov-2009
 *         Time: 11.33.08
 */
public class AjaxJSONUtilities {

  private static String JSON_OBJ_REQ = "jsonObjReq";

  public static String ELEMENT_SUCCESS = "success";
  public static String ELEMENT_ERROR_MESSAGE = "errorMessage";
  public static String ELEMENT_HTML = "html";
  public static String ELEMENT_CALLBACK = "callback";

  public static JSONObject initializeJSON() {
    JSONObject resp = new JSONObject();

    setSuccessToObj(resp, false);
    setErrorMessageToObj(resp, "");
    setHtmlToObj(resp, "");
    setCallbackToObj(resp, "");

    return resp;
  }

  public static void jsonifyResponse(JSONObject resp, HttpServletResponse response) throws IOException {
    HttpUtilities.jsonifyResponse(resp, response);
  }

  public static void finallyJSONResponse(String errorMessage, HttpServletResponse response) throws IOException {
    response.setHeader("Cache-Control", "no-cache");
    response.setContentType("application/json; charset=utf-8");
    response.getWriter().write("{\"success\":false,\"errorMessage\":\""+JSP.javascriptEncode(errorMessage)+"\",\"html\":\"\",\"callback\":\"\"}");
  }

  public static JSONObject setObjectOnRequest(JSONObject resp, HttpServletRequest request) {
    request.setAttribute(JSON_OBJ_REQ, resp);
    return resp;
  }

  public static JSONObject getObjectFromRequest(HttpServletRequest request) {
    JSONObject resp;
    if(null != request.getAttribute(JSON_OBJ_REQ)) {
      resp = (JSONObject)request.getAttribute(JSON_OBJ_REQ);
    } else {
      resp = initializeJSON();
    }
    return resp;
  }

  public static void setCallbackToObj(JSONObject jso, String callback) {
    jso.element(ELEMENT_CALLBACK, callback);
  }
  public static void setSuccessToObj(JSONObject jso, boolean success) {
    jso.element(ELEMENT_SUCCESS, success);
  }
  public static void setHtmlToObj(JSONObject jso, String html) {
    jso.element(ELEMENT_HTML, html);
  }
  public static void setErrorMessageToObj(JSONObject jso, String errorMessage) {
    jso.element(ELEMENT_ERROR_MESSAGE, errorMessage);
  }
}
