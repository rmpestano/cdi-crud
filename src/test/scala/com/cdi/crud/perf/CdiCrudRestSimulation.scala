
package com.cdi.crud.perf

import java.util.UUID

import com.google.gson.{JsonPrimitive, JsonObject}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.concurrent.forkjoin.ThreadLocalRandom

class CdiCrudRestSimulation extends Simulation {

  val printSession =
    exec(session => {
      println(session)
      session
    })

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

  val rnd = scala.util.Random

  val carIdsCheck = jsonPath("$..id").findAll.saveAs("carIds") //used by delete scenario

  val eTagCheck = header("ETag").saveAs("eTag")

  val countCheck = jsonPath("$..*").ofType[Int].saveAs("count")

  val idsFeed = csv("car-ids.csv").circular


  val countCarsRequest = {
    http("count cars") //<1> //stores the request in a local variable
    .get("rest/cars/count")
    .check(status.is(200), countCheck)
  }


  val listCarsRequest = http("list cars") //<1> //stores the request in a local variable
    .get("rest/cars/")
    .queryParam("start","${page}")//random page was saved in count request
    .queryParam("max",10)
    .check(status.is(200),carIdsCheck) //<2> request assertion

  val findCarRequest = http("find car")
    .get("rest/cars/${carIds.random()}")
    .check(status.in(200,404))// 404 = car deleted by concurrent user


  val addCarRequest = http("add car")
    .post("rest/cars/")
    .body(ELFileBody("cars.json")).asJSON
    .check(status.is(201))

  val deleteCarRequest = http("remove car")
    .delete("rest/cars/${carIds.random()}")
    .header("user","admin")
    .check(status.in(204,404)) //404 - a concurrent user deleted before

  val saveCount = exec(session => {
    val count = session("count").as[Int]
    session.set("page", ThreadLocalRandom.current.nextInt(count)-1)//save a random db page based on the number of cars
  })

  val listCarsScenario = scenario("List cars scenario")
    .exec(countCarsRequest)
    .pause(80 milliseconds)
    .exec(saveCount)
    .pause(80 milliseconds)
    .exec(listCarsRequest)
    //.exec(printSession)
    .pause(50 milliseconds)// users don't click buttons at the same time

  val findCarsScenario = scenario("Find cars scenario")
    //.feed(carIds) will find always the same cars
    .exec(countCarsRequest)
    .pause(80 milliseconds)
    .exec(saveCount)
    .pause(80 milliseconds)
    .exec(listCarsRequest) //saves a list of random ids in carIds session variable
    .pause(80 milliseconds)
    .exec(findCarRequest)
    .pause(50 milliseconds)

  val addCarsScenario = scenario("Add cars scenario")
    .exec(session =>
    session.set("userName",UUID.randomUUID().toString)
    )
    .exec(addCarRequest)
    .pause(50 milliseconds)

  val deleteCarsScenario = scenario("Delete cars scenario")
    .exec(countCarsRequest)
    .pause(80 milliseconds)
    .exec(saveCount)
    .pause(80 milliseconds)
    .exec(listCarsRequest) //save first car id in gatling session (tá salvando o mesmo id 25 vezes
    .pause(80 milliseconds)
    .exec(deleteCarRequest)
    .pause(50 milliseconds)

  setUp(
    listCarsScenario.inject(
      rampUsers(5) over(5 seconds),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
      //constantUsersPerSec(500) during (1 minutes))
     ),
    findCarsScenario.inject(
      atOnceUsers(5),
      rampUsers(10) over(10 seconds),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
    ),
    addCarsScenario.inject(
      atOnceUsers(10),
      rampUsers(10) over(10 seconds),
      rampUsersPerSec(initialUsersPerScenario) to (totalUsersPerScenario) during(scenarioDurationInSeconds seconds)
    ),
    deleteCarsScenario.inject(
      atOnceUsers(5),
      rampUsers(10) over(5 seconds),
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