package com.humanharvest.organz.resolvers;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.administrator.CommandView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class CommandRunnerRest implements CommandRunner {

    @Override
    public String execute(String commandText) {
        CommandView commandView = new CommandView(commandText);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity<CommandView> entity = new HttpEntity<>(commandView, httpHeaders);

        ResponseEntity<String> responseEntity = State.getRestTemplate()
                .postForEntity(State.getBaseUri() + "commands", entity, String.class);

        return responseEntity.getBody();
    }
}