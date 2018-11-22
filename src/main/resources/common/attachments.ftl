<#if json.files?? && (json.files.size() > 0) >
	        <h2>Attachments</h2>
	        <#assign size = json.files.size() - 1 >
	        <#list 0..size as x>
	            <#assign node = json.files.get(x) >
	  	        <a href="${node.url.asText()}" title=""${node.name.asText()}"><img src="${node.thumbnailUrl.asText()}"/></a>
	        </#list>
</#if>