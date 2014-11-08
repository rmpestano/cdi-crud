Feature: Search cars

@whitebox
Scenario Outline: simple search and update
Given search car with model "Ferrari"
When update model to "Audi"
Then searching car by model "<model>" must return <number> of records
Examples:
| model | number |
| Audi  | 1      |
| outro | 0      |

@whitebox
Scenario Outline: search car by price
When search car with price less than <price>
Then must return <number> cars
Examples:
| price     | number |
| 1390.2    | 0      |
| 1390.3    | 1      |
| 10000.0   | 2      |
| 13000.0   | 3      |

@blackbox
Scenario Outline: search car by id
When search car by id <id>
Then must find car with model "<model>" and price <price>
Examples:
| id     | model   |  price  |
| 1      | Ferrari | 2450.8  |
| 2      | Mustang | 12999.0 |
| 3      | Porche  | 1390.3  |
