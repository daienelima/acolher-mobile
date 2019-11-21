package br.com.acolher.model;

import java.util.Arrays;

public class NotificacaoResponse {

    private int multicast_id;
    private int success;
    private int failure;
    private int canonical_ids;
    private String[] results;

    public NotificacaoResponse() {
    }

    public NotificacaoResponse(int multicast_id, int success, int failure, int canonical_ids, String[] results) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
    }

    public int getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(int multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public String[] getResults() {
        return results;
    }

    public void setResults(String[] results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "NotificacaoResponse{" +
                "multicast_id=" + multicast_id +
                ", success=" + success +
                ", failure=" + failure +
                ", canonical_ids=" + canonical_ids +
                ", results=" + Arrays.toString(results) +
                '}';
    }
}