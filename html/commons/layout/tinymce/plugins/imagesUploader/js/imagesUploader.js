tinyMCEPopup.requireLangPack();
var ImagesUploaderDialog = {
	init : function(ed) {
		tinyMCEPopup.resizeToInnerSize();
	},
	insert : function(file, title) {
		var ed = tinyMCEPopup.editor, dom = ed.dom;
		tinyMCEPopup.execCommand('mceInsertContent', false, dom.createHTML('img', {
			src : file,
//			alt : ed.getLang(title),
			title : title,
			border : 0
		}));
		tinyMCEPopup.close();
	}
};
tinyMCEPopup.onInit.add(ImagesUploaderDialog.init, ImagesUploaderDialog);