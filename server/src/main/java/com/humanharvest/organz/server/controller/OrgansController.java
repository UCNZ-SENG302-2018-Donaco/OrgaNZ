package com.humanharvest.organz.server.controller;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
//import com.humanharvest.organz.actions.client.DeleteDonatedOrganAction;
import com.humanharvest.organz.actions.client.DeleteDonatedOrganAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.client.Views;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<PaginatedDonatedOrgansList> getOrgansToDonate(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken,
            @RequestParam(required = false) Set<String> regions,
            @RequestParam(required = false) EnumSet<Organ> organType)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        //State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Set<String> newRegions = new HashSet<>();
        if (regions != null) {
            for (String region : regions) {
                newRegions.add(region.replace("%20", " "));
            }
        }

        // Get all organs for donation
        Collection<DonatedOrganView> donatedOrgans = State.getClientManager().getAllOrgansToDonate().stream()
                .map(DonatedOrganView::new)
                .collect(Collectors.toList());

        // Filter by region and organ type if the params have been set
        Stream<DonatedOrganView> stream = donatedOrgans.stream();
        List<DonatedOrganView> filteredOrgans = stream

                .filter(regions == null ? o -> true : organ -> newRegions.isEmpty() ||
                        newRegions.contains(organ.getDonatedOrgan().getDonor().getRegion()) ||
                        ( newRegions.contains("International") && organ.getDonatedOrgan().getDonor().getCountry() !=
                                Country.NZ))


                .filter(organType == null ? o -> true : organ -> organType.isEmpty() ||
                        organType.contains(organ.getDonatedOrgan().getOrganType()))

                .collect(Collectors.toList());

        PaginatedDonatedOrgansList paginatedDonatedOrgansList = new PaginatedDonatedOrgansList(filteredOrgans,
                filteredOrgans.size());


        return new ResponseEntity<>(paginatedDonatedOrgansList, HttpStatus.OK);
    }

    @JsonView(Views.Details.class)
    @DeleteMapping("/organs/{uid}/{id}")
    public ResponseEntity<DonatedOrgan> manuallyExpireOrgan(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token",required = false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {
        //State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (!client.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        DonatedOrgan donatedOrgan = client.get().getDonatedOrganById(id);

        DeleteDonatedOrganAction action = new DeleteDonatedOrganAction(client.get(),donatedOrgan,State
                .getClientManager());
        State.getActionInvoker(authToken).execute(action);

        Client client1 = State.getClientManager()
                .getClientByID(client.get().getUid())
                .orElseThrow(IllegalStateException::new);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client1.getETag());


        return new ResponseEntity<>(donatedOrgan,headers,HttpStatus.OK);

    }

}
