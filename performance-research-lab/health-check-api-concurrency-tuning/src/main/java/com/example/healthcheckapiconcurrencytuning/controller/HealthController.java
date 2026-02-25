package com.example.healthcheckapiconcurrencytuning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 *
 * gc日志查看需要参数设置,如下:
 *  -Xlog:gc*:file=gc.log:time,level,tags:filecount=10,filesize=100m
 *      -Xlog:gc*：开启所有 GC 相关日志（JDK 9+ 语法，JDK 8 使用 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log）。
 *      file=gc.log：将 GC 日志输出到当前目录的 gc.log 文件中。
 *      filecount=10,filesize=100m：日志轮转，防止单个文件过大。
 *
 *
 * 查看jvm参数信息
 * jps
 * 93355 Jps
 * 93341 HealthCheckApiConcurrencyTuningApplication
 * jinfo -flags 93341
 * VM Flags:
 * -XX:CICompilerCount=4 -XX:ConcGCThreads=2 -XX:+FlightRecorder -XX:G1ConcRefinementThreads=8 -XX:G1EagerReclaimRemSetThreshold=32 -XX:G1HeapRegionSize=4194304 -XX:G1RemSetArrayOfCardsEntries=32 -XX:G1RemSetHowlMaxNumBuckets=8 -XX:G1RemSetHowlNumBuckets=8 -XX:InitialHeapSize=402653184 -XX:+ManagementServer -XX:MarkStackSize=4194304 -XX:MaxHeapSize=6442450944 -XX:MaxNewSize=3862953984 -XX:MinHeapDeltaBytes=4194304 -XX:MinHeapSize=8388608 -XX:NonNMethodCodeHeapSize=16384 -XX:NonProfiledCodeHeapSize=0 -XX:-ProfileInterpreter -XX:ProfiledCodeHeapSize=0 -XX:SoftMaxHeapSize=6442450944 -XX:TieredStopAtLevel=1 -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseNUMA -XX:-UseNUMAInterleaving
 *
 *
 *
 * 程序启动vm参数配置
 *      -Xms6g -Xmx6g -XX:+UseG1GC -XX:G1HeapRegionSize=4m -XX:ConcGCThreads=4 -XX:G1ConcRefinementThreads=8 -XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=40 -XX:CICompilerCount=4 -XX:+ProfileInterpreter -XX:NonProfiledCodeHeapSize=256m -XX:ProfiledCodeHeapSize=256m -XX:TieredStopAtLevel=4 -XX:+UseCompressedOops -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:-UseNUMA -XX:-UseNUMAInterleaving -Xlog:gc*:file=gc.log:time,level,tags:filecount=10,filesize=10m
 */
@RestController
public class HealthController {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
