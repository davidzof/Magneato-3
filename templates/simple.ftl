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