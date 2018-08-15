Feature: Does GET /matchOrgansToRecipients work?
  Scenario: Get a list of recipients for organs
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    When I get /matchOrganToRecipients/
    Then the result is ok