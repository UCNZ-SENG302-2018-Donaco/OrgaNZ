Feature: Does POST /login/administrator/ work?

  Scenario: Login with valid credentials
    Given there is a test administrator with the username test and password test
    When I post to /login/administrator/ using { "username": "test", "password": "test" }
    Then the result is ok
    Then the field token exists

  Scenario: Login with invalid username
    Given there is a test administrator with the username test and password test
    When I post to /login/administrator/ using { "username": "bad", "password": "test" }
    Then the result is unauthenticated

  Scenario: Login with invalid password
    Given there is a test administrator with the username test and password test
    When I post to /login/administrator/ using { "username": "test", "password": "bad" }
    Then the result is unauthenticated