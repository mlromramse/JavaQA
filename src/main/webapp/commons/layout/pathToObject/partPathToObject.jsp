<%@ page import="org.jblooming.ontology.PerformantNodeSupport,
                 org.jblooming.operator.Operator,
                 org.jblooming.persistence.PersistenceHome,
                 org.jblooming.security.Securable,
                 org.jblooming.utilities.JSP,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.button.ButtonLink,
                 org.jblooming.waf.html.container.DivOnMouseover,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.PathToObject,
                 org.jblooming.waf.view.PageState,
                 java.util.Collections,
                 java.util.Iterator,
                 java.util.List"%><%

  PageState pageState = PageState.getCurrentPageState();
  PathToObject pathToObject = (PathToObject) JspIncluderSupport.getCurrentInstance(request);

  if (PathToObject.INITIALIZE.equals(request.getAttribute(PathToObject.ACTION))) {

} else if (PathToObject.DRAW.equals(request.getAttribute(PathToObject.ACTION))) {
  %><div class="pathToObject"><%

  Operator logged = pageState.getLoggedOperator();
  PerformantNodeSupport node = pathToObject.node;
  boolean isSecurable = node instanceof Securable;

  if (node != null) {
    if (pathToObject.rootDestination != null) {
        pathToObject.rootDestination.enabled = true;
        pathToObject.rootDestination.toHtmlInTextOnlyModality(pageContext);
        %><%=pathToObject.separator%><%
    }

      if (pathToObject.destination == null) {
        pathToObject.destination = pageState.thisPage(request);
        pathToObject.destination.setCommand(Commands.EDIT);
      }

      boolean isNew =node.isNew();

      boolean isNewAndUseParent = pathToObject.useParentIfNew && isNew;

      PerformantNodeSupport parentNode = null;
      if (isNewAndUseParent) {
        String parId = pageState.getEntry(Fields.PARENT_ID).stringValueNullIfEmpty();
        if (parId != null)
          parentNode = (PerformantNodeSupport) PersistenceHome.findByPrimaryKey(pathToObject.mainClass, parId);
      }

      List<PerformantNodeSupport> ancestors = (isNew && parentNode!=null ? parentNode.getAncestors() : node.getAncestors());

      int ancestorSize = ancestors.size();
      if ((!isNew || ancestorSize > 1) || (isNew && parentNode!=null)) {

        // ----------- loop ancestors and itself
        for (int i = 0; i < ancestorSize; i++) {
          PerformantNodeSupport anc = ancestors.get(i);
          //peak of elegance
          if (isSecurable && (pathToObject.canClick!=null && !((Securable)anc).hasPermissionFor(logged,pathToObject.canClick)) )
            continue;

          //------------------------------compute brothers
          List<PerformantNodeSupport> ancBrothers = anc.getBrothers();
          if (JSP.ex(ancBrothers)) {
            if (pathToObject.comparator!=null)
               Collections.sort(ancBrothers,pathToObject.comparator);


            ButtonJS opener= new ButtonJS("");
            opener.iconChar="&ugrave;";
            DivOnMouseover ancDomo= new DivOnMouseover(opener);

            int counter = 1;

            for (PerformantNodeSupport broth: ancBrothers){
              if (isSecurable && (pathToObject.canClick!=null && !((Securable)anc).hasPermissionFor(logged,pathToObject.canClick)) )
                continue;

              pathToObject.destination.mainObjectId = broth.getId();
              ancDomo.addButton(ButtonLink.getTextualInstance(broth.getName(),pathToObject.destination.getNewInstance()));

              counter ++ ;

            }
            if (counter>1){
              ancDomo.toHtml(pageContext);
            }
          }

          // ------------------------ edit ancestor
          String label = anc.getName();
          label = StringUtilities.convertHtmlToTxt(label);

          pathToObject.destination.mainObjectId = anc.getId();
          ButtonLink edit = new ButtonLink(label, pathToObject.destination);

          if (i==ancestorSize-1){
            %><div class="currentNode"><%
            edit.toHtmlInTextOnlyModality(pageContext);
          } else {
            edit.toHtmlInTextOnlyModality(pageContext);
          }


          // ------------------------ separator
          if (i < ancestorSize - 1 && ancestorSize >0) {
            %><%=pathToObject.separator%><%
          }
        }


        if (!isNew) {
          Iterator it;
          if (pathToObject.comparator==null)
            it = node.getChildrenIteratorByName();
          else
            it = node.getChildrenIterator(pathToObject.comparator);

          %><script>child<%=node.getId().toString()%>= new Array();<%
          int counter = 1;

          ButtonJS opener= new ButtonJS("");
          opener.iconChar="&ugrave;";
          DivOnMouseover chdDomo= new DivOnMouseover(opener);

          while (it.hasNext()) {
            PerformantNodeSupport child = (PerformantNodeSupport) it.next();

            //peak of elegance
            if (isSecurable && (pathToObject.canClick!=null && !((Securable)child).hasPermissionFor(logged,pathToObject.canClick)) )
              continue;

            pathToObject.destination.mainObjectId = child.getId();

            chdDomo.addButton(ButtonLink.getTextualInstance(child.getName(),pathToObject.destination.getNewInstance()));
            counter ++ ;
          }
          %></script><%
          if ( counter>1 ) {
            chdDomo.toHtml(pageContext);
          }
        }

        %></div><%

      }
      if (isNew) {
        if (ancestorSize >0 && parentNode != null) {
          %><%=pathToObject.separator%><%
        }
        %>...<%
        }
      }

    %></div><%

  } else if (PathToObject.CLOSE.equals(request.getAttribute(PathToObject.ACTION))){

 }

%>