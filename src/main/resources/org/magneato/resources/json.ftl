<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link type="text/css" rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
    <!-- jquery -->
    <script src="/webjars/jquery/jquery.min.js"></script>
    <!-- bootstrap -->
    <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    <script src="/assets/js/jquery.json-editor.min.js"></script>
    
    <title>Raw JSON Editor</title>
    <style type="text/css">
        body {
            margin: 0;
            padding: 0;
            width: 80%;
            margin: 50px auto 100px;
        }
        #json-input {
            display: block;
            width: 100%;
            height: 200px;
        }
        .myButton {
            display: block;
            height: 28px;
            margin: 20px 0;
            border-radius: 3px;
            border: 2px solid;
            cursor: pointer;
        }
        #json-display {
            border: 1px solid #000;
            margin: 0;
            padding: 10px 20px;
        }
    </style>
</head>

<body>
<div class="container">
    <h1>Raw JSON Editor</h1>
    Be careful!!!
    
 
    <textarea id="json-input" autocomplete="off">
		<#if value?? >
    		${value?no_esc}
    	</#if>
	</textarea>
	<button class="myButton" id="translate">Reset</button>
    	<pre id="json-display"></pre>
	<button class="myButton" id="save">Save JSON</button>

    <script type="text/javascript">
        function getJson() {
            try {
                return JSON.parse($('#json-input').val());
            } catch (ex) {
                alert('Wrong JSON Format: ' + ex);
            }
        }
        
        function afterSuccess(data) {
	        var obj = $.parseJSON(JSON.stringify(data));
	        
	        <!-- we need this url from server -->
	        if (typeof obj.error === 'undefined') {
	            window.location.replace(obj.url);
	            console.log(obj.url);
	        } else {
	            alert(obj.error);
	        }
    	}

        var editor = new JsonEditor('#json-display', getJson());
        $('#translate').on('click', function () {
        console.log("load editor");
            editor.load(getJson());
        });
        
        $('#save').on('click', function () {
			var data = $('#json-display').text();
            
            $.ajax({
                contentType: 'application/json',
                data: data,
                dataType: 'JSON',
				type: 'PUT',
				url: '/save/${url}',
                success: function (data) {
                	afterSuccess(data)
            	}
			});
         });
    </script>
</div>

<#include "/common/copyright.ftl">
</body>
</html>