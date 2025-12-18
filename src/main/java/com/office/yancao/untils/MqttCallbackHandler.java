package com.office.yancao.untils;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

/**
 * MQTT 消息回调处理器
 * 用于处理接收到的消息和连接状态变化
 */
@Slf4j
@Component
public class MqttCallbackHandler implements MqttCallback {

    /**
     * 连接丢失时的回调
     */
    @Override
    public void connectionLost(Throwable cause) {
        log.error("MQTT连接丢失: {}", cause.getMessage());
        // 这里可以实现重连逻辑
    }

    /**
     * 收到消息时的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        log.info("收到MQTT消息 - 主题: {}, 内容: {}, QoS: {}", topic, payload, message.getQos());

        // 根据不同的主题处理不同的业务逻辑
        if (topic.startsWith("workshop/")) {
            // 处理车间相关消息
            handleWorkshopMessage(topic, payload);
        }

        // TODO: 添加更多的消息处理逻辑
    }

    /**
     * 消息传递完成时的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            log.debug("消息发布成功: {}", token.getMessageId());
        } catch (Exception e) {
            log.error("获取消息ID失败", e);
        }
    }

    /**
     * 处理车间相关消息
     */
    private void handleWorkshopMessage(String topic, String payload) {
        log.info("处理车间消息: {}", topic);
        // TODO: 实现具体的业务逻辑
        // 例如：更新数据库、触发其他业务操作等
    }
}