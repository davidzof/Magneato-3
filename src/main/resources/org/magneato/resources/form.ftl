<#-- @ftlvariable name="" type="com.javaeeeee.dwstart.resources.FormView" -->
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Form</title>
    
    <!-- jquery -->
        <script type="text/javascript" src="//code.jquery.com/jquery-1.11.1.min.js"></script>
        <!-- file upload -->
<!-- fileupload control (for UploadField) -->
<link rel="stylesheet" type="text/css" href="http://www.alpacajs.org//lib/blueimp-file-upload/css/jquery.fileupload.css"/>
<link rel="stylesheet" type="text/css" href="http://www.alpacajs.org//lib/blueimp-file-upload/css/jquery.fileupload-ui.css"/>
<script src="http://www.alpacajs.org//lib/blueimp-file-upload/js/vendor/jquery.ui.widget.js"></script>
<script src="http://www.alpacajs.org//lib/blueimp-file-upload/js/jquery.iframe-transport.js"></script>
<script src="http://www.alpacajs.org//lib/blueimp-file-upload/js/jquery.fileupload.js"></script>
<script src="http://www.alpacajs.org//lib/blueimp-file-upload/js/jquery.fileupload-process.js"></script>
<script src="http://www.alpacajs.org//lib/blueimp-file-upload/js/jquery.fileupload-ui.js"></script>

 
        <!-- bootstrap -->
        <link type="text/css" rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" />
        <script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
 
        <!-- handlebars -->
        <script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/handlebars.js/4.0.5/handlebars.js"></script>
 
        <!-- alpaca -->
        <link type="text/css" href="//code.cloudcms.com/alpaca/1.5.24/bootstrap/alpaca.min.css" rel="stylesheet" />
        <script type="text/javascript" src="//code.cloudcms.com/alpaca/1.5.24/bootstrap/alpaca.min.js"></script>
</head>
<body>
         <div id="form"></div>
        <script type="text/javascript">
            $(document).ready(function() {
                $("#form").alpaca({
                    "data": <#if body ??>
    ${body?no_esc}
    <#else>
    {
                        "name": "Diego Maradona",
                        "feedback": "Very impressive.",
                        "ranking": "excellent"
                    }
                      </#if> 
                      ,
                    "schema": {
                        "title":"User Feedback",
                        "description":"What do you think about Alpaca?",
                        "type":"object",
                        "properties":
                        
                         {
                        

                        
                            "name": {
                                "type":"string",
                                "title":"Name",
                                "required":true
                            },
                            "feedback": {
                                "type":"string",
                                "title":"Feedback"
                            },
                            "ranking": {
                                "type":"string",
                                "title":"Ranking",
                                "enum":['excellent','ok','so so'],
                                "required":true
                            },
                            "files": {
                                "type": "array",
                                "title": "Files",
                                "required": true
                            }
            
                            
                            
                        }
                                     
                    },
                    "options": {
                        "form":{
                            "buttons":{
                                "submit":{
                                    "title": "Send Form Data",
                                    "click": function() {
                                        var data = this.getValue();
                                        $.ajax({
											contentType: 'application/json',
											data: JSON.stringify(data),
											type: "POST",
											url: 'http://localhost:8080/${url}.htm',
											success:  afterSuccess
										})

                                    }
                                }
                            }
                        },
                        "helper": "Tell us what you think about Alpaca!",
                        "fields": {
                          "files": {
                            "type": "upload",
                            "maxFileSize": 25000000,
                            "maxNumberOfFiles": 3,
                            "fileTypes": "(\.|\/)(gif|jpe?g|png)$",
                            "upload": {
                              "url": "http://localhost:8080/upload/${url}.htm",
                              "autoUpload": true
                            },
                            "name": "files"
                          },
                          "name": {
                            "size": 20,
                            "helper": "Please enter your name."
                          },
                          "feedback" : {
                                "type": "textarea",
                                "name": "your_feedback",
                                "rows": 5,
                                "cols": 40,
                                "helper": "Please enter your feedback."
                          },
                          "ranking": {
                                "type": "select",
                                "helper": "Select your ranking.",
                                "optionLabels": ["Awesome!",
                                    "It's Ok",
                                    "Hmm..."]
                            }
                        }   
                    },
                    "view" : "bootstrap-edit",
                    "postRender": function(control) {
                        control.childrenByPropertyId["name"].getFieldEl().css("background-color", "lightgreen");
                        
                        
                    }
                });
            });
            
            function afterSuccess()
{

    window.location.replace("http://localhost:8080/${url}.htm");
}
        </script>
<#include "/common/copyright.ftl">
</body>
</html>