package com.nowcoder.community;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class ElasticSearchTests {
    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    //private ElasticsearchTemplate elasticTemplate;
    private ElasticsearchRestTemplate elasticTemplate;

    @Autowired
    private RestHighLevelClient client; // ????????????springCloud?????????java??????es?????????+

    @Test
    public void testInsert(){
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList(){
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101, 0, 100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100,0));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("????????????,????????????.");
        discussRepository.save(post);
    }

    @Test
    public void testDelete() {
         //discussRepository.deleteById(231);
        discussRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository() throws IOException {
         // ???????????????????????????????????????
         //NativeSearchQuery searchQuery= new NativeSearchQueryBuilder()
         //       .withQuery(QueryBuilders.multiMatchQuery("???????????????", "title", "content"))
         //       .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
         //       .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
         //       .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
         //       .withPageable(PageRequest.of(0, 10))
         //       .withHighlightFields(
         //               new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
         //               new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
         //       ).build();

        // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // ???????????????????????????????????????, ??????????????????.

        //Page<DiscussPost> page = discussRepository.search(searchQuery);
        //System.out.println(page.getTotalElements());
        //System.out.println(page.getTotalPages());
        //System.out.println(page.getNumber());
        //System.out.println(page.getSize());
        //for (DiscussPost post : page) {
        //    System.out.println(post);
        //}

        // ??????????????? ??????elasticSearch ??? rest??????
        //1. ??????Request
        SearchRequest request = new SearchRequest("discusspost");
        //2. ??????DSL
        request.source()
                .query(QueryBuilders.multiMatchQuery("???????????????", "title", "content"))
                .highlighter(new HighlightBuilder().field("content").field("title").preTags("<em>").postTags("</em>"))
                .sort("type", SortOrder.DESC)
                .sort("score", SortOrder.DESC)
                .sort("createTime", SortOrder.DESC)
                .from(0).size(11);

        //3. ????????????
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4.????????????
        SearchHits searchHits = response.getHits();
        //4.1 ???????????????
        TotalHits total = searchHits.getTotalHits();
        System.out.println("????????????" + total + "?????????");
        //4.2 ????????????
        SearchHit[] hits = searchHits.getHits();
        //4.3 ??????
        List<DiscussPost> discussPosts = new ArrayList<>();
        for (SearchHit hit : hits) {
            // ????????????source
            String json = hit.getSourceAsString();
            // ????????????
            DiscussPost discussPost = JSON.parseObject(json, DiscussPost.class);
            //System.out.println(discussPost);

            // ??????????????????
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            // ???????????????
            if (!(highlightFields == null || highlightFields.size() == 0)) {
                // ?????????????????????????????????
                HighlightField highlightField = highlightFields.get("content");
                if (highlightField != null) {
                    // ???????????????
                    String content = highlightField.getFragments()[0].string();
                    System.out.println(content);
                    // ?????????????????????
                    discussPost.setContent(content);
                }

                highlightField = highlightFields.get("title");
                if (highlightField != null) {
                    // ???????????????
                    String title = highlightField.getFragments()[0].string();
                    System.out.println(title);
                    // ?????????????????????
                    discussPost.setTitle(title);
                }
            }

            //System.out.println(discussPost);
            discussPosts.add(discussPost);
        }
    }

    //@Test
    //public void testSearchByTemplate() {
    //    SearchQuery searchQuery = new NativeSearchQueryBuilder()
    //            .withQuery(QueryBuilders.multiMatchQuery("???????????????", "title", "content"))
    //            .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
    //            .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
    //            .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
    //            .withPageable(PageRequest.of(0, 10))
    //            .withHighlightFields(
    //                    new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
    //                    new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
    //            ).build();
    //
    //    Page<DiscussPost> page = elasticTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
    //        @Override
    //        public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
    //            SearchHits hits = response.getHits();
    //            if (hits.getTotalHits() <= 0) {
    //                return null;
    //            }
    //
    //            List<DiscussPost> list = new ArrayList<>();
    //            for (SearchHit hit : hits) {
    //                DiscussPost post = new DiscussPost();
    //
    //                String id = hit.getSourceAsMap().get("id").toString();
    //                post.setId(Integer.valueOf(id));
    //
    //                String userId = hit.getSourceAsMap().get("userId").toString();
    //                post.setUserId(Integer.valueOf(userId));
    //
    //                String title = hit.getSourceAsMap().get("title").toString();
    //                post.setTitle(title);
    //
    //                String content = hit.getSourceAsMap().get("content").toString();
    //                post.setContent(content);
    //
    //                String status = hit.getSourceAsMap().get("status").toString();
    //                post.setStatus(Integer.valueOf(status));
    //
    //                String createTime = hit.getSourceAsMap().get("createTime").toString();
    //                post.setCreateTime(new Date(Long.valueOf(createTime)));
    //
    //                String commentCount = hit.getSourceAsMap().get("commentCount").toString();
    //                post.setCommentCount(Integer.valueOf(commentCount));
    //
    //                // ???????????????????????????
    //                HighlightField titleField = hit.getHighlightFields().get("title");
    //                if (titleField != null) {
    //                    post.setTitle(titleField.getFragments()[0].toString());
    //                }
    //
    //                HighlightField contentField = hit.getHighlightFields().get("content");
    //                if (contentField != null) {
    //                    post.setContent(contentField.getFragments()[0].toString());
    //                }
    //
    //                list.add(post);
    //            }
    //
    //            return new AggregatedPageImpl(list, pageable,
    //                    hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
    //        }
    //    });
    //
    //    System.out.println(page.getTotalElements());
    //    System.out.println(page.getTotalPages());
    //    System.out.println(page.getNumber());
    //    System.out.println(page.getSize());
    //    for (DiscussPost post : page) {
    //        System.out.println(post);
    //    }
    //}

}
