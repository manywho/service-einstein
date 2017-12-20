package com.manywho.services.einstein;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ApplicationConfiguration implements Configuration {
    @Configuration.Setting(name = "Account ID", contentType = ContentType.String)
    private String accountId;

    @Configuration.Setting(name = "Private Key", contentType = ContentType.String)
    private String privateKey;

    public String getAccountId() {
        return accountId;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
