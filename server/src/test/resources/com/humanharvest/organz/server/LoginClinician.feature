Feature: Does POST /login/clinician/ work?

  Scenario: Login with valid credentials
    Given there is a test clinician with the staff-id 1 and password test
    When I post to /login/clinician/ using { "staffId": 1, "password": "test" }
    Then the result is ok
    Then the field token exists

  Scenario: Login with invalid username
    Given there is a test clinician with the staff-id 1 and password test
    When I post to /login/clinician/ using { "staffId": 2, "password": "test" }
    Then the result is unauthenticated

  Scenario: Login with invalid password
    Given there is a test clinician with the staff-id 1 and password test
    When I post to /login/clinician/ using { "staffId": 1, "password": "bad" }
    Then the result is unauthenticated