<%@ page import="org.hibernate.SessionFactory,
                 org.hibernate.engine.CascadesProxy,
                 org.hibernate.persister.collection.AbstractCollectionPersister,
                 org.hibernate.type.Type,
                 org.jblooming.ontology.Identifiable,
                 org.jblooming.ontology.Node,
                 org.jblooming.oql.OqlQuery,
                 org.jblooming.persistence.PersistenceHome,
                 org.jblooming.utilities.JSP,
                 org.jblooming.utilities.ReflectionUtilities,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.I18nConstants,
                 org.jblooming.waf.constants.ObjectEditorConstants,
                 org.jblooming.waf.constants.PlatformConstants,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.button.ButtonSubmit,
                 org.jblooming.waf.html.container.ButtonBar,
                 org.jblooming.waf.html.container.Container,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.DeletePreviewer,
                 org.jblooming.waf.html.display.FeedbackError,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.RadioButton,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.settings.PersistenceConfiguration,
                 org.jblooming.waf.view.ClientEntry,
                 org.jblooming.waf.view.PageState, java.util.*, org.jblooming.waf.settings.I18n"%><%

  DeletePreviewer deletePreviewer = (DeletePreviewer) JspIncluderSupport.getCurrentInstance(request);
  Form f = deletePreviewer.form;

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.getSessionState();
  Skin skin = sessionState.getSkin();

  Identifiable delendo = deletePreviewer.delendo;
  //necessary to initialize and hence inspect all data
  delendo = ReflectionUtilities.getUnderlyingObject(delendo);
%><!-- START DEL --><!-- div id="modalDiv" style="position:absolute; left:0; top:0; id="modalDiv" style="position:absolute; background-color:#CCCCCC; opacity:0.5; filter:alpha(opacity=50);"></div -->
<!--div id="modal2" style="position:absolute; z-index:2; left:0; top:0; width:100%; height:100%;"--><%
  boolean foundSomeReference = false;

  // test for exception in previous deleting
  boolean problemInRemoving = Commands.DELETE.equals(pageState.getCommand());
  Exception ex = (Exception) pageState.getAttribute(PlatformConstants.DELETE_EXCEPTION);

  boolean delendaIsNode = delendo instanceof Node;
  boolean delendaParentIsNull = !delendaIsNode || ((Node) delendo).getParentNode() == null;
  boolean delendaHasChildren = delendaIsNode && ((Node) delendo).getChildrenNode().iterator().hasNext();
  Class objClass = delendo.getClass();
  String clazzName = PersistenceHome.deProxy(objClass.getName());
  objClass = Class.forName(clazzName);
  PersistenceConfiguration persistenceConf = PersistenceConfiguration.getInstance(objClass);
  SessionFactory sf = persistenceConf.getSessionFactory();

  String[] names = ReflectionUtilities.getPropertyNames(delendo);

  String humanName = JSP.limWr(JSP.encode(delendo.getName()), 150);

  Container box = new Container();
  box.title = I18n.get(I18nConstants.DELETE_PREVIEW, humanName + " (id:" + delendo.getId() + ")");
  box.width = "70%";
  //box.height = "400";
  box.draggable = true;
  box.collapsable = true;
  box.centeredOnScreen = true;
  //box.overflow = "auto";
  box.closeable = true;
  box.setCssPostfix("warn");
  box.start(pageContext);

  if (problemInRemoving) {
    FeedbackError fe = new FeedbackError();
    fe.errorCode = I18n.get(I18nConstants.DELETE_PROBLEM_FOR_OBJECT, humanName + " (id:" + delendo.getId() + ")");
    fe.toHtml(pageContext);
%><font color="red"><%=fe.errorCode%></font><%
  }

%> <table class="table"><%

