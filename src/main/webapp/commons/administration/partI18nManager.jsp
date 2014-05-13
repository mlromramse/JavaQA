<%@ page import="org.jblooming.operator.Operator,
                 org.jblooming.operator.User,
                 org.jblooming.oql.OqlQuery,
                 org.jblooming.security.PlatformPermissions,
                 org.jblooming.utilities.CodeValueList,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.html.button.*,
                 org.jblooming.waf.html.container.Container,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.*,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.settings.Application,
                 org.jblooming.waf.settings.ApplicationState,
                 org.jblooming.waf.settings.I18n,
                 org.jblooming.waf.settings.I18nEntryPersistent,
                 org.jblooming.waf.settings.businessLogic.I18nController,
                 org.jblooming.waf.view.PageSeed,
                 org.jblooming.waf.view.PageState,
                 java.util.*, org.jblooming.waf.settings.businessLogic.I18nAction" %><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%
  // number of rows displayed
  int MAXROWS = 200;

  PageState pageState = PageState.getCurrentPageState();

  User loggedUser = pageState.getLoggedOperator();

  I18n i18nManager = ApplicationState.i18n;

  // hack to temporary disable i18nedit
  String old_status = i18nManager.getEditStatus();
  i18nManager.setEditStatus(I18n.EDIT_STATUS_READ);
  // temporary disable catching labels
  boolean catchState = i18nManager.catchUsedLabels;
  i18nManager.catchUsedLabels = false;

  pageState.addClientEntry(Fields.FORM_PREFIX + "I18N_MODALITY", old_status);
  pageState.addClientEntry(Fields.FORM_PREFIX + "I18N_LENIENT", i18nManager.getLenient());
  pageState.addClientEntry("CATCHUSEDLABELS", catchState);
  pageState.addClientEntry("ENABLED_LANGUAGES", ApplicationState.applicationSettings.get("ENABLED_LANGUAGES"));



  Container box = new Container("bx_18li");
  box.title = I18n.get("I18N_MANAGER");
  box.start(pageContext);

  loggedUser.testPermission(PlatformPermissions.i18n_manage);

  PageSeed url = pageState.thisPage(request);

  url.setCommand(Commands.FIND);
  if (pageState.getEntry(Fields.APPLICATION_NAME).stringValueNullIfEmpty() != null)
    url.addClientEntry(Fields.APPLICATION_NAME, pageState.getEntry(Fields.APPLICATION_NAME).stringValueNullIfEmpty());
  url.addClientEntry(Fields.FORM_PREFIX + "code", "");
  Form form = new Form(url);
  form.encType = Form.MULTIPART_FORM_DATA;

  RadioButton rb_read = new RadioButton(I18n.get("MODALITY_READ"), Fields.FORM_PREFIX + "I18N_MODALITY", I18n.EDIT_STATUS_READ, "&nbsp;&nbsp;", null, false, null, pageState);
  RadioButton rb_edit = new RadioButton(I18n.get("MODALITY_EDIT"), Fields.FORM_PREFIX + "I18N_MODALITY", I18n.EDIT_STATUS_EDIT, "&nbsp;&nbsp;", null, false, null, pageState);
  RadioButton rb_append = new RadioButton(I18n.get("MODALITY_APPEND"), Fields.FORM_PREFIX + "I18N_MODALITY", I18n.EDIT_STATUS_APPEND, "&nbsp;&nbsp;", null, false, null, pageState);

  RadioButton rbl_none = new RadioButton(I18n.get("LENIENT_NONE"), Fields.FORM_PREFIX + "I18N_LENIENT", "" + I18n.LENIENT_NONE, "&nbsp;&nbsp;", null, false, null, pageState);
  RadioButton rbl_lang = new RadioButton(I18n.get("LENIENT_LANG"), Fields.FORM_PREFIX + "I18N_LENIENT", "" + I18n.LENIENT_LANG, "&nbsp;&nbsp;", null, false, null, pageState);
  RadioButton rbl_app = new RadioButton(I18n.get("LENIENT_APP"), Fields.FORM_PREFIX + "I18N_LENIENT", "" + I18n.LENIENT_APP, "&nbsp;&nbsp;", null, false, null, pageState);
  RadioButton rbl_appLang = new RadioButton(I18n.get("LENIENT_APP_LANG"), Fields.FORM_PREFIX + "I18N_LENIENT", "" + I18n.LENIENT_APP_LANG, "&nbsp;&nbsp;", null, false, null, pageState);

  Set<String> supportedLanguages = i18nManager.supportedLanguages;
  Set<String> filterForLanguages = new TreeSet();

  Map applics = ApplicationState.platformConfiguration.applications;

  CodeValueList cvl = new CodeValueList();
  cvl.add("", "-- all --");
  for (Iterator iterator = applics.keySet().iterator(); iterator.hasNext();) {
    String s = (String) iterator.next();
    Application app = (Application) applics.get(s);
    cvl.add(app.getName(), app.getName());
  }


  Combo applicationsCombo = new Combo(Fields.FORM_PREFIX + "APPLICATION", "&nbsp;", null, 20, cvl, "onChange=\"obj('" + form.id + "').submit();\"");
  applicationsCombo.label = I18n.get("APPLICATIONS");

  String focusedApplicationName = pageState.getEntry(Fields.FORM_PREFIX + "APPLICATION").stringValue();

  if (focusedApplicationName == null) {
    focusedApplicationName = "";
  }

  SortedMap entries;
  if (focusedApplicationName.length() <= 0) {
    entries = i18nManager.codeEntries;
  } else {
    entries = I18n.getEntriesForApplication(focusedApplicationName);
  }


  String searchText = pageState.getEntry("SEARCH_TEXT").stringValueNullIfEmpty();


  boolean searchTextEnabled = JSP.ex(searchText);

  boolean suspectSearchEnabled = pageState.getEntry("SUSPECT").checkFieldValue();
  boolean searchFromDBOnlyEnabled = !ApplicationState.platformConfiguration.development && pageState.getEntry("FROMDBONLY").checkFieldValue();

  boolean searchEnabled = searchTextEnabled || suspectSearchEnabled || searchFromDBOnlyEnabled;

  CheckField catchUsedLabels = new CheckField("CATCHUSEDLABELS", "&nbsp;", true);
  catchUsedLabels.label = I18n.get("CATCHUSEDLABELS");

  form.start(pageContext);

  Container modality = new Container("i18Mod");
  modality.title = I18n.get("I18N_BEHAVIOUR");
  modality.collapsable = true;
  modality.status = Container.COLLAPSED;
  //modality.setCssPostfix(Css.postfixThin);
  //modality.level=1;
  modality.start(pageContext);
