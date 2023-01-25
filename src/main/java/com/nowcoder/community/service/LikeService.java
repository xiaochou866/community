package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param userId 点赞的人的用户id
     * @param entityType 是文章还是评论
     * @param entityId 文章或者评论的id
     * @param entityUserId 写这篇文章或者评论的用户的id 对应comment数据库中的userId字段
     */
    // 点赞
    public void like(int userId, int entityType, int entityId, int entityUserId){

        // String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId); //看看该userId是否已经点过赞了
        // if(isMember){ // 相当于 点击两次点赞 点赞取消
        //     redisTemplate.opsForSet().remove(entityLikeKey, userId);
        // }else{
        //     redisTemplate.opsForSet().add(entityLikeKey, userId);
        // }

        // Redis事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId); // 点赞是针对谁的?
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId); // 这个用户收到了多少点赞?

                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                operations.multi();
                if(isMember){
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    // 查询某实体点赞数量
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * @param userId 用户id 检查该用户是不是给该实体点过赞
     * @param entityType 该实体的类型 是文章的评论呢 还是评论的回复呢
     * @param entityId 该comment是针对于谁写的? 文章: 文章id 评论: 评论id
     * @return userId这个人给entity点过赞返回1 否则 返回0
     */
    // 查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId)?1:0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return count == null?0:count.intValue();
    }
}
