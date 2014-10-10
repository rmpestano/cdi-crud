Feature: Search and update car

Scenario Outline: simple search and update
Given search car with model "Ferrari"
When update model to "Audi"
Then searching car by model "<model>" must return <number> of records
Examples:
| model | number |
| Audi  | 1      |
| outro | 0      |
