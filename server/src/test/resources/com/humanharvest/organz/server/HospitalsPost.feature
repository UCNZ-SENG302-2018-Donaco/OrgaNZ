Feature: Does POST /config/hospitals work?

  Scenario: Post a list of hospitals
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    Given there is a test hospital
    When I post to /config/hospitals using [{"name": "Test Hospital", "latitude": 1, "longitude": 1, "address": "1 Test St", "organs": []}]
    Then the result is ok

  Scenario: Fail to post a list of hospitals without authorisation
    Given authentication is required
    When I post to /config/hospitals using [{"name": "Test Hospital", "latitude": 1, "longitude": 1, "address": "1 Test St", "organs": []}]
    Then the result is unauthenticated

  Scenario: Fail to post an invalid list of hospitals
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    Given there is a test hospital
    When I post to /config/hospitals using [{"garbageField": "random data"}]
    Then the result is bad request