%>

<table border="0" cellpadding="2" cellspacing="0" style="background-color: #fff">
  <tr>
    <td>
      <%=I18n.get("SELECT_I18N_MODALITY")%>:
      <%rb_read.toHtml(pageContext);%>&nbsp;&nbsp;
      <%rb_edit.toHtml(pageContext);%>&nbsp;&nbsp;
      <%rb_append.toHtml(pageContext);%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <%catchUsedLabels.toHtml(pageContext);%>


      <br> <%=I18n.get("SELECT_I18N_LENIENT_LEVEL")%>
      : <%rbl_none.toHtml(pageContext);%>&nbsp;&nbsp; <%rbl_lang.toHtml(pageContext);%>&nbsp;&nbsp;<%rbl_app.toHtml(pageContext);%>&nbsp;&nbsp;<%rbl_appLang.toHtml(pageContext);%>
      <br>
    </td><td>
    <%
      ButtonSubmit save = new ButtonSubmit(form);
      save.variationsFromForm.setCommand(I18n.CMD_CHANGEMODALITY);
      save.label = I18n.get("CHANGE_MODALITY");
      save.toHtml(pageContext);
    %>
  </td>
    <td>
      <%
        PageSeed ps = pageState.pageFromCommonsRoot("administration/i18nMonitor.jsp");
        ps.setPopup(true);
        ButtonLink monitor = new ButtonLink(ps);
        monitor.label = "monitor";
        monitor.target = ButtonLink.TARGET_BLANK;
        monitor.popup_height = "500";
        monitor.popup_width = "500";
        monitor.popup_resizable = "yes";
        monitor.popup_scrollbars = "yes";

        monitor.toHtml(pageContext);


        SmartCombo users= new SmartCombo("USERTOASSIGN", Operator.class,"loginName");
        users.separator="</td><td>";
        users.label="Give to an user the permission to maintain a language.  Choose the user";

      %>
    </td>
  </tr>
</table>
<hr>
<table>
  <tr>
    <td><% users.toHtml(pageContext);%></td>
    <td>language code</td><td> <input type ="text" size="2" id="LANG_TO_EDIT" name="LANG_TO_EDIT"> </td>
    <td><%new ButtonJS("assign","assignLangToUser();").toHtml(pageContext);%></td>
    <td>(empty to remove)</td>
  </tr>
