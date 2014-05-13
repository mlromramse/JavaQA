//var loadedTab;
loadedTabs = new Array();
function getIndex(val) {
  for (i = 0; i < loadedTabs.length; i++) {
    if (loadedTabs[i] == val) return i;
  }
  return -1;
}

function loadTabSet(href, tabSetId) {
  if (getIndex(tabSetId) == -1) {
    loadSyncroContent(href, "div_tabset_" + tabSetId);
    loadedTabs.push(tabSetId);
  }
}

function hideTabsAndShow(tabId, tabSetId) {
  $("#TRTABSET_" + tabSetId + " > td:not(.tabDisabled)").removeClass('tabSelected').addClass('tabUnselected');
  $("#TABSETPART_" + tabSetId + " > div").hide();
  $("#div_tabset_" + tabId).show();

  $("#" + tabId).removeClass('tabUnselected').addClass('tabSelected');
}

function setTabHiddenValue(tabId, tabSetId) {
  $("#" + tabSetId).val(tabId);
}

$(document).ready(function() {
  // show tabset with errors
  var errSpan = $("span.errImg");
  if (errSpan.length > 0) {
    var theTabDiv = errSpan.eq(0).parents("[isTabSetDiv='true']");
    if (theTabDiv.length > 0)
      hideTabsAndShow(theTabDiv.attr("thisTabId"), theTabDiv.attr("thisTabsetId"));
  }
});
