package com.manywho.services.einstein.authentication;

import com.google.common.base.Strings;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.sdk.services.configuration.ConfigurationParser;
import com.manywho.services.einstein.ApplicationConfiguration;
import com.manywho.services.einstein.einstein.EinsteinClient;

import javax.inject.Inject;

public class AuthenticationManager {
    private final ConfigurationParser configurationParser;
    private final EinsteinClient einsteinClient;

    @Inject
    public AuthenticationManager(ConfigurationParser configurationParser, EinsteinClient einsteinClient) {
        this.configurationParser = configurationParser;
        this.einsteinClient = einsteinClient;
    }

    public AuthenticatedWhoResult authenticate(AuthenticationCredentials credentials) {
        ApplicationConfiguration configuration = configurationParser.from(credentials);

        // Get the token for this user
        String token = einsteinClient.getToken(credentials.getUsername(), configuration);

        // If we don't have a token, we're not logged in
        if (Strings.isNullOrEmpty(token)) {
            return AuthenticatedWhoResult.createDeniedResult();
        }

        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        authenticatedWhoResult.setDirectoryId("einstein");
        authenticatedWhoResult.setDirectoryName("Einstein");
        authenticatedWhoResult.setEmail(credentials.getUsername());
        authenticatedWhoResult.setFirstName("Einstein");
        authenticatedWhoResult.setIdentityProvider("?");
        authenticatedWhoResult.setLastName("User");
        authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName("?");
        authenticatedWhoResult.setToken(token);
        authenticatedWhoResult.setUserId(credentials.getUsername());
        authenticatedWhoResult.setUsername(credentials.getUsername());

        return authenticatedWhoResult;
    }
}
