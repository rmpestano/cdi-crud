package com.cdi.crud.perf


import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.session.Expression

class CdiCrudSimulation extends Simulation {


  val httpProtocol = http
    .baseURL("http://localhost:8080/cdi-crud")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,application/json;charset=utf-8")
    .acceptEncodingHeader("gzip, deflate")
    .inferHtmlResources()
    .connection( """keep-alive""")
    /*.contentTypeHeader("application/json; charset=UTF-8")*/
    .contentTypeHeader("*/*")
    .acceptLanguageHeader("pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0")



  //JSF requests and scenarios

  val jsf_headers = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Pragma" -> "no-cache",
    "X-Requested-With" -> "XMLHttpRequest")

  val jsfViewStateCheck = regex( """="javax.faces.ViewState" value="([^"]*)"""")
    .saveAs("viewState")

  def jsfGet(name: String, url: Expression[String]) =
    http(name)
      .get(url)
      .check(jsfViewStateCheck)

  def jsfPost(name: String, url: Expression[String]) = http(name)
    .post(url)
    .formParam("javax.faces.ViewState", "${viewState}")
    .check(jsfViewStateCheck)

  def openDialogRequest = jsfPost("request_open_dialog","/index.faces?dswid=4728")
    .header("Faces-Request", "partial/ajax")
    .formParam("javax.faces.partial.ajax", "true")
    .formParam("javax.faces.source", "openLogin")
    .formParam("javax.faces.partial.execute", "openLogin")
    .formParam("javax.faces.partial.render", "logonForm:logonPanel")
    .formParam("openLogin", "openLogin")
    .formParam("javax.faces.ClientWindow", "4728")
    .check(status.is(200))

  def doLogonRequest =
    jsfPost("request_logon","/index.faces?dswid=4728")
      .header("Faces-Request", "partial/ajax")
      .formParam("javax.faces.partial.ajax", "true")
      .formParam("javax.faces.source", "logonForm:btLogin")
      .formParam("javax.faces.partial.execute", "@all")
      .formParam("logonForm:btLogin", "logonForm:btLogin")
      .formParam("logonForm", "logonForm")
      .formParam("logonForm:user", "admin")//input text
      .formParam("javax.faces.ClientWindow", "4728")
      .check(status.is(200))


  val loginScenario = scenario("login")
    .exec(
      jsfGet("saveState","/index.faces")
      .resources(http("request_resources").get( "/"))
     .check(status.is(200))
    )
    /*.exec(openDialogRequest)
    .pause(2)
    .exec(doLogonRequest)
    .pause(1)*/

  setUp(
    loginScenario.inject( atOnceUsers(1) )
  )
  .protocols(httpProtocol)
    .assertions(
      global.successfulRequests.percent.greaterThan(95)
    )


}