<#-- @ftlvariable name="" type="org.magneato.resources.CreatePageView" -->
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Page</title>

    <!-- jquery -->
    <script type="text/javascript" src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    <!-- bootstrap -->
    <link type="text/css" rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css"/>
    <script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
</head>
<body>
  <div class="container">
    <h1>Create a new page</h1>
    <p>Select a template from the list below to create a new page</p>
    <form action="/create" method="get">
      <#list templates as template>
        <div class="row">
          <div class="col-sm-8">
            <input type="radio" name="editTemplate" value="${template.name}"> ${template.description}<br>
          </div>
        </div>
      </#list>
      <input type="submit" value="Submit">
    </form>
  </div>   
<#include "/common/copyright.ftl">
</body>
</html>