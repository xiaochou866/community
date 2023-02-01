package com.nowcoder.community.service;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    //private ElasticsearchTemplate elasticTemplate;
    private ElasticsearchRestTemplate elasticTemplate;

    @Autowired
    private RestHighLevelClient client; // 之前学习springCloud中使用java操作es的方法+

    public void saveDiscussPost(DiscussPost post) {
        discussRepository.save(post);
    }

    public void deleteDiscussPost(int id) {
        discussRepository.deleteById(id);
    }

    public Map<String, Object> searchDiscussPost(String keyword, int current, int limit) throws IOException {
        //1. 准备Request
        SearchRequest request = new SearchRequest("discusspost");
        //2. 准备DSL
        request.source()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .highlighter(new HighlightBuilder().field("title").field("content").preTags("<em>").postTags("</em>"))
                .sort("type", SortOrder.DESC)
                .sort("score", SortOrder.DESC)
                .sort("createTime", SortOrder.DESC)
                .from(current).size(limit);

        //3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //4.解析响应
        SearchHits searchHits = response.getHits();
        //4.1 获取总条数
        Map<String, Object> searchResult = new HashMap<>();

        TotalHits total = searchHits.getTotalHits();
        //System.out.println("共搜索到" + total + "条数据");
        searchResult.put("total", total.value);

        //4.2 文档数组
        SearchHit[] hits = searchHits.getHits();
        //4.3 遍历
        List<DiscussPost> discussPosts = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            DiscussPost discussPost = JSON.parseObject(json, DiscussPost.class);
            System.out.println(discussPost);

            // 获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            // 健壮性验证
            if (!(highlightFields == null || highlightFields.size() == 0)) {
                // 根据字段名获取高亮结果
                HighlightField highlightField = highlightFields.get("content");
                if (highlightField != null) {
                    // 获取高亮值
                    String content = highlightField.getFragments()[0].string();
                    // 覆盖非高亮结果
                    discussPost.setContent(content);
                }

                highlightField = highlightFields.get("title");
                if (highlightField != null) {
                    // 获取高亮值
                    String title = highlightField.getFragments()[0].string();
                    // 覆盖非高亮结果
                    discussPost.setTitle(title);
                }
            }

            //System.out.println(discussPost);
            discussPosts.add(discussPost);
        }

        //System.out.println(discussPosts.size());
        searchResult.put("data", discussPosts);
        return searchResult;
    }
}
