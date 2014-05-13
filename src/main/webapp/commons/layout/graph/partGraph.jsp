<%@ page import="org.jblooming.utilities.CodeValueList,
                 org.jblooming.utilities.JSP,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.Graph,
                 org.jblooming.waf.html.input.CheckField,
                 org.jblooming.waf.html.input.ColorValueChooser, org.jblooming.waf.html.input.Combo, org.jblooming.waf.html.input.TextField, org.jblooming.waf.view.PageState, java.util.List" %><%


  Graph graph = (Graph) JspIncluderSupport.getCurrentInstance(request);
  PageState pageState = PageState.getCurrentPageState();
  String fieldName=graph.fieldName;



  if ("DRAW".equals(request.getAttribute(ColorValueChooser.ACTION))) {

    //default configuration values
    pageState.getEntryOrDefault("showLegenda", Fields.TRUE);
    pageState.getEntryOrDefault("legendaPosition", "ne");
    pageState.getEntryOrDefault("plotType", "bars");
    pageState.addClientEntry("width", "800");
    pageState.addClientEntry("height", "400");

    pageState.addClientEntry("showMarker", true);

    pageState.addClientEntry("yaxisFormatString", "%1.1f");
    //pageState.addClientEntry("yaxisInterval", "");
    pageState.addClientEntry("showGrid", true);

    pageState.addClientEntry("pieShadow", true);
    pageState.addClientEntry("fillSlices", true);
    pageState.addClientEntry("colBackground", "fefefe");
    pageState.addClientEntry("colGrid", "e0e0e0");

    pageState.addClientEntry("shadowOffset", "2");
    pageState.addClientEntry("shadowAlpha", "0.08");
    pageState.addClientEntry("shadowAngle", "45");
    pageState.addClientEntry("shadowDepth", "5");

    pageState.addClientEntry("barPadding", "8");
    pageState.addClientEntry("barMargin", "10");

    pageState.addClientEntry("lineWidth", "2.5");
    pageState.addClientEntry("markerSize", "9");

    pageState.addClientEntry("sliceMargin", "0");
    pageState.addClientEntry("pieLineWidth", "2.5");


    //make the data url
    pageState.addClientEntry(fieldName + "_url",JSP.w(graph.dataUrl));

    // make for configuration
    String configs = graph.configuration;
    List<String> params = StringUtilities.splitToList(configs, "\n");
    for(String param : params) {
      if(JSP.ex(param)) {
        String[] array = param.split("\t");
        pageState.addClientEntry(array[0], array[1]);
      }
    }

    // make for datas
    String values = graph.values;
    if(!JSP.ex(values)) {  // default example data
      values = "label\tserie\n"+
             "jan\t1\n"+
             "feb\t2\n"+
             "mar\t4\n"+
             "apr\t8";
    }
    pageState.addClientEntry(fieldName+"_values", values);

    if(graph.maxSize>0) {
      double max = Math.max(pageState.getEntry("width").intValueNoErrorCodeNoExc(), pageState.getEntry("height").intValueNoErrorCodeNoExc());
      if(max>0) {
        double dd = ((double )graph.maxSize)/max;
        pageState.addClientEntry("width", (int)(dd*pageState.getEntry("width").intValueNoErrorCodeNoExc()) );
        pageState.addClientEntry("height", (int)(dd*pageState.getEntry("height").intValueNoErrorCodeNoExc()) );
      } else{
        pageState.addClientEntry("width", 200);
        pageState.addClientEntry("height", 200);
      }
    }

  %>

<div id="graph_<%=fieldName%>" class="graphDiv" fieldName="<%=fieldName%>">
    <textarea style="display:none;" rows="4" cols="80" id="<%=fieldName%>_config" name="<%=fieldName%>_config" class="config"></textarea>
    <textarea style="display:none;" rows="4" cols="80" id="<%=fieldName%>_values" name="<%=fieldName%>_values" class="values"><%=JSP.htmlEncode(pageState.getEntry(fieldName+"_values").stringValueNullIfEmpty())%></textarea>


<%--  --------------------------------------- START graph placeholder --------------------------------------- --%>
<div id="graphDiv_<%=fieldName%>" class="graph"></div>
<%--  --------------------------------------- END graph placeholder --------------------------------------- --%>
    
<%
  if (graph.editableData){
    ButtonJS edit = new ButtonJS("edit data", "$('#graph_"+fieldName+" #tableDataWrapper').toggle(''); " + graph.additionalOnclickScriptOnEdit +"");
    edit.toHtml(pageContext);
  }

  if (graph.editableConfig){
    ButtonJS param = new ButtonJS("edit configuration", "$('#graph_"+fieldName+" #config').toggle(); " + graph.additionalOnclickScriptOnEdit +"");
    param.toHtml(pageContext);
  }

%>


   <br>
   <div id="config" style="display:none;background-color:#fff;
    -moz-border-radius:5px;
    -webkit-border-radius:5px;
    padding:10px;
    margin-bottom:10px;
    border:1px solid #e5e5e5;">
    <h3>General configuration</h3>
   <table cellpadding="4" cellspacing="0">
    <tr><td width="100"><%
        CodeValueList cvl=new CodeValueList("lines","bars","pie");
        Combo combo = new Combo("plotType", "</td><td>", null, 15, cvl, null);
        combo.label = "Type";
        combo.toHtml(pageContext);

        %></td>
     </tr>
     <tr>
       <td><% new TextField("Title","title","</td><td colspan='3'>",40,false).toHtml(pageContext);%></td></tr>
   <tr>
       <td><% new TextField("Width","width","</td><td>",5,false).toHtml(pageContext);%></td>
       <td><% new TextField("Height","height","</td><td>",5,false).toHtml(pageContext);%></td>
   </tr>
   <tr>
       <td><% new CheckField("Show legenda","showLegenda","</td><td>",true).toHtml(pageContext);%></td>
       <td><% cvl=new CodeValueList("ne", "e", "se", "s", "sw", "w","nw", "n");
         Combo legeCombo = new Combo("legendaPosition", "</td><td>", null, 15, cvl, null);
         legeCombo.label="Legenda position";
         legeCombo.toHtml(pageContext);%></td>
       </tr>
     <tr><td><% new TextField("Background color","colBackground","</td><td>",8,false).toHtml(pageContext);%>
     <span class="colorSpan" onclick="$(this).prevAll(':text:first').trigger('click')" style="background-color:<%="#"+pageState.getEntry("colBackground").stringValueNullIfEmpty()%>;">&nbsp;</span></td>

    </tr>
   </table>
  <%

    /*new TextField("shadow Angle","shadowAngle","&nbsp;",4,false).toHtml(pageContext);
    new TextField("shadow Alpha","shadowAlpha","&nbsp;",4,false).toHtml(pageContext);
    new TextField("shadowOffset","shadowOffset","&nbsp;",4,false).toHtml(pageContext);
    new TextField("shadowDepth","shadowDepth","&nbsp;",4,false).toHtml(pageContext);*/
    %>


    <div id="lines_options" class="addConfParams" >
      <h3>Lines options</h3>
      <table cellpadding="4" cellspacing="0" border="0">
    <tr>
      <td width="100"><%
        new TextField("Line width","lineWidth","</td><td>",4,false).toHtml(pageContext);  %></td>
        <td><% new CheckField("Fill","lineFill","&nbsp;",false).toHtml(pageContext);   %>&nbsp;&nbsp;&nbsp;<% new CheckField("Fill to zero","lineFillToZero","&nbsp;",false).toHtml(pageContext);%></td>
      </tr><tr>
      <td><% new CheckField("Show marker","showMarker","</td><td>",true).toHtml(pageContext);   %></td>
      <td><%
        cvl=new CodeValueList("circle","diamond","square","x","plus", "dash", "filledCircle", "filledDiamond", "filledSquare");
        Combo combo1 = new Combo("markerStyle" , "&nbsp;", null, 15, cvl, null);
        combo1.label="Marker style";                
        combo1.toHtml(pageContext);%>
      </td><td><%
        new TextField("Size","markerSize","&nbsp;",4,false).toHtml(pageContext);%></td></tr>
       </table>   
    </div>
    <%--
    <div id="bars_options" class="addConfParams">
      <h3>Bars options</h3>
      <table cellpadding="4" cellspacing="0">
        <tr>
           <td width="100"><% new TextField("Padding","barPadding","</td><td>",3,false).toHtml(pageContext);%></td>
          <td width="50"><% new TextField("Margin","barMargin","</td><td>",3,false).toHtml(pageContext);%><%
            //new CheckField("show horizontally","barDirection","&nbsp;",true).toHtml(pageContext);
          %></td></tr>
       </table> 
    </div>
    --%>

    <div id="pie_options"  class="addConfParams">
      <h3>Pie options</h3>
        <table cellpadding="4" cellspacing="0" border="0">
        <tr>
           <td width="100"><% new CheckField("Fill slices","fillSlices","&nbsp;",false).toHtml(pageContext);%></td>
            <td><% new TextField("Slice margin","sliceMargin","</td><td>",3,false).toHtml(pageContext);%></td>
        </tr>
        <tr>
           <td><%
            new CheckField("Shadow","pieShadow","&nbsp;",false).toHtml(pageContext);%></td>
           <td><% new TextField("Line width","pieLineWidth","</td><td>",3,false).toHtml(pageContext);%></td>
           <td><% new TextField("Diameter","diameter","&nbsp;",3,false).toHtml(pageContext); %></td>
       </tr>
       </table> 
    </div>

     <div id="axis_options" class="addConfParams">
       <h3>Axis options</h3>
       <table cellpadding="4" cellspacing="0" border="0">
        <tr>
           <td width="100"><% new TextField("x-axis label", "xaxisLabel", "</td><td>", 30, false).toHtml(pageContext);%></td></tr>
         <tr>
           <td><% new TextField("y-axis label", "yaxisLabel", "</td><td>", 30, false).toHtml(pageContext);%></td></tr>
          <tr><td><%
             new CheckField("Show grid","showGrid","&nbsp;",false).toHtml(pageContext);%><%
             //new TextField("y-axis min value", "yaxisMin", "&nbsp;", 6, false).toHtml(pageContext);%>&nbsp;&nbsp; <%
             //new TextField("y-axis tick interval", "yaxisInterval", "&nbsp;", 6, false).toHtml(pageContext);%>&nbsp;&nbsp; <%
             //new TextField("y-axis format", "yaxisFormatString", "&nbsp;", 6, false).toHtml(pageContext);
           %></td>
              <td><% new TextField("Grid color","colGrid","&nbsp;",8,false).toHtml(pageContext);%>
              <span class="colorSpan" onclick="$(this).prevAll(':text:first').trigger('click')" style="background-color:<%="#"+pageState.getEntry("colGrid").stringValueNullIfEmpty()%>;">&nbsp;</span></td>
              </td>
       </tr>
       </table>
     </div>

   </div>


<div id="tableDataWrapper" style="display:none; padding:10px">
  <% //-------  url DATAURL -----
    TextField url = TextField.getURLInstance(fieldName+"_url");
    //TextField url = new TextField(fieldName+"_url","&nbsp;");
    //url.label="get data from this url:";
    url.label="loading data from an url:";
    url.separator="&nbsp;";
    url.fieldSize=40;
    //url.fieldClass=url.fieldClass+" url";
    url.toHtml(pageContext);
  %>

<br><span class="helpText">In this case the format must be '\t' separated values (col by col) with '\n' (row by row).</span>
<br><br>
<table id="tableDataExternal" style="display:none" border="0"><tr><td id="dataPlace">
<!-- here the table data -->

</td><td style="background-color:#c0c0c0"><img src="<%=pageState.getSkin().imgPath%>/addLittle.gif" title="add a series" onclick="addColumn(this)"></td></tr>
<tr><td align="center" style="background-color:#c0c0c0"><img src="<%=pageState.getSkin().imgPath%>/addLittle.gif" onclick="addRow(this)" title="add row"></td></tr>
</table> </div>



</div>
<script type="text/javascript">
  $(document).ready(function(){
    graphSetup("<%=fieldName%>");
  })

</script>


  <%

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------- INITIALIZE ------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------

  }else if (ColorValueChooser.INITIALIZE.equals(request.getAttribute(ColorValueChooser.ACTION))) {
  %>
    <style type="text/css">
      .tableData {
        border-spacing:0;
        border-collapse:collapse;
        margin:0;
      }

      #tableDataWrapper{
        background-color:#e6e7dd;
        -moz-border-radius:5px;
        -webkit-border-radius:5px;
        padding:10px;
        margin-bottom:10px;
        border:1px solid #e5e5e5;
      }

      .tableData td{
        width:150px;
        height:20px;
        margin:0px;
        padding:2px;
        border:0px;
      }
      .tableData td input{
        width:100%;
        height:100%;
        border:none;
        -moz-border-radius:0px;
      }
      .tableTools{
        width:14px;
        height:14px;
        background-image:url("<%=pageState.getSkin().imgPath%>list/del.gif");
        position:absolute;
     }

      .col{
        margin-top:-30px;
        margin-left:75px;
      }

      .row{
        margin-top:-15px;
        margin-left:-10px;
      }

      .highLight{
        background-color:yellow;
      }

      .addConfParams{
        display:none;
        padding-top:20px;
      }

      .colorSpan {
        display:inline-block;
        -moz-box-shadow:1px 1px 2px gray;
        border:1px solid gray;
        cursor:pointer;
        height:17px !important;
        margin-left:3px;
        width:17px !important;
      }
