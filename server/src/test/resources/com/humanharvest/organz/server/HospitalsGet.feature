Feature: Does GET /config/hospitals work?

  Scenario: Get the list of hospitals
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    Given there is a test hospital
    When I get /config/hospitals
    Then the result is ok
    Then the content type is json

  Scenario: Fail to get a list of recipients without authorisation
    Given authentication is required
    When I get /config/hospitals
    Then the result is unauthenticated