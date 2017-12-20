package com.manywho.services.einstein.authorization;

import com.manywho.sdk.api.AuthorizationType;
import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.api.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.types.TypeBuilder;
import com.manywho.sdk.services.types.system.$User;

import javax.inject.Inject;

public class AuthorizationManager {
    private final TypeBuilder typeBuilder;

    @Inject
    public AuthorizationManager(TypeBuilder typeBuilder) {
        this.typeBuilder = typeBuilder;
    }

    public ObjectDataResponse authorize(AuthenticatedWho authenticatedWho, ObjectDataRequest request) {
        String status = "401";

        switch (request.getAuthorization().getGlobalAuthenticationType()) {
            case AllUsers:
                // If it's a public user (i.e. not logged in) then return a 401
                if (authenticatedWho.getUserId().equals("PUBLIC_USER")) {
                    status = "401";
                } else {
                    status = "200";
                }

                break;
            case Public:
                status = "200";
                break;
            case Specified:
                throw new UnsupportedOperationException("Using the Specified authentication type isn't supported");
            default:
                break;
        }

        $User user = new $User();
        user.setAuthenticationType(AuthorizationType.UsernamePassword);
        user.setDirectoryId("einstein");
        user.setDirectoryName("Einstein");
        user.setUserId("");
        user.setStatus(status);

        return new ObjectDataResponse(typeBuilder.from(user));
    }

    public ObjectDataResponse groupAttributes() {
        throw new UnsupportedOperationException();
    }

    public ObjectDataResponse groups(ObjectDataRequest request) {
        throw new UnsupportedOperationException();
    }

    public ObjectDataResponse userAttributes() {
        throw new UnsupportedOperationException();
    }

    public ObjectDataResponse users(ObjectDataRequest request) {
        throw new UnsupportedOperationException();
    }
}