</style>

<link rel="stylesheet" href="<%=request.getContextPath()+"/commons/layout/colorPicker/css/colorpicker.css"%>" type="text/css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/commons/js/jquery/jqplot/jquery.jqplot.css" />

<script>
$(function() {
  if (isExplorer)
  initialize(contextPath + "/commons/js/jquery/jqplot/excanvas.min.js",true);

  initialize(contextPath + "/commons/layout/colorPicker/js/colorpicker.js",true);
  initialize(contextPath + "/commons/js/jquery/jqplot/jquery.jqplot.min.js",true);
  initialize(contextPath + "/commons/js/jquery/jqplot/plugins/jqplot.barRenderer.min.js",true);
  initialize(contextPath + "/commons/js/jquery/jqplot/plugins/jqplot.pieRenderer.min.js",true);
  initialize(contextPath + "/commons/js/jquery/jqplot/plugins/jqplot.categoryAxisRenderer.min.js",true);

 });



function graphSetup(fieldName){
  var myGraph=$("#graph_"+fieldName);

  // fill data table and config
  fillDataFromTA(myGraph);


  //bind events on url
    myGraph.find(".url").keyup(function(){
     if (!$(this).val())
        myGraph.find("#tableDataExternal").show();
     else
        myGraph.find("#tableDataExternal").hide();
   }).change(function(){
      fillDataFromTA(myGraph);
      refreshData( myGraph);
    });

  //bind events on config
   myGraph.find("#plotType").change(function(){
     myGraph.find("#config .addConfParams").hide();
    if ("pie"==$(this).val()){
       myGraph.find("#pie_options").fadeIn();
    } else if ("bars"==$(this).val()){
       myGraph.find("#bars_options,#axis_options").fadeIn();
    } else {
       myGraph.find("#lines_options,#axis_options").fadeIn();
    }
  }).trigger('change');

   myGraph.find("#config :text").blur(function(){refreshData( myGraph)});
   myGraph.find("#config :radio").click(function(){refreshData( myGraph)});
   myGraph.find("#config :checkbox").click(function(){refreshData( myGraph)});
   myGraph.find("#config select").bind("change",function(){refreshData( myGraph)});

  //color selectors
   myGraph.find('#colBackground, #colGrid').ColorPicker({
    onSubmit: function(hsb, hex, rgb, el) {
      $(el).val(hex);
      $(el).ColorPickerHide();
      $(el).nextAll("span:first").css("background-color","#"+hex);
      refreshData( myGraph);
    },
    onBeforeShow: function () {
      $(this).ColorPickerSetColor(this.value);
    }
  });

  //draw the graph
  refreshData( myGraph);
}


