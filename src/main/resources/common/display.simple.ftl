<#-- @ftlvariable name="" type="com.javaeeeee.dwstart.resources.PageView" -->
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
        <link type="text/css" rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" />
        <script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
</head>
<body> 
<div class="container">
  <div class="row">
    <div class="col-sm-4">
      Name: ${json.name.asText()}
    </div>
  </div>    
  <div class="row">
    <div class="col-sm-4">
      Feedback: ${json.feedback.asText()}
    </div>
  </div>
  <div class="row">
      <div class="col-sm-4">
        Ranking: ${json.ranking.asText()}
      </div>
  </div>
  
  
  <div class="row">
    <div class="col-sm-4">
      <h2>Attachments</h2>
      <#assign size = json.files.size() - 1 >
      <#list 0..size as x>
        ${x}
  	    <#assign node = json.files.get(x) >
  	    <a href="${node.url.asText()}" title=""${node.name.asText()}"><img src="${node.thumbnailUrl.asText()}" /></a>
      </#list>
    </div>
  </div>
</div>

         
<#include "/common/copyright.ftl">
</body>
</html>