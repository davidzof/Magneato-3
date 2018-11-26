<!-- tinymce (for the tinymce field) -->
<script src="http://www.alpacajs.org/lib/tinymce/tinymce.js"></script>

<!-- summernote editor -->
<!-- include summernote css/js -->
<link href="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.9/summernote.css" rel="stylesheet">
<script src="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.9/summernote.js"></script>


<!-- moment for date and datetime controls -->
<script src="http://www.alpacajs.org/lib/moment/min/moment-with-locales.min.js"></script>

<!-- bootstrap datetimepicker for date and datetime controls -->
<script src="http://www.alpacajs.org/lib/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js"></script>
<link rel="stylesheet" media="screen" href="http://www.alpacajs.org/lib/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css"/>

<script type="text/javascript">
    $(document).ready(function () {
        $("#form").alpaca({
        "data":
    <#if body ??>
        ${body?no_esc}
    <#else>
    {
        "title": "The Page Title",
        "feedback": "Type your content here...",
        "category": "Technology",
        "metadata": ${metaData?no_esc}
    }
    </#if>
            ,
            "schema": {
                "title": "Simple Edit Page",
                "description": "This is an example of a basic page with Title, Editor Field and Attachments",
                "type": "object",
                "properties": {
                    "title": {
                        "type": "string",
                        "title": "title",
                        "required": true
                    },
                    "feedback": {
                        "type": "string",
                        "title": "Feedback"
                    },
                    "files": {
                        "type": "array",
                        "title": "Files"
                    },
                    "category": {
                        "title": "Category",
                        "enum": [
                            "Home News",
                            "Technology",
                            "World News"
                        ]
                    },
                    "metadata": {
                        "type": "object",
                        "required": false,
                        "properties": {
                            "edit_template": {
                                "type": "string",
                                "required": true
                            },
                            "display_template": {
                                "type": "string",
                                "required": true
                            },
                            "create_date": {
                                "type": "string",
                                "required": true,
                                "format": "date"
                            },
                            "ip_addr": {
                                "type": "string",
                                "required": false
                            },
                            "owner": {
                                "type": "string",
                                "required": false
                            },
                            "canonical_url": {
                                "type": "string",
                                "required": false
                            },
							"perms": {
                                "type": "integer"
                            },
                            "relations": {
                            	"title": "Relations",
                            	"type": "array"
                            },
							"groups": {
                            	"title": "Groups",
                            	"type": "array"
                            }
                        }
                    }
                },
                "dependencies": {
                    "feedback": ["category"]
                }
            },
            "options": {
                "form": {
                    "buttons": {
                        "submit": {
                            "title": "Save",
                            "click": function () {
                                var data = this.getValue();
                                $.ajax({
                                    contentType: 'application/json',
                                    data: JSON.stringify(data),
                                    dataType: 'JSON',
                                    <#if url?has_content >
                                    	type: 'PUT',
                                    	url: '/save/${url}',
                                    <#else>
                                    	type: 'POST',
                                    	url: '/save',
                                    </#if>
                                    
                                    success: function (data) {
                                        afterSuccess(data)
                                    }
                                })
                            }
                        }
                    }
                },
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
                        done: function(e,data){
                        alert("here");
        			    },
                        "name": "files"
                    },
                    "title": {
                        "size": 20,
                        "helper": "Please enter the page title."
                    },
                    "feedback": {
                        "type": "summernote",
                        "name": "your_feedback",
                        "rows": 5,
                        "cols": 40,
                        "helper": "Please enter your feedback.",
                        "dependencies" : {
                            "category" : ["Home News", "Technology","World News"]
                        }

                    },
                    "category": {
                       "type": "select",
                       "label": "New select",
                        "optionLabels": [
                            "Home News",
                            "Technology",
                            "World News"
                        ]
                    },
                    "metadata": {
                        "type": "object",
                        "label": "Meta Data",
                        "validate": true,
                        "disabled": false,
                        "hidden": false,
                        "showMessages": true,
                        "legendStyle": "button",
                        "fields": {
                            "edit_template": {
                                "type": "text",
                                "readonly": true,
                                "label": "Edit Template"
                            },
                            "display_template": {
                                "type": "text",
                                "label": "Display Template"
                            },
                            "perms": {
                                "type": "integer",
                                "label": "Permissions"
                            },
                            "owner": {
                                "type": "text",
                                "label": "Owner"
                            },
                            "ip_addr": {
                                "type": "text",
                                "label": "IP Address"
                            },
                            "canonical_url": {
                                "type": "text",
                                "label": "Canonical URL"
                            },
                            "create_date": {
                                "type": "datetime",
                                "label": "Create Date",
                                "picker": {
                                    "useCurrent": false,
                                    "format": "YYYY-MM-DD HH:mm:ss",
                                    "locale": "en_US",
                                    "dayViewHeaderFormat": "MMMM YYYY",
                                    "extraFormats": [
                                        "MM/DD/YYYY hh:mm:ss a",
                                        "MM/DD/YYYY HH:mm",
                                        "MM/DD/YYYY"
                                    ]
                                },
                                "dateFormat": "YYYY-MM-DD HH:mm:ss"
                            }
                        }
                    }
                }
            },
            "view": "bootstrap-edit",
            "postRender": function (control) {
                control.childrenByPropertyId["files"].on("change", function() {
                
                	// intercept file uploads and do some post processing, for example get EXIF file data
					let files = this.getValue();
					
            		console.log(files.length);
            		if (files.length > 0) {
            			let last = files[files.length -1];
            			let type = last.name.split('.').pop()
            			console.log("last uploaded file type " + type);
            			
            			console.log("title " + control.childrenByPropertyId["title"].getValue()); // get / set a field behind the scenes, not visible
            			//set an input field value, will be visible to end user
            			//$('input[name="title"]').val('some value');
            			
            		}	
        		});
            }
        });
    });
</script>