/*
________________________________________________________________________________________________________________________________________________________________________


    COLLECTIONS

________________________________________________________________________________________________________________________________________________________________________

*/

  CascadesProxy csp = new CascadesProxy();
  for (int i = 0; i < names.length; i++) {
    String name = names[i];
    Object propertyValue = ReflectionUtilities.getFieldValue(name,delendo); //entityPersister.getPropertyValue(delendo, name, HibernateFactory.getSession().getEntityMode());

    if (propertyValue != null) {
      if (propertyValue instanceof Collection &&
              ((Collection) propertyValue).size() > 0 &&
              !csp.doesCascadeOnDelete(i,delendo)
              ) {

        Collection coll = (Collection) propertyValue;
        Object sample = coll.iterator().next();

        if (sample instanceof Identifiable) {
          foundSomeReference = true;
          boolean membersAreNodesOfTheSameFamily = sample instanceof Node && ReflectionUtilities.instanceOf(sample,(deletePreviewer.normalizeInstanceToSuperclass!=null ? deletePreviewer.normalizeInstanceToSuperclass : delendo.getClass()));
          //boolean membersAreChildren = delendaIsNode && coll.equals(((Node) delendo).getChildrenNode());
          if (membersAreNodesOfTheSameFamily) {

          %><tr height="40"><td><%=I18n.get("COLLECTION_REFERENCE")%>:
            <b><%=name%> <%=coll.size()%></b>&nbsp;<%=I18n.get(I18nConstants.MOVE_TO_ROOT_OR_DELETE)%></td></tr><%

            RadioButton rbGlobalUnlink = new RadioButton(I18n.get(I18nConstants.UNLINK), ObjectEditorConstants.FLD_DELETE_STYLE + "__" + name, Commands.UNLINK, "&nbsp;", "", false, "", pageState);
            RadioButton rbGlobalMove = new RadioButton(I18n.get(I18nConstants.MOVE_TO_PARENT), ObjectEditorConstants.FLD_DELETE_STYLE + "__" + name, Commands.UP, "&nbsp;", "", false, "", pageState);
            //                 RadioButton rbGlobalDel = new RadioButton(ObjectEditorConstants.FLD_DELETE_STYLE+"__"+name, Commands.DELETE, I18n.DELETE, "&nbsp;", "", false, 0, "", pageState);
            RadioButton rbGlobalDelRec = new RadioButton(I18n.get(I18nConstants.DELETE_DESCENDANTS), ObjectEditorConstants.FLD_DELETE_STYLE + "__" + name, Commands.DELETE_DESCENDANTS, "&nbsp;", "", false, "", pageState);
            String resetMain =
                    "obj('" + rbGlobalUnlink.id + "').checked=false;" +
                            //"obj('"+rbGlobalDel.id +"').checked=false;" +
                            (!delendaParentIsNull ? "obj('" + rbGlobalMove.id + "').checked=false;" : "") +
                            (membersAreNodesOfTheSameFamily ? "obj('" + rbGlobalDelRec.id + "').checked=false;" : "");

            String resetChild = "";
            for (java.util.Iterator iterator = coll.iterator(); iterator.hasNext();) {
              Identifiable ident = (Identifiable) iterator.next();
            %><tr><td><table>
                <tr><td width="40"><%Img.imgSpacer("40", "1", pageContext);%></td>
                    <td width="150" height="25" valign="middle"><%=ident.getName() + " (id:" + ident.getId() + ")"%></td>
                    <td><%

                  RadioButton rbUnlink = new RadioButton(I18n.get(I18nConstants.UNLINK), ObjectEditorConstants.FLD_DELETE_STYLE + "__" + name + "__" + ident.getId(), Commands.UNLINK, "&nbsp;", "", false, "", pageState);
                  rbUnlink.script = resetMain;
                  %><%rbUnlink.toHtml(pageContext);%><%

                  if (!delendaParentIsNull) {
                    //sposta
                    RadioButton rbMove = new RadioButton(I18n.get(I18nConstants.MOVE_TO_PARENT), ObjectEditorConstants.FLD_DELETE_STYLE + "__" + name + "__" + ident.getId(), Commands.UP, "&nbsp;", "", false, "", pageState);
                    if (coll.size() > 1)
                      rbMove.script = resetMain;
                    resetChild += "obj('" + rbMove.id + "').checked=false;";
                    %><%rbMove.toHtml(pageContext);%><%
                  }
                  //RadioButton rbDel = new RadioButton(ObjectEditorConstants.FLD_DELETE_STYLE+"__"+name+"__"+ident.getId(), Commands.DELETE, I18n.DELETE, "&nbsp;", "", false, 0, "", pageState);
                  //if (coll.size()>1)
                  //  rbDel.script = resetMain;
                  %> <%--=rbDel.toHtml()--%><%

                  //elimina figli
                  RadioButton rbDelRec = new RadioButton(I18n.get(I18nConstants.DELETE_DESCENDANTS), ObjectEditorConstants.FLD_DELETE_STYLE+"__"+name+"__"+ident.getId(), Commands.DELETE_DESCENDANTS, "&nbsp;", "", false, "", pageState);
                  if (coll.size()>1)
                  rbDelRec.script = resetMain;
                  resetChild += "obj('"+rbDelRec.id +"').checked=false;";
                    %> <%rbDelRec.toHtml(pageContext);%><%

                  resetChild += "obj('" + rbUnlink.id + "').checked=false;";

                 %></td></tr></table>
              </td></tr><%
            }

            if (coll.size() > 1) {
          %><tr><td><table class="table"><tr>
                <td width="40"><%Img.imgSpacer("40", "1", pageContext);%></td>
                <td width="150" height="25" valign="middle"><b><%= I18n.get(I18nConstants.EVERY_ITEM)%></b></td>
                <td><%

                  rbGlobalUnlink.script = resetChild;
                  rbGlobalMove.script = resetChild;
                  //rbGlobalDel.script=resetChild;
                  rbGlobalDelRec.script = resetChild;

                %><b><%rbGlobalUnlink.toHtml(pageContext);%></b><%
                  if (!delendaParentIsNull) {
                    //sposta
                %><b><%rbGlobalMove.toHtml(pageContext);%></b><%
                  }

                %> <%--=rbGlobalDel.toHtml()--%><%
                if (membersAreNodesOfTheSameFamily) {
                  //elimina figli
              %><b><%rbGlobalDelRec.toHtml(pageContext);%></b><%
                }
              %></td></tr></table></td></tr><%
            }
          }
        }
      }
    }
  }
  /*
  ________________________________________________________________________________________________________________________________________________________________________


      REFERRERS

  ________________________________________________________________________________________________________________________________________________________________________

  */

