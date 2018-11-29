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
        var now = new Date();

        $("#form").alpaca({
        "data":
    <#if body ??>
        ${body?no_esc}
    <#else>
    {
        "activity": "Alpine Ski",
        "child": false,
        "ski_difficulty": {
            "rating": "1.1",
            "bra": 1
        },
        "date": moment().format("MM/DD/YYYY"),
        "difficulty": {
            "choice": "1"
        },
        "technical_c": {
            "imperial": false,
            "orientation": "North",
        },
        "metadata": ${metaData?no_esc}
    }
    </#if>
            ,
            "schema": {
                "title": "Trip Report",
                "description": "Tell people about your trip",
                "type": "object",
                "properties": {
                    "title": {
                        "type": "string",
                        "required": true,
                        "title": "Destination"
                    },
                    "child": {},
                    "activity": {
                        "title": "Activity",
                        "required": true,
                        "type": "string",
                        "enum": ["Alpine Ski", "Off Piste Skiing", "Nordic Ski", "Ski Touring", "Mountain Biking", "Road Cycling", "Running", "Roller Ski", "Snow shoeing", "Hike", "Climb", "Via Ferrata", "Trail"]
                    },
                    "date": {
                        "required": true,
                        "title": "Date"
                    },
                    "description": {
                        "required": true,
                        "title": "Description"
                    },
                    "conditions": {
                        "title": "Conditions"
                    },
                    "ski_difficulty": {
                        "title": "Difficulty",
                        "type": "object",
                        "dependencies": "activity",
                        "properties": {
                            "rating": {
                                "title": "Ski Rating",
                                "required": true,
                                "enum": ["1.1", "1.2", "1.3", "2.1", "2.2", "2.3", "3.1", "3.2", "3.3", "4.1", "4.2", "4.3", "5.1", "5.2", "5.3", "5.4", "5.5", "5.6"]
                            },
                            "bra": {
                                "title": "Avalanche Risk",
                                "required": true,
                                "enum": ["1", "2", "3", "4", "5", "-"]
                            },
                            "snowline": {
                                "title": "Snowline",
                                "required": false
                            }
                        }
                    },
                    "difficulty": {
                        "type": "object",
                        "dependencies": "activity",
                        "properties": {
                            "rating": {
                                "title": "Difficulty",
                                "required": true,
                                "enum": ["1", "2", "3", "4", "5"]
                            }
                        }
                    },
                    "technical_c": {
                        "title": "Trip data",
                        "type": "object",
                        "dependencies": "activity",
                        "properties": {
                            "imperial": {},
                            "max": {
                                "title": "Maximum Altitude",
                                "required": true,
                                "minimum": 0,
                                "maximum": 29500
                            },
                            "min": {
                                "title": "Minimum Altitude",
                                "minimum": 0,
                                "maximum": 29500
                            },
                            "distance": {
                                "title": "Distance",
                                "minimum": 0.0,
                                "maximum": 1000
                            },
                            "climb": {
                                "title": "Climb",
                                "minimum": 0,
                                "maximum": 250000
                            },
                            "descent": {
                                "title": "Descent",
                                "minimum": 0,
                                "maximum": 250000
                            },
                            "title": "Start Location",
                        	"type": "object",
	                        "properties": {                    
	                            "lat": {
	                                "minimum": -180,
	                                "maximum": 180,
	                                "title": "Latitude"
	                            },
	                            "lon": {
	                                "minimum": -180,
	                                "maximum": 180,
	                                "title": "Longitude"
	                            }
                            },
                            "orientation": {
                                "title": "Orientation",
                                "required": true,
                                "enum": ["North", "North-West", "West", "South-West", "South", "South-East", "East", "North-East", "Various"]
                            },
                        }
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
                        done: function (e, data) {
                            alert("here");
                        },
                        "name": "files"
                    },
                    "title": {
                        "helper": "e.g. Col du Tepey, Mount Whitney, Chamonix etc."
                    },
                    "child": {
                    	"rightLabel": "Is this the first description of this route? (allows related trip reports to be created)"
                    },
                    "activity": {
                        "hideNone": true,
                        "sort": true,
                        "type": "select"
                    },
                    "date": {
                        "type": "date",
                        "picker": {
                            "maxDate": new Date().setDate(now.getDate())
                        }
                    },
                    "description": {
                        "type": "summernote",
                        "rows": 10,
                        "cols": 80,
                        "helper": "Describe your trip, the route taken etc."
                    },
                    "conditions": {
                        "type": "summernote",
                        "rows": 5,
                        "cols": 40,
                        "helper": "Describe the conditions (weather, road access, snow or trail, any difficulties you encountered)"
                    },
                    "ski_difficulty": {
                        "dependencies": {
                            "activity": ["Ski Touring", "Off Piste Skiing"]
                        },
                        "fields": {
                            "rating": {
                                "helper": "Toponeige scale",
                                "type": "select"
                            },
                            "bra": {
                                "helper": "Max risk from the bulletin or your own estimate",
                                "type": "select"
                            },
                            "snowline": {
                                "helper": "Altitude of skiable snow (meters or feet)",
                                "type": "integer"
                            }
                        },
                    },
                    "technical_c": {
                        "helper": "these fields will be autofilled if you upload a gpx file",
                        "fields": {
                            "imperial": {
                                "rightLabel": "Check for Imperial measurements"
                            },
                            "max": {
                                "type": "integer",
                                "helper": "meters or feet",
                            },
                            "min": {
                                "type": "integer",
                                "helper": "meters or feet",
                            },
                            "min": {
                                "type": "integer",
                                "helper": "meters or feet",
                            },
                            "climb": {
                                "type": "integer",
                                "helper": "amount of climbing in meters or feet",
                            },
                            "descent": {
                                "type": "integer",
                                "helper": "amount of descent in meters or feet",
                            },
                            "distance": {
                                "type": "number",
                                "helper": "km or miles",
                            },
                            "location" : {
	                            "lat": {
	                                "type": "number"
	                            },
	                            "lon": {
	                                "type": "number"
	                            }
                            },
                            "orientation": {
                                "type": "select",
                                "sort": false
                            }
                        },
                    },
                    "difficulty": {
                        "dependencies": {
                            "activity": ["Hike", "Snow shoeing", "Road Cycling", "Mountain Biking", "Trail", "Via Ferrata"]
                        }
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
                                "readonly": true,
                                "label": "Edit Template"
                            },
                            "display_template": {
                                "type": "text",
                                "label": "Display Template"
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
                                "readonly": true,
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
            "view": {
                "parent": "bootstrap-edit",
                "layout": {
                    "template": "threeColumnGridLayout",
                    "bindings": {
                        "title": "column-1",
                        "activity": "column-2",
                        "imperial": "column-3",
                        "date": "column-3",
                        "ski_difficulty": "column-3",
                        "difficulty": "column-3",
                        "description": "column-4",
                        "conditions": "column-4",
                        "technical_c": "column-2",
                        "files": "column-4",
                        "child": "column-4",
                        "metadata": "column-4"

                    }
                },
                "templates": {
                    "threeColumnGridLayout": '<div class="row">' + '{{#if options.label}}<h2>{{options.label}}</h2><span></span>{{/if}}' + '{{#if options.helper}}<p>{{options.helper}}</p>{{/if}}' + '<div id="column-1" class="col-md-12"> </div>' + '<div id="column-2" class="col-md-6"> </div>' + '<div id="column-3" class="col-md-6"> </div>' + '<div id="column-4" class="col-md-12"> </div>' + '<div class="clear"></div>' + '</div>'
                }
            },
            "postRender": function (control) {
                control.childrenByPropertyId["files"].on("change", function () {

                    // intercept file uploads and do some post processing, for example get EXIF file data
                    let files = this.getValue();

                    console.log(files.length);
                    if (files.length > 0) {
                        let last = files[files.length - 1];
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