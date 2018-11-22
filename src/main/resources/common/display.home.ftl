<#include "/common/header.ftl">

<div class="container">
    <div class="starter-template">
        <h1>${json.title.asText()}</h1>

        ${json.feedback.asText()?no_esc}

		<#if json.files?? && (json.files.size() > 0) >
	        <h2>Attachments</h2>
	        <#assign size = json.files.size() - 1 >
	        <#list 0..size as x>
	            <#assign node = json.files.get(x) >
	  	        <a href="${node.url.asText()}" title=""${node.name.asText()}"><img src="${node.thumbnailUrl.asText()}"/></a>
	        </#list>
		</#if>
        
        <h2>Related articles</h2>
        <#list search(0,10,null) as row>
            <#assign node = toJsonNode(row)>
            <a href="/${node._id.asText()}/${node._source.metadata.canonical_url.asText()}">${node._source.title.asText()}</a><br/>
        </#list>
    </div>

    <div class="row">
        <div class="col-sm-8">
        <#include "/common/copyright.ftl">
        </div>
    </div>
</div>
</body>
</html>