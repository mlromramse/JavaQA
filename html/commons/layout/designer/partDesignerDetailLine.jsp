<%@ page import="org.jblooming.designer.DesignerField,
                 org.jblooming.designer.Detail,
                 org.jblooming.ontology.Identifiable,
                 org.jblooming.ontology.LookupSupport,
                 org.jblooming.utilities.ReflectionUtilities,
                 org.jblooming.waf.html.button.ButtonImg,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.DateField,
                 org.jblooming.waf.html.input.SmartCombo,
                 org.jblooming.waf.view.PageState,
                 java.util.Date,
                 java.util.List, org.jblooming.waf.settings.I18n"%>
<%
  PageState pageState = PageState.getCurrentPageState();
  String detailName = request.getParameter("detailName");
  Detail detail = (Detail) pageState.getSessionState().getAttributes().get(detailName);
  String rowLine = request.getParameter("rowLine");
  int id;
  if ("ADD_LINE".equals(pageState.getCommand())) {
    id = detail.maxLine + 1;
    detail.maxLine = id;
  } else
    id = Integer.parseInt(rowLine);
  if (detail.detailDesignerFields != null && detail.detailDesignerFields.size() > 0) {
    %><tr id="TR_<%=detailName+'_'+id%>"><%
    for (DesignerField df : detail.detailDesignerFields.values()) {
      Class type = Class.forName(df.kind);
      List classes = ReflectionUtilities.getInheritedClasses(type);
      if (classes.contains(Date.class)) {
        pageState.initedElements.add(DateField.init);
      } else      if (classes.contains(LookupSupport.class) || classes.contains(Identifiable.class) || df.smartCombo != null) {
        pageState.initedElements.add(SmartCombo.class.getName());
      }
      df.label = "";
      df.separator = "";
      String origName = df.name;
      df.name = detail.name + "_" + df.name + "_" + id;
      %><td><%df.toHtml(pageContext);%></td><%
      df.name = origName;


    }
  }
  ButtonJS bs = new ButtonJS();
  bs.confirmQuestion = I18n.get("PROCEED_LINE_DELETION");
  bs.onClickScript = "$('#TR_"+detailName+"_"+rowLine+"').remove();";
  bs.confirmRequire = detail.confirmRequire;
  Img img= new Img(pageState.getSkin().imgPath + "list/del.gif", "" + id);
  ButtonImg bi = new ButtonImg(bs,img);
%><td><%bi.toHtml(pageContext);%></td>