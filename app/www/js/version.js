var version = "1.3"
checkVersion = function (callback) {
    $.ajax({
        url: getPrefix() + "/chat/app/version",
        type: "get",
        dataType: "json",
        success: function(result) {
            if (version !== result.data) {
                // 提示下载新版本
                swal("new version!", "检测到新版本，是否下载？", "warning").then(value => {
                    if (value) {
                        window.location = "http://www.molapages.xyz/download/molachat.apk"
                    }
                    callback()
                })
            } else {
                callback()
            }
        },
        error: function(result) {
            callback()
        }
    });
}