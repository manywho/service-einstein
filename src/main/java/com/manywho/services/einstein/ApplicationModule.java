package com.manywho.services.einstein;

import com.google.inject.AbstractModule;
import com.manywho.services.einstein.guice.HttpClientProvider;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.inject.Singleton;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CloseableHttpClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
    }
}