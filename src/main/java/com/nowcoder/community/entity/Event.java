package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {

    private String topic; // 评论 点赞 关注
    private int userId; // 进行评论 点赞 关注 的人 也就是当前的登录用户
    private int entityType; // 当前用户进行 评论 点赞 关注 的实体的类型
    private int entityId; // 当前用户进行 评论 点赞 关注 的实体的ID
    private int entityUserId; //toId message发到的对象
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    // 注意这里的set方法 都会将该实例对象进行返回 可以进行链式编程
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
