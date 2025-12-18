package com.office.yancao.untils;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {

    // EMQX 服务器地址 - 开发环境使用 localhost
    private String brokerUrl = "tcp://localhost:1883";

    // 客户端ID - 建议使用唯一标识
    private String clientId = "workshop_server_" + System.currentTimeMillis();

    // 用户名和密码 (如果EMQX配置了认证)
    private String username;
    private String password;

    // 连接选项
    private int connectionTimeout = 30;
    private int keepAliveInterval = 60;
    private boolean cleanSession = true;
    private boolean automaticReconnect = true;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setUserName(username);
        options.setPassword(password != null ? password.toCharArray() : null);
        options.setConnectionTimeout(connectionTimeout);
        options.setKeepAliveInterval(keepAliveInterval);
        options.setCleanSession(cleanSession);
        options.setAutomaticReconnect(automaticReconnect);

        // 设置遗嘱消息（可选）- 当客户端异常断开时，服务器会发布此消息
        // options.setWill("workshop/server/status", "offline".getBytes(), 1, true);

        return options;
    }

    @Bean
    public MqttClient mqttClient(MqttConnectOptions options) throws MqttException {
        // 使用内存持久化，适合服务器端
        MemoryPersistence persistence = new MemoryPersistence();

        MqttClient client = new MqttClient(brokerUrl, clientId, persistence);

        // 设置回调处理器（可选）
        client.setCallback(new MqttCallbackHandler());

        // 连接到服务器
        try {
            client.connect(options);
            System.out.println("MQTT客户端连接成功: " + brokerUrl);

            // 订阅一些主题（如果需要接收消息）
            // client.subscribe("workshop/+/task/response", 1); // 订阅任务响应

        } catch (MqttException e) {
            System.err.println("MQTT连接失败: " + e.getMessage());
            throw e;
        }

        return client;
    }
}
