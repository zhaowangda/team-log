<img src="<<=AVATAR>>" alt="" class="large-avatar">
<div class="log-main">
    <div><a href="#" class="user-link" id="u<<=USERID>>"><strong><<=USERNAME>></strong></a><span class="pretty-time" value="<<=CREATETIME>>" title="<<=CommonUtils.formatUtcDate(CREATETIME)>>" style="padding-left:20px;"><<=CommonUtils.formatUtcDate(CREATETIME)>></span></div>
    <div class="log-main-indicator">
        <div class="log-main-indicator-back"><div class="log-main-indicator-front"></div></div>
    </div>
    <div class="log-main-content"><<=DESCRIPTION>></div>
    <div>
        <<_.each(TAGS.split(','),function(tag){>>
        <span class="badge"><i class="icon-tag icon-white"></i><<=tag>></span>
        <<});>>
    </div>
    <div class="log-main-icons"><span id="comments-<<=ID>>"><<=COMMENTS>> <i class="icon-comment"></i></span><span style="margin-left: 20px;" id="nice-<<=ID>>"><<=NICE>> <i class="icon-heart"></i></span></div>
</div>
<div class="log-main-comments hide"></div>
