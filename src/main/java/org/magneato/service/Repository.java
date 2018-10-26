package org.magneato.service;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class Repository {
    private static final String INDEXTYPE = "_doc";

	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	/**
	 * Implemented Singleton pattern here so that there is just one connection
	 * at a time.
	 * 
	 * @return RestHighLevelClient
	 */
	/*
	 * private static synchronized RestHighLevelClient makeConnection() {
	 * 
	 * if(restHighLevelClient == null) { restHighLevelClient = new
	 * RestHighLevelClient( RestClient.builder( new HttpHost(HOST, PORT_ONE,
	 * SCHEME), new HttpHost(HOST, PORT_TWO, SCHEME))); }
	 * 
	 * return restHighLevelClient;
	 * 
	 * }
	 */

	PreBuiltTransportClient client = null;

	// https://tutorial-academy.com/elasticsearch-6-create-index-bulk-insert-delete-java-api/

	public Repository(String clusterName, String clusterIp, int clusterPort)
			throws UnknownHostException {

		Settings settings = Settings.builder().put("cluster.name", clusterName)
				.put("client.transport.ignore_cluster_name", true)
				.put("client.transport.sniff", true).build();

		log.debug("Creating connection to Elastic on " + clusterIp + " port: " +clusterPort);
		// create connection
		client = new PreBuiltTransportClient(settings);
		client.addTransportAddress(new TransportAddress(InetAddress
				.getByName(clusterIp), clusterPort));

	}

	public void close() {
		if (client != null) {
			client.close();
		}

	}

	public boolean isHealthy() {
	/*	final ClusterHealthResponse response = client.admin().cluster()
				.prepareHealth().setWaitForGreenStatus()
				.setTimeout(TimeValue.timeValueSeconds(2)).execute()
				.actionGet();

		if (response.isTimedOut()) {
			log.error("Elastic Search Health Check Timeout");
			return false;
		}
		*/

		return true;
	}

	public boolean isIndexRegistered(String indexName) {
		// check if index already exists
		log.debug("is index registered " +indexName);
		final IndicesExistsResponse ieResponse = client.admin().indices()
				.prepareExists(indexName).get(TimeValue.timeValueSeconds(1));

		// index not there
		if (!ieResponse.isExists()) {
			return false;
		}

		return true;
	}

	public boolean createIndex(String indexName, String numberOfShards,
			String numberOfReplicas) {
		log.debug("create index " +indexName);

		CreateIndexResponse createIndexResponse = client
				.admin()
				.indices()
				.prepareCreate(indexName)
				.setSettings(
						Settings.builder()
								.put("index.number_of_shards", numberOfShards)
								.put("index.number_of_replicas",
										numberOfReplicas)).get();

		if (createIndexResponse.isAcknowledged()) {
			return true;
		}

		return false;
	}

	public SearchResponse queryResultsWithAgeFilter(String indexName, int from, int to) {
		SearchResponse scrollResp = client
				.prepareSearch(indexName)
				// sort order
				.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
				// keep results for 60 seconds
				.setScroll(new TimeValue(60000))
				// filter for age
				.setPostFilter(
						QueryBuilders.rangeQuery("age").from(from).to(to))
				// maximum of 100 hits will be returned for each scroll
				.setSize(100).get();

		return scrollResp;
		
		
	}

	public long delete(String indexName, String key, String value) {
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE
				.newRequestBuilder(client)
				.filter(QueryBuilders.matchQuery(key, value)).source(indexName)
				.refresh(true).get();

		log.info("Deleted " + response.getDeleted() + " element(s)!");

		return response.getDeleted();
	}

	public String insert(String json) {
		IndexResponse response = client.prepareIndex("my-index", INDEXTYPE)
				.setSource(json, XContentType.JSON).get();

		String id = response.getId();
		log.info("id " + id);
		return id;
	}

	public String insert(String uri, String json) {
		String id = uri.substring(uri.lastIndexOf('.')+1);
		log.info("id " + id);
		IndexResponse response = client.prepareIndex("my-index", INDEXTYPE, id)
				.setSource(json, XContentType.JSON).get();

		return id;
	}

	public String get(String uri) {
		String id = uri.substring(uri.lastIndexOf('.')+1);
		log.info("id " + id);
		GetResponse response = client.prepareGet("my-index", "_doc", id).get();
		return response.getSourceAsString();
	}
}
