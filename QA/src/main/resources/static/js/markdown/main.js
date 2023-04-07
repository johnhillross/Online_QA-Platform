$(function() {
	if ($('#editor').length > 0) {
	    var editor = editormd("editor", {
	    	emoji: true,
	        taskList : true,
            tex : true,
            htmlDecode: true, 
	        width: "100%",
	        height: "400px",
	        path : "../../../../editormd/lib/"  // Autoload modules mode, codemirror, marked... dependents libs path
	    });
	}
});