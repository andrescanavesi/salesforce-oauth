package com.example.salesforce.oauth.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a json error, example:
 *
 * {"error":"invalid_grant","error_description":"ip restricted"}
 *
 * @author Andres Canavesi
 */
public class ErrorResponse {

    private String error;
    @SerializedName("error_description")
    private String errorDescription;

    /**
     *
     * @return
     */
    public String getError() {
        return error;
    }

    /**
     *
     * @param error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     *
     * @return
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     *
     * @param errorDescription
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

}
