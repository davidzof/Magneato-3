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
                <li class="active"><a href="/">Home</a></li>
                <li class="divider"></li>
                <li><a href="/edit/${uri}">Edit</a></li>
            </ul>

            <ul class="nav navbar-nav right">
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