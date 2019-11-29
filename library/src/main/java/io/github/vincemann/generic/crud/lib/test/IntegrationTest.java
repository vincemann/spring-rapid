package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.baseUrl.BaseAddress_Provider;
import lombok.Getter;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public abstract class IntegrationTest implements BaseAddress_Provider {
    private static final String LOCAL_HOST = "http://127.0.0.1";

    @Getter
    private String url;
    @Getter
    @LocalServerPort
    private String port;
    private static TestRestTemplate restTemplate;

    public IntegrationTest(String url) {
        this.url = url;
    }

    public IntegrationTest() {
        this.url= LOCAL_HOST;
    }

    @BeforeAll
    public static void setUp() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        clientHttpRequestFactory.setBufferRequestBody(false);
        restTemplate = new TestRestTemplate();
        restTemplate.getRestTemplate().setRequestFactory(clientHttpRequestFactory);
    }

    public String getUrlWithPort(){
        return url+":"+port;
    }

    @Override
    public String provideAddress() {
        return getUrlWithPort();
    }

    public TestRestTemplate getRestTemplate() {
        return restTemplate;
    }
}
