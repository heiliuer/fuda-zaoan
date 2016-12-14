chrome.runtime.sendMessage({
    type: "select",
    data: "Hello"
});
// alert("send msg");


function getStorage(callback) {
    chrome.storage.sync.get('data', function (data) {
        console.log("get data:", data.data);
        callback(data.data)
    });
}

getStorage(function (data) {
    loadData(data);
})


function loadData(DATA) {
    var autoLogin = function () {
        $("#userid").val(DATA.userid)
        $("#password").val(DATA.userkey)
        $("#login > div.wrapper > div > div > div.form-actions > button").click();

        //防止提示异地登录
        setTimeout(function () {
            $("#layui-layer1 > div.layui-layer-btn > a.layui-layer-btn0.ui-link").click();
        }, 200);
    }

    function autoEnterSignPage() {
        window.location.href = "/App/Account/User/SignPanel";
    }


    function autoEnterSignFrame() {
        $("#ToSign > a > div > span")[0].click();


        var action = function () {
            var iframeWindow = getIframeWindow('#iframe[src="Sign"]');

            $(iframeWindow.document).ready(function () {
                iframeWindow.document.querySelector("#dian").click();
                sign();
            })

            //onload 方法无法监听
            iframeWindow.onload = function () {
            }
        }
        setTimeout(action, 500);

        // var inAtTimeFindIframe = inAtTime(function () {
        //     if (getIframDoc('#iframe[src="Sign"]')) {
        //         inAtTimeFindIframe.clear();
        //     }
        // }, 10, 1000 * 10, action)

    };

    if (DATA.enable && window.location.host == "zao.fzu4.com") {
        window.onload = function () {
            var pathname = location.pathname;
            if (pathname == "/Login/") {
                autoLogin();
            } else if (pathname == "/App/") {
                autoEnterSignPage()
            } else if (pathname == "/App/Account/User/SignPanel") {
                autoEnterSignFrame()
            }
        }
    }

    function getIframDoc(selector) {
        var docs = $(selector).contents();
        if (docs.length > 0) {
            return docs[0];
        }
        return null;
    }

    function getIframeWindow(selector) {
        var iframe = $(selector)[0];
        return iframe ? iframe.contentWindow : null;
    }


    function sign() {
        var iframe = getIframDoc('#iframe');
        console.log("iframe doc:", iframe);
        if (iframe) {
            var form = iframe.querySelector("#ctl01");
            var Second = iframe.querySelector("#Second");
            var Wrong = iframe.querySelector("#Wrong");
            Second.value = "16";
            Wrong.value = "20";
            form.submit();
            console.log("submit")
        }
    }
}


