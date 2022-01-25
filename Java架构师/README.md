> 1、确认并发编程.xml 是否更改: Condition算法更改、日期13号 2、CycleBarrier实现与Reentrantlock 是否开放state状态 3、Rabbitmq 消息丢失场景、解决方案 4、Rabbitmq 重复消费场景



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





监控系统： zibbix, 普罗米修斯Prometheus








监控系统、预警系统

> jvm 内存、gc监控、报警
> 线上调优
现网考虑:考虑不影响线上请求执行



JVM优化就是优化GC, 降低Full GC频繁发生，尽量别让对象进入老年代
对象进入老年代场景：


FullGC合理频率: 几小时、几天发生一次


OOM发生前兆，FullGC频繁发生






确定jvm调优工程化流程
------------------------------------------------
> 如何评估线上JVM内存大小
> jvm 内存阈值、gc频率监控、报警
> 线上解决预警问题
现网考虑:考虑不影响线上请求执行
让对象进入老年代原则


https://www.cnblogs.com/ivictor/p/5849061.html


