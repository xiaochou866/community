function like(btn, entityType, entityId, entityUserId, postId){
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType, "entityId":entityId, "entityUserId":entityUserId,"postId":postId},
        function (data){
            data = $.parseJSON(data); // 将Json串转化为javascript对象
            if (data.code == 0){ // 发出请求处理之后返回响应数据没有问题
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
            }else{
                alert(data.msg)
            }
        }
    )

}