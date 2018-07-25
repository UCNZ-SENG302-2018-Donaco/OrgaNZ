package com.humanharvest.organz.resolvers.administrator;

public interface ClientFileResolver {

    byte[] exportClients();

    String importClients(byte[] data, String fileExtension);
}
