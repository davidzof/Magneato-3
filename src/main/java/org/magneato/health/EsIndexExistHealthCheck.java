package org.magneato.health;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.collect.ImmutableList;

/**
 * A {@link HealthCheck} which checks if one or more indices exist in
 * Elasticsearch.
 * 
 * @see <a
 *      href="http://www.elasticsearch.org/guide/reference/api/admin-indices-indices-exists/">Admin
 *      Indices Indices Exists</a>
 */
public class EsIndexExistHealthCheck extends HealthCheck {
	private Client client;
	private String[] indices;

	/**
	 * Construct a new Elasticsearch index exists health check.
	 * 
	 * @param client
	 *            an Elasticsearch {@link Client} instance connected to the
	 *            cluster
	 * @param indices
	 *            a {@link List} of indices in Elasticsearch which should be
	 *            checked
	 * @throws IllegalArgumentException
	 *             if {@code indices} was {@literal null} or empty
	 */
	public EsIndexExistHealthCheck(Client client, List<String> indices) {
		checkArgument(!indices.isEmpty(), "At least one index must be given");

		this.client = checkNotNull(client);
		this.indices = checkNotNull(indices.toArray(new String[indices.size()]));
	}

	/**
	 * Construct a new Elasticsearch index exists health check.
	 * 
	 * @param client
	 *            an Elasticsearch {@link org.elasticsearch.client.Client}
	 *            instance connected to the cluster
	 * @param indexName
	 *            the index in Elasticsearch which should be checked
	 */
	public EsIndexExistHealthCheck(Client client, String indexName) {
		this(client, ImmutableList.of(indexName));
	}

	public boolean isExist() throws Exception {
		return check().isHealthy();
	}

	/**
	 * Perform a check of the number of documents in the Elasticsearch indices.
	 * 
	 * @return if the Elasticsearch indices exist, a healthy
	 *         {@link com.codahale.metrics.health.HealthCheck.Result};
	 *         otherwise, an unhealthy
	 *         {@link com.codahale.metrics.health.HealthCheck.Result} with a
	 *         descriptive error message or exception
	 * @throws Exception
	 *             if there is an unhandled error during the health check; this
	 *             will result in a failed health check
	 */
	@Override
	protected Result check() throws Exception {
		final IndicesExistsResponse indicesExistsResponse = client.admin()
				.indices().prepareExists(indices).get();

		if (indicesExistsResponse.isExists()) {
			return Result.healthy();
		} else {
			return Result.unhealthy("One or more indices do not exist.");
		}
	}
}