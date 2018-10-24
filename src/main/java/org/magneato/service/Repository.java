package org.magneato.service;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class Repository {
    public Repository() {
    }
    /**
     * Implemented Singleton pattern here
     * so that there is just one connection at a time.
     * @return RestHighLevelClient
     */
    /*
    private static synchronized RestHighLevelClient makeConnection() {

        if(restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT_ONE, SCHEME),
                            new HttpHost(HOST, PORT_TWO, SCHEME)));
        }

        return restHighLevelClient;

    }
    */

    PreBuiltTransportClient client = null;
    //https://tutorial-academy.com/elasticsearch-6-create-index-bulk-insert-delete-java-api/

    public Repository( String clusterName, String clusterIp, int clusterPort ) throws UnknownHostException {

        Settings settings = Settings.builder()
                .put( "cluster.name", clusterName )
                .put( "client.transport.ignore_cluster_name", true )
                .put( "client.transport.sniff", true )
                .build();

        // create connection
        client = new PreBuiltTransportClient( settings );
        client.addTransportAddress( new TransportAddress( InetAddress.getByName( clusterIp ), clusterPort) );

    }

    public void close() {
        if (client != null) {
            client.close();
        }

    }

    public boolean isHealthy() {

        final ClusterHealthResponse response = client
                .admin()
                .cluster()
                .prepareHealth()
                .setWaitForGreenStatus()
                .setTimeout( TimeValue.timeValueSeconds( 2 ) )
                .execute()
                .actionGet();

        if ( response.isTimedOut() ) {
            return false;
        }

        return true;
    }


    public boolean isIndexRegistered( String indexName ) {
        // check if index already exists
        final IndicesExistsResponse ieResponse = client
                .admin()
                .indices()
                .prepareExists( indexName )
                .get( TimeValue.timeValueSeconds( 1 ) );

        // index not there
        if ( !ieResponse.isExists() ) {
            return false;
        }

        return true;
    }


    public boolean createIndex( String indexName, String numberOfShards, String numberOfReplicas ) {
        CreateIndexResponse createIndexResponse =
                client.admin().indices().prepareCreate( indexName )
                        .setSettings( Settings.builder()
                                .put("index.number_of_shards", numberOfShards )
                                .put("index.number_of_replicas", numberOfReplicas )
                        )
                        .get();

        if( createIndexResponse.isAcknowledged() ) {
            return true;
        }

        return false;
    }



    public void queryResultsWithAgeFilter( String indexName, int from, int to ) {
        SearchResponse scrollResp =
                client.prepareSearch( indexName )
                        // sort order
                        .addSort( FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC )
                        // keep results for 60 seconds
                        .setScroll( new TimeValue( 60000 ) )
                        // filter for age
                        .setPostFilter( QueryBuilders.rangeQuery( "age" ).from( from ).to( to ) )
                        // maximum of 100 hits will be returned for each scroll
                        .setSize( 100 ).get();

        // scroll until no hits are returned
        do {
            int count = 1;
            for ( SearchHit hit : scrollResp.getHits().getHits() ) {
                Map<String,Object> res = hit.getSourceAsMap();

                // print results
                for( Map.Entry<String,Object> entry : res.entrySet() ) {
                    //logger.info( "[" + count + "] " + entry.getKey() + " --> " + entry.getValue() );
                }
                count++;
            }

            scrollResp = client.prepareSearchScroll( scrollResp.getScrollId() ).setScroll( new TimeValue(60000) ).execute().actionGet();
            // zero hits mark the end of the scroll and the while loop.
        } while( scrollResp.getHits().getHits().length != 0 );
    }


    icsearch behavior.


    public long delete( String indexName, String key, String value ) {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder( client )
                        .filter( QueryBuilders.matchQuery( key, value ) )
                        .source( indexName )
                        .refresh( true )
                        .get();

        logger.info( "Deleted " + response.getDeleted() + " element(s)!" );

        return response.getDeleted();
    }

    public long delete( String indexName, String key, String value ) {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder( client )
                        .filter( QueryBuilders.matchQuery( key, value ) )
                        .source( indexName )
                        .refresh( true )
                        .get();


        return response.getDeleted();
    }


    public static void main(String [] args) {

        {
            // read properties
            PropertyReader properties = null;
            Repository es = null;

            try {
                //properties = new PropertyReader( getRelativeResourcePath( "config.properties" ) );

                String numberOfShards  	= 1;
                String numberOfReplicas	= 1;

                String clusterName 		= properties.read( CLUSTER_NAME );

                String indexName 		= properties.read( INDEX_NAME );
                String indexType 		= properties.read( INDEX_TYPE );

                String ip 				= properties.read( IP );
                int    port				= Integer.parseInt( properties.read( PORT ) );

                Repository es = new Repository("myCluster", "localhost", 9200);

                // check if elastic search cluster is healthy
                es.isHealthy();

                // check if index already existing
                if( !es.isIndexRegistered( indexName ) ) {
                    // create index if not already existing
                    es.createIndex( indexName, numberOfShards, numberOfReplicas );
                    // manually insert some test data
                    //es.bulkInsert( indexName, indexType );
                    // insert some test data (from JSON file)
                    //				es.bulkInsert( indexName, indexType, getRelativeResourcePath( "data.json" ) );
                }

                // retrieve elements from the user data where age is in between 15 ad 50
                es.queryResultsWithAgeFilter( indexName, 15, 50 );

                es.delete( indexName, "name", "Peter Pan" );

                // retrieve elements from the user data where age is in between 15 ad 50
                es.queryResultsWithAgeFilter( indexName, 15, 50 );
            }
            catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }
            catch ( UnknownHostException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            // required when parsing JSON
            //		catch ( ParseException e ) {
            //			e.printStackTrace();
            //		}
            finally {
                if (es!=null) {
                    es.close();
                }
            }
    }
}
