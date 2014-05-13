function createDropDown(txtField, ifrWidth, ifrHeight) {
  var hiddField=txtField.nextAll("input:hidden:first");
  var dropDown=txtField.nextAll(".cbDropDown:first");
  if (dropDown.size()==0) {
    dropDown = $("<span>");
    dropDown.addClass("cbDropDown");
    dropDown.css({position:"absolute",width:ifrWidth?ifrWidth:300,height : ifrHeight ? ifrHeight : 100,overflow:"auto"});
    dropDown.css({"margin-left":-txtField.outerWidth(),"margin-top":txtField.outerHeight()});
    dropDown.css({"background-color":"white","visibility":"hidden"}).bringToFront();
    dropDown.css({"border":"1px solid #ccc"});

    dropDown.attr( "fieldId",hiddField.attr("id"));
    txtField.after(dropDown);
  }
  dropDown.keepItVisible();

}

function refreshDropDown(dropDown, txt, key) {
  row = 0;
  var hiddField = dropDown.nextAll("input:hidden:first");

  txt = txt.replace(/&backslash;/g, '\\');
  dropDown.load(contextPath+"/commons/layout/smartCombo/partSmartCombo.jsp",
  {id: dropDown.attr("fieldId"),
    filter: txt,
    key:key,
    hiddenValue:hiddField.val(),
    ststrnz: (new Date()).getTime()}, function() {
    dropDown.show();
  });
}

function finalizeOperation(dropDown, required, addAllowed) {

  var txtField = dropDown.prevAll("input:text:first");
  var hidField = dropDown.nextAll("input:hidden:first");

  if (required) {
    if (addAllowed) {
      if (txtField.val() == '') {
        txtField.addClass('formElementsError');
      } else {
        txtField.removeClass('formElementsError');
      }
    } else {
      if (hidField.val() == '') {
        txtField.addClass('formElementsError');
      } else {
        txtField.removeClass('formElementsError');
      }
    }
  } else {
    if (!addAllowed && (txtField.val() != '' && hidField.val() == '')) {
      txtField.addClass('formElementsError');
    } else {
      txtField.removeClass('formElementsError');
    }
  }

  //hide image linking to entity on new selection
  var linkedEnt = txtField.nextAll("._lnk:first");
  if (linkedEnt) {
    if (hidField.val() != hidField.attr('oldValue')) {
      linkedEnt.hide();
    } else {
      linkedEnt.show();
    }
  }

  dropDown.remove();

  var fieldId=hidField.attr("id");
  try {
    if (eval("typeof(letSubmit" + fieldId+")") == 'function' ){
      eval("letSubmit" + fieldId + "()");
    }
  } catch (e){}
}

function scrollDropDown(dropDown, inc) {
  dropDown.scrollTop(inc);
}

function stopKeyEvent(event) {
  var ret=true;
  switch (event.keyCode) {
    case 13: //enter
      stopBubble(event);
      ret= false;
      break;
  }
  return ret;
}

var row = 0;
var theRow = false;

function manageKeyEvent(txtField, oEvent, required, addAllowed) {
  var dropDown=txtField.nextAll(".cbDropDown:first");
  var totalRow=dropDown.find("tr[selectValue]").size()+1;
  var hidField = txtField.nextAll("input:hidden:first");

  var refreshQueue;
  var ret = true;
  switch (oEvent.keyCode) {

    case 38: //up arrow
      var nextRowId = row > 1 ? "ROW_" + row : "ROW_1";
      if (row > 1) {
        row--;
        var rowId = "ROW_" + row;
        theRow = dropDown.find("#"+rowId);
        thePrevRow = dropDown.find("#"+nextRowId);
        if (theRow.position().top<0)
          dropDown.scrollTop(dropDown.scrollTop()-theRow.outerHeight()-3);
        theRow.addClass("trSel");
        if (thePrevRow.size()>0)
          thePrevRow.removeClass("trSel");
        return false;
      }
      break;

    case 40: //down arrow
      var prevRowId = "ROW_" + (row);
      if (row < totalRow - 1) {
        row++;
        var rowId = "ROW_" + row;
        theRow = dropDown.find("#"+rowId);
        thePrevRow = dropDown.find("#"+prevRowId);

        if (theRow.position().top>(dropDown.height()-theRow.outerHeight()))
          dropDown.scrollTop(dropDown.scrollTop()+theRow.outerHeight()+3);

        theRow.addClass("trSel");
        if (thePrevRow.size()>0)
          thePrevRow.removeClass("trSel");
        return false;
      }
      break;

    case 33: //page up
      var prevRowId = row > 1 ? "ROW_" + row : "ROW_1";
      if (row > 1) {
        row = 1;
        var rowId = "ROW_" + row;
        theRow = dropDown.find("#"+rowId);
        thePrevRow = dropDown.find("#"+prevRowId);
        dropDown.scrollTop(0);
        theRow.addClass("trSel");
        if (thePrevRow.size()>0){
          thePrevRow.removeClass("trSel");
        }
        return false;
      }
      break;
  
    case 34: //page down
      var nextRowId = row > 1 ? "ROW_" + row : "ROW_1";
      if (row < totalRow - 1) {
        row = totalRow - 1;
        var rowId = "ROW_" + row;

        theRow = dropDown.find("#"+rowId);
        thePrevRow = dropDown.find("#"+nextRowId);

        dropDown.scrollTop(20000);
        theRow.addClass("trSel");
        if (thePrevRow.size()>0){
          thePrevRow.removeClass("trSel");
        }
        return false;
      }
      break;

    // WARNING added on 12/12/2007 in order to manage correctly combo.addAllowed
    case 37: //left arrow
    case 39: //right arrow
    case 36: //home
    case 35: //end
    case 27: //esc
    case 16: //shift
    case 17: //ctrl
    case 18: //alt
    case 20: //caps lock
      row = 0;
      var cleanedText = txtField.val().replace(/\\/g, '&backslash;');
      dropDown.stopTime("refreshQueue");
      dropDown.oneTime(500,"refreshQueue",function(){
        refreshDropDown($(this),cleanedText,"");
      });
      break;

    case 255: // ???
      break;


    case 8: //backspace
    case 46: //delete
      hidField.val("");
      var cleanedText = txtField.val().replace(/\\/g, '&backslash;');
      dropDown.stopTime("refreshQueue");
      dropDown.oneTime(500,"refreshQueue",function(){
        refreshDropDown($(this),cleanedText,"");
      });
      break;


    case 9:  case 13: //enter
    if (theRow) {
      stopBubble(oEvent);
      txtField.val($(theRow).attr('selectText'));
      hidField.val($(theRow).attr('selectValue'));
      txtField.get(0).blur();

      row = 0;
      theRow = false;
      return false;
      break;
    }


    default:
       hidField.val( "");  // WARNING added on 12/12/2007 in order to manage correctly combo.addAllowed
      row = 0;
      var cleanedText = txtField.val().replace(/\\/g, '&backslash;');
      dropDown.stopTime("refreshQueue");
      dropDown.oneTime(500,"refreshQueue",function(){
        refreshDropDown($(this),cleanedText,"");
      });
      break;
  }
  return false;
}


function removeSmartComboEntry(smartComboName) {
  obj(smartComboName).value = '';
  obj(smartComboName + '_txt').value = '';
}

function setSmartComboEntry(smartComboName, code, descr) {
  obj(smartComboName).value = code;
  obj(smartComboName + '_txt').value = descr;
}

