<%@ page import="org.jblooming.utilities.JSP,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.button.ButtonSupport,
                 org.jblooming.waf.html.container.Tab,
                 org.jblooming.waf.html.container.TabSet,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.view.PageState, java.util.Iterator" %><%

  PageState pagestate = PageState.getCurrentPageState();
  SessionState sm = SessionState.getSessionState(request);
  Skin skin = sm.getSkin();

  /**
   * How the focused tab is determined? using following priority
   *
   * 1) if there is the client entry it wins allways!
   *
   * 2) else if there is a tab selected, it will be focused
   *
   * 3) then if the tabset is sortable and user-sorted, first in order is shown
   * 
   * 4) nada de nada? the first one!
   *
   */




  if (TabSet.INITIALIZE.equals(request.getAttribute(Commands.COMMAND))) { // ------------------------------------------------------ Container INITIALIZE

    %><script>
      $(function(){initialize("<%=request.getContextPath()+"/commons/layout/tabSet/partTabSet.js"%>",true)});
    </script>

<%

  } else if (TabSet.BAR.equals(request.getAttribute(Commands.COMMAND))) {

    TabSet tabSet = (TabSet) JspIncluderSupport.getCurrentInstance(request);

    //determine the focused tab and set it as focused
    Tab focusedTab=tabSet.setFocusedTab(pagestate);

%><table cellspacing=0 cellpadding=0 width="100%" border=0 valign="top" >
        <tr>
          <td><%
              TextField tf = new TextField("hidden", tabSet.id, "", 20);
              tf.preserveOldValue = false;
              tf.label = "";
              tf.toHtml(pageContext);

          %><table cellspacing=0 cellpadding=0 width="100%" border=0 style="padding-top:3px;">
              <tr id="TRTABSET_<%=tabSet.id%>"><%
                if (tabSet.indentTab) {
                %><td nowrap width="10" class="tabUnselected"><%Img.imgSpacer("1", "1", pageContext);%></td><%
                }

                int size = 1;
                if (tabSet.wideTabs)
                  size = 100 / tabSet.tabSetSize();

                Iterator i = tabSet.iterator();
                while (i.hasNext()) {
                  Tab tab = (Tab) i.next();
                  String hideAndShowScript = "hideTabsAndShow('" + tab.domId + "','" + tabSet.id + "');";
                  String setTabHiddenValue = "setTabHiddenValue('" + tab.domId + "','" + tabSet.id + "');";

                  // if no button specified ->  create a js ones
                  if (tab.button == null) {
                    ButtonJS bjs = new ButtonJS(setTabHiddenValue+hideAndShowScript);
                    if (JSP.ex(tab.additionalScript))
                      bjs.onClickScript += tab.additionalScript;
                    bjs.label = tab.caption;
                    bjs.enabled = tab.enabled;
                    tab.button=bjs;

                  // does not append hideAndShowScript, usually when there is a button it is a SubmitButton. But is this true always? We will see sooner hahahahahahahahahah!
                  // this prevents swap before to re-load page
                  } else {
                    if (tab.button.additionalOnClickScript != null)
                      tab.button.additionalOnClickScript += setTabHiddenValue;
                    else
                      tab.button.additionalOnClickScript = setTabHiddenValue;
                    tab.enabled = tab.button.enabled;
                  }

                  tab.button.outputModality = ButtonSupport.TEXT_ONLY;
                  String buttonClass = "tabUnselected";

                  if (tab.focused)
                    buttonClass = "tabSelected";
                  else if (!tab.enabled)
                    buttonClass = "tabDisabled";

              %><td id="<%=tab.domId%>" tabId="<%=tabSet.id+"#"+tab.id%>" nowrap width="<%=size%>%" class="<%=buttonClass%> tab" <%=tab.enabled ? tab.button.generateLaunchJs() : ""%>>
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                      <td align="right" class="handler" style="line-height:0px;"><img src="<%=skin.imgPath%>tab/tabLeft.png"></td>
                      <%
                         Img img=null;
                        if (tab.imgTab != null) {
                          /*
                          Per avere una forma corretta del tabSet le immagini devono avere altezza massima di 19px!!!
                           */
                            img =tab.imgTab;
                            if(!JSP.ex(img.height)) img.height="17px";
                            img.script=" style='position:relative' class='tabImg' ";
                        }
                        if(img!=null)  {
                           %>
                        <td align="right"  background="<%=skin.imgPath%>tab/tabMiddle.png" style="padding-top:2px; <%=tab.enabled ? "cursor:pointer;" : "cursor:default"%>"><%img.toHtml(pageContext);%></td>
                        <%
                        }

                      %>
                      <td align="center" valign="middle" background="<%=skin.imgPath%>tab/tabMiddle.png" style="<%=tab.enabled ? "cursor:pointer;" : "cursor:default"%>" <%=tab.generateToolTip()%> nowrap><%tab.button.toHtml(pageContext);%></td>
                      <td align="left" width="1" style="line-height:0px;"><img src="<%=skin.imgPath%>tab/tabRight.png"></td>
                    </tr>
                  </table></td><%
                  }

              %><td class="tabUnselected"><%Img.imgSpacer("1", "1", pageContext);%></td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td class="tabContainer">
            <table border=0 width="100%" style="padding:3px;margin-top:10px;">
              <tr>
                <td id="TABSETPART_<%=tabSet.id%>" ><%

  } else if (TabSet.END.equals(request.getAttribute(Commands.COMMAND))) {

    %>         </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
<%

  } else if (Tab.START.equals(request.getAttribute(Commands.COMMAND))) {
    Tab tab = (Tab) JspIncluderSupport.getCurrentInstance(request);

  %><!-- TAB <%=tab.caption%> START -->
    <div id="div_tabset_<%=tab.domId%>" style="<%= tab.tabSet.style!=null ? tab.tabSet.style : ""%><%= tab.focused ? "display:block;" : "display:none;"%>" thisTabsetId="<%=tab.tabSet.id%>" thisTabId="<%=tab.domId%>" isTabSetDiv="true"><%

  } else if (Tab.END.equals(request.getAttribute(Commands.COMMAND))) {
  %></div><%

  }
  %>
