package com.humanharvest.organz.server.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Views;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    @JsonView(Views.Overview.class)
    public ResponseEntity<List<Client>> getClients() {
        return new ResponseEntity<>(State.getClientManager().getClients(), HttpStatus.OK);
    }

    @RequestMapping(value = "/clients", method = RequestMethod.POST)
    @JsonView(Views.Overview.class)
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        State.getClientManager().addClient(client);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/clients/{id}", method = RequestMethod.GET)
    @JsonView(Views.Details.class)
    public ResponseEntity<Client> getClient(@PathVariable int id) {
        Client client = State.getClientManager().getClientByID(id);
        if (client == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(client, HttpStatus.OK);
        }
    }
}
