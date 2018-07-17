Feature: Does GET /administrators/ work?
  Scenario: Get all administrators
    Given there is a test administrator with the username Test1 and password Test1
    Given there is a test administrator with the username Test2 and password Test2
    When I get /administrators/
    Then the result is ok

    #The default administrator is created implicitly
    Then the result has 3 elements
    Then result 0's username is admin
    Then result 1's username is Test1
    Then result 2's username is Test2

  Scenario: Get filtered administrators
    Given there is a test administrator with the username Test1 and password Test1
    Given there is a test administrator with the username Test2 and password Test2
    When I get /administrators/?q=Test1
    Then the result is ok
    Then the result has 1 elements
    Then result 0's username is Test1

  Scenario: Get count of administrators
    Given there is a test administrator with the username Test1 and password Test1
    Given there is a test administrator with the username Test2 and password Test2
    When I get /administrators/?offset=1&count=1
    Then the result is ok
    Then the result has 1 elements
    Then result 0's username is Test1

  Scenario: Does not return password
    Given there is a test administrator with the username Test and password Test
    When I get /administrators/
    Then the result is ok
    Then result 0's password does not exist

  Scenario: Not being authenticated fails
    Given authentication is required
    When I get /administrators/
    Then the result is unauthenticated

  Scenario: Given authentication succeeds
    Given authentication is required
    Given there is a test administrator with the username Test and password Test
    Given the authentication token is from administrator Test
    When I get /administrators/
    Then the result is ok