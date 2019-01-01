<#-- @ftlvariable name="" type="org.magneato.resources.SearchView" -->
<#assign title = 'Search Results'>

<#include "/common/header.ftl">

<div class="mycontainer">
    <h1>Search Results</h1>
    <div class="row">
           <#list paginator.results as row>
            <#assign article = toJsonNode(row)>
            <a class="fauxlink" href="/${article._id.asText()}/${article._source.metadata.canonical_url.asText()}">
              <h2 class="article">${article._source.title.asText()}</h2>

            <p>
              <#if article._source.files?? && (article._source.files.size() > 0) >
                <img class="thumb" src=${article._source.files.get(0).thumbnailUrl.asText()} align="left"/>
              </#if>
       
            </p>
            </a>
            <p><strong>${article._source.metadata.create_date.asText()}</p>
            
        </#list>
    </div>
</div>
<#include "/common/copyright.ftl">

</body>
</html>