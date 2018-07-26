package com.humanharvest.organz.resolvers.administrator;

public interface FileResolver {

    byte[] exportClients();

    byte[] exportClinicians();

    String importClients(byte[] data, String fileExtension);
}
