<%@ page import =" org.jblooming.page.Page,
                    org.jblooming.utilities.JSP,
                    org.jblooming.waf.constants.I18nConstants,
                    org.jblooming.waf.html.button.ButtonImg,
                    org.jblooming.waf.html.button.ButtonSubmit,
                    org.jblooming.waf.html.core.JspIncluderSupport,
                    org.jblooming.waf.html.display.Img,
                    org.jblooming.waf.html.display.Paginator,
                    org.jblooming.waf.html.input.TextField,
                    org.jblooming.waf.html.layout.Skin,
                    org.jblooming.waf.html.state.Form,
                    org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState"%><%

  PageState pageState = PageState.getCurrentPageState();
  Paginator paginator = (Paginator) JspIncluderSupport.getCurrentInstance(request);
  Skin skin = pageState.sessionState.getSkin();
  String imgPath = skin.imgPath;
  final Page currentPage = pageState.getPage();
  Form f = paginator.form;

  if (Paginator.Modality.DEFAULT.equals(paginator.modality)) {

    Img img = new Img(imgPath + "first.png", I18nConstants.FIRST, "12", "12");
    ButtonSubmit button = new ButtonSubmit(f);
    ButtonImg bi = new ButtonImg(button, img);

    String cTL = "containerTitle_light";

%>
<table align="left" cellpadding="1" cellspacing="0" border="0" class="paginator">
  <tr ><%
    if (currentPage != null) { %>
      <script language="javascript">
        function newNumberOfPages (pageSizeFieldId) {
          var pageSize = obj(pageSizeFieldId).value;
          var numberOfElements = <%=currentPage.getTotalNumberOfElements()%>;
          return Math.ceil(numberOfElements/pageSize);
        }
      </script><%
        final int lastPageNumber = currentPage != null ? currentPage.getLastPageNumber() : 0;

        final int pageNumber = currentPage.getPageNumber();
        pageState.addClientEntry(Paginator.FLD_PAGE_NUMBER, pageNumber+1);
        //TextField tf = new TextField("", Paginator.FLD_PAGE_NUMBER, "</td><td>", 2, false);
        TextField tf = TextField.getIntegerInstance(Paginator.FLD_PAGE_NUMBER);
        tf.separator="</td><td>";
        tf.fieldSize=4;
        tf.label="";
        tf.fieldClass = "paginator";
        tf.preserveOldValue= false;    //Silvia Chelazzi 26-05-2008
        tf.addKeyPressControl(13,
                  "if (obj('"+tf.id+"').value>newNumberOfPages('"+Paginator.FLD_PAGE_SIZE+"')) obj('"+tf.id+"').value=newNumberOfPages('"+Paginator.FLD_PAGE_SIZE+"');" +
                  "obj('" + pageState.getForm().getUniqueName() + "').submit();", "onkeyup");

        %> <td ><%=currentPage.getTotalNumberOfElements()%>&nbsp;<%=paginator.objectsFound%>;&nbsp;</td><%
          if (pageNumber > 1) {
            button.additionalOnClickScript= "obj('"+tf.id+"').value='"+(1)+"';";
            %> <td ><% bi.toHtml(pageContext); %></td><%
          }

          if (currentPage.hasPreviousPage()) {
            button.additionalOnClickScript= "obj('"+tf.id+"').value='"+(pageNumber)+"';";
            img.imageUrl = imgPath + "previous.png";
            /**********************************************/
          
            img.toolTip = paginator.previous;
            %> <td> <% bi.toHtml(pageContext); %></td><%
          }
          %> <td> <%=paginator.page%>&nbsp; <%


          %> <%tf.toHtml(pageContext);%></td><td ><%=paginator.of%>&nbsp;<%=lastPageNumber + 1%></td><%
          if (currentPage.hasNextPage()) {

            button.additionalOnClickScript= "obj('"+tf.id+"').value='"+(pageNumber + 2)+"';";
            img.imageUrl = imgPath + "next.png";
            img.toolTip = paginator.next;
            %> <td valign="middle"><% bi.toHtml(pageContext); %></td><%
          }

          if (pageNumber + 1 < lastPageNumber) {
            button.additionalOnClickScript= "obj('"+tf.id+"').value='"+(lastPageNumber+1)+"';";
            img.imageUrl = imgPath + "last.png";
            img.toolTip = paginator.last;
            %> <td valign="middle"><% bi.toHtml(pageContext); %></td> <%
          }
         %><td valign="middle">&nbsp;&nbsp;<%=paginator.pageSize%>&nbsp; <%
          pageState.addClientEntry(Paginator.FLD_PAGE_SIZE, currentPage.getPageSize());
          TextField psize = TextField.getIntegerInstance(Paginator.FLD_PAGE_SIZE);
          psize.label="";
          psize.separator= "</td><td>";
          psize.fieldSize=4;
          psize.fieldClass = "paginator";
          psize.preserveOldValue=  false;
          psize.addKeyPressControl(13, "obj('" + Paginator.FLD_PAGE_NUMBER + "').focus();", "onkeyup");
          %><%psize.toHtml(pageContext);%></td><%

        if (paginator.showGoLink) {
          ButtonSubmit bs = new ButtonSubmit(f);
          img = new Img(imgPath+"reload.png", I18n.get("UPDATE"));
          ButtonImg reloadImg = new ButtonImg(bs,img);
          
          %><td valign="middle"><%reloadImg.toHtml(pageContext);%></td><%
        }

      } else {
        %> <td valign="middle" class="<%=cTL%>"> <%=paginator.no_filters%>.</td><%
      }
    %>
  </tr> </table><%
  } else if (Paginator.Modality.REPORT.equals(paginator.modality)) {

    int from = (currentPage.getPageNumber()*currentPage.getPageSize())+1;
    int to = Math.min(from+currentPage.getPageSize()-1,currentPage.getTotalNumberOfElements());

    %><b><%=from%>-<%=to%></b> <%=paginator.of%> <b><%=currentPage.getTotalNumberOfElements()%></b><%
  }else if(Paginator.Modality.EXTENDED.equals(paginator.modality)) {%>
    <b><%=JSP.w(paginator.paginatorTitle)%></b>   
  <%}

  %>
