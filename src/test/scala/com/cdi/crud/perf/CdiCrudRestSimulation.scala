package com.cdi.crud.perf

import java.util.UUID

import com.google.gson.{JsonPrimitive, JsonObject}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CdiCrudRestSimulation extends Simulation {


  var totalUsersPerScenario = 30
  var initialUsersPerScenario = 1
  var scenarioDurationInSeconds = 15 //2 users per second
  var expectedMaxResponseTime = 600
  var expectedMeanResponseTime = 70
  var expectedRequestPerSecond = 18


  if (System.getProperty("torture") != null) {
    println("torture mode on!")
    totalUsersPerScenario = 80 //x3 scenario = 210 simultaneous users
    initialUsersPerScenario = 1
    scenarioDurationInSeconds = 400
    expectedMaxResponseTime = 800 //because of too high concurrency some requests take longer
    expectedMeanResponseTime = 25 //mean is lower because of caches(JPA, rest, etc...)
    expectedRequestPerSecond = 120 // 6000 req per minute
  }


  val httpProtocol = http
    .baseURL("http://localhost:8080/cdi-crud/")
    .acceptHeader("application/json;charset=utf-8")
    /*.connection( """keep-alive""")*/
    .contentTypeHeader("application/json; charset=UTF-8")

  val carIdCheck = jsonPath("$[0].id").ofType[Int].saveAs("carId") //used by delete scenario

  val carIds = csv("car-ids.csv").circular

  val listCarsRequest = http("list cars") //<1> //stores the request in a local variable
    .get("rest/cars/")
    .queryParam("start",0).queryParam("max",10)
    .check(status.is(200),carIdCheck) //<2> request assertion


  val findCarRequest = http("find car") //TODO use etag
    .get("rest/cars/${id}")
    .check(status.in(200,404))//car deleted by concurrent user


  val addCarRequest = http("add car")
    .post("rest/cars/")
    .body(ELFileBody("users.json")).asJSON
    .check(status.is(201))

  val deleteCarRequest = http("remove car")
    .delete("rest/cars/${carId}")
    .header("user","admin")
    .check(status.in(204,404)) //404 - a concurrent user deleted before

  val listCarsScenario = scenario("List cars scenario")
    .exec(listCarsRequest)
    .pause(50 milliseconds)// users don't click buttons at the same time

  val findCarsScenario = scenario("Find cars scenario")
    .feed(carIds)
    .exec(findCarRequest)
    .pause(50 milliseconds)

  val addCarsScenario = scenario("Add cars scenario")
    .exec(session =>
    session.set("userName",UUID.randomUUID().toString)
    )
    .exec(addCarRequest)
    .pause(50 milliseconds)

  val deleteCarsScenario = scenario("Delete cars scenario")
    .exec(listCarsRequest) //save first car id in gatling session (tá salvando o mesmo id 25 vezes
    .pause(80 milliseconds)
    .exec(deleteCarRequest)
    .pause(50 milliseconds)

  setUp(  
    listCarsScenario.inject(
      atOnceUsers(5),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
      //constantUsersPerSec(500) during (1 minutes))
     ),
    findCarsScenario.inject(
      atOnceUsers(5),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
    ),
    addCarsScenario.inject(
      atOnceUsers(10),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
    ),
    deleteCarsScenario.inject(
      atOnceUsers(5),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
    )

  )
    .protocols(httpProtocol)
    .assertions(
      global.successfulRequests.percent.greaterThan(95),
      global.responseTime.max.lessThan(expectedMaxResponseTime),
      global.responseTime.mean.lessThan(expectedMeanResponseTime),
      global.requestsPerSec.greaterThan(expectedRequestPerSecond)

    )

}