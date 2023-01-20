package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHoler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHoler hostHoler;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;


    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHoler.getUser();
        if(user == null){
            // 如果用户没有处于登录状态
            return CommunityUtil.getJSONString(403, "你还没有登录哦！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况 后面进行统一处理
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId); // 根据传入的DiscussPost的id字段去数据库中查询DiscussPost实例
        model.addAttribute("post", post);

        // 这里需要根据帖子中的userId去查询用户信息并展示用户的头像等信息
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // 评论的分页信息
        page.setLimit(5); // 设置每一页显示的评论的个数
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount()); // 获取该帖子的评论的个数

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        // ENTITY_TYPE_POST 这个常量值为1 表示是对于帖子而言的评论
        // post.getId() 对应的是数据库中 entity_id字段 评论是针对帖子而言的其entity_id字段 就是该评论所属于的post的id字段
        // page.offSet() 是根据传入的page的 current 和 limit 进行计算的
        // 这里的分页 服用了 index.html中的分页功能
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffSet(), page.getLimit());

        // 评论VO列表 可见对象
        List<Map<String, Object>> commentVoList = new ArrayList<>(); // 最终要拿到thymeleaf html中进行渲染的对象
        if(commentList!=null){
            // 主体逻辑 就是遍历帖子对应的所有评论 然后在遍历评论下的所有回复
            for(Comment comment: commentList){
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 恢复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply: replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0?null:userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList); //将一个评论的回复全部加到commentVo中
                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId()); // 对每个帖子的评论数量进行了记录
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }


}
