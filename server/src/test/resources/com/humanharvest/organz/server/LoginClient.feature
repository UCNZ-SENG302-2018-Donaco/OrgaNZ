Feature: Does POST /login/client/ work?
  Scenario: Login with valid credentials
    Given there is a test client
    When I post to /login/client/ using { "id": 1 }
    Then the result is ok
    Then the field token exists

  Scenario: Login with invalid username
    Given there is a test client
    When I post to /login/client/ using { "id": 1000 }
    Then the result is unauthenticated