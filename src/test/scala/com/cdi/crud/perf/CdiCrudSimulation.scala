package com.cdi.crud.perf

import java.util.UUID

import com.google.gson.{JsonPrimitive, JsonObject}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CdiCrudSimulation extends Simulation {


  var totalUsersPerScenario = 10
  var initialUsersPerScenario = 1
  var scenarioDurationInSeconds = 30
  var expectedMaxResponseTime = 300
  var expectedMeanResponseTime = 50
  var expectedRequestPerSecond = 500


  if (System.getProperty("torture") != null) {
    println("torture mode on!")
    totalUsersPerScenario = 50
    initialUsersPerScenario = 1
    scenarioDurationInSeconds = 300
    expectedMaxResponseTime = 500 //because of too high concurrency some requests take longer
    expectedMeanResponseTime = 15 //mean is lower because of caches(JPA, rest, etc...)
    expectedRequestPerSecond = 50000
  }


  val httpProtocol = http
    .baseURL("http://localhost:8080/cdi-crud/")
    .acceptHeader("application/json;charset=utf-8")
    /*.connection( """keep-alive""")*/
    .contentTypeHeader("application/json; charset=UTF-8")


  val carIds = csv("car-ids.csv").circular

  val listCarsRequest = http("list cars") //<1> //stores the request in a local variable
    .get("rest/cars/")
    .check(status.is(200)) //<2> request assertion


  val findCarRequest = http("find car") //TODO use etag
    .get("rest/cars/${id}")
    .check(status.is(200))


  val addCarRequest = http("add car")
    .post("rest/cars/")
    .body(ELFileBody("users.json")).asJSON
    .check(status.is(201))

  val listCarsScenario = scenario("List cars scenario")
    .exec(listCarsRequest)
    .pause(50 milliseconds)

  val findCarsScenario = scenario("Find cars scenario")
    .feed(carIds)
    .exec(findCarRequest)
    .pause(50 milliseconds)

  val addCarsScenario = scenario("Add cars scenario")
    .exec(session => {
    session.set("userName",UUID.randomUUID().toString)
    session
    })
    .exec(addCarRequest)
    .pause(50 milliseconds)

  setUp(  
    /*listCarsScenario.inject(
      atOnceUsers(20), 
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
      //constantUsersPerSec(500) during (1 minutes))
     ),
    findCarsScenario.inject(
      atOnceUsers(20),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
      //constantUsersPerSec(500) during (1 minutes))
    ),*/
    addCarsScenario.inject(
      atOnceUsers(20),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
      //constantUsersPerSec(500) during (1 minutes))
    )

  )
    .protocols(httpProtocol)
    .assertions(
      global.successfulRequests.percent.greaterThan(90),
      global.responseTime.max.lessThan(expectedMaxResponseTime),
      global.responseTime.mean.lessThan(expectedMeanResponseTime),
      global.requestsPerSec.greaterThan(expectedRequestPerSecond)

    )

}