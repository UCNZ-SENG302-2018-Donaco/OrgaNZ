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
    Then the paginated result has 2 elements

  Scenario: Getting multiple sorted clients by name
    Given there is a test client named Jan Micheal Vincent
    Given there is a test client named Fred Bob Smith
    When I get /clients/?sortOption=NAME
    Then the result is ok
    Then the content type is json
    Then the paginated result has 2 elements
    Then paginated result 0's firstName is Fred
    Then paginated result 1's firstName is Jan

  Scenario: Getting multiple sorted clients by id
    Given there is a test client named Jan Micheal Vincent
    Given there is a test client named Fred Bob Smith
    When I get /clients/?sortOption=ID
    Then the result is ok
    Then the content type is json
    Then the paginated result has 2 elements
    Then paginated result 0's firstName is Jan
    Then paginated result 1's firstName is Fred

  Scenario: Getting multiple sorted clients by age
    Given there is a test client named Jan Micheal Vincent
    Given there is a test client named Fred Bob Smith
    Given client 1's age is 20
    Given client 2's age is 18
    When I get /clients/?sortOption=AGE
    Then the result is ok
    Then the content type is json
    Then the paginated result has 2 elements
    Then paginated result 0's firstName is Fred
    Then paginated result 1's firstName is Jan

  Scenario: Getting multiple filtered clients by gender
    Given there is a test client named Jan Micheal Vincent
    Given there is a test client named Fred Bob Smith
    Given client 1's birthGender is Male
    Given client 2's birthGender is Female
    When I get /clients/?birthGenders=MALE
    Then the result is ok
    Then the content type is json
    Then the paginated result has 1 elements
    Then paginated result 0's firstName is Jan