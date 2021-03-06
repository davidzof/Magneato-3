<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Editing ${editTemplate}</title>
    
    <!-- jquery -->
    <script type="text/javascript" src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    
    <!-- file upload -->
    <!-- fileupload control (for UploadField) -->
    <link rel="stylesheet" type="text/css" href="http://www.alpacajs.org//lib/blueimp-file-upload/css/jquery.fileupload.css"/>
    <link rel="stylesheet" type="text/css" href="http://www.alpacajs.org//lib/blueimp-file-upload/css/jquery.fileupload-ui.css"/>
    <script src="http://www.alpacajs.org/lib/blueimp-file-upload/js/vendor/jquery.ui.widget.js"></script>
    <script src="http://www.alpacajs.org/lib/blueimp-file-upload/js/jquery.iframe-transport.js"></script>
    <script src="http://www.alpacajs.org/lib/blueimp-file-upload/js/jquery.fileupload.js"></script>
    <script src="http://www.alpacajs.org/lib/blueimp-file-upload/js/jquery.fileupload-process.js"></script>
    <script src="http://www.alpacajs.org/lib/blueimp-file-upload/js/jquery.fileupload-ui.js"></script>

 
    <!-- bootstrap -->
    <link type="text/css" rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" />
    <script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
 
    <!-- handlebars -->
    <script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/handlebars.js/4.0.5/handlebars.js"></script>
 
    <!-- alpaca -->
    <script src="/library/alpaca/bootstrap/alpaca.css"></script>
    <script src="/library/alpaca/bootstrap/alpaca.js"></script>
</head>
<body>
<div class="container">
     <div id="form"></div>
     <#include "/common/${editTemplate}.ftl">
     <#include "/common/copyright.ftl">
</div>
</body>
</html>