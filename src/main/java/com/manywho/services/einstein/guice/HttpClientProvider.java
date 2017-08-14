package com.manywho.services.einstein.guice;

import com.google.inject.Provider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientProvider implements Provider<CloseableHttpClient> {

    @Override
    public CloseableHttpClient get() {
        return HttpClients.createDefault();
    }
}