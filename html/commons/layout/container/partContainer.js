
jQuery.fn.centerOnScreen= function () {
  return this.each(function () {
    var container = $(this);
    container.css("position","absolute");
    container.css("top", (document.body.scrollTop + (document.body.clientHeight - container.outerHeight()) / 2) + 'px');
    container.css("left", (document.body.scrollLeft + (document.body.clientWidth - container.outerWidth()) / 2) + 'px');
  });
};



jQuery.fn.containerBuilder = function () {
  return this.each(function () {
    var container = $(this);
    //console.debug("containerBuilder",container);
    var id = container.attr("id");
    var sendDataToServer = container.is("[saveStatus]");
    var suffix = container.attr("cmdSuffix");
    var status = container.attr("status");

    if (container.hasClass("draggable")) {
      var params = {
        handle:".containerTitle",
        stack:".container.draggable",
        stop: function(event, ui) {
          if (sendDataToServer) {
            executeCommand("MOVE" + suffix, "DOM_ID=" + id + "&X=" + container.position().left + "&Y=" + container.position().top);
          }
        }
      };
      if (container.attr("containment"))
        params.containment=container.attr("containment");
      
      container.draggable(params);

    }
    if (container.hasClass("resizable")) {
      container.resizable({
        stop: function(){
          if (sendDataToServer) {
            executeCommand("RESIZE" + suffix, "DOM_ID=" + id + "&W=" + container.outerWidth() + "&H=" + container.height());
          }
        },
        resize: function (e,ui){
          var container = $(this);
          var newH = container.height()-container.find(".containerTitle:first").height();
          container.find(".containerBody:first").height(newH);
        }
      });
    }

    if (container.hasClass("centeredOnScreen")) {
      console.debug("centeredOnScreen");
      container.centerOnScreen();
      if (status != "HIDDEN")
        container.show();
      
    }

    if (container.hasClass("collapsable")) {
      // everything is done by css 
    }

    if (container.hasClass("closeable")) {
      container.find(".stsHide").show();
    }

    if (container.hasClass("iconizable")) {
      container.find(".stsIconize").show();
    }


    //events on container

    container.bind("iconize", function(e) {
      //todo implementare iconize
      e.stopPropagation();

    }).bind("hide", function(e) {
      e.stopPropagation();
      container.hide();
      container.attr("status","HIDDEN");
      if (sendDataToServer)
        executeCommand("HIDE" + suffix, "DOM_ID=" + id);

    }).bind("show", function(e) {
      e.stopPropagation();
      container.show();
      container.attr("status","DEFAULT");
      if (sendDataToServer)
        executeCommand("SHOW" + suffix, "DOM_ID=" + id);

    }).bind("collapse", function(e) {
      e.stopPropagation();
      container.attr("status","COLLAPSED");
      if (sendDataToServer)
        executeCommand("COLLAPSE" + suffix, "DOM_ID=" + id);


    }).bind("restore", function(e) {
      e.stopPropagation();
      container.attr("status","DEFAULT");
      if (sendDataToServer)
        executeCommand("RESTORE" + suffix, "DOM_ID=" + id);

    }).bind("toggle", function(e) {
      e.stopPropagation();
      if($(this).attr("status")=="HIDDEN")
        container.trigger("show");
      else
        container.trigger("hide");
    });

  });
};


/*
      collapseScript = "contractContent('ctdivbdid_" + container.getId() + "');" +
              ButtonJS.getCommandJS(command,SystemConstants.DOM_ID,container.getId()) +
              "swapStatusImage('" + container.getContainerDivId() + "','imgSts" + container.getId() + "','" + imgPath + "container/max.png','" + imgPath + "container/min.png');";

 */


