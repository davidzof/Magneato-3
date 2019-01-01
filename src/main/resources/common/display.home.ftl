<#include "/common/header.ftl">

  <div class="mycontainer">
    <div class="row">
      <div class="col-sm-12">
        <h2 class="home">the source for backcountry skiing</h2>
          ${json.feedback.asText()?no_esc}
      </div>
    </div>

    <div class="row">
      <div class="col-sm-6">
        <h2 class="home">Articles</h2>
          <#list search(0,5,"metadata.edit_template=article") as row>
            <#assign article = toJsonNode(row)>
            <a class="fauxlink" href="/${article._id.asText()}/${article._source.metadata.canonical_url.asText()}">
              <h2>${article._source.title.asText()}</h2>

            <p>
              <#if article._source.files?? && (article._source.files.size() > 0) >
                <img class="thumb" src=${article._source.files.get(0).thumbnailUrl.asText()} align="left"/>
              </#if>
              ${getFirstPara(article._source.content.asText())?no_esc}
            </p>
            </a>

            

            <p><strong>Posted by ${article._source.metadata.owner.asText()} on the ${parseDate(article._source.metadata.create_date.asText(),"dd MMM yyyy")}</strong></p>
        </#list>

        <a class="btn btn-primary" href="/create?child=true">New Article</a><br/>
      </div>
      <div class="col-sm-6">
        <h2 class="home">Trip Reports</h2>
        <p>Coming soon!</p>
      </div>
    </div>
  </div><!-- container -->

  <div id="footer">
   <#include "/common/copyright.ftl">
  </div>
</body>
</html>
