package com.microsoft.benchmark.mysql

import de.codecentric.gatling.jdbc.Predef._
import de.codecentric.gatling.jdbc.check.JdbcSimpleCheck
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.DurationInt
import scala.util.Random

class SystemBenchPerformanceTest extends Simulation{

  val jdbcConfig = jdbc.url("jdbc:mysql://aa-master-e2-32c-4t.mysql.database.chinacloudapi.cn:3306/sbtest?useSSL=true&requireSSL=false").username("huqianghui@aa-master-e2-32c-4t").password("Andy@163.com").driver("com.mysql.cj.jdbc.Driver")
  // val jdbcConfig = jdbc.url("jdbc:mysql://vm-mysqlserver-32c-e2.chinaeast2.cloudapp.chinacloudapi.cn:3306/mall_spdb?useSSL=true&requireSSL=false").username("myadmin").password("password@123ABC").driver("com.mysql.cj.jdbc.Driver")

  val searchPkFeeder = Iterator.continually(Map("searchRandomPk" -> Random.nextInt(10000000)))

  val id = new AtomicInteger(10000001)
  val pkIdFeeder: Iterator[Map[String, Int]] = Iterator.continually(Map("autoPk" -> id.getAndIncrement()))

  val systemBenchServiceScenario1 = scenario("system-bench-case1")
    .feed(pkIdFeeder)
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest1")
      .where("id > 10000000")
    ).exec(jdbc("insert sbtest1")
    .insert()
    .into("sbtest1" +
      "(id ," +
      "k ," +
      "c ," +
      "pad)")
    .values( "${autoPk}," +
      "${autoPk}," +
      "'98756348016-69119199127-93668626818-58833859316-23503426990-30887483852-09587067298-07595478603-14470766687-79320847421'," +
      "'82343182218-75122207739-98082475690-33929140169-00985969597'"))
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest1")
      .where("id > 10000000"))
    .feed(searchPkFeeder)
    .repeat(20,"searchCount"){
      exec(jdbc("search sbtest1 records")
        .select("*")
        .from("sbtest1")
        .where("id = ${searchRandomPk}")
        .check(JdbcSimpleCheck(result =>
          {
            result.toList.length == 1
          }))
      )
    }


  val systemBenchServiceScenario2 = scenario("system-bench-case2")
    .feed(pkIdFeeder)
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest2")
      .where("id > 10000000")
    ).exec(jdbc("insert sbtest2")
    .insert()
    .into("sbtest2" +
      "(id ," +
      "k ," +
      "c ," +
      "pad)")
    .values( "${autoPk}," +
      "${autoPk}," +
      "'98756348016-69119199127-93668626818-58833859316-23503426990-30887483852-09587067298-07595478603-14470766687-79320847421'," +
      "'82343182218-75122207739-98082475690-33929140169-00985969597'"))
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest2")
      .where("id > 10000000"))
    .feed(searchPkFeeder)
    .repeat(20,"searchCount"){
      exec(jdbc("search sbtest2 records")
        .select("*")
        .from("sbtest2")
        .where("id = ${searchRandomPk}")
        .check(JdbcSimpleCheck(result =>
        {
          result.toList.length == 1
        }))
      )
    }


  val systemBenchServiceScenario3 = scenario("system-bench-case3")
    .feed(pkIdFeeder)
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest3")
      .where("id > 10000000")
    ).exec(jdbc("insert sbtest3")
    .insert()
    .into("sbtest3" +
      "(id ," +
      "k ," +
      "c ," +
      "pad)")
    .values( "${autoPk}," +
      "${autoPk}," +
      "'98756348016-69119199127-93668626818-58833859316-23503426990-30887483852-09587067298-07595478603-14470766687-79320847421'," +
      "'82343182218-75122207739-98082475690-33929140169-00985969597'"))
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest3")
      .where("id > 10000000"))
    .feed(searchPkFeeder)
    .repeat(20,"searchCount"){
      exec(jdbc("search sbtest3 records")
        .select("*")
        .from("sbtest3")
        .where("id = ${searchRandomPk}")
        .check(JdbcSimpleCheck(result =>
        {
          result.toList.length == 1
        }))
      )
    }

  val systemBenchServiceScenario4 = scenario("system-bench-case4")
    .feed(pkIdFeeder)
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest4")
      .where("id > 10000000")
    ).exec(jdbc("insert sbtest4")
    .insert()
    .into("sbtest4" +
      "(id ," +
      "k ," +
      "c ," +
      "pad)")
    .values( "${autoPk}," +
      "${autoPk}," +
      "'98756348016-69119199127-93668626818-58833859316-23503426990-30887483852-09587067298-07595478603-14470766687-79320847421'," +
      "'82343182218-75122207739-98082475690-33929140169-00985969597'"))
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest4")
      .where("id > 10000000"))
    .feed(searchPkFeeder)
    .repeat(20,"searchCount"){
      exec(jdbc("search sbtest4 records")
        .select("*")
        .from("sbtest4")
        .where("id = ${searchRandomPk}")
        .check(JdbcSimpleCheck(result =>
        {
          result.toList.length == 1
        }))
      )
    }

  val systemBenchServiceScenario5 = scenario("system-bench-case5")
    .feed(pkIdFeeder)
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest5")
      .where("id > 10000000")
    ).exec(jdbc("insert sbtest5")
    .insert()
    .into("sbtest5" +
      "(id ," +
      "k ," +
      "c ," +
      "pad)")
    .values( "${autoPk}," +
      "${autoPk}," +
      "'98756348016-69119199127-93668626818-58833859316-23503426990-30887483852-09587067298-07595478603-14470766687-79320847421'," +
      "'82343182218-75122207739-98082475690-33929140169-00985969597'"))
    .exec(jdbc("delete test records")
      .delete()
      .from("sbtest5")
      .where("id > 10000000"))
    .feed(searchPkFeeder)
    .repeat(20,"searchCount"){
      exec(jdbc("search sbtest5 records")
        .select("*")
        .from("sbtest5")
        .where("id = ${searchRandomPk}")
        .check(JdbcSimpleCheck(result =>
        {
          result.toList.length == 1
        }))
      )
    }


  setUp(
    systemBenchServiceScenario1.inject(constantConcurrentUsers(10).during(120.seconds)),
    systemBenchServiceScenario2.inject(constantConcurrentUsers(10).during(120.seconds)),
    systemBenchServiceScenario3.inject(constantConcurrentUsers(10).during(120.seconds)),
    systemBenchServiceScenario4.inject(constantConcurrentUsers(10).during(120.seconds)),
    systemBenchServiceScenario5.inject(constantConcurrentUsers(10).during(120.seconds))
  ) .protocols(jdbcConfig)
    .assertions(global.failedRequests.count.is(0))

  /*setUp(
    payServiceScenario.inject(
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

}
