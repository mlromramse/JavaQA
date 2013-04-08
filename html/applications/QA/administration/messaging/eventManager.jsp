<%@ page import="org.jblooming.messaging.SomethingHappened,
                 org.jblooming.operator.Operator,
                 org.jblooming.persistence.objectEditor.FieldFeature,
                 org.jblooming.persistence.objectEditor.ObjectEditor,
                 org.jblooming.persistence.objectEditor.businessLogic.ObjectEditorController, org.jblooming.waf.ScreenBasic, org.jblooming.waf.html.button.ButtonLink, org.jblooming.waf.html.input.SmartCombo, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState"
%>
<%
  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;

    Operator logged = (Operator) pageState.getLoggedOperator();
    logged.testIsAdministrator();

    // definizione object editor

    ObjectEditor objectEditor = new ObjectEditor(I18n.get("EVENT_MANAGEMENT"), SomethingHappened.class, pageContext);
    objectEditor.defaultOrderBy="event.id";
    objectEditor.query = "from "+SomethingHappened.class.getName()+" as event";
    objectEditor.mainHqlAlias = "event";
    objectEditor.addDisplayField("theClass", I18n.get("EVENT_EVENT_TYPE"));
    objectEditor.addDisplayField("identifiableId", I18n.get("EVENT_EVENT_OBJECT"));
    objectEditor.addDisplayField("eventType", I18n.get("EVENT_COMMAND"));
    //objectEditor.addDisplayField("messageParams", I18n.get("EVENT_MESSAGE_PARAMS"));
    objectEditor.addDisplayField("happenedAt", I18n.get("EVENT_HAPPENED_AT"));
    objectEditor.addDisplayField("happeningExpiryDate", I18n.get("EVENT_HAPPENING_EXPIRY_DATE"));

    // Operator combo
    String hql = "select operator.id, operator.name || ' ' || operator.surname from " + Operator.class.getName() + " as operator";
    String whereForId = "where operator.id = :" + SmartCombo.FILTER_PARAM_NAME;
    String whereForFiltering =
    " where operator.name || ' ' || operator.surname like :" + SmartCombo.FILTER_PARAM_NAME +
    " or operator.surname || ' ' || operator.name like :" + SmartCombo.FILTER_PARAM_NAME + " order by operator.surname, operator.name";
    SmartCombo ops = new SmartCombo("owner", hql, whereForFiltering, whereForId);

    FieldFeature ffo = new FieldFeature("whoCausedTheEvent",I18n.get("EVENT_WHO_CAUSED"));
    ffo.smartCombo = ops;
    ffo.smartComboClass = Operator.class;
    objectEditor.addDisplayField(ffo);

    // edit == list
    objectEditor.editFields=objectEditor.displayFields;

    PageSeed listenerManager = new PageSeed("listenerManager.jsp");
    PageSeed messageManager = new PageSeed("messageManager.jsp");

    objectEditor.additionalButtons.add( new ButtonLink(I18n.get("LISTENER_MANAGEMENT"), listenerManager));
    objectEditor.additionalButtons.add( new ButtonLink(I18n.get("MESSAGING_MANAGEMENTS"), messageManager));

    ObjectEditorController oec = new ObjectEditorController(objectEditor);

    ScreenBasic.preparePage(oec,pageContext);
    pageState.perform(request, response).toHtml(pageContext);
  } else {
    pageState.getMainJspIncluder().toHtml(pageContext);
%>
<%
  }
%>


