Feature: Does GET /matchOrgansToRecipients work?
  Scenario: Get a list of recipients for an organ
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    When I get /matchOrganToRecipients
    Then the result is ok

  Scenario: Fail to get a list of recipients for an invalid organ
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    When I get /matchOrganToRecipients/ using invalidjson
    Then the result is bad request

  Scenario: Fail to get a list of recipients without authorisation
    When I get /matchOrganToRecipients/ using {"organType": "Liver"}
    Then the result is bad request