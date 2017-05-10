package com.example.salesforce.oauth.web;

import com.example.salesforce.oauth.domain.AccessTokenResponse;
import com.example.salesforce.oauth.domain.CountResponse;
import com.example.salesforce.oauth.util.WsHelper;
import com.sforce.soap.partner.GetUserInfoResult;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 * @see the Oauth flow at: https://developer.salesforce.com/page/Digging_Deeper_into_OAuth_2.0_on_Force.com
 *
 * @author Andres Canavesi
 */
@Named(value = "oauthManagedBean")
@ViewScoped
@ManagedBean
public class OauthManagedBean {

    private static final Logger LOG = Logger.getLogger(OauthManagedBean.class.getName());

    private GetUserInfoResult userInfo;
    private Integer leadsCount;

    /**
     *
     */
    @PostConstruct
    public void init() {
        try {
            LOG.info("\n***** START *****");

            Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

            if (map.containsKey("error")) {
                String description = map.get("error_description");
                throw new Exception(description);
            }

            String code = map.get("code");
            if (code == null) {
                throw new RuntimeException("There is not a parameter called code. This parameter comes from Salesforce");
            }
            LOG.log(Level.INFO, "\nThe code from Salesforce: {0}", code);

            WsHelper wsHelper = new WsHelper();

            /**
             * After getting the code we have to get the access token. IMPORTANT: It's a good practice to keep
             * accessToken token only in memory, do not store. Store refreshToken (encrypted) instead
             */
            AccessTokenResponse accessTokenResponse = wsHelper.requestAccessTokenByCode(code);
            //****** From this line we can do requests that require authentication ******

            if (accessTokenResponse.getRefreshToken() == null) {
                throw new Exception("Response from Salesforce has not refresh token. Maybe you didn't enable the permission 'Perform requests on your behalf at any time (refresh_token, offline_access)' in your connected app ");
            }

            wsHelper.setSalesforceAccessToken(accessTokenResponse.getAccessToken());
            wsHelper.setSalesforceTokenType(accessTokenResponse.getTokenType());
            wsHelper.setSalesforceInstanceUrl(accessTokenResponse.getInstanceUrl());

            userInfo = wsHelper.requestUserInfo();
            LOG.log(Level.INFO, "User full name: {0}. User email: {1}", new Object[]{userInfo.getUserFullName(), userInfo.getUserEmail()});

            String query = "SELECT COUNT() FROM Lead LIMIT 1000";

            //count leads using SOAP API
            leadsCount = wsHelper.executeCountQuerySOAP(query);
            LOG.log(Level.INFO, "Count leads using SOAP: {0}", leadsCount);

            //count leads using REST API
            query = query.replaceAll(" ", "+"); // <<<------- https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/dome_query.htm
            CountResponse count = wsHelper.executeCountQueryREST(query);
            LOG.log(Level.INFO, "Count leads using REST: {0}", count.getTotalSize());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            LOG.info("\n***** DONE *****");
        }

    }

    /**
     *
     * @return
     */
    public GetUserInfoResult getUserInfo() {
        return userInfo;
    }

    /**
     *
     * @return
     */
    public Integer getLeadsCount() {
        return leadsCount;
    }

}
