<#include "/common/header.ftl">


<div class="container">
    <div class="starter-template">
        <h1>${json.title.asText()}</h1>

        ${json.content.asText()?no_esc}

       <#include "/common/attachments.ftl">
        
    </div>

    <a href="/create">Add New  Page</a><br/>
    <a href="/create?clone=true">Add New Cloned Page</a><br/>
	
	<h2>Comments</h2>
        <#list search(0,10,"metadata.relations=${id}") as row>
            <#assign node = toJsonNode(row)>
            <a href="/${node._id.asText()}/${node._source.metadata.canonical_url.asText()}">${node._source.title.asText()}</a><br/>
        </#list>
        <a class="btn btn-primary" href="/create?child=true">Add Comment</a><br/>

    <div class="row">
        <div class="col-sm-8">
        <#include "/common/copyright.ftl">
        </div>
    </div>
</div>
</body>
</html>