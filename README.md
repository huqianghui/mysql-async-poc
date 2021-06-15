# MySQL Replication Latency Monitor

## MySQL slave status

show slave status\G的输出结果，需要监控下面三个参数： 1）Slave_IO_Running：该参数可作为io_thread的监控项，Yes表示io_thread的和主库连接正常并能实施复制工作，No则说明与主库通讯异常，多数情况是由主从间网络引起的问题； 2）Slave_SQL_Running：该参数代表sql_thread是否正常，YES表示正常，NO表示执行失败，具体就是语句是否执行通过，常会遇到主键重复或是某个表不存在。  3）Seconds_Behind_Master：是通过比较sql_thread执行的event的timestamp和io_thread复制好的event的timestamp(简写为ts)进行比较，而得到的这么一个差值； NULL—表示io_thread或是sql_thread有任何一个发生故障，也就是该线程的Running状态是No，而非Yes。 0 — 该值为零，是我们极为渴望看到的情况，表示主从复制良好，可以认为lag不存在。 正值 — 表示主从已经出现延时，数字越大表示从库落后主库越多。 负值 — 几乎很少见，我只是听一些资深的DBA说见过，其实，这是一个BUG值，该参数是不支持负值的，也就是不应该出现。 ----------------------------------------------------------------------------------------------------------------------------- Seconds_Behind_Master的计算方式可能带来的问题： relay-log和主库的bin-log里面的内容完全一样，在记录sql语句的同时会被记录上当时的ts，所以比较参考的值来自于binlog，其实主从没有必要与NTP进行同步，也就是说无需保证主从时钟的一致。其实比较动作真正是发生在io_thread与sql_thread之间，而io_thread才真正与主库有关联，于是，问题就出来了，当主库I/O负载很大或是网络阻塞，io_thread不能及时复制binlog（没有中断，也在复制），而sql_thread一直都能跟上io_thread的脚本，这时Seconds_Behind_Master的值是0，也就是我们认为的无延时，但是，实际上不是，你懂得。这也就是为什么大家要批判用这个参数来监控数据库是否发生延时不准的原因，但是这个值并不是总是不准，如果当io_thread与master网络很好的情况下，那么该值也是很有价值的。之前，提到Seconds_Behind_Master这个参数会有负值出现，我们已经知道该值是io_thread的最近跟新的ts与sql_thread执行到的ts差值，前者始终是大于后者的，唯一的肯能就是某个event的ts发生了错误，比之前的小了，那么当这种情况发生时，负值出现就成为可能。

## pt-heartbeat

1.在test数据库中定义一张表heartbeat，只含有一列，字段类型为时间戳

2.主库中通过event每隔一秒，更新表heartbeat中的时间戳字段

3.更新操作被放入binlog中

4.备库拉取主库binlog，备库重放binlog，备库中表heartbeat的时间戳字段也会随之更新

5.我们再获取备库当前时间，将当前时间与表T中的时间戳字段相减，得出时间差。

那么binlog中就是这样的，每隔一秒左右(并不百分百准确)就穿插着对表T的更新。
这样得出的时间差就是备库当前重放的日志在主库执行时间相对于当前时间的差值，也就是主备延时的差值，比如主库在10:00:01对某表PPP执行了更新操作，备库却在11:00:01才重放该条binlog event，那么主备数据延迟就是一个小时。

## pt-heartbeat与proxySQL整合

由于在paas平台中，mysql的用户名是user@host的形式，如果要实现failover的话，host信息要屏蔽掉，这样就需要proxySQL来做一层代理。
proxySQL本身功能很多也比较成熟，同时也内置的形式支持pt-heartbeat，这样延迟数据就可以在统计表中能查询到，而不是一个标准输出流

oxySQL1.4.4之后，对pt-heartbeat内嵌式支持。
1.	ProxySQL since version 1.4.4 has built-in support to use pt-heartbeat. We only need to specify the heartbeat table as follows:

SET mysql-monitor_replication_lag_use_percona_heartbeat = 'percona.heartbeat';
LOAD MYSQL VARIABLES TO RUNTIME;
SAVE MYSQL VARIABLES TO DISK;

2.	接着就是解决pt-heartbeat怎么写进在master mysql中写heartbeat表，这个很简单就是配置pt-heartbeat文件，通过daemon进程来实现：
We need to create a file to store pt-heartbeat configuration, e.g /etc/percona-toolkit/pt-heartbeat-prod.conf:
utc
replace
daemonize
pid=/var/run/pt-heartbeat-prod.pid
database=percona
table=heartbeat
interval=0.01
port=3306
user=monitor
password=******
host=127.0.0.1

We point pt-heartbeat to go through ProxySQL and route its traffic to the writer hostgroup. In order to do this, we need a query rule:

3.	这里是通过proxysql的方式来实现数据写入的，而不是直接写mysql，
同时为了查询

INSERT INTO mysql_query_rules (rule_id, active, username, destination_hostgroup, apply) VALUES (1, 1, "monitor", 10, 1)
LOAD MYSQL QUERY RULES TO RUNTIME;
SAVE MYSQL QUERY RULES TO DISK;

4.	创建相应的database：percona和table：heartbeat

5.	如果有权限问题，可以关闭selinux
然后连接proxysql的用户端口，就可以查看heartbeat的数据变化

6.	然后配置让proxySQL来去读延迟时间
select @@mysql-monitor_replication_lag_use_percona_heartbeat;
set mysql-monitor_replication_lag_use_percona_heartbeat = 'percona.heartbeat';
save mysql variables to disk; load mysql variables to runtime;

这里测试使用了sysbench来测试，同时也通过代码方式，显示用户sql的方式测试，使用gatling。
详细参加示例代码。
