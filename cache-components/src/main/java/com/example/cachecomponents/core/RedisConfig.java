package com.example.cachecomponents.core;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Redis连接配置类
 *
 * <p>
 * 功能：
 * - 配置主从连接
 * - 配置连接池
 * - 配置序列化方式
 *
 * 架构支持：
 * - 主写从读
 * - Lettuce连接池
 */
@Configuration
public class RedisConfig {

    // ====================== 1. 配置Lettuce客户端资源（通用） ======================
    @Bean
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    // ====================== 2. 写连接工厂（主节点，无只读限制） ======================
    @Bean("redisWriteConnectionFactory")
    @Primary // 主连接工厂，作为默认
    public RedisConnectionFactory writeConnectionFactory() {
        // 1. 读取写库配置
        RedisStandaloneConfiguration writeConfig = new RedisStandaloneConfiguration();
        writeConfig.setHostName("192.168.1.100"); // 对应yml中的spring.redis.write.host
        writeConfig.setPort(6379); // 对应yml中的spring.redis.write.port
        writeConfig.setPassword("your-password"); // 有密码则配置
        writeConfig.setDatabase(0);

        // 2. 配置Lettuce客户端（无只读策略，允许写）
        LettuceClientConfiguration writeClientConfig = LettucePoolingClientConfiguration.builder()
                .clientResources(clientResources())
                .commandTimeout(Duration.ofSeconds(5)) // 命令超时
                .poolConfig(redisPoolConfig()) // 绑定连接池
                .build();

        // 3. 创建写连接工厂
        return new LettuceConnectionFactory(writeConfig, writeClientConfig);
    }

    // ====================== 3. 读连接工厂（从节点，配置只读策略） ======================
    @Bean("redisReadConnectionFactory")
    public RedisConnectionFactory readConnectionFactory() {
        // 1. 读取读库配置
        RedisStandaloneConfiguration readConfig = new RedisStandaloneConfiguration();
        readConfig.setHostName("192.168.1.101"); // 对应yml中的spring.redis.read.host
        readConfig.setPort(6379);
        readConfig.setPassword("your-password");
        readConfig.setDatabase(0);

        // 2. 配置Lettuce客户端（核心：指定只读策略）
        LettuceClientConfiguration readClientConfig = LettucePoolingClientConfiguration.builder()
                .clientResources(clientResources())
                .commandTimeout(Duration.ofSeconds(5))
                .poolConfig(redisPoolConfig())
                .readFrom(ReadFrom.REPLICA_PREFERRED) // ✅ 只读策略：优先从从节点读取
                .build();

        // 3. 创建读连接工厂
        return new LettuceConnectionFactory(readConfig, readClientConfig);
    }

    // ====================== 4. 连接池配置（通用） ======================
    @Bean
    @ConfigurationProperties("spring.redis.lettuce.pool") // 绑定yml中的连接池配置
    public GenericObjectPoolConfig<RedisConnection> redisPoolConfig() {
        return new GenericObjectPoolConfig<>();
    }

    // ====================== 5. 写专用RedisTemplate ======================
    @Bean("redisWriteTemplate")
    public RedisTemplate<String, Object> writeTemplate(
            @Qualifier("redisWriteConnectionFactory") RedisConnectionFactory factory) {
        return createRedisTemplate(factory);
    }

    // ====================== 6. 读专用RedisTemplate ======================
    /*@Bean("redisReadTemplate")
    public RedisTemplate<String, Object> readTemplate(
            @Qualifier("redisReadConnectionFactory") RedisConnectionFactory factory) {
        return createRedisTemplate(factory);
    }*/

    // ====================== 通用方法：创建RedisTemplate（避免重复代码） ======================
    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 配置序列化（避免乱码，必加）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // key和hashKey用字符串序列化
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        // value和hashValue用JSON序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        // 初始化模板（必须调用）
        template.afterPropertiesSet();
        return template;
    }
}