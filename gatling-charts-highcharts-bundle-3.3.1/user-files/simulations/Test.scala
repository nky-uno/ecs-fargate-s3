import scala.concurrent.duration._
import scala.io.Source
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.util.Random
import java.util.Date
import java.lang.Thread
import java.util.concurrent.TimeUnit

class Test_Api extends Simulation {


val api_conf = ConfigFactory.load("api_test.conf")
val api_host = api_conf.getString("api_host")

val httpProtocol_api = http
    .baseUrl(api_host)
    .connectionHeader("close")
    .silentResources
    .disableFollowRedirect
    
    
val headers_api = Map(
    "Host" -> "com",
    "User-agent" -> "last")

val csvFeederApi = ( 1 to 20000 by 1 ).toStream.map(i => Map("test_api" -> f"$i%010d" )).toArray

val runtime = api_conf.getInt("test_term")

val ramptime_limit = 60

val Req_tps = Map(
    "api" -> BigDecimal(api_conf.getInt("Req_tps.api"))
    )

val User_count = Map(
    "api" -> BigDecimal(api_conf.getInt("User_count.api"))
    )

val api = scenario("API")
    .feed(csvFeederApi.circular)
    .exec(http("api")
    .get("/")
    .headers(headers_api)
    .header("User-agent","last${test_api}")
    )



setUp(
api.inject(
constantConcurrentUsers(User_count("api").toInt) during(60 minute))
.throttle(
reachRps(Req_tps("api").toInt) in (15 second), 
holdFor(10 minute),
jumpToRps(10),
holdFor(30 minute)
).protocols(httpProtocol_api)
)
}

