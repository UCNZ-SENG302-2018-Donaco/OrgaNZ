package com.humanharvest.organz.server;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.enums.Organ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        State.init(DataStorageType.MEMORY);
        ClientManager clientManager = State.getClientManager();
        Client jack = new Client("Jack", "EOD", "Steel", LocalDate.of(1997,04,21), 1);
        clientManager.addClient(jack);
        jack.setOrganDonationStatus(Organ.HEART, true);
        jack.setOrganDonationStatus(Organ.KIDNEY, true);
        clientManager.addClient(new Client("Second", "Test", "Client", LocalDate.of(1987,12,21), 2));
        SpringApplication.run(Application.class, args);
    }
}