function fillDataFromTA(myGraph){
  //must read from url?
  var url = myGraph.find(":text.url").val();
  if (url){
    var dataFromFile = getContent("<%=pageState.pageFromRoot("site/proxy.jsp").toLinkToHref()+"&URL="%>"+encodeURIComponent(url));
    myGraph.find(".values").val(dataFromFile);
  } else {
    myGraph.find("#tableDataExternal").show();
  }

  // build the table from textArea value
  var datas= myGraph.find(".values").val();

  var tdContainer= myGraph.find("#dataPlace");
  tdContainer.empty();
  var tb=$("<table id='data' class='tableData'></table>");
  var table = datas.split("\n");
  $.each(table,function(){
    var tr=$("<tr></tr>");
    var cells=this.split("\t");
    $.each(cells,function(){
      tr.append("<td><input type='text' value='"+this+"'></td>");
    })
    tb.append(tr);
  });
  tdContainer.append(tb);

  // bind events on cells
  myGraph.find(".tableData :text").tableDataBind();

}


function refreshData(graphDiv) {
  //console.debug("refreshing");
  var fieldName=graphDiv.attr("fieldName");


  function getField(fieldId){
    return graphDiv.find("#"+fieldId);
  }

  var table=graphDiv.find(".tableData");

  var firstRowIsLabel=true;

  // check if first columns is label or not
  var firstColumnIsLabel=false;
  table.find("tr:gt(0)").each(function(){
    if (isNaN(Number($(this).find(":text:first").val()))){
      firstColumnIsLabel=true;
      return false;
    }
  })
  //console.debug("firstColumnIsLabel: "+firstColumnIsLabel);

  // plotType
  var plotType = getField("plotType").val();

  var rows=table.find("tr").size();
  var cols=table.find("tr:first").children().size();

  var seriesData=[];
  var seriesConfig=[];
  var cells=table.find(":text");

  //extract labels
  var labels=[];
  if (firstColumnIsLabel)
    table.find("tr:gt(0)").find(":text:first").each(function(){labels.push($(this).val())});

// set the datas
  for (var c=(firstColumnIsLabel?1:0);c<cols;c++){
    var data=[];
    for (var r=(firstRowIsLabel?1:0);r<rows;r++){
      var val = Number(cells.eq(r*cols+c).val());
      if (isNaN(val))
        val=0;

      if (firstColumnIsLabel)
        data.push([labels[r-1],val]);
      else
        data.push(val);
    }
    seriesData.push(data);


    //serie config
    var serieConf= new Object();
    serieConf.label=cells.eq(c).val();
    seriesConfig.push(serieConf);

  }


  <%-- ---------------------------------- start config ---------------------- --%>
  var config= new Object();


  // title
  config.title= getField("title").valNull();



  config.axes=new Object();
  config.axes.xaxis=new Object();
  config.axes.yaxis=new Object();

  //default axis behaviour
  config.axesDefaults=new Object();
  config.axesDefaults.tickOptions=new Object();
  config.axesDefaults.tickOptions.showGridline=getField("showGrid").val()=="yes";
  //config.axesDefaults.tickOptions.markSize=40 ;//$("#showGrid").val()=="yes";


  //y axis format
  //config.axes.yaxis.tickOptions={formatString:$("#yaxisFormatString").val()};
  //if($("#yaxisMin").val()!="") config.axes.yaxis.min=$("#yaxisMin").val();
  //if($("#yaxisInterval").val()!="") config.axes.yaxis.tickInterval=$("#yaxisInterval").val();
  config.axes.yaxis.label=getField("yaxisLabel").valNull();

  //x axis format
  config.axes.xaxis.label=getField("xaxisLabel").valNull();

  // set the axis renderer
  if (plotType!="pie")
    config.axes.xaxis.renderer=$.jqplot.CategoryAxisRenderer;


  //legenda
  config.legend = new Object();
  config.legend.show=getField("showLegenda").val()=="yes";
  config.legend.location=getField("legendaPosition").valNull();

  config.seriesDefaults=new Object();
  config.seriesDefaults.rendererOptions=new Object();

  // grid and background
  config.grid=new Object();
  config.grid.background="#"+getField("colBackground").valNull();
  config.grid.gridLineColor="#"+getField("colGrid").valNull();



  if (plotType=="bars"){
    config.seriesDefaults.renderer=$.jqplot.BarRenderer;

    // bar renderer options
//    config.seriesDefaults.rendererOptions.barPadding=getField("barPadding").valNull();
//    config.seriesDefaults.rendererOptions.barMargin=getField("barMargin").valNull();
//
  } else if (plotType=="pie"){
    config.seriesDefaults.renderer=$.jqplot.PieRenderer;

    // pie renderer options
    config.seriesDefaults.rendererOptions.lineWidth=getField("pieLineWidth").valNull();
    config.seriesDefaults.rendererOptions.sliceMargin=getField("sliceMargin").valNull();
    config.seriesDefaults.rendererOptions.diameter=getField("diameter").valNull();
    config.seriesDefaults.rendererOptions.shadow=getField("pieShadow").val()=="yes"
    config.seriesDefaults.rendererOptions.fill=getField("fillSlices").val()=="yes"


  } else {

    //line renderer option
    config.seriesDefaults.fill=getField("lineFill").val()=="yes";
    config.seriesDefaults.fillToZero=getField("lineFillToZero").val()=="yes";


  }

  //set markers
  config.seriesDefaults.showMarker=getField("showMarker").val()=="yes";
  config.seriesDefaults.markerOptions=new Object();
  config.seriesDefaults.markerOptions.style=getField("markerStyle").valNull();
  config.seriesDefaults.markerOptions.size=getField("markerSize").valNull();


  //line width
  config.seriesDefaults.lineWidth=getField("lineWidth").val();


  //shadow config
  config.seriesDefaults.shadowDepth=getField("shadowDepth").valNull();
  config.seriesDefaults.shadowOffset=getField("shadowOffset").valNull();
  config.seriesDefaults.shadowAlpha=getField("shadowAlpha").valNull();
  config.seriesDefaults.shadowAngle=getField("shadowAngle").valNull();

  config.series=seriesConfig;

  <%-- ---------------------------------- end config ---------------------- --%>

//  console.debug(seriesData);
//  console.debug(config);


  //resize the placeholder
  graphDiv.find('.graph').width(getField("width").val()+"px").height(getField("height").val()+"px");

  graphDiv.find('.graph').empty();
  var plot = $.jqplot(graphDiv.find('.graph').attr("id"), seriesData,config);



  //extracts all data ready to store on db

  var serialized="";

  graphDiv.find("#config :input").each(function(i,el){
    var value = $(el).val();
    if(value) serialized=serialized+$(el).attr("name")+"\t"+value+"\n";
  })

  var datas=""
  // get values from data table   \t \n separated
  table.find("tr").each(function(i,el){
    datas=datas+(i==0?"":"\n");
    $(this).find(":text").each(function(i,el){
      datas=datas+(i==0?"":"\t")+$(this).val();
    })
  })
  // set data to saved textAreas
  graphDiv.find(".config").val(serialized);
  graphDiv.find(".values").val(datas);

}



