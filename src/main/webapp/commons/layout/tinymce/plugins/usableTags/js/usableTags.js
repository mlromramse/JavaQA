tinyMCEPopup.requireLangPack();
var UsableTagsDialog = {
	init : function(ed) {
		tinyMCEPopup.resizeToInnerSize();
	},
	insert : function(tag, title) {
   	tinyMCEPopup.execCommand('mceInsertContent', false, tag);
		tinyMCEPopup.close();
	}
};
tinyMCEPopup.onInit.add(UsableTagsDialog.init, UsableTagsDialog);