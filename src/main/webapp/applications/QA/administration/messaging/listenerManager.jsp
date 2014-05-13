<%@ page import=" org.jblooming.messaging.Listener,
          org.jblooming.operator.Operator,
          org.jblooming.persistence.objectEditor.FieldFeature,
          org.jblooming.persistence.objectEditor.ObjectEditor,
          org.jblooming.persistence.objectEditor.businessLogic.ObjectEditorController,
          org.jblooming.waf.ScreenBasic,
          org.jblooming.waf.html.button.ButtonLink, org.jblooming.waf.html.input.SmartCombo, org.jblooming.waf.html.layout.Skin, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState"%>
<%
  PageState pageState = PageState.getCurrentPageState();
  if (!pageState.screenRunning) {
    pageState.screenRunning = true;

    Operator logged = (Operator) pageState.getLoggedOperator();
    logged.testIsAdministrator();
    
    // definizione object editor

    /*

 private String theClass;
  private String identifiableId;

  private String eventType;
  private Date validityStart;
  private Date validityEnd;
  private String media;
  private Operator owner;

  private String body;

  private SerializedMap<String, String> additionalParams = new SerializedMap();

  private boolean oneShot;

  private boolean listenDescendants = false;

  private Date lastMatchingDate;


    */


    ObjectEditor objectEditor = new ObjectEditor(I18n.get("LISTENER_MANAGEMENT"), Listener.class, pageContext);
    objectEditor.query = "from "+Listener.class.getName()+" as listener";
    objectEditor.mainHqlAlias = "listener";
    objectEditor.defaultOrderBy="listener.id";    

    
    objectEditor.addDisplayField("theClass", I18n.get("LISTENER_THE_CLASS"));
    objectEditor.addDisplayField("identifiableId", I18n.get("LISTENER_IDENTIFIABLE_ID"));
    objectEditor.addDisplayField("validityStart", I18n.get("LISTENER_VALIDITY_START"));
    objectEditor.addDisplayField("validityEnd", I18n.get("LISTENER_VALIDITY_END"));
    objectEditor.addDisplayField("eventType", I18n.get("LISTENER_COMMAND"));
    objectEditor.addDisplayField("media", I18n.get("LISTENER_MEDIA"));

    // Operator combo
    String hql = "select operator.id, operator.name || ' ' || operator.surname from " + Operator.class.getName() + " as operator ";
    String whereForId = "where operator.id = :" + SmartCombo.FILTER_PARAM_NAME;
    String whereForFiltering =
    " where operator.name || ' ' || operator.surname like :" + SmartCombo.FILTER_PARAM_NAME +
    " or operator.surname || ' ' || operator.name like :" + SmartCombo.FILTER_PARAM_NAME+ " order by operator.surname, operator.name";
    SmartCombo ops = new SmartCombo("owner", hql, whereForFiltering, whereForId);

    FieldFeature ffo = new FieldFeature("owner",I18n.get("OWNER"));
    ffo.smartCombo = ops;
    ffo.smartComboClass = Operator.class;
    objectEditor.addDisplayField(ffo);


    objectEditor.addDisplayField("body", I18n.get("LISTENER_BODY"));
    //objectEditor.addDisplayField("additionalParams", I18n.get("LISTENER_ADDITIONAL_MESSAGE"));
    objectEditor.addDisplayField("lastMatchingDate", I18n.get("LISTENER_LAST_MATCHING_DATE"));
    objectEditor.addDisplayField("listenDescendants", I18n.get("LISTENER_LISTEN_DESCENDANTS"));
    objectEditor.addDisplayField("oneShot", I18n.get("LISTENER_ONE_SHOT"));
 /*   FieldFeature ff = new FieldFeature("oneShot", I18n.get("LISTENER_ONE_SHOT"));
    ff.boolAsCombo = true;
    ff.usedForSearch = true;
    ff.usedComboForSearch = true;
    objectEditor.addDisplayField(ff);*/

    // edit == list
    objectEditor.editFields=objectEditor.displayFields;

    PageSeed eventManager = new PageSeed("eventManager.jsp");
    PageSeed messageManager = new PageSeed("messageManager.jsp");
    objectEditor.additionalButtons.add( new ButtonLink(I18n.get("EVENT_MANAGEMENT"), eventManager));
    objectEditor.additionalButtons.add( new ButtonLink(I18n.get("MESSAGING_MANAGEMENTS"), messageManager));

    ObjectEditorController oec = new ObjectEditorController(objectEditor);

    ScreenBasic.preparePage(oec,pageContext);
    pageState.perform(request, response).toHtml(pageContext);

    pageState.perform(request, response).toHtml(pageContext);
  } else {
    final Skin skin = pageState.getSkin();
    pageState.getMainJspIncluder().toHtml(pageContext);
%>
<%
  }
%>
