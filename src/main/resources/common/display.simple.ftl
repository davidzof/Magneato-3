<#-- @ftlvariable name="" type="org.magneato.resources.PageView" -->
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>${json.title.asText()}</title>

    <!-- jquery -->
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <!-- bootstrap -->
    <link type = "text/css" rel = "stylesheet" href="https://getbootstrap.com/docs/3.3/dist/css/bootstrap.min.css" />
    <script type="text/javascript" src = "https://getbootstrap.com/docs/3.3/dist/js/bootstrap.min.js" ></script>
    <style>
        body {
            padding-top: 50px;
        }

        .starter-template {
            padding: 40px 15px;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Magneato</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Home</a></li>
                <li class="divider"></li>
                <li><a href="">Edit</a></li>
                <li><a href="#contact">Contact</a></li>
            </ul>

            <ul class="right">
                <li class="search">
                    <form method="post" action="search.do">
                        <input type="search" placeholder="Search" name="keyWords"/>
                        <input type="hidden" name="start" value="0"/>
                        <input type="hidden" name="end" value="10"/>
                    </form>
                </li>
                <li class="divider"></li>
                <li class="has-dropdown" id="usernameResult">
                </li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>


<div class="container">
    <div class="starter-template">
        <h1>${json.title.asText()}</h1>

        ${json.feedback.asText()?no_esc}

        <h2>Attachments</h2>
        <#assign size = json.files.size() - 1 >
        <#list 0..size as x>
            <#assign node = json.files.get(x) >
  	        <a href="${node.url.asText()}" title=""${node.name.asText()}"><img src="${node.thumbnailUrl.asText()}"/></a>
        </#list>
        
    </div>

    <a href="/edit/${uri}">Edit</a><br/>
    <a href="/create/clone">Add New  Page</a><br/>
    <a href="/create/clone">Add New Cloned Page</a><br/>
	<a href="/create/child">Add New Child Page</a><br/>

    <div class="row">
        <div class="col-sm-8">
        <#include "/common/copyright.ftl">
        </div>
    </div>
</div>
</body>
</html>