Feature: Does DELETE /client/ work?

  Scenario: Delete a non-existent client
    When I delete /clients/5
    Then the result is not found

  #Perform a valid delete with the correct ETag
  Scenario: Delete a valid client
    Given there is a test client
    Given I have an etag from client 1
    When I delete /clients/1
    Then the result is ok