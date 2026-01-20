package com.example;

/**
 * HelloWorld
 * System.out.println 注意事项:
 *      同步 IO 阻塞线程，降低并发能力
 *          System.out 对应的 PrintStream 是 “同步流”，每次调用 println() 都会获取一个内部锁（synchronized），多个线程同时调用时会排队等待锁，导致线程阻塞
 *      IO 效率极低，占用 CPU / 内存资源
 *          为什么终端 IO 速度远慢于内存 / 磁盘？
 *              简单说：内存 / 磁盘是 “给程序用的存储设备”，设计目标是「快」；终端是 “给人看的交互设备”，设计目标是「易读」，效率天然不在一个层级。
 *              IO 速度的本质是「数据传输介质的读写效率」，不同介质的效率差距是数量级的：
 *                  存储/传输介质       读写速度（参考）       核心原因
 *                  ------------------------------------------------------------------------------------------
 *                  内存（RAM）         几十GB/s              电子信号直接读写，无机械运动，数据传输无需硬件机械操作
 *                  磁盘（SSD）         几千MB/s              电子读写，无机械延迟（HDD硬盘仅几百MB/s，存在磁头旋转/寻道延迟）
 *                  终端（控制台）       几十KB~几MB/s         终端是「人机交互设备」，每输出1个字符需经历以下耗时步骤：
 *                                                           1. 操作系统将数据传递给终端程序（如cmd、终端模拟器）；
 *                                                           2. 终端程序解析字符编码、渲染文字（CPU计算消耗）；
 *                                                           3. 刷新屏幕显示（硬件刷新延迟，需同步视觉输出）；
 *                                                           该过程是「软件+硬件」双重耗时，效率远低于纯存储介质
 *
 *      无日志分级 / 过滤，浪费资源且难以维护
 *          生产环境中，调试级的日志（如System.out打印的临时信息）和核心业务日志会混在一起，导致无效日志过多—— 这些冗余日志不仅占用磁盘空间，还会增加日志传输、存储、分析的成本；
 *          无法通过配置动态关闭调试日志（必须修改代码重新部署），即使是不需要的日志也会持续输出，浪费 IO 资源
 *      无法异步 / 批量处理，放大 IO 开销
 *          System.out.println 是同步、单次 IO，每调用一次就触发一次 IO，频繁调用会把 “小 IO” 放大成 “性能瓶颈”
 *      无缓冲 / 优化，终端渲染消耗额外资源
 *          System.out 的缓冲策略（行缓冲）远不如日志框架的 “块缓冲” 高效；
 *          若终端处于 “实时显示” 状态（比如有人在服务器上打开了程序的控制台），终端渲染大量日志内容会消耗服务器的 CPU 资源（终端渲染是计算密集型操作）
 */
public class HelloWorld {
    public static void main(String[] args) {
        printlnHelloWorld();
        printlnCommonDataTypes();
        printlnTheSplicedContent();
        printlnWithoutNewLine();
    }

    /**
     * Prints "Hello World!" to the console.
     * System：Java 内置的系统类（代表当前程序运行的系统环境）
     * out：System类的静态成员变量（类型是PrintStream，代表 “标准输出流”，默认指向控制台）
     * println()：PrintStream类的方法（全称 “print line”，意为 “打印内容并换行”）
     */
    public static void printlnHelloWorld() {
        System.out.println("Hello World!");
    }

    /**
     * 打印一些常用数据类型到控制台
     */
    public static void printlnCommonDataTypes() {
        // 打印整数
        System.out.println(123);
        // 打印浮点数
        System.out.println(3.14);
        // 打印布尔值
        System.out.println(true);
        // 打印字符
        System.out.println('A');
    }

    /**
     * 打印拼接内容到控制台
     */
    public static void printlnTheSplicedContent() {
        String name = "Java";
        int version = 17;
        // 拼接字符串、变量、数字
        System.out.println("我在学习" + name + "版本" + version);
        // 错误示例：先算10+20=30，再拼接字符串
        System.out.println("结果是：" + 10 + 20); // 输出：结果是：1020
        // 正确示例：用括号让10+20先运算
        System.out.println("结果是：" + (10 + 20)); // 输出：结果是：30
    }

    /**
     * 不换行打印
     */
    public static void printlnWithoutNewLine() {
        System.out.print("Hello");
        System.out.print("World");
    }
}
