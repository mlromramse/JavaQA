/*
 * Created by Roberto Bicchierai and Pietro Polsinelli.
 * User: Pietro Polsinelli
 * Date: Jul 8, 2002
 * Time: 10:18:53 AM
 */
package org.jblooming.waf.constants;

public interface SecuredNodeConstants {

  String SECUREDNODE = "SECUREDNODE";

// form fields
  String FLD_NODE_ID = Fields.FORM_PREFIX + SECUREDNODE + "NODE_ID";
  String FLD_TYPE = Fields.FORM_PREFIX + SECUREDNODE + "TYPE";
  String FLD_AGG_DESCENDANTS = Fields.FORM_PREFIX + SECUREDNODE + "FLD_AGG_DESCENDANDS";
  String FLD_AGG_ANCESTORS = Fields.FORM_PREFIX + SECUREDNODE + "FLD_AGG_ANCESTORS";
  String FLD_NAME = Fields.FORM_PREFIX + SECUREDNODE + "FLD_NAME";

// commands
// session scoped
// i18n
  String I18N_WORKGROUP =  SECUREDNODE + "WORKGROUP";
  String I18N_IN_WORKGROUP = SECUREDNODE + "IN_WORKGROUP";
  String I18N_OUT_WORKGROUP =  SECUREDNODE + "OUT_WORKGROUP";
  String I18N_AGGREGATED =  SECUREDNODE + "AGGREGATED";
  String I18N_CANDIDATES =  SECUREDNODE + "CANDIDATES";
  String I18N_WORKGROUPS =  SECUREDNODE + "WORKGROUPS";

// configured urls
  String ROOT_WORKGROUP = SECUREDNODE + "ROOT_WORKGROUP";

// errors
// other constants

}
