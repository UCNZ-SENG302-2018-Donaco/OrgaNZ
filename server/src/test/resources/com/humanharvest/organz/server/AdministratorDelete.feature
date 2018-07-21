Feature: Does DELETE /administrators/ work?
  Scenario: Delete a non-existant administrator
    When I delete /administrators/test
    Then the result is not found

  #Test to see if a 428 Precondition Required error is thrown if we do not supply any If-Match header
  Scenario: Delete a administrator without an etag
    Given there is a test administrator
    When I delete /administrators/test
    Then the result is precondition required

  #Test to see if a 412 Precondition Failed error is thrown if we supply a non matching If-Match header
  Scenario: Delete a administrator with a bad etag
    Given there is a test administrator
    Given I have an etag of value x
    When I delete /administrators/test
    Then the result is precondition failed

  #Perform a valid delete with the correct ETag
  Scenario: Delete a valid administrator
    Given there is a test administrator
    Given I have an etag from administrator test
    When I delete /administrators/test
    Then the result is ok