document.addEventListener('DOMContentLoaded', function () {
});

var $form = $("#user-form");
var $userkey = $("#userkey");
var $userid = $("#userid");


function setStorage(data) {
    chrome.storage.sync.set({"data": data}, function () {
        console.log("save data:", data);
    });
}
function getStorage(callback) {
    chrome.storage.sync.get('data', function (data) {
        console.log("get data:", data.data);
        callback(data.data)
    });
}


getStorage(function (data) {
    $userkey.val(data.userkey)
    $userid.val(data.userid)
});

$form.on("submit", function () {
    setStorage({
        userkey: $userkey.val(),
        userid: $userid.val()
    })
    setTimeout(function () {
        chrome.runtime.reload();
    }, 200)
    return false;
})