%><tr><td>&nbsp;</td></tr><%

  // case when referrer has delendo as property
  Map acm = sf.getAllClassMetadata();
  Set keysAcm = acm.keySet();

  if (keysAcm != null && keysAcm.size() > 0) {
    for (Iterator iterator = keysAcm.iterator(); iterator.hasNext();) {
      String className = (String) iterator.next();
      //it may be an entity name, and not a class
      Class persClass = null;
      try {
        persClass = Class.forName(className);
      } catch (ClassNotFoundException e) { }

      if (persClass!=null && !persClass.equals(delendo.getClass())) {


        Type[] types = ReflectionUtilities.getPropertyTypes(className);
        List camps = new ArrayList();
        for (int i = 0; i < types.length; i++) {
          Type type = types[i];
          //verify that it works on hierarchies
          if (delendo.getClass().equals(type.getReturnedClass())) {
            //potentially refers delendo
            camps.add(ReflectionUtilities.getPropertyNames(className)[i]);
          }
        }
        if (camps.size() > 0) {
          String hql = "select count(o.id) from " + persClass.getName() + " as o where ";
          for (Iterator iterator1 = camps.iterator(); iterator1.hasNext();) {
            String s = (String) iterator1.next();
            hql = hql + "o." + s + " = :delendo or ";
          }
          hql = hql.substring(0, hql.length() - 4);
          OqlQuery oql = new OqlQuery(hql);
          oql.getQuery().setEntity("delendo", delendo);
          long size = ((Long) oql.uniqueResult()).intValue();
          if (size > 0) {
            foundSomeReference = true;
            final String pcm = persClass.getName();
            %><tr><td>
              "<%=StringUtilities.deCamel(pcm.substring(pcm.lastIndexOf(".") + 1))%>"
              <%=I18n.get(I18nConstants.DELETE_REFERENCES_IN_FIELD, size + "", camps.toString())%>
            </td></tr><%
          }
        }
      }
    }
  }

  // case when referrer has delendo as member of collection
  Map collMeta = sf.getAllCollectionMetadata();
  Set keysCollMeta = collMeta.keySet();

  if (keysCollMeta != null && keysCollMeta.size() > 0) {
    for (java.util.Iterator iterator = keysCollMeta.iterator(); iterator.hasNext();) {
      String persCollFullName = (String) iterator.next();
      AbstractCollectionPersister cm = (AbstractCollectionPersister) collMeta.get(persCollFullName);

      final Class returnedClass = cm.getElementType().getReturnedClass();
      // I want to find delendo in collections not in delendo
      Class ownerClass = cm.getElementClass();//getOwnerClass();
      if (ownerClass != null && !ownerClass.equals(delendo.getClass()) && returnedClass.equals(delendo.getClass())) {
        // candidate found
        //String hql = "select count(o.id) from " + ownerClass.getName() + " as o where :delendo in elements(o." + cm.getName().substring(cm.getName().lastIndexOf(".")+1) + ") ";
        String hql = "select count(o.id) from " + ownerClass.getName() + " as o left join o." +
                     cm.getName().substring(cm.getName().lastIndexOf(".") + 1) + " as prop where prop = :delendo ";

        //select user
        //from User as user
        //left join user.messages as msg

        OqlQuery oql = new OqlQuery(hql);
        oql.getQuery().setEntity("delendo", delendo);
        int size = ((Integer) oql.uniqueResult()).intValue();
        if (size > 0) {
          foundSomeReference = true;
          %><tr><td>
            <b><%=size%></b> references found in collection
            "<%=cm.getName().substring(cm.getName().lastIndexOf(".") + 1).toLowerCase()%>" of
            "<%=ownerClass.getName().substring(ownerClass.getName().lastIndexOf(".") + 1).toLowerCase() %>".
          </td></tr><%
        }
      }
    }
  }

