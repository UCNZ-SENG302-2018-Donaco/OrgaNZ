Feature: Does DELETE /administrators/ work?

  Scenario: Delete a non-existant administrator
    When I delete /administrators/test
    Then the result is not found

  #Perform a valid delete with the correct ETag
  Scenario: Delete a valid administrator
    Given there is a test administrator with the username test1 and password test
    Given there is a test administrator with the username test2 and password test
    Given I have an etag from administrator test1
    When I delete /administrators/test1
    Then the result is ok

  Scenario: Attempt to delete the default administrator
    Given there is a test administrator
    Given I have an etag from administrator test
    When I delete /administrators/admin
    Then the result is bad request