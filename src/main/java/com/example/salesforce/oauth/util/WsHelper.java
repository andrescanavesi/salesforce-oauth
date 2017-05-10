package com.example.salesforce.oauth.util;

import com.example.salesforce.oauth.domain.ErrorResponse;
import com.example.salesforce.oauth.domain.CountResponse;
import com.example.salesforce.oauth.domain.AccessTokenResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.logging.Logger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import java.util.logging.Level;

/**
 * A helper to make requests through the RESTful and the SOAP Salesforce APIs
 *
 * @author Andres Canavesi
 */
public class WsHelper {

    private static final Logger LOG = Logger.getLogger(WsHelper.class.getName());

    private String apiVersion;
    private String vApiVersion;
    private final Gson gson;
    private PartnerConnection partnerConnection;
    private String salesforceAccessToken;
    private String salesforceTokenType;
    private String salesforceInstanceUrl;

    /**
     *
     */
    public WsHelper() {
        apiVersion = "39.0";
        vApiVersion = "v" + apiVersion;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     *
     * @return
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     *
     * @param apiVersion
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        this.vApiVersion = "v" + apiVersion;
    }

    /**
     *
     * @return
     */
    public String getSalesforceAccessToken() {
        return salesforceAccessToken;
    }

    /**
     *
     * @param salesforceAccessToken
     */
    public void setSalesforceAccessToken(String salesforceAccessToken) {
        this.salesforceAccessToken = salesforceAccessToken;
    }

    /**
     *
     * @return
     */
    public String getSalesforceTokenType() {
        return salesforceTokenType;
    }

    /**
     *
     * @param salesforceTokenType
     */
    public void setSalesforceTokenType(String salesforceTokenType) {
        this.salesforceTokenType = salesforceTokenType;
    }

    /**
     *
     * @return
     */
    public String getSalesforceInstanceUrl() {
        return salesforceInstanceUrl;
    }

    /**
     *
     * @param salesforceInstanceUrl
     */
    public void setSalesforceInstanceUrl(String salesforceInstanceUrl) {
        this.salesforceInstanceUrl = salesforceInstanceUrl;
    }

    /**
     *
     * @param code
     * @return
     * @throws Exception
     */
    public AccessTokenResponse requestAccessTokenByCode(String code) throws Exception {
        LOG.info("\nRequesting access token by code...");
        String url = "https://login.salesforce.com/services/oauth2/token";
        HttpResponse<String> response = Unirest.post(url)
                .field("code", code)
                .field("grant_type", "authorization_code")
                .field("client_id", Credentials.OAUTH_CLIENT_ID)
                .field("client_secret", Credentials.OAUTH_CLIENT_SECRET)
                .field("redirect_uri", Credentials.OAUTH_CALLBACK_URL)
                .asString();

        LOG.log(Level.INFO, "\nResponse status: {0} {1} {2}", new Object[]{response.getStatus(), response.getStatusText(), response.getBody()});
        validateResponse(response);
        LOG.info("\nAccess token ok");
        return gson.fromJson(response.getBody(), AccessTokenResponse.class);
    }

    /**
     *
     * @param refreshToken
     * @return
     * @throws Exception
     */
    public AccessTokenResponse requestAccessTokenByRefreshToken(String refreshToken) throws Exception {
        LOG.info("\nRequesting access token by refresh token...");
        String url = "https://login.salesforce.com/services/oauth2/token";
        HttpResponse<String> response = Unirest.post(url)
                .field("grant_type", "refresh_token")
                .field("client_id", Credentials.OAUTH_CLIENT_ID)
                .field("client_secret", Credentials.OAUTH_CLIENT_SECRET)
                .field("refresh_token", refreshToken)
                .asString();

        LOG.log(Level.INFO, "\nResponse status: {0} {1} {2}", new Object[]{response.getStatus(), response.getStatusText(), response.getBody()});
        validateResponse(response);
        LOG.info("\nAccess token ok");
        return gson.fromJson(response.getBody(), AccessTokenResponse.class);

    }

    /**
     *
     * @param query
     * @return
     * @throws Exception
     */
    public String executeQueryREST(String query) throws Exception {
        validateConnectionSetUp();
        String restQuery = salesforceInstanceUrl + "/services/data/" + vApiVersion + "/query?q=" + query;
        LOG.log(Level.INFO, "\n\nDoing request to: {0}", restQuery);

        HttpResponse<String> response = Unirest.get(restQuery)
                .header("authorization", salesforceTokenType + " " + salesforceAccessToken)
                .asString();

        LOG.log(Level.INFO, "\nResponse status: {0} {1} {2}", new Object[]{response.getStatus(), response.getStatusText(), response.getBody()});
        validateResponse(response);

        return response.getBody();

    }

    /**
     *
     * @param query
     * @return
     * @throws Exception
     */
    public CountResponse executeCountQueryREST(String query) throws Exception {
        String response = executeQueryREST(query);
        return gson.fromJson(response, CountResponse.class);

    }

    /**
     *
     * @param query
     * @return @throws Exception
     */
    public Integer executeCountQuerySOAP(String query) throws Exception {
        LOG.log(Level.INFO, "\nCount query: {0}", query);
        validateConnectionSetUp();
        QueryResult queryResult = partnerConnection.query(query);
        LOG.log(Level.INFO, "\n{0}", queryResult.getSize());
        return queryResult.getSize();
    }

    /**
     *
     *
     * @return @throws Exception
     */
    public GetUserInfoResult requestUserInfo() throws Exception {
        LOG.info("\nRequesting user info...");
        validateConnectionSetUp();
        return partnerConnection.getUserInfo();

    }

    /**
     *
     * @param response
     * @throws Exception
     */
    private void validateResponse(HttpResponse<String> response) throws Exception {
        if (response.getStatus() != 200) {
            String jsonError = response.getBody();
            LOG.log(Level.SEVERE, "\nJson repsonse error: {0}", jsonError);
            ErrorResponse error = gson.fromJson(jsonError, ErrorResponse.class);
            if (error.getErrorDescription().equalsIgnoreCase("ip restricted")) {
                //this is an example of error, you can add more
                throw new Exception("The org seems to have ip restriction");
            } else {
                throw new Exception("Error doing the request. " + response.getStatusText());
            }

        }
    }

    /**
     *
     * @throws ConnectionException
     */
    private void validateConnectionSetUp() throws ConnectionException {
        if (salesforceAccessToken == null) {
            throw new IllegalStateException("The Salesforce access token is null. Unable to make requests without this attribute");
        }
        if (salesforceInstanceUrl == null) {
            throw new IllegalStateException("The Salesforce instance url is null. Unable to make requests without this attribute");
        }
        if (salesforceTokenType == null) {
            throw new IllegalStateException("The Salesforce token type is null. Unable to make requests without this attribute");
        }
        ConnectorConfig enterpriseConfig = new ConnectorConfig();
        enterpriseConfig.setSessionId(salesforceAccessToken);
        enterpriseConfig.setServiceEndpoint(salesforceInstanceUrl + "/services/Soap/u/" + apiVersion);
        enterpriseConfig.setAuthEndpoint(salesforceInstanceUrl + "/services/Soap/u/" + apiVersion);

        partnerConnection = Connector.newConnection(enterpriseConfig);

    }

}
