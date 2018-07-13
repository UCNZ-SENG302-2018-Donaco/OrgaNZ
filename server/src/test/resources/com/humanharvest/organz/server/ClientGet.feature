Feature: Does GET /client/ work?
  Scenario: Getting the first client
    Given there is a test client named Jan Micheal Vincent
    When I get /clients/1
    Then the result is ok
    Then the content type is json
    Then the field firstName is Jan

  Scenario: Getting an invalid client
    When I get /clients/2
    Then the result is not found

  Scenario: Getting multiple clients
    Given there is a test client named Jan Micheal Vincent
    Given there is a test client named Fred Bob Smith
    When I get /clients/
    Then the result is ok
    Then the content type is json
    Then the result has 2 elements
    Then result 0's firstName is Jan
    Then result 0's region does not exist