function addColumn(el){
  var table = $(el).parents("table:first").find("table.tableData");
  table.find("tr").append("<td><input type='text'></td>");
  table.find(":text").gridCellUnBind().tableDataBind();
  //focus first col
  table.find("tr:first :text:last").focus();
}

function removeCol(el,col){
  var table = $(el).parents("table:first");
  var col=table.find("tr").find("td:eq("+col+")");
  col.find("*").addClass("highLight");
  if (confirm("remove column?")){
    var div=table.parents("div.graphDiv:first");
    col.remove();
    refreshData(div);
  } else {
    col.find("*").removeClass("highLight");
  }
};

function addRow(el){
  var addedTr = $(el).parents("table:first").find("table.tableData tr:last").clone(true).insertAfter("#data tr:last");
  //clear text
  addedTr.find(":text").val("");

  //focus first cell
  addedTr.find(":text:first").focus();
}
    
function removeRow(el,row){
  var tr = $(el).parents("tr:first");
  tr.find("*").addClass("highLight");
  if (confirm("remove row?")){
    var div=tr.parents("div.graphDiv:first");
    tr.remove();
    refreshData(div);
  } else {
    tr.find("*").removeClass("highLight");
  }
}



function cellFocus(e) {
  var table=$(this).select().attr("old",$(this).val()).parents(".tableData");

  hideTools(table);
  var cells=table.find(":text");
  var cols=table.find("tr:first td").size();

  var index=cells.index(this);

  var col= index % cols;
  var row= Math.floor(index/cols);

  showTools(table,row,col,cols);

};

