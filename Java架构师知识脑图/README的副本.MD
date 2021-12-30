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

线程CPU飙升分析
线程死锁分析

> Jstack


内存溢出分析

> Jprofiler分析

JVM内存调优
JVMGC调优






工具使用






道系统大概多久会因为Young GC的执行而卡顿多久


优化思路其实简单来说就是尽量让每次Young GC后的存活对象小于Survivor区域的50%，都留存在年轻代里。尽量别让对象进入老年 代。尽量减少Full GC的频率，避免频繁Full GC对JVM性能的影响

cms gc日志查看


parlgc日志查看

G1日志查看

fullgc停顿时间
younggc停顿时间




