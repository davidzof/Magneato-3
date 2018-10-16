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
    <h1>Create page</h1>
    <p>Select a template from the list below to create a new page</p>
    <div class="row">
      <div class="col-sm-4">
      <#list templates as template>
          <h3>${template.name}</h3>
          ${template.description}
          <h3>View</h3>
          <#list template.views as view>
              ${view}<br/>
          </#list>
      </#list>
        </div>
    </div>
</div>

         
<#include "/common/copyright.ftl">
</body>
</html>