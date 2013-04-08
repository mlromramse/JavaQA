<%@ page import=" org.jblooming.ontology.Node,
                  org.jblooming.waf.SessionState,
                  org.jblooming.waf.constants.I18nConstants,
                  org.jblooming.waf.html.core.JspIncluderSupport,
                  org.jblooming.waf.html.display.Img,
                  org.jblooming.waf.html.display.TreeLine"%><%
  
  TreeLine line = (TreeLine)JspIncluderSupport.getCurrentInstance(request);
  SessionState sess= SessionState.getSessionState(request);
  Node node = line.node;
  boolean isLeaf = ! node.getChildrenNode().iterator().hasNext();

  String name = (isLeaf ? "" : "<b>");
  name += node.getName();
  name += (isLeaf ? "" : "</b>");

  Img nodeImage = line.image;
  nodeImage.translateToolTip=false;
  Img open = new Img(sess.getSkin().imgPathPlus+"open.gif", I18nConstants.OPEN);
  open.id = "folder" + node.getIntId();
  open.script="onclick=\"showBranch('"+node.getId().toString()+"');swapFolder('folder"+node.getId().toString()+"');\"";

%><table width="100%" cellspacing="0" cellpadding="0" border="0" class="underline">
  <tr <% if(!isLeaf){%>    <%}%>>
    <td>
      <table border="0" cellspacing="0" cellpadding="2" >
        <tr height="20" valign="top"><%
        if (!isLeaf) {
        %><td widht="25"><%open.toHtml(pageContext);%></td><%
        } else if (0==1) {
        %><td widht="25"><%Img.imgSpacer("16","16", pageContext);%></td><%
        }
        %><td widht="100%" nowrap><%
        if (!isLeaf)
          nodeImage.script="onclick=\"showBranch('"+node.getId().toString()+"');swapFolder('folder"+node.getId().toString()+"');\"";
        %><%nodeImage.toHtml(pageContext);%></td >
          <td nowrap><%=name%>&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
</table>