</table>
<hr>
<table>
  <tr>

    <td><%
      Uploader issueFile = new Uploader("I18N_FILE", form, pageState);
      issueFile.label = "";
      issueFile.separator = "</td><td nowrap>";
      issueFile.size = 15;
      issueFile.treatAsAttachment = true;
      issueFile.toHtml(pageContext);

    %></td><td><%

      ButtonSubmit doUpload = new ButtonSubmit(form);
      doUpload.variationsFromForm.setCommand("I18N_FILE");
      doUpload.label = I18n.get("I18N_FILE");
      doUpload.toHtml(pageContext);
    %>
    </td>

    <td><%
      ButtonSupport export = ButtonSubmit.getSubmitInstanceInPopup(form, request.getContextPath() + "/commons/administration/i18nExport.jsp", 600, 400);
      export.label = I18n.get("I18N_EXPORT");
      export.toHtml(pageContext);
    %>
    </td>
    <td width="20">&nbsp;</td><td><%
      TextField enabledLang= new TextField(I18n.get("I18N_ENABLED_LANGUAGES"),"ENABLED_LANGUAGES","</td><td>",30,false);
      enabledLang.toHtml(pageContext);
    %></td><td><%
    ButtonSubmit sEL = new ButtonSubmit(form);
    sEL.variationsFromForm.setCommand("I18N_SAVE_ENABLED_LANG");
    sEL.label = I18n.get("SAVE");
    sEL.toHtml(pageContext);
    %>
    </td>
  </tr>
</table>
<%

  modality.end(pageContext);
  Container boxAction = new Container();
  boxAction.title = I18n.get("ACTIONS");
  boxAction.setCssPostfix("thin");
  boxAction.start(pageContext);


%>
<table border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td><%
      ButtonSubmit reload = new ButtonSubmit(form);
      reload.variationsFromForm.setCommand(I18n.CMD_RELOAD);
      reload.label = I18n.get("I18N_RELOAD");
      reload.toHtml(pageContext);
    %></td>

    <td><%
      ButtonSubmit dump = new ButtonSubmit(form);
      dump.variationsFromForm.setCommand(I18n.CMD_DUMP);
      dump.label = I18n.get("I18N_DUMP");
      dump.hasFocus=I18n.dumpNeeded;
      dump.toHtml(pageContext);
    %></td>

    <td>
      &nbsp;&nbsp;<%
      TextField tfNewLanguage = new TextField("TEXT", "", Fields.FORM_PREFIX + "ADD_NEW_LANGUAGE", "&nbsp;", 2, false);
      tfNewLanguage.toHtml(pageContext);

      ButtonSubmit addLang = new ButtonSubmit(form);
      addLang.variationsFromForm.setCommand(I18n.CMD_NEW_LANGUAGE);
      addLang.label = I18n.get("I18N_NEW_LANGUAGE");
    %></td>

    <td><%
      addLang.toHtml(pageContext);

      ButtonSubmit addEntry = new ButtonSubmit(form);
      addEntry.variationsFromForm.setCommand(I18n.CMD_NEW_ENTRY);
      addEntry.variationsFromForm.setHref("i18nEdit.jsp");
      addEntry.label = I18n.get("I18N_NEW_ENTRY");
    %></td>

    <td><%
      addEntry.toHtml(pageContext);

%>
    </td>
  </tr>
</table>
<%
  boxAction.end(pageContext);

  Container boxSearch = new Container("bx_18se");
  boxSearch.title = I18n.get("SEARCH");
  boxSearch.setCssPostfix("thin");
  boxSearch.start(pageContext);
