package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    // 某个实体的赞 用于统计某个post或者针对某个评论的点赞数量
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }


    // 某个用户的赞 用于记录一个用户收到的赞的数量
    // like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
}
