package org.magneato.managed;

import io.dropwizard.lifecycle.Managed;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
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
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.magneato.service.ElasticSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedElasticClient implements Managed {
	private static final String INDEXTYPE = "_doc"; // from ES 6.* always _doc
	private final ElasticSearch configuration;

	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	PreBuiltTransportClient client = null;

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

		CreateIndexResponse createIndexResponse = client
				.admin()
				.indices()
				.prepareCreate(configuration.getIndexName())
				.setSettings(
						Settings.builder()
								.put("index.number_of_shards", configuration.getNumberOfShards())
								.put("index.number_of_replicas",
										configuration.getNumberOfReplicas())).get();

		if (createIndexResponse.isAcknowledged()) {
			return true;
		}

		return false;
	}

	public SearchResponse queryResultsWithAgeFilter(int from,
			int to) {
		SearchResponse scrollResp = client
				.prepareSearch(configuration.getIndexName())
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

	public long delete(String key, String value) {
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE
				.newRequestBuilder(client)
				.filter(QueryBuilders.matchQuery(key, value)).source(configuration.getIndexName())
				.refresh(true).get();

		log.info("Deleted " + response.getDeleted() + " element(s)!");

		return response.getDeleted();
	}

	public String insert(String json) {
		IndexResponse response = client.prepareIndex(configuration.getIndexName(), INDEXTYPE)
				.setSource(json, XContentType.JSON).get();

		String id = response.getId();
		log.info("id " + id);
		return id;
	}

	public String insert(String uri, String json) {
		String id = uri.substring(uri.lastIndexOf('.') + 1);
		log.info("id " + id);
		IndexResponse response = client.prepareIndex(configuration.getIndexName(), INDEXTYPE, id)
				.setSource(json, XContentType.JSON).get();

		return id;
	}

	public String get(String uri) {
		String id = uri.substring(uri.lastIndexOf('.') + 1);
		log.info("id " + id);
		GetResponse response = client.prepareGet(configuration.getIndexName(), "_doc", id).get();
		return response.getSourceAsString();
	}

	@Override
	public void start() throws Exception {
	    // NO OP
	}

	@Override
	public void stop() throws Exception {
		this.close();
	}
}