%>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td>
      <%applicationsCombo.toHtml(pageContext);%> <%=I18n.get("AVAILABLE_LANGUAGES")%>:
      <%

        for (Iterator iterator = supportedLanguages.iterator(); iterator.hasNext();) {
          String lang = (String) iterator.next();

          CheckBox chbx = new CheckBox("", Fields.FORM_PREFIX + "LANGUAGES" + lang, "&nbsp;", null, false, false, "", false);
          chbx.script = "onChange=\"obj('" + form.id + "').submit();\"";
          chbx.label = "";

          if (pageState.getEntry(Fields.FORM_PREFIX + "LANGUAGES" + lang).stringValue() != null) {
            filterForLanguages.add(lang);
          }
      %><%=lang%>:<%chbx.toHtml(pageContext);%>&nbsp;&nbsp;<%
      }

      if(filterForLanguages.size()<=0)
        filterForLanguages.addAll(supportedLanguages);

      if (!ApplicationState.platformConfiguration.development) {
        %>&nbsp;&nbsp;&nbsp;&nbsp;  <%

        CheckField chbxFromDB = new CheckField("FROMDBONLY", "&nbsp;", false);
        chbxFromDB.label = I18n.get("I18N_FROMDBONLY");
        chbxFromDB.script = "onChange=\"obj('" + form.id + "').submit();\"";
        chbxFromDB.putLabelFirst = true;
        chbxFromDB.toHtml(pageContext);
      }
      %>&nbsp;&nbsp;&nbsp;&nbsp;  <%

      CheckField chbxSuspect = new CheckField("SUSPECT", "&nbsp;", false);
      chbxSuspect.label = I18n.get("SUSPECT");
      chbxSuspect.script = "onChange=\"obj('" + form.id + "').submit();\"";
      chbxSuspect.putLabelFirst = true;
      chbxSuspect.toHtml(pageContext);
    %>&nbsp;&nbsp;&nbsp;&nbsp;  <%

      boolean filterForMissingInLanguage = pageState.getEntry("SEARCH_MISSING_IN_LANGUAGE").checkFieldValue();
      CheckField chbxMissing = new CheckField("SEARCH_MISSING_IN_LANGUAGE", "&nbsp;", false);
      chbxMissing.label = I18n.get("SEARCH_MISSING_IN_LANGUAGE");
      chbxMissing.additionalOnclickScript = "checkForMissingLanguage();";
      chbxMissing.putLabelFirst = true;
      chbxMissing.toHtml(pageContext);
    %>&nbsp;&nbsp;&nbsp;&nbsp;  <%

      TextField tfSearch = new TextField("TEXT", I18n.get("SEARCH"), "SEARCH_TEXT", "&nbsp;", 22, false);
      tfSearch.addKeyPressControl(13, "obj('" + form.id + "').submit();", "onkeyup");
      pageState.setFocusedObjectDomId(tfSearch.id);
      tfSearch.script = "";
      tfSearch.toHtml(pageContext);
      %>
    </td>
  </tr>
</table>


