package com.example.salesforce.oauth.util;

/**
 *
 * @author Andres Canavesi
 */
public class Credentials {

    ////////////////////// CONFIG //////////////////////////////////////////////////////
    //Create a connected app in your org to fill these attributes.
    // Selected OAuth Scopes:
    // Access your basic information (id, profile, email, address, phone)
    // Access and manage your data (api)
    // Perform requests on your behalf at any time (refresh_token, offline_access)
    ///////////////////////////////////////////////////////////////////////////////////
    /**
     *
     */
    public static final String OAUTH_CALLBACK_URL = "";

    /**
     * Also known as "Consumer Key"
     */
    public static final String OAUTH_CLIENT_ID = "";

    /**
     * Also known as "Consumer Secret"
     */
    public static final String OAUTH_CLIENT_SECRET = "";
}
