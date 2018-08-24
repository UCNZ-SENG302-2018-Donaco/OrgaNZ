Feature: Does DELETE /client/ work?

  Scenario: Delete a non-existant client
    When I delete /clients/5
    Then the result is not found

  #Test to see if a 428 Precondition Required error is thrown if we do not supply any If-Match header
  Scenario: Delete a client without an etag
    Given there is a test client
    When I delete /clients/1
    Then the result is precondition required

  #Test to see if a 412 Precondition Failed error is thrown if we supply a non matching If-Match header
  Scenario: Delete a client with a bad etag
    Given there is a test client
    Given I have an etag of value x
    When I delete /clients/1
    Then the result is precondition failed

  #Perform a valid delete with the correct ETag
  Scenario: Delete a valid client
    Given there is a test client
    Given I have an etag from client 1
    When I delete /clients/1
    Then the result is ok