Feature: Does PATCH /clients/ work?
  Scenario: Update a non-existant client
    When I patch to /clients/5 using { "middleName": "New" }
    Then the result is not found

  Scenario: Update a client using invalid json
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using notvalidjson
    Then the result is bad request

  Scenario: Update a client without an etag
    Given there is a test client
    When I patch to /clients/1 using { "middleName": "New" }
    Then the result is precondition required

  Scenario: Update the client with a bad etag
    Given there is a test client
    Given I have an etag of value x
    When I patch to /clients/1 using { "middleName": "New" }
    Then the result is precondition failed

  Scenario: Update a client using valid data
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "middleName": "New" }
    Then the result is ok

  Scenario: Update a client replacing all the data
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using {"firstName": "Fred", "middleName": "Bob", "lastName": "Smith", "preferredName": "Joe", "currentAddress": "123 Fake St"}
    Then the result is ok

  Scenario: Update a client with a null value
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "firstName": null }
    Then the result is bad request

  Scenario: Update the uid field
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "uid": 500 }
    Then the result is ok
    Then the field uid is 1

  Scenario: Update the region
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "region": "CANTERBURY" }
    Then the result is ok
    #assertEquals(Region.CANTERBURY, testClient.getRegion());

  Scenario: Update to an invalid region
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "region": "NOTAREGION" }
    Then the result is bad request

  Scenario: Update blood type
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "bloodType": "O_NEG" }
    Then the result is ok
    #assertEquals(BloodType.O_NEG, testClient.getBloodType());

  Scenario: Update gender and genderIdentity
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "gender": "MALE", "genderIdentity": "FEMALE" }
    Then the result is ok
    #assertEquals(Gender.MALE, testClient.getGender());
    #assertEquals(Gender.FEMALE, testClient.getGenderIdentity());

  Scenario: Update height and weight
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "weight": 50, "height": 180 }
    Then the result is ok
    #assertEquals(50.0, testClient.getWeight());
    #assertEquals(180.0, testClient.getHeight());

  Scenario: Update dates
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "dateOfBirth": "1987-01-01", "dateOfDeath": "1997-01-01" }
    Then the result is ok
    #assertEquals(LocalDate.of(1987, 1, 1), testClient.getDateOfBirth());
    #assertEquals(LocalDate.of(1997, 1, 1), testClient.getDateOfDeath());

  Scenario: Update to future
    Given there is a test client
    Given I have an etag from client 1
    When I patch to /clients/1 using { "dateOfBirth": "3000-01-01" }
    Then the result is bad request