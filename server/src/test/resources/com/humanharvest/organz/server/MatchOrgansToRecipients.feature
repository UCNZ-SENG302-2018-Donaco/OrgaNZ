Feature: Does GET /matchOrgansToRecipients work?
  Scenario: Get a list of recipients for an organ
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    Given there is a test donated organ
    When I get /matchOrganToRecipients with a valid donated organ id
    Then the result is ok
    Then the content type is json

  Scenario: Fail to get a list of recipients for an invalid organ
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    Given there is a test donated organ
    When I get /matchOrganToRecipients/0
    Then the result is not found

  Scenario: Fail to get a list of recipients without authorisation
    Given authentication is required
    When I get /matchOrganToRecipients/0
    Then the result is unauthenticated