<#-- @ftlvariable name="" type="com.javaeeeee.dwstart.resources.PageView" -->
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>${json.title.asText()}</title>
    
    <!-- jquery -->
        <script type="text/javascript" src="//code.jquery.com/jquery-1.11.1.min.js"></script>
        <!-- bootstrap -->
        <link type="text/css" rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" />
        <script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
</head>
<body> 
 <!-- TOP NAVIGATION -->
        <nav class="top-bar">
    <ul class="left">
      <li class="active" ><a href="">Home</a></li>
      <li class="divider"></li>
      <li ><a href="navigate.do?template=route&namespace=-">Routes</a></li>
      <li class="divider"></li>
      <li ><a href="navigate.do?template=tr&namespace=-">Trip Reports</a></li>
      <li ><a href="navigate.do?template=article&namespace=-&style=2">Forum</a></li>
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
    </nav>
    
    
<div class="container">
  <div class="row">
    <div class="col-sm-8">
      <h1>${json.title.asText()}</h1>
    </div>
  </div>    
  <div class="row">
    <div class="col-sm-8">
      ${json.feedback.asText()?no_esc}
    </div>
  </div>

  <div class="row">
    <div class="col-sm-8">
      <h2>Attachments</h2>
      <#assign size = json.files.size() - 1 >
      <#list 0..size as x>
  	    <#assign node = json.files.get(x) >
  	    <a href="${node.url.asText()}" title=""${node.name.asText()}"><img src="${node.thumbnailUrl.asText()}" /></a>
      </#list>
    </div>
  </div>

    <div class="row">
     <div class="col-sm-8">
     <#list search(0,10,null) as row>
     	${row}
     	</#list>
     </div>
     </div>
    <div class="row">
     <div class="col-sm-8">
        <#include "/common/copyright.ftl">
     </div>
    </div>
    </div>
</body>
</html>