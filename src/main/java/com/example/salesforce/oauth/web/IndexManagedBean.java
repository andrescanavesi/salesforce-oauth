package com.example.salesforce.oauth.web;

import com.example.salesforce.oauth.util.Credentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 * The entry point of the web app (/index.html).
 *
 * It just displays an authorization link with a callback to our webapp: /oauth.xhtml
 *
 * @see the Oauth flow at: https://developer.salesforce.com/page/Digging_Deeper_into_OAuth_2.0_on_Force.com
 *
 * @author Andres Canavesi
 */
@Named(value = "indexManagedBean")
@ViewScoped
@ManagedBean
public class IndexManagedBean {

    private static final Logger LOG = Logger.getLogger(IndexManagedBean.class.getName());
    /**
     * This the URL that the user has to open in the browser to request a code. Then we are able to request the access
     * token with that code (this operation occurs in OauthManagedBean)
     */
    private String oauthUrl;

    /**
     *
     */
    @PostConstruct
    public void init() {
        validateSetUp();
        //the url to request the code to get the access token.
        StringBuilder builder = new StringBuilder();
        builder.append("https://login.salesforce.com/services/oauth2/authorize?response_type=code&client_id=")
                .append(Credentials.OAUTH_CLIENT_ID)
                .append("&redirect_uri=")
                .append(Credentials.OAUTH_CALLBACK_URL);

        oauthUrl = builder.toString();
        LOG.log(Level.INFO, "Oauth url: {0}", oauthUrl);

    }

    private void validateSetUp() {
        if (Credentials.OAUTH_CALLBACK_URL == null
                || Credentials.OAUTH_CALLBACK_URL.trim().equals("")
                || Credentials.OAUTH_CLIENT_ID == null
                || Credentials.OAUTH_CLIENT_ID.trim().equals("")
                || Credentials.OAUTH_CLIENT_SECRET == null
                || Credentials.OAUTH_CLIENT_SECRET.trim().equals("")) {
            throw new IllegalStateException("Open Credentials class and put your connected app data");
        }
    }

    /**
     *
     * @return
     */
    public String getOauthUrl() {
        return oauthUrl;
    }

}
