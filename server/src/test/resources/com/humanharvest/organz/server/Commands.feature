Feature: Does POST /commands/ work?
  Scenario: Post help
    When I post to /commands/ using {"command": "help"}
    Then the result is ok
    Then the result contains OrgaNZ