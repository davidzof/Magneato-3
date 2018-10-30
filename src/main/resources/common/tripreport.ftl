<!-- tinymce (for the tinymce field) -->
<script src="https://cloud.tinymce.com/stable/tinymce.min.js"></script>
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
        "metadata": { "edit_template" : "simple", "display_template" : "" }
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
                    "metadata": {
                        "type": "object",
                        "required": false,
                        "properties": {
                            "edit_template": {
                                "type": "string",
                                "required": false
                            },
                            "display_template": {
                                "type": "string",
                                "required": false
                            },
                            "create_date": {
                                "type": "string",
                                "required": false,
                                "formate": "date"
                            }
                        }
                    }
                }
            },
            "options": {
                "form": {
                    "buttons": {
                        "submit": {
                            "title": "Send Form Data",
                            "click": function () {
                                var data = this.getValue();
                                $.ajax({
                                    contentType: 'application/json',
                                    data: JSON.stringify(data),
                                    dataType: 'JSON',
                                    type: 'POST',
                                    url: '/save/${url}',
                                    success: function (data) {
                                        afterSuccess(data)
                                    }
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
                    "feedback": {
                        "type": "tinymce",
                        "name": "your_feedback",
                        "rows": 5,
                        "cols": 40,
                        "helper": "Please enter your feedback."
                    },
                    "metadata": {
                        "type": "object",
                        "label": "Meta Data",
                        "helpers": [],
                        "helpersPosition": "below",
                        "validate": true,
                        "disabled": false,
                        "readonly": false,
                        "hidden": false,
                        "showMessages": true,
                        "collapsible": false,
                        "legendStyle": "button",
                        "fields": {
                            "edit_template": {
                                "type": "text",
                                "label": "Edit Template"
                            },
                            "display_template": {
                                "type": "text",
                                "label": "Display Template"
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