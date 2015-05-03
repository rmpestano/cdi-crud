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
  Scenario Outline: Remove cars by id
    When user is logged in as "<user>"
    And search car by id 1
    And click on remove button
    Then message "<message>" should be displayed
    Examples:
     | user   | message                          |
     | admin  | Car Ferrari removed successfully |
     | guest  | Access denied                    |
