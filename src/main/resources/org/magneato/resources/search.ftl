<#-- @ftlvariable name="" type="org.magneato.resources.SearchView" -->
<#assign title = 'Search Results'>
<#include "/common/header.ftl">

<div class="mycontainer">
  <div class="row">
    <div class="col-sm-12">
      <h1 class="header">Search Results</h1>
    </div>
  </div>

  <#list paginator.results as row>
    <#assign article = toJsonNode(row)>
    <div class="row">
      <div class="col-sm-12">
        <a class="fauxlink" href="/${article._id.asText()}/${article._source.metadata.canonical_url.asText()}">
        <h2 class="article">${article._source.title.asText()}</h2>
        <#if article._source.files?? && (article._source.files.size() > 0) >
            <img class="img-responsive" src=${article._source.files.get(0).thumbnailUrl.asText()} align="left"/>
        </#if>

        <p><strong>${article._source.metadata.create_date.asText()}</strong></p>
        </a>
      </div>
    </div>
  </#list>
</div>

<div id="footer">
  <#include "/common/copyright.ftl">
</div>

</body>
</html>
