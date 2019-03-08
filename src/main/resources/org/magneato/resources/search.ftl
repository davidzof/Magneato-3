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
          <p>
            <#if article._source.files?? && (article._source.files.size() > 0) >

              <img class="img-responsive thumb" src=${article._source.files.get(0).thumbnailUrl.asText()} align="left"/>
            </#if>
            ${getFirstPara(article._source.content.asText())?no_esc}
          </p>
        </a>
        <p><strong>${article._source.metadata.create_date.asText()}</strong></p>
        </a>
      </div>
    </div>
  </#list>

  
  <div class="row">
    <#assign navBarSize = 10>
    <#assign lastPage = (paginator.total / paginator.size)?ceiling >
    <#assign currentPage = (paginator.current / paginator.size) >
 
    <nav aria-label="Page navigation example">
        <ul class="pagination">


            <#if (currentPage > 0)>
              <#if paginator.facets?? >
                <li class="page-item"><a class="page-link" href="/facets/${paginator.query}/${paginator.facets}/${currentPage - 1}/${paginator.size}">Previous</a></li>
              <#else>
                <li class="page-item"><a class="page-link" href="/search/${paginator.query}/${currentPage - 1}/${paginator.size}">Previous</a></li>
              </#if>
            <#else>
              <li class="page-item disabled"><a class="page-link" href="#">Previous</a></li>
            </#if>

            <#if (currentPage > (navBarSize/2))>
              <#assign startPage = (currentPage - (navBarSize/2))>
              <#if ((startPage + navBarSize) > lastPage)>
                <#assign startPage = (lastPage-navBarSize)>
              </#if>
            <#else>
              <#assign startPage = 1>
            </#if>
            <#if (lastPage < navBarSize)>
              <#assign navBarSize = lastPage>
            </#if>

            <#list startPage..startPage+(navBarSize-1) as page>
              <#if (currentPage+1) = page>
                <li class="page-item disabled"><a class="page-link" href="#">${page}</a></li>
              <#else>
                <#if paginator.facets?? >
                  <li class="page-item"><a class="page-link" href="/facets/${paginator.query}/${paginator.facets}/${page-1}/${paginator.size}">${page}</a></li>
                <#else>
                  <li class="page-item"><a class="page-link" href="/search/${paginator.query}/${page-1}/${paginator.size}">${page}</a></li>
                </#if>
              </#if>
            </#list>
            <#if (currentPage < lastPage -1)>
              <#if paginator.facets?? >
                <li class="page-item"><a class="page-link" href="/facets/${paginator.query}/${paginator.facets}/${currentPage+1}/${paginator.size}">Next</a></li>
              <#else>
                <li class="page-item"><a class="page-link" href="/search/${paginator.query}/${currentPage+1}/${paginator.size}">Next</a></li>
              </#if>
            <#else>
              <li class="page-item disabled"><a class="page-link" href="#">Next</a></li>
            </#if>
        </ul>
    </nav>
  </div>
</div>

<div id="footer">
  <#include "/common/copyright.ftl">
</div>

</body>
</html>

