微服务介绍
http://wiki.inhuawei.com/pages/viewpage.action?pageId=284011104
http://wiki.inhuawei.com/pages/viewpage.action?pageId=124104800


微服务划分:
>https://www.cnblogs.com/shuyouliu/p/springcloudservice.html



构建高性能Web站点

大规模Web服务开发技术

淘宝技术这十年，完整最终确认版

大型网站技术架构 核心原理与案例分析

------------------------------------------------------------------------------------






JVM内存调优
JVMGC调优









道系统大概多久会因为Young GC的执行而卡顿多久


优化思路其实简单来说就是尽量让每次Young GC后的存活对象小于Survivor区域的50%，都留存在年轻代里。尽量别让对象进入老年 代。尽量减少Full GC的频率，避免频繁Full GC对JVM性能的影响

cms gc日志查看


parlgc日志查看

G1日志查看

fullgc停顿时间
younggc停顿时间




一般JAVA微服务CPU占用由两部分组成：
JVM底层执行调用（垃圾回收GC+编译器JIT）
业务逻辑执行调用



明显是GC存在问题，可以从以下方面进行优化：
降低GC频率
降低GC停顿时间
减少并行收集器线程数




优化内存目的
> 防止JVM发生OOM
> 防止GC频繁执行


优化GC目的
> 防止GC频繁执行占用CPU过高影响业务占用CPU资源
> 防止JVM停顿，影响请求接口响应时间


评估微服务内存容量、堆各区域比例
> 将临时对象留在年轻代



监控系统、报警系统



Arthas

GC日志自动分析

人工查看
gceasy(https://gceasy.io)，





监控系统： zibbix, 普罗米修斯Prometheus、Grafana








监控系统、预警系统

> jvm 内存、gc监控、报警
> 线上调优
现网考虑:考虑不影响线上请求执行



JVM优化就是优化GC, 降低Full GC频繁发生，尽量别让对象进入老年代
对象进入老年代场景：


FullGC合理频率: 几小时、几天发生一次


OOM发生前兆，FullGC频繁发生



ELK+sleuth+zipkin: 日志收集、性能监控、监控调用链、响应时间


Prometheus、Grafana、alertmanager、企业微信报警



JVM 堆内存、GC监控：
Prometheus、Grafana、Grafana-JVM(Micrometer)、alertmanager、企业微信报警




线程池监控：
Prometheus、Grafana、alertmanager、企业微信报警



服务组jvm监控预警工程化流程
------------------------------------------------
> 评估现网JVM内存
- 计算稳定状态下稳定长期存活对象大小
评估稳定长期存活对象内存 
gc日志、jmap,压测后停止压测查看多次Full GC后计算新生代大小、老年代大小、方法区大小




评估整个集群并发量
评估单实例承受并发量

评估单实例承受并发量下临时对象内存大小

永久区: 并发量下永久区2倍
新生代: 容纳指定并发量下接口响应时间
老年代: 




- 计算对象大小


- 垃圾算法选择


> 组内微服务jvm 内存阈值、gc频率监控、预警
- 原理：java 内存、gc接口


> 线上解决预警问题
- 现网考虑:考虑不影响线上请求执行 -- 摘掉一台分析、流量复制、重现


题外要点
-----------------
- 让对象进入老年代原则
- 复制算法耗时分析-扫描阶段、复制阶段







JVM配置模板

如果内存不大，比如4核8G的机器，可以用默认的Parallel垃圾收集器，如果对停顿时间有一定要求，Jdk 1.8版本可以使用ParNew+CMS 垃圾收集器组合，比如下面配置(参考JVM调优课程)：

‐Xms3072M ‐Xmx3072M ‐Xmn2048M ‐Xss1M ‐XX:PermSize=256M ‐XX:MaxPermSize=256M ‐XX:SurvivorRatio=8
-XX:MaxTenuringThreshold=5 ‐XX:PretenureSizeThreshold=1M ‐XX:+UseParNewGC ‐XX:+UseConcMarkSweepGC
‐XX:CMSInitiatingOccupancyFaction=92 ‐XX:+UseCMSCompactAtFullCollection ‐XX:CMSFullGCsBeforeCompaction=0



https://www.bilibili.com/video/BV1sC4y1873R?p=15
https://www.bilibili.com/video/BV11f4y177hG?from=search&seid=14931956178314838900&spm_id_from=333.337.0.0
https://vip.tulingxueyuan.cn/detail/v_6009258ee4b0d0af0d8bae32/3?from=p_6006cac4e4b00ff4ed156218&type=8&parent_pro_id=p_6006d828e4b0ab9a254a5a96
https://www.bilibili.com/video/BV1qo4y1o7Ep?p=12
https://www.bilibili.com/video/BV1nf4y1T7Md?p=9


内存分配与回收策略>特性>动态年龄晋升



百万任务级别线程池监控预警工程化流程
------------------------------------------------



> 组内微服务内百万任务量  线程池监控、预警
> 现网解决预警问题



单表亿级别数据量Sql优化工程化流程
-------------------------------------------------


> 监控慢Sql
> 调优慢sql










































