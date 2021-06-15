package com.microsoft.benchmark.mysql

import de.codecentric.gatling.jdbc.Predef._
import de.codecentric.gatling.jdbc.builder.column.ColumnHelper._
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation


class MySQLGatlingPerformanceTest extends Simulation {

  val jdbcConfig = jdbc.url("jdbc:mysql://aa-master-e2-32c-4t.mysql.database.chinacloudapi.cn:3306/wltest?useSSL=true&requireSSL=false").username("huqianghui@aa-master-e2-32c-4t").password("Andy@163.com").driver("com.mysql.jdbc.Driver")

  val testScenario = scenario("createTable").
    exec(jdbc("bar table")
      .drop()
      .table("bar")
    ).
    exec(jdbc("bar table")
      .create()
      .table("bar")
      .columns(
        column(
          name("abc"),
          dataType("INTEGER"),
          constraint("PRIMARY KEY")
        )
      )
    ).repeat(10, "n") {
    exec(jdbc("insertion")
      .insert()
      .into("bar")
      .values("${n}")
    )
  }.pause(1).
    exec(jdbc("selection")
      .select("*")
      .from("bar")
      .where("abc=4")
    )

  /*setUp(testScenario.inject(atOnceUsers(30)))
      .protocols(jdbcConfig)
      .assertions(global.failedRequests.count.is(0))*/

 /* setUp(testScenario.inject(
    constantConcurrentUsers(30).during(10.seconds)))
    .protocols(jdbcConfig)
    .assertions(global.failedRequests.count.is(0))*/

  /*setUp(
    testScenario.inject(
      nothingFor(4 seconds), // 1
      atOnceUsers(10), // 2
      rampUsers(10) during (5 seconds), // 3
      constantUsersPerSec(20) during(15 seconds), // 4
      constantUsersPerSec(20) during(15 seconds) randomized, // 5
      rampUsersPerSec(10) to 20 during(10 minutes), // 6
      rampUsersPerSec(10) to 20 during(10 minutes) randomized, // 7
      heavisideUsers(1000) during(20 seconds) // 10
    ).protocols(jdbcConfig)
  )*/

  setUp(testScenario.inject(atOnceUsers(1)))
    .protocols(jdbcConfig)
    .assertions(global.failedRequests.count.is(0))


}
