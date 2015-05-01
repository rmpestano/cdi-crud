Feature: Search cars

Scenario Outline: simple search and update
Given search car with model "Ferrari"
When update model to "Audi"
Then searching car by model "<model>" must return <number> of records
Examples:
| model | number |
| Audi  | 1      |
| outro | 0      |

Scenario Outline: search car by price
When search car with price less than <price>
Then must return <number> cars
Examples:
| price     | number |
| 1390.2    | 0      |
| 1390.3    | 1      |
| 10000.0   | 2      |
| 13000.0   | 3      |