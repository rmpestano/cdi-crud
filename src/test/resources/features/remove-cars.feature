Feature: Remove cars

  @whitebox
  Scenario: remove car successfully
    Given user is logged in as "admin"
    And search car with model "Ferrari"
    When "Ferrari" is removed
    Then there is no more cars with model "Ferrari"

  @whitebox
  Scenario: should fail to remove car without permission
    Given user is logged in as "guest"
    And search car with model "Ferrari"
    When "Ferrari" is removed
    Then error message must be "Access denied"

  @blackbox
  Scenario Outline: search car by id
    When search car by id <id>
    Then must find car with model "<model>" and price <price>
    Examples:
      | id | model   | price   |
      | 1  | Ferrari | 2450.8  |
      | 2  | Mustang | 12999.0 |
      | 3  | Porche  | 1390.3  |
