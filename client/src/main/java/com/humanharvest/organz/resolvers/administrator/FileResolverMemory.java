package com.humanharvest.organz.resolvers.administrator;

public class FileResolverMemory implements FileResolver {

    @Override
    public byte[] exportClients() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] exportClinicians() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String importClients(byte[] data, String fileExtension) {
        throw new UnsupportedOperationException();
    }
}
