package com.humanharvest.organz;

/**
 * Class to store statistics used on the dashboard
 */
public class DashboardStatistics {

    private int clientsTotal;
    private int donorsTotal;
    private int receiversTotal;
    private int donorReceiverTotal;

    private int matchesTotal;
    private int organsTotal;

    public int getClientsTotal() {
        return clientsTotal;
    }

    public void setClientsTotal(int clientsTotal) {
        this.clientsTotal = clientsTotal;
    }

    public int getDonorsTotal() {
        return donorsTotal;
    }

    public void setDonorsTotal(int donorsTotal) {
        this.donorsTotal = donorsTotal;
    }

    public int getReceiversTotal() {
        return receiversTotal;
    }

    public void setReceiversTotal(int receiversTotal) {
        this.receiversTotal = receiversTotal;
    }

    public int getMatchesTotal() {
        return matchesTotal;
    }

    public void setMatchesTotal(int matchesTotal) {
        this.matchesTotal = matchesTotal;
    }

    public int getOrgansTotal() {
        return organsTotal;
    }

    public void setOrgansTotal(int organsTotal) {
        this.organsTotal = organsTotal;
    }

    public int getDonorReceiverTotal() {
        return donorReceiverTotal;
    }

    public void setDonorReceiverTotal(int donorReceiverTotal) {
        this.donorReceiverTotal = donorReceiverTotal;
    }
}
