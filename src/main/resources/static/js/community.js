/**
 *  评论问题
 */
function post() {
   var questionId = $("#question_id").val();
   var content = $("#comment_content").val();
   comment2Target(questionId,1,content);
}

/**
 *  评论 评论
 */
function comment(e) {
    var commentId = e.getAttribute("data");
    var content = $("#input-" + commentId).val();
    comment2Target(commentId,2,content);
}
/**
 *
 */
function comment2Target(targetId , type ,content){
    if (!content){
        alert("不能回复空内容");
        return;
    }
    $.ajax({
        type:"POST",
        url:"/comment",
        contentType:"application/json",
        data:JSON.stringify({
            "parentId": targetId,
            "content":content,
            "type" : type
        }),
        success:function(response){
            if(response.code == 200){
                window.location.reload();
            }else{
                if(response.code == 2003){
                    var isAccepted = confirm(response.message);
                    if(isAccepted){
                        window.open("https://github.com/login/oauth/authorize?client_id=00ede82d3f65724ec2ae&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                        window.localStorage.setItem("closeable", "true");
                    }
                }
            }
        },
        dataType: "json"
    });
}
/**
 *  打开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    if($("#comment-" + id).hasClass("in")){
        // 闭合
        $("#comment-" + id).removeClass("in");
        e.classList.remove("active");
    }else{
        // 展开
        var subCommentContainer = $("#comment-" + id);
        if(subCommentContainer.children().length != 1 ){

        }else{
            $.getJSON( "/comment/" + id, function(data) {
                $.each(data.data.reverse(), function(index, comment){
                    console.log(comment.content);
                    var comments = $("<div/>" , {
                        "class" : "col-lg-12 col-md-12 col-sm-12 col-xs-12 comment-list",
                    });
                    var mediaElement = $("<div />",{
                        "class" : "media",
                    });
                    var media_leftElement = $("<div />",{
                        "class" : "media-left"
                    });
                    var linkElement = $("<a />", {
                        "href" : "#"
                    });
                    var avatar = $("<img />", {
                        "class" : "media-object img-rounded",
                        "src" : comment.user.avatarUrl
                    });
                    var media_bodyElement = $("<div />",{
                        "class" : "media-body"
                    });
                    var nameElement = $("<h4 />", {
                        "class" : "media-heading",
                        "html": comment.user.name
                    });
                    var contentElement = $("<div/>" , {
                        "html": comment.content
                    });
                    var menu = $("<div />" , {
                        "class" : "menu"
                    });
                    var timeElement = $("<div />" , {
                        "class" : "pull-right",
                        "html" : moment(comment.gmtCreate).format("YYYY-MM-DD HH:mm")
                    });

                    linkElement.append(avatar);
                    media_leftElement.append(linkElement);
                    mediaElement.append(media_leftElement);

                    media_bodyElement.append(nameElement);
                    media_bodyElement.append(contentElement);
                    menu.append(timeElement);
                    media_bodyElement.append(menu);
                    mediaElement.append(media_bodyElement);

                    comments.append(mediaElement);

                    subCommentContainer.prepend(comments);
                });
            });
        }
        $("#comment-" + id).addClass("in");
        e.classList.add("active");
    }
}

/**
 *
 * @param e
 */
function selectTag(e) {
    var previous = $("#tag").val();
    var value = e.getAttribute("data-tag");
    if(previous.indexOf(value) == -1){
        if(previous){
            $("#tag").val(previous + ',' + value);
        }else{
            $("#tag").val(value);
        }
    }
}

/**
 *
 */
function showSelectTag() {
    $("#selectTag").show();
}