function cellBlur(e){
  var el=$(this);

  //remove tools
  var table=$(this).parents(".tableData");
  hideTools(table);

  if (el.attr("old")!=el.val()){
    refreshData($(this).parents(".graphDiv"));
    el.attr("value",el.val());
  }
};


function showTools(table,row,col,cols){
  //console.debug("col:"+col +" row:"+row);

  table.find("td:eq("+col+")").append("<div class='tableTools col' onmousedown='removeCol(this,"+col+")'></div>");
  table.find("td:eq("+row*cols+")").append("<div class='tableTools row' onmousedown='removeRow(this,"+row+")'></div>");
}

function hideTools(table){
  table.find(".tableTools").remove();
}

jQuery.fn.tableDataBind= function(){
  this.each(function(){
    $(this).bind("focus", cellFocus).bind("blur",cellBlur).bind("keydown", cellKey);
  });
  return this;
}

jQuery.fn.gridCellUnBind= function(){
  this.each(function(){
    $(this).unbind("focus", cellFocus).unbind("blur",cellBlur).unbind("keydown", cellKey);
  });
  return this;
}

jQuery.fn.valNull= function(){
  var val=this.eq(0).val();
  if (val=="")
    val=null;
  return val;
}


//   ---------------------------------------------   KEYBOARD  ---------------------------------------------
function cellKey (event) {
  var theCell = $(this);
  var table=theCell.parents(".tableData");



  var isCtrl = false;
  if(event.ctrlKey)
    isCtrl = true;

  var rows=table.find("tr").size();
  var cols=table.find("tr:first td").size();

  //console.debug("rows:"+rows+" cols:"+cols);

  var cells=table.find(":text");
  var cellCount=cells.size()-1;
  var index=cells.index(this);
  var oldIndex=index;

 //console.debug("cellCount:"+cellCount +" index:"+index);
  var ret = true;
  switch (event.keyCode) {

    case 37: //left arrow
      index= index>0?index-1:cellCount;
      ret = false;
      break;

    case 38: //up arrow
      index= index>=cols?index-cols:index;
      ret = false;
      break;
    case 40: //down arrow
      index= index<=cellCount-cols?index+cols:index;
      ret=false;
      break;
    case 36: //home
      index=0;
      ret=false;
      break;
    case 35: //end
      index=cellCount;
      ret=false;
      break;

    case 9: //tab
    case 13: //enter   // on last cell auto add a row
      if (index==cellCount)
        addRow(theCell.parents("table:first"));
      else
        index=index<cellCount?index+1:0;
      ret=false;
      break;

    case 39: //right arrow
      index=index<cellCount?index+1:0;
      ret=false;
      break;

    case 86: // ^V
      ret=true;
      theCell.one("keyup",afterPaste);
      break;
  }

  if (oldIndex!=index){
    cells.get(index).focus();
  }


  //console.debug("new index:"+index);

  return ret;

};

function afterPaste(){
  var theCell = $(this);
  var table=theCell.parents(".tableData");
  var rows=table.find("tr").size();
  var cols=table.find("tr:first td").size();

  //console.debug("rows:"+rows+" cols:"+cols);

  var cells=table.find(":text");
  var cellCount=cells.size()-1;
  var index=cells.index(this);
  var oldIndex=index;
  //console.debug("after paste rows:"+rows+" cols:"+cols+ " pasted:"+theCell.val());
}
    </script>
    <%
  } else {



  }

%>