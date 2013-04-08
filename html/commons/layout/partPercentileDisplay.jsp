<%@ page import="org.jblooming.utilities.JSP"%>
<%@ page import="org.jblooming.waf.SessionState"%>
<%@ page import="org.jblooming.waf.html.core.JspIncluderSupport"%>
<%@ page import="org.jblooming.waf.html.display.PercentileDisplay"%>
<%
  PercentileDisplay pd = (PercentileDisplay) JspIncluderSupport.getCurrentInstance(request);

  int w=(int)pd.percentile;
  if (pd.percentile>100)
    w=(int)((100/pd.percentile)*100);


  int h = Integer.parseInt(pd.height.replaceAll("px", ""));

%><table style="width:<%=pd.width%>;height:<%=pd.height%>;" title="<%=JSP.w(pd.toolTip)%>" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        <div class="perc" style="width:100%;height:<%=pd.height%>;background-color:<%=pd.percentile>100?pd.percentileOverflowColor:pd.backgroundColor%>;position:relative;">
          <div style="background-color:<%=pd.percentileColor%>;width:<%=w%>%;height:<%=pd.height%>;"></div>
        </div>
      </td>
      <td class="percentileLabel" style="width:<%=h*3%>px; font-size:<%=h%>px;color:<%=pd.percentile>100?pd.percentileOverflowFontColor:pd.percentileFontColor%>" valign="middle"><%=JSP.perc(pd.percentile,pd.fractionDigits)+"%"%></td>
    </tr>

  </table>