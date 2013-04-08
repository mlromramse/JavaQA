/**
 * $Id: editor_plugin_src.js 520 2008-01-07 16:30:32Z spocke $
 *
 * @author OpenLab
 * @copyright Copyright 2004-2008, OpenLab, All rights reserved.
 */
(function() {
	tinymce.create('tinymce.plugins.ImagesManagerPlugin', {
		init : function(ed, url) {
		   //imageManagerData='PATH='+relPath+'&objectClass='+tinyMCE.settings['objectClass']+'&imageSearchField='+tinyMCE.settings['imageSearchField'];
			// Register commands
			ed.addCommand('mceImagesManager', function() {			  
				ed.windowManager.open({
					file : url + '/imagesManager.jsp?PATH='+relPath+'&objectClass='+tinyMCE.settings['objectClass']+'&imageSearchField='+tinyMCE.settings['imageSearchField'],
					width : 700 + parseInt(ed.getLang('imagesManager.delta_width', 0)),
					height : 480 + parseInt(ed.getLang('imagesManager.delta_height', 0)),
					inline : 1
				}, {
					plugin_url : url, // Plugin absolute URL
					some_custom_arg : 'custom arg' // Custom argument
				});
			});
			// Register buttons
			ed.addButton('imagesManager', {title : 'Images Manager', cmd : 'mceImagesManager', image : url + '/images/imagesManager.gif'});
		},
		getInfo : function() {
			return {
				longname : 'ImagesManager plugin',
				author : 'OpenLab',
				authorurl : 'http://www.open-lab.com',
				version : "1.0"
			};
		}
	});
	// Register plugin
	tinymce.PluginManager.add('imagesManager', tinymce.plugins.ImagesManagerPlugin);
})();