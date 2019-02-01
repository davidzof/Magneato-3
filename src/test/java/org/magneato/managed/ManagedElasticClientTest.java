package org.magneato.managed;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.junit.Before;
import org.junit.Test;
import org.magneato.MagneatoConfiguration;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ManagedElasticClientTest {
    @Before
    public void setUp() {
        // set up search mocks here
        SearchRequestBuilder searchBuilder = Mockito
                .mock(SearchRequestBuilder.class);
    }

    @Test
    public void aggregationTest() {

    }

}
