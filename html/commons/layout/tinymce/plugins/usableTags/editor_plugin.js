/**
 * $Id: editor_plugin_src.js 520 2008-01-07 16:30:32Z spocke $
 *
 * @author OpenLab
 * @copyright Copyright � 2004-2008, OpenLab, All rights reserved.
 */
//var imageManagerData;
(function() {
	tinymce.create('tinymce.plugins.UsableTagsPlugin', {
		init : function(ed, url) {
			// Register commands
			ed.addCommand('mceUsableTags', function() {
				ed.windowManager.open({
					file : url + '/usableTags.jsp',
					width : 700 + parseInt(ed.getLang('usableTags.delta_width', 0)),
					height : 480 + parseInt(ed.getLang('usableTags.delta_height', 0)),
					inline : 1
				}, {
					plugin_url : url, // Plugin absolute URL
					some_custom_arg : 'custom arg' // Custom argument
				});
			});
			// Register buttons
			ed.addButton('usableTags', {title : 'Tags', cmd : 'mceUsableTags', image : url + '/images/usableTags.gif'});
		},
		getInfo : function() {
			return {
				longname : 'UsableTags plugin',
				author : 'OpenLab',
				authorurl : 'http://www.open-lab.com',
				version : "1.0"
			};
		}
	});
	// Register plugin
	tinymce.PluginManager.add('usableTags', tinymce.plugins.UsableTagsPlugin);
})();