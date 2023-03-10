// 群聊逻辑
$(document).ready(function () {
    //const，创建session
    const CREATE_SESSION = 220;

    var $friend_list = $(".friend-list")[0];

    $chatMsg = $(".chat__messages")[0];

    // 点击事件
    var menu = $("#menu")
    $(document).on('click', "#menu-icon", function(e) {
        openFAB(menu)
    })
    $(document).on('click', '#multichat', function(e) {
        enterMutiChat(e);
        closeFAB(menu)
        $('.tooltipped').tooltip('remove'); 
        showToast("已进入群聊会话，可以畅所欲言", 1800)    
    });

    // 进入群聊区域
    function enterMutiChat(e) {
        // 在好友区添加一个空白，然后点击
        var dom = chatterDom(
            "大家随便聊聊", 
            "img/mola.png", 
            false, 
            "", 
            "");
        dom.id = "mutichat-dom"
        
        $friend_list.append(dom)
        let $tempDom = $("#mutichat-dom");
        let photo = $(".contact__photo");
        
        // 头像过度动画
        photo.animate({opacity:0},200)
        setTimeout(function() {
            $(".contact__photo").css({opacity:1})
        },1000)
        $tempDom.click()
        $tempDom.remove()
        // 清空区域，初始化签名
        initMutiChat();
        // ui结束，切换会话
    }

    function initMutiChat() {
        //设置当前chatter为公共chatter
        var activeChatter = {
            createTime: 99999999,
            id: "temp-chatter",
            imgUrl: "img/mola.png",
            ip: "127.0.0.1",
            name: "大家随便聊聊",
            signature: "这里是mola的公共聊天室",
            status: 1,
            isTemp:true
        };
        setActiveChatter(activeChatter);
        // 设置签名
        var sign = cutStrByByte(activeChatter.signature, 28);
        $(".chat__status").text(sign);
        //获取session
        var socket = getSocket();
        var action = new Object();
        action.code = CREATE_SESSION;
        action.msg = "ok";
        // 发送公共chatter的固定sessionId
        action.data = "common-session";
        //向服务器发送数据
        socket.send(JSON.stringify(action));
    }

    /**
     * dom区域 内容 、是否是自己、chatter
     */
    commonMessageDom = function (message, isMain ,chatter) {
        // 时间dom
        let timeDoc = timeDom(message.createTime)
        if (timeDoc) {
            $chatMsg.append(timeDoc)
        }
        let content = message.content
        //拼装dom
        var mainDoc = document.createElement("div");
        $(mainDoc).addClass("chat__msgRow");

        var mainDocChild = document.createElement("div")
        var commonName = document.createElement("div")

        var imgDoc = document.createElement("img");

        if (isMain) {
            //头像img
            imgDoc.src = getChatterImage();
            $(imgDoc).addClass("contact__photo");
            $(imgDoc).css('float', 'right');
            $(imgDoc).css('display', 'inline');
            $(imgDoc).css('margin-right', '0rem');

            $(mainDocChild).css('margin-right', '0.5rem');
            $(mainDocChild).addClass("chat__message notMine");
    
        }
        else {
            commonName.innerText = chatter.name
            imgDoc.src = chatter.imgUrl;
            $(imgDoc).addClass("contact__photo");
            $(imgDoc).css('float', 'left');
            $(imgDoc).css('display', 'inline');
            $(imgDoc).css('margin-right', '0rem');

            $(mainDocChild).css('margin-left', '0.5rem');
            $(mainDocChild).addClass("chat__message mine");

            $(commonName).addClass("chat-common-left")
        }

        mainDoc.append(imgDoc);
        mainDocChild.innerHTML = twemoji.parse(content,{"folder":"svg","ext":".svg","base":"asset/","size":15});
        mainDoc.append(commonName);
        mainDoc.append(mainDocChild);

        return mainDoc;
        
    }

    commonFileDom = function(message, isUpload, isMain, uploadId, url, chatter) {
        // 时间dom
        let timeDoc = timeDom(message.createTime)
        if (timeDoc) {
            $chatMsg.append(timeDoc)
        }
        let filename = message.fileName
        if (url !== 'javascript:;'){
            url = getPrefix() + url
        }
        var mainDoc = document.createElement("div");
        $(mainDoc).addClass("chat__msgRow");

        var mainDocChild = document.createElement("div");

        var imgDoc = document.createElement("img");
        var commonName = document.createElement("div")
        if (isMain) {
            //头像img

            imgDoc.src = getChatterImage();
            $(imgDoc).addClass("contact__photo");
            $(imgDoc).css('float', 'right');
            $(imgDoc).css('display', 'inline');
            $(imgDoc).css('margin-right', '0rem');

            $(mainDocChild).css('margin-right', '0.5rem');
            $(mainDocChild).css('text-align', 'center');
            $(mainDocChild).addClass("chat__message notMine");
        } else {

            imgDoc.src = chatter.imgUrl;
            commonName.innerText = chatter.name
            $(imgDoc).addClass("contact__photo");
            $(imgDoc).css('float', 'left');
            $(imgDoc).css('display', 'inline');
            $(imgDoc).css('margin-right', '0rem');

            $(mainDocChild).css('margin-left', '0.5rem');
            $(mainDocChild).css('text-align', 'center');
            $(mainDocChild).addClass("chat__message mine");

            $(commonName).addClass("chat-common-left")
        }
        mainDoc.append(imgDoc)
        mainDoc.append(commonName)
        //添加取消图片
        var cancelImg = document.createElement("img");
        cancelImg.id = "cancel" + uploadId
        cancelImg.src = "img/close-circle.svg"
        $(cancelImg).css("width", "1.2rem")
        $(cancelImg).css("float", "right")
        //添加文件图片
        var imgLink = document.createElement("a");
        //imgLink.href = url;
        imgLink.target = "_blank";
        var fileImg = document.createElement("img");
        if (isImg(url)) {
            // 是图片
            fileImg.src = url
            // 同步图片到holder，显示
            $(fileImg).on('click', function() {
                syncToHolder(url)
            })
            $(fileImg).addClass("imgFile");
        } else {
            fileImg.src = "img/file.svg"
            $(fileImg).css("width", "6rem")
        }
        fileImg.id = "img" + uploadId
        imgLink.append(fileImg);
        if (isUpload && uploadId != "ready") {
            $(fileImg).css("margin-left", "1.2rem")
            mainDocChild.append(cancelImg)
        }
        mainDocChild.append(imgLink)

        //添加进度条
        if (isUpload) {
            var progress = document.createElement("div");
            $(progress).addClass("progress");
            progressData = document.createElement("div");
            progressData.id = uploadId;
            $(progressData).addClass("determinate");
            if (uploadId == "ready") {
                $(progressData).css("width", "100%")
            } else {
                $(progressData).css("width", "0%")
            }

            progress.append(progressData);
            mainDocChild.append(progress);
        }

        var fileSrc = document.createElement("a");
        $(fileSrc).css("display", "block");
        $(fileSrc).css("text-align", "center");
        fileSrc.innerText = filename;
        fileSrc.href = url;
        if (url !== 'javascript:;'){
            fileSrc.target = "_blank";
        }
        fileSrc.id = "src" + uploadId;
        mainDocChild.append(fileSrc);
        mainDoc.append(mainDocChild);
        return mainDoc;
    }
})