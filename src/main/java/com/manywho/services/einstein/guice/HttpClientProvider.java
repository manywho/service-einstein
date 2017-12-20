package com.manywho.services.einstein.guice;

import com.google.inject.Provider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientProvider implements Provider<CloseableHttpClient> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClientProvider.class);

    @Override
    public CloseableHttpClient get() {
        return HttpClients.createDefault();
    }
}