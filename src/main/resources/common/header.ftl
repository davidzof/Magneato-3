<#-- @ftlvariable name="" type="org.magneato.resources.PageView" -->
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>${json.title.asText()}</title>

    <!-- jquery -->
    <script src="/webjars/jquery/jquery.min.js"></script>
    <!-- bootstrap -->
    <link type="text/css" rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
    <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    <style>
        .starter-template {
            padding: 40px 15px;
        }
    </style>
</head>
<body>

  <nav class="navbar navbar-inverse">
    <div class="container-fluid">
      <!-- Brand and toggle get grouped for better mobile display -->
      <div class="navbar-header">
        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
          <span class="sr-only">Toggle navigation</span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <!-- a class="navbar-brand" href="#">Brand</a -->
      </div>

      <!-- Collect the nav links, forms, and other content for toggling -->
      <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
        <ul class="nav navbar-nav">
          <li class="active"><a href="/">Home <span class="sr-only">(current)</span></a></li>
        </ul>

        <ul class="nav navbar-nav navbar-right">
          <form class="navbar-form navbar-left">
            <div class="form-group">
              <input type="search" placeholder="Search" name="keyWords" class="form-control"/>
              <input type="hidden" name="start" value="0"/>
              <input type="hidden" name="end" value="10"/>
            </div>
            <button type="submit" class="btn btn-default">Search</button>
          </form>
          <li class="divider"></li>

          <li class="dropdown" id="userList">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Guest User <span class="caret"></span></a>
            <ul class="dropdown-menu">
              <li><a href="/login">Sign-in</a></li>
              <li><a href="#">Register</a></li>
            </ul>
          </li>
        </ul>
      </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
  </nav>

<script>
$(document).ready(function(){
	$.getJSON( "/login/credentials", function( data ) {
		console.log(data);	
	  
	  if (data.principal && 0 != data.principal.length) {
	  	$('#userList').html('<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">' + data.principal + ' <span class="caret"></span></a>');
	  	$('#userList').append('<ul class="dropdown-menu"><li><a href="/logout">Sign-out</a></li><li role="separator" class="divider"></li><li><a href="/edit/${uri}">Edit</a></li><li><a href="/create">New  Page</a></li><li><a href="/create?clone=true">Clone Page</a></li>');
	  	$('#userList').append('</ul>');
	  }
	  
	});
});
</script>