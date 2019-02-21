package org.magneato.managed;

import io.dropwizard.lifecycle.Managed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.magneato.service.ElasticSearch;
import org.magneato.utils.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedElasticClient implements Managed {
    private static final String INDEXTYPE = "_doc"; // from ES 6.* always _doc
    private final ElasticSearch configuration;

    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private PreBuiltTransportClient client;

    public ManagedElasticClient(ElasticSearch configuration)
            throws UnknownHostException {
        this.configuration = configuration;

        Settings settings = Settings.builder()
                .put("cluster.name", configuration.getClusterName())
                .put("client.transport.ignore_cluster_name", true)
                .put("client.transport.sniff", true).build();

        // create connection
        client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress
                .getByName(configuration.getHostname()), configuration
                .getPort()));

    }

    public PreBuiltTransportClient getClient() {
        return client;
    }

    public void close() {
        if (client != null) {
            client.close();
        }

    }

    public boolean createIndex() {
        log.info("Create ES Index " + configuration.getIndexName());

        CreateIndexRequest request = new CreateIndexRequest(
                configuration.getIndexName());
        request.settings(Settings
                .builder()
                .put("index.number_of_shards",
                        configuration.getNumberOfShards())
                .put("index.number_of_replicas",
                        configuration.getNumberOfReplicas()));

        CreateIndexResponse response = client.admin().indices().create(request)
                .actionGet();

        return response.isAcknowledged();
    }

    // http://localhost:9200/main-index/_mappings/_doc
    public void createMapping() {
        File file;
        BufferedReader reader = null;

        try {

            file = new File("esmapping.json");
            reader = new BufferedReader(new FileReader(file));

            StringBuilder mappingSource = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                mappingSource.append(line);

            }

            log.debug("ES Json Mapping " + mappingSource.toString());

            AcknowledgedResponse response = client.admin().indices()
                    .preparePutMapping(configuration.getIndexName())
                    .setType(INDEXTYPE)
                    .setSource(mappingSource.toString(), XContentType.JSON)
                    .execute().actionGet();

            System.out.println("repsonse " + response.isAcknowledged());
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * @param from
     * @param size
     * @param query
     * @return
     * @see <a
     * href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html">ES
     * Search</a>
     */
    // http://localhost:9200/main-index/_search?q=*.*
    // http://localhost:9200/main-index/_search?q=metadata.template:article&sort=metadata.create_date:asc
    public List<String> search(int from, int size, String query ) {
        return search(from, size, query, null).getResults() ;
    }

    public Pagination search(int from, int size, String query, String facets) {
        log.debug("search " + query);
        Pagination pagination = new Pagination();
pagination.setQuery(query);
pagination.setSize(size);

        SearchRequestBuilder searchBuilder = client
                .prepareSearch(configuration.getIndexName())
                .setTypes(INDEXTYPE)
                .addSort(
                        new FieldSortBuilder("metadata.create_date")
                                .order(SortOrder.DESC)).setFrom(from)
                .setSize(size);

        if (query != null && !query.isEmpty()) {
            // add search query
            String[] tokens = query.split("\\&");
            for (String token : tokens) {
                int index = token.indexOf('=');
                if (index != -1) {
                    String field = token.substring(0, index);
                    String value = token.substring(index + 1);
                    searchBuilder.setQuery(QueryBuilders.matchQuery(field,
                            value));

                }
            }
        }

        facets="activity_c,technical_c.orientation";
        if (facets != null) {
            // comma separated list of facets to collect
            String[] tokens = facets.split("\\,");
            AggregationBuilder aggregation = AggregationBuilders.global("facets");
            for (String token: tokens) {
                aggregation.subAggregation(AggregationBuilders.terms(token).field(token));
            }
            searchBuilder.addAggregation(aggregation);
        }

        SearchResponse response = searchBuilder.get();

        ArrayList<String> docs = new ArrayList<String>();
        SearchHits searchHits = response.getHits();
        for (SearchHit hit : searchHits) {
            hit.getId(); // need to return this
            docs.add(hit.toString());
        }
        pagination.setResults(docs);

        // get the results
        if (facets != null) {
            // comma separated list of facets to collect
            String[] tokens = facets.split("\\,");
            // sr is here your SearchResponse object

            for (String token: tokens) {
                Global global = response.getAggregations().get("facets");
                Terms activity = global.getAggregations().get(token);
                for (Terms.Bucket tb : activity.getBuckets()) {
                    System.out.println(tb.getKey() + ":" + tb.getDocCount());
                }
            }
        }

        return pagination;
    }


    public Pagination generalSearch(int from, int size, String query) {
        log.debug("general search " + query + " size " + size + " from " + from);
        SearchRequestBuilder searchBuilder = client
                .prepareSearch(configuration.getIndexName())
                .setTypes(INDEXTYPE)
                // mayb do this if there are aggs
                /*.addSort(
                        new FieldSortBuilder("metadata.create_date")
                                .order(SortOrder.DESC)).setFrom(from)*/
                .setSize(size).setFrom(from);

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery(query, "title", "content");

        multiMatchQueryBuilder.minimumShouldMatch("75%");
        searchBuilder.setQuery(multiMatchQueryBuilder);


        SearchResponse response = searchBuilder.get();


        ArrayList<String> docs = new ArrayList<String>();
        SearchHits searchHits = response.getHits();

        for (SearchHit hit : searchHits) {
            hit.getId(); // need to return this
            docs.add(hit.toString());
        }

        Pagination pagination = new Pagination();
        pagination.setResults(docs);
        pagination.setTotal(searchHits.totalHits);
        pagination.setCurrent(from);
        pagination.setSize(size);
        pagination.setQuery(query);
        return pagination;
    }

    public long delete(String key, String value) {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery(key, value))
                .source(configuration.getIndexName()).refresh(true).get();

        log.info("Deleted " + response.getDeleted() + " element(s)!");

        return response.getDeleted();
    }

    public String insert(String json) {
        IndexResponse response = client
                .prepareIndex(configuration.getIndexName(), INDEXTYPE)
                .setSource(json, XContentType.JSON).get();

        String id = response.getId();
        log.info("id " + id);
        return id;
    }

    public String insert(String id, String json) {

        log.info("id " + id);
        IndexResponse response = client
                .prepareIndex(configuration.getIndexName(), INDEXTYPE, id)
                .setSource(json, XContentType.JSON).get();
        // response.status().getStatus();
        return id;
    }

    public String get(String id) {
        log.info("id " + id);
        GetResponse response = client.prepareGet(configuration.getIndexName(),
                "_doc", id).get();
        return response.getSourceAsString();
    }

    @Override
    public void start() {
        // NO OP
    }

    @Override
    public void stop() {
        this.close();
    }
}
