// document.addEventListener('DOMContentLoaded', function () {
//     var port = chrome.runtime.connect({name: "knockknock"});
//     port.postMessage({joke: "Knock knock"});
//     port.onMessage.addListener(function (msg) {
//         alert(msg);
//         if (msg.question == "Who's there?") {
//             port.postMessage({answer: "Madame"});
//         }
//         else if (msg.question == "Madame who?") {
//             port.postMessage({answer: "Madame... Bovary"});
//         }
//     });
// });

var $form = $("#user-form");
var $userkey = $("#userkey");
var $userid = $("#userid");
var $enable = $("#enable");


function setStorage(data) {
    chrome.storage.sync.set({"data": data}, function () {
        // console.log("save data:", data);
    });
}
function getStorage(callback) {
    chrome.storage.sync.get('data', function (data) {
        // console.log("get data:", data.data);
        callback(data.data)
    });
}




getStorage(function (data) {
    $userkey.val(data.userkey);
    $userid.val(data.userid);
    $enable.prop("checked", data.enable);
});



$form.on("submit", function () {
    setStorage({
        userkey: $userkey.val(),
        userid: $userid.val(),
        enable: $enable.prop("checked")
    });
    chrome.runtime.sendMessage({
        type: "refresh"
    });
    window.close();
    return false;
})



