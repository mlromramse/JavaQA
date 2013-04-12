<%@ page
        import="com.QA.QAOperator, com.QA.Question, com.QA.QuestionRevision, com.QA.Tag, com.QA.waf.QAScreenApp, com.QA.waf.UserDrawer,
         org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState, java.util.ArrayList, java.util.Date, java.util.List" %>
<%!
  class  QuestionRevisionLocal  {

    private QAOperator editor;
    private Date revisionDate;
    private Question revisionOf;

    private String formerSubject;
    private String formerDescription;
    private List<Tag> formerTags = new ArrayList();

    public QuestionRevisionLocal() {

    }

      public QuestionRevisionLocal(QuestionRevision questionRevision) {

           super();
          this.formerDescription = questionRevision.getFormerDescription();

          this.formerSubject = questionRevision.getFormerSubject();
          this.formerTags = questionRevision.getFormerTags();
          this.revisionOf = questionRevision.getRevisionOf();
          this.editor = questionRevision.getEditor();
          this.revisionDate = questionRevision.getRevisionDate();
      }

  }
%>
<%
  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    lw.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME") + " - " + I18n.g("QA_QUESTION_REVISIONS");
    pageState.perform(request, response);
    pageState.toHtml(pageContext);

  } else {

    Question q = Question.load(pageState.mainObjectId);

%><div id="content">
<script>/*type="text/javascript" src="../js/jsdiff.js"*/

    /*
     * Javascript Diff Algorithm
     *  By John Resig (http://ejohn.org/)
     *  Modified by Chu Alan "sprite"
     *
     * Released under the MIT license.
     *
     * More Info:
     *  http://ejohn.org/projects/javascript-diff-algorithm/
     */

    function escapex(s) {
        var n = s;
        n = n.replace(/&/g, "&amp;");
        n = n.replace(/</g, "&lt;");
        n = n.replace(/>/g, "&gt;");
        n = n.replace(/"/g, "&quot;");

        return n;
    }

    function diffString( o, n ) {
        o = o.replace(/\s+$/, '');
        n = n.replace(/\s+$/, '');

        var out = diff(o == "" ? [] : o.split(/\s+/), n == "" ? [] : n.split(/\s+/) );
        var str = "";

        var oSpace = o.match(/\s+/g);
        if (oSpace == null) {
            oSpace = ["\n"];
        } else {
            oSpace.push("\n");
        }
        var nSpace = n.match(/\s+/g);
        if (nSpace == null) {
            nSpace = ["\n"];
        } else {
            nSpace.push("\n");
        }

        if (out.n.length == 0) {
            for (var i = 0; i < out.o.length; i++) {
                str += '<del>' + escapex(out.o[i]) + oSpace[i] + "</del>";
            }
        } else {
            if (out.n[0].text == null) {
                for (n = 0; n < out.o.length && out.o[n].text == null; n++) {
                    str += '<del>' + escapex(out.o[n]) + oSpace[n] + "</del>";
                }
            }

            for ( var i = 0; i < out.n.length; i++ ) {
                if (out.n[i].text == null) {
                    str += '<ins>' + escapex(out.n[i]) + nSpace[i] + "</ins>";
                } else {
                    var pre = "";

                    for (n = out.n[i].row + 1; n < out.o.length && out.o[n].text == null; n++ ) {
                        pre += '<del>' + escapex(out.o[n]) + oSpace[n] + "</del>";
                    }
                    str += " " + out.n[i].text + nSpace[i] + pre;
                }
            }
        }

        return str;
    }

    function randomColor() {
        return "rgb(" + (Math.random() * 100) + "%, " +
                (Math.random() * 100) + "%, " +
                (Math.random() * 100) + "%)";
    }
    function diffString2( o, n ) {
        o = o.replace(/\s+$/, '');
        n = n.replace(/\s+$/, '');

        var out = diff(o == "" ? [] : o.split(/\s+/), n == "" ? [] : n.split(/\s+/) );

        var oSpace = o.match(/\s+/g);
        if (oSpace == null) {
            oSpace = ["\n"];
        } else {
            oSpace.push("\n");
        }
        var nSpace = n.match(/\s+/g);
        if (nSpace == null) {
            nSpace = ["\n"];
        } else {
            nSpace.push("\n");
        }

        var os = "";
        var colors = new Array();
        for (var i = 0; i < out.o.length; i++) {
            colors[i] = randomColor();

            if (out.o[i].text != null) {
                os += '<span style="background-color: ' +colors[i]+ '">' +
                        escapex(out.o[i].text) + oSpace[i] + "</span>";
            } else {
                os += "<del>" + escapex(out.o[i]) + oSpace[i] + "</del>";
            }
        }

        var ns = "";
        for (var i = 0; i < out.n.length; i++) {
            if (out.n[i].text != null) {
                ns += '<span style="background-color: ' +colors[out.n[i].row]+ '">' +
                        escapex(out.n[i].text) + nSpace[i] + "</span>";
            } else {
                ns += "<ins>" + escapex(out.n[i]) + nSpace[i] + "</ins>";
            }
        }

        return { o : os , n : ns };
    }

    function diff( o, n ) {
        var ns = new Object();
        var os = new Object();

        for ( var i = 0; i < n.length; i++ ) {
            if ( ns[ n[i] ] == null )
                ns[ n[i] ] = { rows: new Array(), o: null };
            ns[ n[i] ].rows.push( i );
        }

        for ( var i = 0; i < o.length; i++ ) {
            if ( os[ o[i] ] == null )
                os[ o[i] ] = { rows: new Array(), n: null };
            os[ o[i] ].rows.push( i );
        }

        for ( var i in ns ) {
            if ( ns[i].rows.length == 1 && typeof(os[i]) != "undefined" && os[i].rows.length == 1 ) {
                n[ ns[i].rows[0] ] = { text: n[ ns[i].rows[0] ], row: os[i].rows[0] };
                o[ os[i].rows[0] ] = { text: o[ os[i].rows[0] ], row: ns[i].rows[0] };
            }
        }

        for ( var i = 0; i < n.length - 1; i++ ) {
            if ( n[i].text != null && n[i+1].text == null && n[i].row + 1 < o.length && o[ n[i].row + 1 ].text == null &&
                    n[i+1] == o[ n[i].row + 1 ] ) {
                n[i+1] = { text: n[i+1], row: n[i].row + 1 };
                o[n[i].row+1] = { text: o[n[i].row+1], row: i + 1 };
            }
        }

        for ( var i = n.length - 1; i > 0; i-- ) {
            if ( n[i].text != null && n[i-1].text == null && n[i].row > 0 && o[ n[i].row - 1 ].text == null &&
                    n[i-1] == o[ n[i].row - 1 ] ) {
                n[i-1] = { text: n[i-1], row: n[i].row - 1 };
                o[n[i].row-1] = { text: o[n[i].row-1], row: i - 1 };
            }
        }

        return { o: o, n: n };
    }

</script>
  <h2><span><%=I18n.g("QUESTION_REVISIONS")%></span></h2>

  <div class="QAMenu actions">

    <ul><li><a href="<%=q.getURL().toLinkToHref()%>" class="backBtn">&nbsp;&nbsp;&nbsp;<%=I18n.g("QUESTION_BACK")%>&nbsp;&nbsp;&nbsp;</a></li></ul>
  </div>

  <div class="contentBox revisions"><%

  if (q.getQuestionRevisions().size() > 0) {

    List<QuestionRevisionLocal> questionRevisions = new ArrayList();

    //add a fake one
    QuestionRevisionLocal current = new QuestionRevisionLocal();
    current.formerDescription = q.getDescription();

    current.formerSubject = q.getSubject();
    current.formerTags = q.getTags();
    current.revisionOf = q;
    current.editor = q.getOwner();
    current.revisionDate = new Date();//q.getLastModified();
    questionRevisions.add(current);


    for (QuestionRevision qr : q.getQuestionRevisions()) {
      questionRevisions.add(new QuestionRevisionLocal(qr));
    }

    QuestionRevisionLocal latest = null;
    QuestionRevisionLocal previous = null;
    boolean firstDiff = true;
      int prog = 0;
    for (QuestionRevisionLocal qr : questionRevisions) {

        prog++;

    //latest revision
    if (latest==null) {
      latest=qr;
      continue;
    }
%>
  <hr><%=I18n.g("QUESTION_REVISIONS_OF")%><h3 class="questionTitle"><a href="<%=q.getURL().toLinkToHref()%>"><%=q.getSubject()%></a></h3>
  <div>

    <div style="display: inline-block;"><%=I18n.g("QUESTION_REVISIONS_FROM")%>
  <%

    QAOperator rev = latest.editor;
/*

    if (firstDiff) {
      rev = q.getOwner();
      firstDiff = false;
    } else {
      rev = previous.editor;
    }
*/
  %>

  <%new UserDrawer(rev,true,30).toHtml(pageContext);%>
  <%=JSP.w(latest.revisionDate)%></div>

    <div style="display: inline-block;"><%=I18n.g("QUESTION_REVISIONS_TO")%>

  <%
    rev = qr.editor;
    /*if (qr.equals(current)) {
      rev =  latest.getEditor();
    } */
  %>
  <%new UserDrawer(rev,true,30).toHtml(pageContext);%>
  <%=JSP.w(qr.revisionDate)%></div></div><%

%><br>Subject:
      <div id="Rev_<%=prog%>"></div>
<script>
  $("#Rev_<%=prog%>").html(diffString(
          "<%=JSP.javascriptEncode(qr.formerSubject)%>",
          "<%=JSP.javascriptEncode(latest.formerSubject)%>"));
</script>
<br><br>

Question:
      <div id="Revq_<%=prog%>"></div>

      <script>
          $("#Revq_<%=prog%>").html(diffString(
          "<%=JSP.javascriptEncode(qr.formerDescription)%>",
          "<%=JSP.javascriptEncode(latest.formerDescription)%>"));
</script>
<br><br>
<%
    previous = latest;
    latest = qr;

  }
    } else {
      %><%=I18n.g("QUESTION_HAS_NO_REVISIONS_YET")%><%
    }

    %></div>

</div><%

  }
%>
