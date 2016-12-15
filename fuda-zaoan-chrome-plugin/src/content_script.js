chrome.runtime.onMessage.addListener(function (msg, sender, sendRequest) {
    switch (msg.type) {
        case 'refresh' :
            window.location.reload();
            break;
    }
});

// chrome.storage.sync.get('data', function (data) {
//     alert(JSON.stringify(data))
//     if (data.data /*&& data.data.enable*/) {
//         alert(1)
//         chrome.tabs.getSelected(null, function (tab) {
//             var tabId = tab.id;
//             var protocolVersion = '1.0';
//             chrome.debugger.attach({
//                 tabId: tabId
//             }, protocolVersion, function () {
//                 if (chrome.runtime.lastError) {
//                     console.log(chrome.runtime.lastError.message);
//                     return;
//                 }
//                 // 2. Debugger attached, now prepare for modifying the UA
//                 chrome.debugger.sendCommand({
//                     tabId: tabId
//                 }, "Network.enable", {}, function (response) {
//                     alert(2)
//                     // Possible response: response.id / response.error
//                     // 3. Change the User Agent string!
//                     chrome.debugger.sendCommand({
//                         tabId: tabId
//                     }, "Network.setUserAgentOverride", {
//                         userAgent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1 wechatdevtools/0.7.0 MicroMessenger/6.3.9 Language/zh_CN webview/0'
//                     }, function (response) {
//                         // Possible response: response.id / response.error
//                         // 4. Now detach the debugger (this restores the UA string).
//                         chrome.debugger.detach({tabId: tabId});
//                         alert(response.id + response.error)
//                     });
//                 });
//             });
//         })
//
//     }
// });

// chrome.runtime.onConnect.addListener(function(port) {
//     console.assert(port.name == "knockknock");
//     port.onMessage.addListener(function(msg) {
//         if (msg.joke == "Knock knock")
//             port.postMessage({question: "Who's there?"});
//         else if (msg.answer == "Madame")
//             port.postMessage({question: "Madame who?"});
//         else if (msg.answer == "Madame... Bovary")
//             port.postMessage({question: "I don't get it."});
//     });
// });




function getStorage(callback) {
    chrome.storage.sync.get('data', function (data) {
        console.log("get data:", data.data);
        callback(data.data)
    });
}

getStorage(function (data) {
    loadData(data);
});


chrome.extension.sendRequest({action: "change-user-agent"});


function loadData(DATA) {
    var autoLogin = function () {
        $("#userid").val(DATA.userid)
        $("#password").val(DATA.userkey)
        $("#login > div.wrapper > div > div > div.form-actions > button").click();

        //防止提示异地登录
        setTimeout(function () {
             $("#layui-layer2 > div.layui-layer-btn > a.layui-layer-btn0").click();
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


