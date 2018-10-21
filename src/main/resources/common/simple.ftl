<script type="text/javascript">
$(document).ready(function() {
  $("#form").alpaca({

    "data":
    <#if body ??>
      ${body?no_esc}
    <#else>
    {
      "title": "The Page Title",
      "feedback": "Very impressive.",
      "ranking": "excellent"
    }
    </#if> 
                      ,
    "schema": {
      "title":"Simple Edit Page",
      "description":"This is an example of a basic page with Title, Edit Field and Attachments",
      "type":"object",
      "properties": {
        "title": {
          "type":"string",
          "title":"title",
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
											dataType: 'JSON',
											type: 'POST',
											url: '/save/${url}',
											success:  function(data) { afterSuccess(data) }
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
                              "url": "/upload",
                              "autoUpload": true
                            },
                            "name": "files"
                          },
                          "title": {
                            "size": 20,
                            "helper": "Please enter the page title."
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
                        control.childrenByPropertyId["title"].getFieldEl().css("background-color", "lightgreen");                  
      }
    });
  });
            
  function afterSuccess(data) {
  
  var obj = $.parseJSON(JSON.stringify(data));
  
  <!-- we need this url from server -->
  console.log(obj.url);
    window.location.replace(obj.url);
  }
</script>