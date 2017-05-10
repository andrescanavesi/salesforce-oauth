package com.example.salesforce.oauth.domain;

/**
 * Represents this response:
 *
 * {"totalSize":11,"done":true,"records":[]} }
 *
 * @author Andres Canavesi
 */
public class CountResponse {

    private Integer totalSize;
    private Boolean done;

    /**
     *
     * @return
     */
    public Integer getTotalSize() {
        return totalSize;
    }

    /**
     *
     * @param totalSize
     */
    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    /**
     *
     * @return
     */
    public Boolean getDone() {
        return done;
    }

    /**
     *
     * @param done
     */
    public void setDone(Boolean done) {
        this.done = done;
    }

}
