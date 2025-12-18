package com.office.yancao.service.admin;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * MQTT 消息服务
 * 用于发送各种类型的消息到EMQX
 */
@Slf4j
@Service
public class MqttService {

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 发送任务通知消息
     * @param workerId 员工ID
     * @param taskInfo 任务信息
     * @return 是否发送成功
     */
    public boolean sendTaskNotification(String workerId, Object taskInfo) {
        try {
            String topic = "workshop/" + workerId + "/task";
            String payload = objectMapper.writeValueAsString(taskInfo);

            return publishMessage(topic, payload, 1, true);
        } catch (Exception e) {
            log.error("发送任务通知失败", e);
            return false;
        }
    }

    /**
     * 发送质量警报消息
     * @param batchNo 批次号
     * @param alertInfo 警报信息
     * @return 是否发送成功
     */
    public boolean sendQualityAlert(String batchNo, Object alertInfo) {
        try {
            String topic = "workshop/quality/alert";
            String payload = objectMapper.writeValueAsString(alertInfo);

            // 质量警报需要确保送达，使用QoS=2
            return publishMessage(topic, payload, 2, true);
        } catch (Exception e) {
            log.error("发送质量警报失败", e);
            return false;
        }
    }

    /**
     * 发送班组广播消息
     * @param groupId 班组ID
     * @param message 消息内容
     * @return 是否发送成功
     */
    public boolean sendGroupBroadcast(String groupId, Object message) {
        try {
            String topic = "workshop/group/" + groupId + "/broadcast";
            String payload = objectMapper.writeValueAsString(message);

            return publishMessage(topic, payload, 1, false);
        } catch (Exception e) {
            log.error("发送班组广播失败", e);
            return false;
        }
    }

    /**
     * 发送紧急停机指令
     * @param machineId 设备ID
     * @param reason 停机原因
     * @return 是否发送成功
     */
    public boolean sendEmergencyStop(String machineId, String reason) {
        try {
            String topic = "workshop/machine/" + machineId + "/control";
            EmergencyStopCommand command = new EmergencyStopCommand("STOP", reason, System.currentTimeMillis());
            String payload = objectMapper.writeValueAsString(command);

            // 紧急指令需要最高优先级，保留消息
            return publishMessage(topic, payload, 2, true);
        } catch (Exception e) {
            log.error("发送紧急停机指令失败", e);
            return false;
        }
    }

    /**
     * 通用消息发布方法（支持任意对象）
     * @param topic 主题
     * @param message 消息对象（会被转为JSON）
     * @param qos 服务质量等级 (0,1,2)
     * @param retained 是否保留消息
     * @return 是否发布成功
     */
    public boolean publishMessage(String topic, Object message, int qos, boolean retained) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            return publishMessage(topic, payload, qos, retained);
        } catch (Exception e) {
            log.error("序列化消息失败 - 主题: {}", topic, e);
            return false;
        }
    }

    /**
     * 原始消息发布方法
     */
    public boolean publishMessage(String topic, String payload, int qos, boolean retained) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes(StandardCharsets.UTF_8));
            message.setQos(qos);
            message.setRetained(retained);

            mqttClient.publish(topic, message);
            log.info("MQTT消息发布成功 - 主题: {}, QoS: {}, 保留: {}", topic, qos, retained);
            return true;
        } catch (Exception e) {
            log.error("MQTT消息发布失败 - 主题: {}", topic, e);
            return false;
        }
    }

    /**
     * 紧急停机命令对象
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class EmergencyStopCommand {
        private String command;
        private String reason;
        private Long timestamp;
    }
}
