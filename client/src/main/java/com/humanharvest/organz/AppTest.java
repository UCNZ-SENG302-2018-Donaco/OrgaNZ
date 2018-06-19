package com.humanharvest.organz;

import org.springframework.web.client.RestTemplate;

public class AppTest {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:8080/", String.class);
        System.out.println(result);
    }
}
