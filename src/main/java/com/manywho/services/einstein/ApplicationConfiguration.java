package com.manywho.services.einstein;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ApplicationConfiguration implements Configuration {
    @Configuration.Setting(name = "Private Key", contentType = ContentType.Password)
    private String privateKey;

    public String getPrivateKey() {
        return privateKey;
    }
}
