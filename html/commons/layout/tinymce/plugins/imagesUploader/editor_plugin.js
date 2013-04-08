/**
 * $Id: editor_plugin_src.js 520 2008-01-07 16:30:32Z spocke $
 *
 * @author OpenLab
 * @copyright Copyright 2004-2008, OpenLab, All rights reserved.
 */
(function() {
	tinymce.create('tinymce.plugins.ImagesUploaderPlugin', {
		init : function(ed, url) {
		   //imageManagerData='PATH='+relPath+'&objectClass='+tinyMCE.settings['objectClass']+'&imageSearchField='+tinyMCE.settings['imageSearchField'];
			// Register commands
			ed.addCommand('mceImagesUploader', function() {
			  if (typeof objectId =='undefined')
			    objectId = '';
			  if (typeof objectClass =='undefined')
			    objectClass = '';

				ed.windowManager.open({
				  file : url + '/imagesUploader.jsp?objectId='+objectId+'&objectClass='+objectClass + '&ts='+new Date().getMilliseconds(),
					width : 700 + parseInt(ed.getLang('imagesUploader.delta_width', 0)),
					height : 480 + parseInt(ed.getLang('imagesUploader.delta_height', 0)),
					inline : 1
				}, {
					plugin_url : url, // Plugin absolute URL
					some_custom_arg : 'custom arg' // Custom argument
				});
			});
			// Register buttons
			ed.addButton('imagesUploader', {title : 'Images Uploader', cmd : 'mceImagesUploader', image : url + '/images/imagesUploader.gif'});
		},
		getInfo : function() {
			return {
				longname : 'ImagesUploader plugin',
				author : 'OpenLab',
				authorurl : 'http://www.open-lab.com',
				version : "1.0"
			};
		}
	});
	// Register plugin
	tinymce.PluginManager.add('imagesUploader', tinymce.plugins.ImagesUploaderPlugin);
})();