<%
  boxSearch.end(pageContext);

  form.end(pageContext);


  // ------------------------------------------------------------------------------------  MAIN TABLE  START ----------------------------------------------

  if (searchEnabled) {
    int howMany=0;
    %>

    <style type="text/css">
      textarea.unselected {
        font-size:12px;
        width:100%;
        height:20px;;
        overflow:hidden;
        border:none;
        background-color:transparent;
        cursor:pointer;
      }
      textarea.selected {
        height:90px;
        overflow:auto;
        background-color:white;
        cursor:default;
      }
      .cell{
        width:100%;
        height:100%;
        border:none;
        padding:3px;
        cursor:pointer;
        font-size:11px;
      }
      .transl{
        font-size:7px;
        font-weight:bold;
        color:blue;
        cursor:pointer;
      }

    </style>


    <table class="table" id="multi">
    <tr>
      <th>&nbsp;</th>
      <th width="30%">code</th>

      <th width="1%"> application</th>
      <%
        for (String lang:filterForLanguages){
            %> <th><%=lang%> </th> <%
        }
      %>
      <th>&nbsp;</th>
    </tr>
    <%

    // load from db
    Set<String> entryFromDB = new HashSet();
    if (searchFromDBOnlyEnabled) {
      List<I18nEntryPersistent> en = new OqlQuery("from " + I18nEntryPersistent.class.getName()).list();
      for (I18nEntryPersistent i : en)
        entryFromDB.add(i.getCode());
    }

    int taCounter=1;
      boolean swap=true;

    for (Iterator iterator = entries.keySet().iterator(); iterator.hasNext();) {
      String code = (String) iterator.next();

      I18n.I18nEntry i18nEntry = (I18n.I18nEntry) (entries.get(code));

      if (searchTextEnabled && !i18nEntry.matches(searchText,filterForLanguages))
        continue;

      if (suspectSearchEnabled && !i18nEntry.isSuspect())
        continue;

      if (!ApplicationState.platformConfiguration.development && searchFromDBOnlyEnabled && !entryFromDB.contains(i18nEntry.code))
        continue;


      // if search for missing skip if all is complete (for selected languages)
      if (filterForMissingInLanguage && JSP.ex(focusedApplicationName)){
        boolean existsAll=true;
        boolean existsAtLeastOne=false;
        //for (Iterator iterator2 = i18nManager.supportedLanguages.iterator(); iterator2.hasNext();) {
        //  String lang = (String) iterator2.next();
        //  if (!filterForLanguageAvailable || pageState.getEntry(Fields.FORM_PREFIX + "LANGUAGES" + lang).stringValueNullIfEmpty()!=null) {

        for (String lang:filterForLanguages ){
            // check if it exists
            if (!JSP.ex(I18n.getRawLabel(code,focusedApplicationName,lang))){
              existsAll=false;
              //break;
            } else {
              existsAtLeastOne=true;
            }
          }
        //}
        if (existsAll || !existsAtLeastOne)
          continue;
      }


      //ButtonSubmit bsEdit = new ButtonSubmit(form);
      //bsEdit.variationsFromForm = edit;
      ButtonJS bsEdit =new ButtonJS("editEntry(this)");
      //bsEdit.label = code;

      Img img = new Img(pageState.getSkin().imgPath + "list/edit.gif", "edit", "", "");
      ButtonImg imgEdit=new ButtonImg(bsEdit, img);

      String tdStyle = "";
      if (!i18nEntry.isSeen() && catchState){
        tdStyle = "border:1px solid red;";
      }

      if (i18nEntry.isSuspect())
        tdStyle = tdStyle+"background-color:#ff6060;";

      for (Iterator iterator1 = i18nEntry.applicationEntries.keySet().iterator(); iterator1.hasNext();) {
        String applicationName = (String) iterator1.next();

        I18n.ApplicationEntry applicationEntry = i18nEntry.applicationEntries.get(applicationName);

        %>
        <tr class="alternate"  id="tr$$$$<%=applicationName%>$$$$<%=i18nEntry.code%>" an="<%=applicationName%>">
          <td valign="top"  width="1%"><%imgEdit.toHtml(pageContext);%></td>
          <td style="<%=tdStyle%>" valign="top" nowrap width="15%"><%

            pageState.addClientEntry("code"+taCounter,code);

            TextField codetf=new TextField("","code"+taCounter,"",2,false);
            codetf.fieldClass="cell";
            codetf.label="";
            codetf.preserveOldValue=true;
            codetf.toHtml(pageContext);
            %></td>
          <td  valign="top" width="1%" nowrap class="appl"><%=applicationName%></td>
          <%
            for (String lang:filterForLanguages ){

              pageState.addClientEntry("ta"+taCounter,JSP.w(applicationEntry.entries.get(lang)));
              TextArea ta= new TextArea("ta"+taCounter,"",30,1,"labelta unselected");
              ta.label="";
              ta.preserveOldValue=false;
              ta.script=" lang=\""+lang+"\"";

              %><td  valign="top" cmenu='cm'><%ta.toHtml(pageContext);%><br>
                <%   if (ApplicationState.platformConfiguration.development) {
                for (String l:filterForLanguages){
                  if (!l.equals(lang)){
                  %><span class="transl"><%=l%></span>&nbsp;&nbsp;<%
                  }
                }
              }%>
          

              </td><%
            taCounter++;
          }
          %><td  valign="top"  width="1%" nowrap><%
            ButtonLink.getAjaxDeleteInstanceForList("&APPNAME=" + JSP.urlEncode(applicationName) + "&CODE=" + JSP.urlEncode(i18nEntry.code), I18n.CMD_REMOVE_LABEL,
                    I18nController.class, "tr$$$$" + applicationName + "$$$$" + i18nEntry.code, pageState).toHtml(pageContext);
          %></td>
        </tr>
        <%

      }

      howMany++;
          if (howMany> MAXROWS){
        %><tr><td colspan="9">Result limited to <%=MAXROWS%> elements. Refine your search.</td></tr><%
        break;
      }
    }
  %></table><%

    // ------------------------------------------------------------------------------------  MAIN TABLE  END ----------------------------------------------

  }

  box.end(pageContext);

  // hack to temporary disable i18nedit
  i18nManager.setEditStatus(old_status);
  i18nManager.catchUsedLabels = catchState;


