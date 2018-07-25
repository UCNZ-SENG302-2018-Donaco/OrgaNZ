Feature: Does POST /administrators/ work?
  Scenario: Creating a valid administrator
    When I post to /administrators/ using { "username": "Test", "password": "Test" }
    Then the result is created
    Then the content type is json
    Then the field username is Test

  Scenario: Create an invalid administrator
    When I post to /administrators/ using { "username": "Test" }
    Then the result is bad request