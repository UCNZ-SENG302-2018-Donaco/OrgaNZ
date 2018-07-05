package com.humanharvest.organz.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class Test1 {
    @Autowired
    private WebTestClient webClient;

    @Test
    public void X() throws Exception {
        webClient.get().uri("/clients").exchange().expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World");
    }
}
