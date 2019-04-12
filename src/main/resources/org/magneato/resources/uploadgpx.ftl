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
    <title>Upload GPX</title>
</head>

<body>
<div class="container">
    <h1>Upload GPX</h1>
    <form action="/uploadgpx/${value}" method="post" enctype="multipart/form-data">
        <div class="form-group row">
            <label>Select gpx file to upload:</label>
            <input class="form-control-file" type="file" name="file" id="fileToUpload"  accept=".gpx, application/octet-stream">
            <#if value?? >
                <input type="hidden" name="parent" value="${value}">
            </#if>
            
        </div>
        <div class="form-group row">
            <input type="submit" value="Upload GPX" name="submit" id="i_submit">
        </div>
    </form>
</div>

<#include "/common/copyright.ftl">
</body>
<script type="4c626df69e801b727f5f35aa-text/javascript">
$('#i_submit').click( function() {
	//check whether browser fully supports all File API
	if (window.File && window.FileReader && window.FileList && window.Blob)
	{
		//get the file size and file type from file input field
		var fsize = $('#i_file')[0].files[0].size;

		if(fsize>1048576) //do something if file size more than 1 mb (1048576)
		{
			alert(fsize +" bites\nToo big!");
			return false;
		}else{
			alert(fsize +" bites\nYou are good to go!");
		}
	}else{
		alert("Please upgrade your browser, because your current browser lacks some new features we need!");
		return false;
	}
	return true;
});

$('#i_submit2').click( function() {
	//check whether browser fully supports all File API
	if (window.File && window.FileReader && window.FileList && window.Blob)
	{
		//get the file size and file type from file input field
		var fsize = $('#i_file2')[0].files[0].size;
		var ftype = $('#i_file2')[0].files[0].type;
		var fname = $('#i_file2')[0].files[0].name;

		if(fsize>1048576) //do something if file size more than 1 mb (1048576)
		{
			alert("Type :"+ ftype +" | "+ fsize +" bites\n(File: "+fname+") Too big!");
		}else{
			alert("Type :"+ ftype +" | "+ fsize +" bites\n(File :"+fname+") You are good to go!");
		}
	}else{
		alert("Please upgrade your browser, because your current browser lacks some new features we need!");
	}
});

$('#i_submit3').click( function() {
	//check whether browser fully supports all File API
	if (window.File && window.FileReader && window.FileList && window.Blob)
	{
		//get the file size and file type from file input field
		var fsize = $('#i_file3')[0].files[0].size;
		var ftype = $('#i_file3')[0].files[0].type;
		var fname = $('#i_file3')[0].files[0].name;

	   switch(ftype)
		{
			case 'image/png':
			case 'image/gif':
			case 'image/jpeg':
			case 'image/pjpeg':
				alert("Acceptable image file!");
				break;
			default:
				alert('Unsupported File!');
		}

	}else{
		alert("Please upgrade your browser, because your current browser lacks some new features we need!");
	}
});
</script>
</html>