package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.testBaseUrlProvider.BaseUrlProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public abstract class IntegrationTest implements BaseUrlProvider {
    private static final String URL = "http://127.0.0.1";

    private String url;
    @LocalServerPort
    private String port;
    private static TestRestTemplate restTemplate;

    public IntegrationTest(String url) {
        this.url = url;
    }

    public IntegrationTest() {
        this.url=URL;
    }

    @BeforeAll
    public static void setUp() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        clientHttpRequestFactory.setBufferRequestBody(false);
        restTemplate = new TestRestTemplate();
        restTemplate.getRestTemplate().setRequestFactory(clientHttpRequestFactory);
    }

    public String getUrl() {
        return url;
    }

    public String getPort() {
        return port;
    }

    public String getUrlWithPort(){
        return url+":"+port;
    }

    @Override
    public String provideUrl() {
        return getUrlWithPort();
    }

    public TestRestTemplate getRestTemplate() {
        return restTemplate;
    }
}
