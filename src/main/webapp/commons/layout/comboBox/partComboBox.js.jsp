<style>
  .trSel {
    background-color: yellow;
  }

  .trNormal {
    background-color: white;
  }


</style>

<script>
function cb_manageKeyEvent(comboType, inputField, keyCode) {
  var div = obj(comboType + "_DIV");
  var totalRow = obj(comboType + "_DIV").getAttribute("totRows");
  var row = obj(comboType + "_DIV").getAttribute("currentRow");
  var isActive = (div.style.display != 'none');

  cb_unSelectRow(div, comboType);

  switch (keyCode) {
    case 38: //up arrow
      if (isActive) {
        row--;
        if (row < 1)
          row = 1;
        var theRow = obj(comboType + "_ROW_" + row);
        if (div.scrollTop > theRow.offsetTop) {
          div.scrollTop = theRow.offsetTop;
        }
        theRow.setAttribute("oldClass", theRow.className);
        theRow.className = "trSel";
        div.setAttribute("currentRow", row);
        return false;
      }
      break;

    case 40: //down arrow
      if (!isActive) {
        div.style.display = '';
        if (row == 0) {
          // guess the line if you scriv something
          // window.document.title="bbbb";
          var inputValue = obj(inputField).value.toLowerCase();
          if (inputValue != '') {
            for (var i = 1; i <= totalRow; i++) {
              var candRow = obj(comboType + "_ROW_" + i);
              if (candRow.getAttribute("value").toLowerCase().indexOf(inputValue) == 0) {
                row = i;
                div.scrollTop = (obj(comboType + "_ROW_" + row).offsetTop );
                break;
              }
            }
          } else {
            row = 1;
            div.scrollTop = (obj(comboType + "_ROW_" + row).offsetTop );
          }
        }
      } else {
        row++;
      }
      if (row > totalRow)
        row = totalRow;
      var theRow = obj(comboType + "_ROW_" + row);
      if (theRow) {            // if the actual content is not listed escape the selection
        if ((theRow.offsetTop + theRow.offsetHeight) > (div.scrollTop + div.offsetHeight)) {
          var dif = theRow.offsetTop + theRow.offsetHeight - div.scrollTop - div.offsetHeight;
          div.scrollTop = div.scrollTop + dif;
        }
        theRow.setAttribute("oldClass", theRow.className);
        theRow.className = "trSel";
        div.setAttribute("currentRow", row);
        return false;
      }
      break;

    case 13: //enter
      if (isActive) {
        var theRow = obj(comboType + "_ROW_" + row);
        if (theRow) {
          theRow.className = theRow.getAttribute("oldClass");
          cb_clickRow(theRow, comboType);
        }
      }
      $("#"+comboType + '_DIV').hide();
      break;

    default:
      div.setAttribute("currentRow", 0);
      $("#"+comboType + '_DIV').hide();
      break;
  }
}

function cb_clickRow(tr, comboType) {
  var comboDiv = $("#"+comboType + '_DIV');
  var fld=obj(comboDiv.attr('openerId'))
  fld.value = tr.getAttribute('value');
  $("#"+comboType + '_DIV').hide();
  eval(comboDiv.attr("onSelectScript"))<%--=JSP.w(comboBox.onSelectScript)--%>
}

function cb_mouseOverRow(tr, comboType, row) {
  var div = obj(comboType + '_DIV');
  var currentRow = div.getAttribute("currentRow");
  var theRow = obj(comboType + "_ROW_" + currentRow);
  if (theRow)
    theRow.className = theRow.getAttribute("oldClass");
  div.setAttribute('currentRow', row);
  tr.setAttribute('oldclass', tr.className);
  tr.className = 'trSel';
}

function cb_unSelectRow(div, comboType) {
  var oldRow = obj(comboType + "_ROW_" + div.getAttribute("currentRow"));
  if (oldRow)
    oldRow.className = oldRow.getAttribute("oldClass");
}

function cb_initializeCombo(theComb, comboType, fieldName,event) {
  nearBestPosition(fieldName, comboType + "_DIV");
  obj(comboType + "_DIV").setAttribute('openerId', fieldName);
  bringToFront(comboType + "_DIV");
  obj(fieldName).setAttribute("dontHide",'false');

  var div = obj(comboType + "_DIV");
  cb_unSelectRow(div, comboType);
  div.setAttribute("currentRow", 0);

  //simul il pig freccia down
  cb_manageKeyEvent(comboType, fieldName, 40);

  //select text
  theComb.select();
}


</script>


