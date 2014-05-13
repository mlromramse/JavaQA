<%@ page import="org.jblooming.operator.Operator, org.jblooming.waf.constants.Fields, org.jblooming.waf.html.button.ButtonJS, org.jblooming.waf.html.core.JspIncluderSupport, org.jblooming.waf.html.input.LoadSaveFilter, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState, java.util.Map, java.util.Set, java.util.TreeSet, org.jblooming.waf.html.core.JST, net.sf.json.JSONArray, org.jblooming.utilities.JSP, net.sf.json.JSONObject" %><%
  PageState pageState = PageState.getCurrentPageState();
  Operator logged = pageState.getLoggedOperator();
  LoadSaveFilter lsfb = (LoadSaveFilter) JspIncluderSupport.getCurrentInstance(request);


  if (LoadSaveFilter.INITIALIZE.equals(request.getAttribute(LoadSaveFilter.ACTION))) {

  %>
   
  <div style="display:none;" id="__btnTmpl">
    <%=JST.start("BTN_LSF")%>
    <span class='teamworkIcon deleteSmall' onclick="$(this).confirm(lsfDelete,i18n.FLD_CONFIRM_DELETE);return false;" title="<%=I18n.get("DELETE")%>">d</span><button onclick="lsfClick($(this));" class="textual noprint (#=obj.selected?'focused':''#)" >(#=name#)</button>
    <%=JST.end()%>
  </div>

  <script type="text/javascript">

    function lsfClick(el){
      $("#<%=Fields.FLD_FILTER_SELECTED%>").val('yes');
      $("#<%=Fields.FLD_FILTER_NAME%>").val(el.text());
      $("#"+el.closest(".customSavedFilters").attr("formId")).submit();
    }

    function lsfSave(el) {
      var lsfEdit = el.closest("[btnId]");
      var lsf = $("#" + lsfEdit.attr("btnId"));
      var form = $("#" + lsf.attr("formId"));
      var input = lsfEdit.find(":text:first");

      if (input.val() != "") {
        var data = {};
        form.find(":input[name]").each(function() {
          var inp = $(this);
          if (inp.val() && inp.val() != "" && !inp.attr("name").startsWith("_"))
            data[inp.attr("name")] = inp.val();
        });

        data.CM = "SVFILTER";
        data.cat = lsf.attr("category");

        $.getJSON("<%=ApplicationState.contextPath%>/commons/layout/loadSaveFilter/lSFAjaxController.jsp", data, function(resp) {
          if (resp.ok) {
            var newFilterBtn = $.JST.createFromTemplate({name:input.val()}, "BTN_LSF");
            lsf.prepend(newFilterBtn);
            newFilterBtn.effect("highlight", { color: "yellow" }, 3000);
            input.val("");
            lsfEdit.prevAll(".saveFilter:first").html("<%=I18n.get("FILTER_SAVED")%>").show().oneTime(5000,"tmpfltlbl",function(){$(this).html("<%=I18n.get("WANT_TO_SAVE_FILTER")%>");});
            lsfEdit.hide();            
          }
        });
      } else {
        lsfEdit.prevAll(".saveFilter:first").show();
        lsfEdit.hide();
      }

    }

    function lsfDelete(el){
      var lsf = el.closest(".customSavedFilters");
      var btn=el.nextAll("button:first");

      var data={
        CM:"RMFILTER",
        cat:lsf.attr("category"),
        "<%=Fields.FLD_FILTER_NAME%>":btn.text()
      };

      $.getJSON("<%=ApplicationState.contextPath%>/commons/layout/loadSaveFilter/lSFAjaxController.jsp",data,function(resp){
        if (resp.ok){
          btn.remove();
          el.remove();
          $("#<%=Fields.FLD_FILTER_NAME%>").val("");
          $("#<%=Fields.FLD_FILTER_SELECTED%>").val("");
        }
      });
    }

    $(function(){
      $("#__btnTmpl").loadTemplates().remove();

    });

  </script>
  <%
    } else  if ("DRAW_EDITOR".equals(request.getAttribute(LoadSaveFilter.ACTION))) {
      %><div class="saveFilter" onclick="$(this).hide();$('#<%=lsfb.id%>_ed').show();"><%=I18n.get("WANT_TO_SAVE_FILTER")%></div>
        <div style="display:none;" class="filterName" id="<%=lsfb.id%>_ed" btnId="<%=lsfb.id%>">
          <%=I18n.get("NEW_FILTER_NAME")%> <input type="text" id="<%=Fields.FLD_FILTER_NAME%>" name="<%=Fields.FLD_FILTER_NAME%>"><button onclick="lsfSave($(this));return false;" class="textual" title="<%=I18n.get("SAVE")%>"><span class="teamworkIcon">&ograve;</span></button>
        </div>
      <%
} else if ("DRAW_BUTTONS".equals(request.getAttribute(LoadSaveFilter.ACTION))){


  Map<String,String> filterMap = logged.getFilters();

  if (logged != null ) {


    JSONArray ja = new JSONArray();
    Set<String> flts = new TreeSet();
    flts.addAll(filterMap.keySet());
    String selFilter = pageState.getEntry(Fields.FLD_FILTER_NAME).stringValueNullIfEmpty();
    for (String key : flts) {
      if (key.startsWith(lsfb.category)) {
        String filterName = key.substring(lsfb.category.length());
        JSONObject jo = new JSONObject();
        if (filterName.equals(selFilter)) {
          jo.element("selected", true);
        }
        jo.element("name", filterName);
        ja.add(jo);
      }
    }

    %><div class="customSavedFilters" formId="<%=lsfb.form.id%>" category="<%=lsfb.category%>" id="<%=lsfb.id%>">
      <input type="hidden" name="<%=Fields.FLD_FILTER_SELECTED%>" id="<%=Fields.FLD_FILTER_SELECTED%>">
      <input type="hidden" name="<%=Fields.FLD_FILTER_CATEGORY%>" id="<%=Fields.FLD_FILTER_CATEGORY%>" value="<%=lsfb.category%>">
    </div>
    <script type="text/javascript">
      $(function(){
        var bts=<%=ja.toString()%>;
        var div=$("#<%=lsfb.id%>");
        for (var i in bts){
          div.append($.JST.createFromTemplate(bts[i],"BTN_LSF"));
        }
      });
    </script>


    <%

    }
  }
%>