%><tr align="center"><td valign="middle"><%

  if (ex != null) {
    Container erc = new Container();
    erc.closeable = true;
    erc.collapsable = true;
    erc.status = Container.COLLAPSED;
    erc.title = I18n.get("PERSISTENCE_ERROR");
    //erc.draggable=true;
    erc.start(pageContext);
    ex.printStackTrace(new java.io.PrintWriter(out));
    erc.end(pageContext);

  } else if (!foundSomeReference) {
%><%=I18n.get("NO_DIRECT_REFERENCES_FOUND")%><%
  }

  /*
  ________________________________________________________________________________________________________________________________________________________________________


      BUTTONS

  ________________________________________________________________________________________________________________________________________________________________________

  */

%></td></tr><tr align="right">
  <td valign="middle"><%

      ButtonBar butBar = new ButtonBar();
      ButtonJS reset = new ButtonJS();
      reset.label = I18n.get("RESET");
      reset.onClickScript = "$('#" + box.getContainerId() + "').hide();";
      butBar.addButton(reset);

      if (!problemInRemoving || delendaHasChildren) {
        ButtonSubmit bs = new ButtonSubmit(f);
        bs.label = I18n.get("DELETE_AND_APPLY");
        bs.variationsFromForm.setCommand(deletePreviewer.cmdDelete);

        /**
         * 18/09/06
         * teoros added
         */
        List<ClientEntry> additionalEntries = deletePreviewer.additionalEntries;
        if (additionalEntries!=null && additionalEntries.size()>0) {
          for (int i = 0; i < additionalEntries.size(); i++) {
            ClientEntry ce = additionalEntries.get(i);
            TextField.hiddenInstanceOfFormToHtml(ce.name, f, pageContext);
            bs.variationsFromForm.addClientEntry(ce.name, ce.stringValueNullIfEmpty());
          }
        }

        butBar.addButton(bs);
      }
      butBar.toHtml(pageContext);

  %></td></tr></table> <!--/td></table--> <%

  box.end(pageContext);
  //box.finalize(pageContext);
%><!-- END DEL --><%--!--/div-->
<!--script>
window.onload = modalDiv;
function hideModal(){
obj('modal1').style.display='none';
obj('modal2').style.display='none';
}
function modalDiv() {
var opacity= "opacity:0.5;";
if(isExplorer){
opacity="filter:alpha(opacity=50);";
}
var bodyHTML = document.body.innerHTML;
var modalDiv = "<div id=\"modal1\" style=\"position:absolute; z-index:1; width:100%; height:100%; background-color:#CCCCCC; "+opacity+"\">&nbsp;</div>"+bodyHTML;
document.body.innerHTML = modalDiv;
}

</script--%>