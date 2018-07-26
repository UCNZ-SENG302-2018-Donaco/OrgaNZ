package com.humanharvest.organz.resolvers.administrator;

import com.humanharvest.organz.state.State;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class FileResolverRest implements FileResolver {

    @Override
    public byte[] exportClients() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        return State.getRestTemplate().exchange(
                State.BASE_URI + "clients/file",
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                byte[].class
        ).getBody();
    }

    @Override
    public byte[] exportClinicians() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());

        return State.getRestTemplate().exchange(
                State.BASE_URI + "clinicians/file",
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                byte[].class
        ).getBody();
    }

    @Override
    public String importClients(byte[] data, String fileExtension) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());
        switch (fileExtension) {
            case "json":
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                break;
            case "csv":
                httpHeaders.setContentType(new MediaType("text", "csv"));
                break;
            default:
                throw new IllegalArgumentException(fileExtension + " is not a type of file that can be imported.");
        }
        HttpEntity<byte[]> request = new HttpEntity<>(data, httpHeaders);

        return State.getRestTemplate().exchange(
                State.BASE_URI + "clients/file",
                HttpMethod.POST,
                request,
                String.class
        ).getBody();
    }
}
