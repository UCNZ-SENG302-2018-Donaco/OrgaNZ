Feature: Does PATCH /administrators/ work?

  Scenario: Update a non-existant administrator
    When I patch to /administrators/test using { "username": "New" }
    Then the result is not found

  Scenario: Update an administrator using invalid json
    Given there is a test administrator
    Given I have an etag from administrator test
    When I patch to /administrators/test using notvalidjson
    Then the result is bad request

  Scenario: Update an administrator without an etag
    Given there is a test administrator
    When I patch to /administrators/test using { "username": "New" }
    Then the result is precondition required

  Scenario: Update an administrator with a bad etag
    Given there is a test administrator
    Given I have an etag of value x
    When I patch to /administrators/test using { "username": "New" }
    Then the result is precondition failed

  Scenario: Update an administrator using valid data
    Given there is a test administrator
    Given I have an etag from administrator test
    When I patch to /administrators/test using { "username": "New" }
    Then the result is ok

  Scenario: Update an administrator replacing all the data
    Given there is a test administrator
    Given I have an etag from administrator test
    When I patch to /administrators/test using {"username": "Fred", "password": "Bob"}
    Then the result is ok

  Scenario: Update an administrator with a null value
    Given there is a test administrator
    Given I have an etag from administrator test
    When I patch to /administrators/test using { "username": null }
    Then the result is bad request