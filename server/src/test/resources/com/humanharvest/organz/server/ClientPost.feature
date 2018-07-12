Feature: Does POST /client/ work?
  Scenario: Creating a valid client
    When I create a client using { "firstName": "New", "lastName": "Test", "dateOfBirth": "1987-01-01" }
    Then the result is created
    Then the content type is json
    Then the field firstName is New
    Then the field lastName is Test
    Then the field dateOfBirth is "1987-01-01"
    Then the field middleName is null

  Scenario: Create an invalid client
    When I create a client using { "firstName": "New", "dateOfBirth": "1987-01-01" }
    Then the result is bad request