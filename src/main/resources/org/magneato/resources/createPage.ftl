<#-- @ftlvariable name="" type="org.magneato.resources.CreatePageView" -->
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Create New Page</title>

    <!-- jquery -->
    <script src="/webjars/jquery/jquery.min.js"></script>
    <!-- bootstrap -->
    <link type="text/css" rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
    <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
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
      <input type="submit" value="Create">
    </form>
  </div>   
<#include "/common/copyright.ftl">
</body>
</html>