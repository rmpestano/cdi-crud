package com.cdi.crud.perf

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CdiCrudSimulation extends Simulation {


  val httpProtocol = http
    .baseURL("http://localhost:8080/cdi-crud/")
    .acceptHeader("application/json;charset=utf-8")
    .contentTypeHeader("application/json; charset=UTF-8")

  val listCarsRequest = http("list cars") //<1> //stores the request in a local variable
    .get("rest/cars/")
    .check(status.is(200)) //<2> request assertion

  val listCarsScenario = scenario("List cars") //<3> a scenario is a group of one or more requests
    .exec(listCarsRequest)

  setUp( //<4> scenario setup
    listCarsScenario.inject(
      atOnceUsers(10), //<4>
      rampUsersPerSec(1) to (10) during(20 seconds), //<5>
      constantUsersPerSec(2) during (15 seconds))
     )
    .protocols(httpProtocol) //<6> request template
    .assertions(//<7>overall assertions
      global.successfulRequests.percent.greaterThan(95), //<8>for all requests
      details("list cars").responseTime.mean.lessThan(50), //<9>for specific group of requests
      details("list cars").responseTime.max.lessThan(300)
    )

}