%> <script type="text/javascript">
  var lastTd;
  $(document).ready(function () {
    $("#multi").find("textarea.labelta").bind("focus", taOnFocus).bind("blur", saveTA);

    <%   if (ApplicationState.platformConfiguration.development) {%>
      $("span.transl").bind("click",function(){
      translate($(this),$(this).text());

    })

    <%}%>

    $("#multi").find("input.cell").bind("blur",renameCode);


  });


  function taOnFocus() {
    var ta = $(this);
    var tr = ta.parents("tr").eq(0);
    if (!ta.hasClass("selected")) {
      $("textarea.labelta.selected").removeClass("selected");
      tr.find("textarea.labelta").addClass("selected");
    }
  }

  function saveTA(){
    var ta = $(this);
    var tr=ta.parents("tr").eq(0);
    if (ta.isValueChanged()){
      showSavingMessage();
      // get label
      var code=tr.find("input.cell").val();
      var lang=ta.attr("lang");
      var appl=tr.find("td.appl").html()
      var label=ta.val();

      var queryString ="code="+encodeURIComponent(code)+"&lang="+encodeURIComponent(lang)+"&appl="+encodeURIComponent(appl)+"&label="+encodeURIComponent(label);

      var error = false;
      var message = "";
      var mustDump=false; 
      eval(getContent("i18nAjaxController.jsp", "<%=Commands.COMMAND%>=<%=Commands.SAVE%>&" + queryString));

      if (error) {
        alert(message);
      } else {
        ta.updateOldValue();
        if (mustDump)
          $("#<%=dump.id%>").addClass("focused");
      }
      hideSavingMessage();
    }
  }

  function renameCode(){
    var inp = $(this);

    if (inp.isValueChanged()){
      showSavingMessage();
      var oldValue = inp.getOldValue();
      var tr=inp.parents("tr").eq(0);
      var appl=tr.find("td.appl").html()

      var mustDump=false;

      var queryString ="newcode="+encodeURIComponent(inp.val())+"&oldcode="+encodeURIComponent(oldValue)+"&appl="+encodeURIComponent(appl);

      eval(getContent("i18nAjaxController.jsp", "<%=Commands.COMMAND%>=RENAME&" + queryString));

      // reset values on the other with the same code
      $("#multi").find("input.cell[value="+oldValue+"]").each(function() {
        $(this).val(inp.val());})

      // set old values for the whole family
      $("#multi").find("input.cell[value="+inp.val()+"]").updateOldValue();      
      if (mustDump)
        $("#<%=dump.id%>").addClass("focused");

      hideSavingMessage();
    }
  }

  function editEntry(but){
    var butt=$(but);
    var tr=butt.parents("tr").eq(0);
    var code=tr.find("input.cell").val();    
    window.location="i18nEdit.jsp?<%=Commands.COMMAND%>=<%=Commands.EDIT%>&<%=Fields.FORM_PREFIX%>code="+encodeURIComponent(code)+"&appTabSet=appTabSet"+tr.attr("an");
  }


  function checkForMissingLanguage(){
    if ($("#<%=Fields.FORM_PREFIX%>APPLICATION").val()=="" && $("#SEARCH_MISSING_IN_LANGUAGE").val()=="<%=Fields.TRUE%>"){
      alert ("<%=I18n.get("MISSING_IN_LANGUAGE_CHOOSE_APPLICATION")%>");
    }

  }

  function assignLangToUser(){
    var queryString ="userId="+$("#USERTOASSIGN").val()+"&lang="+encodeURIComponent($("#LANG_TO_EDIT").val());

    getContent("i18nAjaxController.jsp", "<%=Commands.COMMAND%>=ASSLANG&" + queryString)
  }

</script>

<%   if (ApplicationState.platformConfiguration.development) {%>
<script type="text/javascript">


  function translate(el,lang) {
    var start = $(el).parents("td:first").find("textarea");
    if (start.val()) {
      var apiurl = "https://www.googleapis.com/language/translate/v2";
      var data = {
        key:"<%=ApplicationState.applicationSettings.get("GOOGLE_TRANSLATE_API_KEY")%>",
        q:start.val(),
        source:start.attr("lang"),
        target:lang
      };

      console.debug(data);

      $.ajax({
        url: apiurl,
        data:data,
        dataType: 'jsonp',
        success: function (ret) {
          console.debug(ret);
          if (!ret || !ret.data || !ret.data.translations)
            console.error(ret);
          var transl = ret.data.translations[0].translatedText;
          var dest = start.parents("tr:first").find("textarea[lang="+lang+"]");
          dest.val(dest.val() + (dest.val()!=""?"\n":"") + transl).blur();
        }
      });
    }
  }

</script><%
  }
%>

