package com.humanharvest.organz;

/**
 * Class to store statistics used on the dashboard
 */
public class DashboardStatistics {

    private int clientCount;
    private int donorCount;
    private int receiverCount;
    private int donorReceiverCount;

    private int organCount;
    private int requestCount;

    public DashboardStatistics(int clientCount, int donorCount, int receiverCount, int donorReceiverCount, int
            organCount, int requestCount) {
        this.clientCount = clientCount;
        this.donorCount = donorCount;
        this.receiverCount = receiverCount;
        this.donorReceiverCount = donorReceiverCount;

        this.organCount = organCount;
        this.requestCount = requestCount;
    }

    public DashboardStatistics() {

    }


    public int getClientCount() {
        return clientCount;
    }

    public void setClientCount(int clientCount) {
        this.clientCount = clientCount;
    }

    public int getDonorCount() {
        return donorCount;
    }

    public void setDonorCount(int donorCount) {
        this.donorCount = donorCount;
    }

    public int getReceiverCount() {
        return receiverCount;
    }

    public void setReceiverCount(int receiverCount) {
        this.receiverCount = receiverCount;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getOrganCount() {
        return organCount;
    }

    public void setOrganCount(int organCount) {
        this.organCount = organCount;
    }

    public int getDonorReceiverCount() {
        return donorReceiverCount;
    }

    public void setDonorReceiverCount(int donorReceiverCount) {
        this.donorReceiverCount = donorReceiverCount;
    }

    public int getNeitherCount() {
        return clientCount - (donorCount + receiverCount + donorReceiverCount);
    }
}
