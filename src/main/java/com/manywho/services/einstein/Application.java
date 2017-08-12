package com.manywho.services.einstein;

import com.manywho.sdk.services.servers.EmbeddedServer;
import com.manywho.sdk.services.servers.Servlet3Server;
import com.manywho.sdk.services.servers.undertow.UndertowServer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.ws.rs.ApplicationPath;
import java.security.Security;

@ApplicationPath("/")
public class Application extends Servlet3Server {
    public Application() {
        Security.addProvider(new BouncyCastleProvider());

        this.addModule(new ApplicationModule());
        this.setApplication(Application.class);
        this.start();
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        EmbeddedServer server = new UndertowServer();
        server.addModule(new ApplicationModule());
        server.setApplication(Application.class);
        server.start();
    }
}