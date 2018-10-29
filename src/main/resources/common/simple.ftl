<!-- tinymce (for the tinymce field) -->
<script src="https://cloud.tinymce.com/stable/tinymce.min.js"></script>

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
        "tags": "",
        "metadata": { "edit_template" : "simple"}
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
                    "tags": {
                        "type": "string",
                        "title": "Tags",
                        "required": false
                    },
                    "files": {
                        "type": "array",
                        "title": "Files",
                        "required": true
                    },
                    "metadata": {
                        "type": "object",
                        "required": false,
                        "properties": {
                            "edit_template": {
                                "type": "string",
                                "required": false,
                                "properties": {}
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
                    "tags": {
                        "type": "tag",
                        "helper": "Enter some tags."
                    },
                    "metadata": {
                        "type": "object",
                        "label": "Meta Data",
                        "helpers": [],
                        "helpersPosition": "below",
                        "validate": true,
                        "disabled": true,
                        "readonly": true,
                        "showMessages": true,
                        "collapsible": false,
                        "legendStyle": "button",
                        "fields": {
                            "edit_template": {
                                "type": "text",
                                "label": "Edit Template",
                                "helpers": [],
                                "helpersPosition": "below",
                                "validate": true,
                                "disabled": false,
                                "showMessages": true,
                                "renderButtons": true,
                                "data": {},
                                "attributes": {},
                                "allowOptionalEmpty": true,
                                "autocomplete": false,
                                "disallowEmptySpaces": false,
                                "disallowOnlyEmptySpaces": false,
                                "fields": {}
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