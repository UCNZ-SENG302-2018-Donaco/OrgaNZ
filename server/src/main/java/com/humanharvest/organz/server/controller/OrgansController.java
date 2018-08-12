package com.humanharvest.organz.server.controller;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.actions.client.ManuallyOverrideOrganAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.Views;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrgansController {

    /**
     * The GET endpoint for getting all organs currently available to be donated
     * @param authToken authentication token - only clinicians and administrators can access donatable organs
     * @return response entity containing all organs that are available for donation
     * @throws GlobalControllerExceptionHandler.InvalidRequestException
     */
    @JsonView(Views.Overview.class)
    @GetMapping("/clients/organs")
    public ResponseEntity<Collection<DonatedOrganView>> getOrgansToDonate(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Collection<DonatedOrganView> donatedOrgans = State.getClientManager().getAllOrgansToDonate().stream()
                .map(DonatedOrganView::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(donatedOrgans, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for manually expiring available organs
     * @param uid The client UID to delete
     * @param id of the available organ
     * @param authToken authentication token - only clinicians and administrators can access donatable organs
     * @return response entity containing the removed organ
     * @throws GlobalControllerExceptionHandler.InvalidRequestException
     */
    @JsonView(Views.Details.class)
    @DeleteMapping("/organs/{uid}/{id}")
    public ResponseEntity<DonatedOrgan> manuallyExpireOrgan(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token",required = false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (!client.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        DonatedOrgan donatedOrgan = client.get().getDonatedOrganById(id);

        ManuallyOverrideOrganAction action = new ManuallyOverrideOrganAction(donatedOrgan, "",
                State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        Client client1 = State.getClientManager()
                .getClientByID(client.get().getUid())
                .orElseThrow(IllegalStateException::new);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client1.getETag());


        return new ResponseEntity<>(donatedOrgan,headers,HttpStatus.OK);

    }

}
