package com.microsoft.benchmark.mysql


import de.codecentric.gatling.jdbc.Predef._
import de.codecentric.gatling.jdbc.check.JdbcSimpleCheck
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

import java.text.SimpleDateFormat
import java.util.Date
import scala.concurrent.duration.DurationInt

class PayServicePerformanceTest extends Simulation{

  val jdbcConfig = jdbc.url("jdbc:mysql://aa-master-e2-32c-4t.mysql.database.chinacloudapi.cn:3306/mall_spdb?useSSL=true&requireSSL=false").username("huqianghui@aa-master-e2-32c-4t").password("Andy@163.com").driver("com.mysql.cj.jdbc.Driver")

 // val jdbcConfig = jdbc.url("jdbc:mysql://vm-mysqlserver-32c-e2.chinaeast2.cloudapp.chinacloudapi.cn:3306/mall_spdb?useSSL=true&requireSSL=false").username("myadmin").password("password@123ABC").driver("com.mysql.cj.jdbc.Driver")

  val feeder = csv("PaySearchFeeder.csv").random


  val payServiceScenario = scenario("pay-case")
    .feed(feeder)
    .exec(jdbc("预支付sql")
    .insert()
    .into("phdj_mall_order_detail" +
      "(order_id ," +
      "mall_order_no ," +
      "shop_name ," +
      "kper_shop_merc_no ," +
      "wszf_shop_merc_no ," +
      "xiaopu_shop_merc_no ," +
      "wxpay_shop_merc_no ," +
      "order_pay_amount ," +
      "goods_info)")
    .values("37638," +
      "'2105190000873622'," +
      "'小浦杂货店'," +
      "0," +
      "0," +
      "'12134534543249'," +
      "'310310058120012'," +
      "0.02," +
      "'[{\"goodsTotalPrice\":\"0.02\",\"goodsName\":\"海狮葵花籽油5L\",\"goodsNum\":\"1\"},{\"goodsTotalPrice\":\"0.00\",\"goodsName\":\"freight\",\"goodsNum\":\"1\"}]' "))
    //.pause(1.seconds)
    .exec(jdbc("支付sql")
    .insert()
    .into("phdj_mall_pay" +
      "(trans_no," +
      "trans_abbr," +
      "acq_ssn," +
      "sett_date," +
      "trans_amount," +
      "resp_code," +
      "account," +
      "card_type," +
      "pay_datetime," +
      "channel_no)")
      .values("'2009230001000000000000002003'," +
        "'KPER'," +
        "'000007657045'," +
        "'20340220'," +
        "2.22," +
        "'00'," +
        "'6217920867575736'," +
        "'1'," +
        "'" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date) + "'," +
        "'00'"))
    //.pause(1.seconds)
    .repeat(3,"searchCount"){
      exec(jdbc("查证sql")
        .select("    id," +
          "trans_no," +
          "trans_abbr," +
          "acq_ssn," +
          "sett_date," +
          "trans_amount," +
          "resp_code," +
          "account," +
          "card_type," +
          "pay_datetime," +
          "channel_no," +
          "is_valid")
        .from("phdj_mall_pay")
        .where("trans_no = '" +
          "${trans_no}" +
          "' and  is_valid = 1")
        .check(JdbcSimpleCheck(result => result.toList.length == 1)))
    }
    .repeat(3,"refundCount"){
    exec(jdbc("退款sql")
      .select("id ," +
        "trans_no ," +
        "detail_id ," +
        "refund_trans_no ," +
        "merc_refund_no ," +
        "merc_trans_seq_no ," +
        "merc_refund_date ," +
        "refund_trans_date ," +
        "trans_abbr ," +
        "trans_type ," +
        "acq_ssn ," +
        "trans_amount ," +
        "resp_code ," +
        "channel_no ," +
        "sett_date ," +
        "o_stt_date ," +
        "o_acq_ssn ," +
        "remark1 ," +
        "remark2 ," +
        "insert_time ," +
        "change_time")
      .from("phdj_mall_refund")
      .where("refund_trans_no = '" +
        "${refund_trans_no}" +
        "'")
      .check(JdbcSimpleCheck(result => result.toList.length == 1)))
  }.repeat(4,"closeCount"){
    exec(jdbc("关单sql")
      .select("id ," +
        "trans_no ," +
        "trans_abbr ," +
        "acq_ssn ," +
        "sett_date ," +
        "trans_amount ," +
        "resp_code ," +
        "account ," +
        "card_type ," +
        "pay_datetime ," +
        "channel_no ," +
        "is_valid")
      .from("phdj_mall_pay")
      .where("trans_no = '" +
        "${trans_no}" +
        "' and is_valid = 1")
      .check(JdbcSimpleCheck(result => result.toList.length >= 1)))
  }



  setUp(payServiceScenario.inject(
    constantConcurrentUsers(50).during(60.seconds)))
    .protocols(jdbcConfig)
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
