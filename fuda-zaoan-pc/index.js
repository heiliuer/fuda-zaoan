var user = require("./user")
/**
 * Created by heiliuer on 2016/12/11 0014.
 */
var baseUrl = "http://zao.fzu4.com/";

var requestNode = require("request");
/*Cookies are disabled by default (else, they would be used in subsequent requests).
 To enable cookies, set jar to true (either in defaults or options).*/
requestNode = requestNode.defaults({jar: true})
// request.debug = true;

var USER_AGENT_WEIXIN1 = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1 wechatdevtools/0.7.0 MicroMessenger/6.3.9 Language/zh_CN webview/0";
var USER_AGENT_WEIXIN2 = "Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; GT-S5660 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 MicroMessenger/4.5.255";
var USER_AGENT_WEIXIN = "MicroMessenger/6.3.9";


var mJar = requestNode.jar()

var cookie = requestNode.cookie('User=UUID=12196106344');

var OPTIONS = {
    headers: {
        'Upgrade-Insecure-Requests': 1,
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'User-Agent': USER_AGENT_WEIXIN,
        "Connection": "keep-alive",
        "Origin": "http://zao.fzu4.com",
        'Accept-Language': 'zh-CN,zh;q=0.8',
        'Cache-Control': 'max-age=0',
        'Referer': 'http://zao.fzu4.com/App/Account/User/Sign'
    },
    jar: mJar
};

var extend = require("./extends");

function getOptions(options) {
    return extend(true, {}, OPTIONS, options);
}


function unUnicode(body) {
    return JSON.stringify(JSON.parse(body));
}


var request = function (options) {
    return new Promise(function (resolve, reject) {
        try {
            requestNode(options, function (error, response, body) {
                var result = {
                    error: error,
                    response: response,
                    body: body
                }
                mJar.setCookie(cookie, '');
                resolve(result)
            })
        } catch (e) {
            reject("请求 " + options.url + " 异常：" + e.message);
        }
    })
}
var commonError = function (error) {
    console.error(error);
}

var cheerio = require("cheerio");


function getSignHidenParam() {
    var signRequest = request(getOptions({
        method: "GET",
        url: baseUrl + "App/Account/User/Sign",//2.getSignHidenParam
        form: {
            Second: "2.6",
            Wrong: "1"
        }
    }));

    signRequest.then(function (result) {
        var body = result.body;

        var $body = cheerio(body);

        var viewstate = $body.find('[name="__VIEWSTATE"]').val();

        if (!viewstate) {
            console.log("getSignHidenParam [viewstate] is empty");
        } else {
            setOnline(viewstate);
        }


    }, commonError);
}

function setOnline(viewstate) {
    var barrRequest = request(getOptions({
        method: "GET",
        url: baseUrl + "App/Account/User/SignPanel?BarrageIndex=" + new Date().getTime() + "&Height=627",//2.setOnline
    }));

    barrRequest.then(function (result) {
        signRequest();
    }, commonError);

    var signRequest = request(getOptions({
        method: "GET",
        url: baseUrl + "App/Default?GetIfOnline=true",//2.setOnline
    }));

    signRequest.then(function (result) {
        sign(viewstate);
    }, commonError);
}

function sign(viewstate) {
    var signRequest = request(getOptions({
        method: "POST",
        url: baseUrl + "App/Account/User/Sign",//2.签到
        form: {
            Second: "2.6",
            "__VIEWSTATE": viewstate,
            Wrong: "1"
        }
    }));

    signRequest.then(function (result) {
        var body = result.body;
        try {
            var jsonBody = JSON.parse(body);
            if (jsonBody.Success) {
                console.log("签到成功\n");
                return;
            }
        } catch (e) {
        }

        var $body = cheerio(body);

        var VIEWSTATE = $body.find('[name="__VIEWSTATE"]').val();

        var failLog = "";

        // if (body.indexOf("Object moved") != -1) {
        //     failLog = "账户在别处已登录， ";
        // }

        try {
            failLog += "签到失败，response body：\n" + unUnicode(body)
        } catch (e) {
            var confirmerror = /confirmerror\('(.*)'\)/.exec(body);
            if (confirmerror && confirmerror.length > 1) {
                failLog += "签到失败，confirmerror：\n" + confirmerror[1];
            } else {
                var showerror = /show\('(.*)'\)/.exec(body);
                if (showerror && showerror.length > 1) {
                    failLog += "签到结果未知，show：\n" + showerror[1];
                } else {
                    failLog += "签到失败，response body：\n" + body
                }
            }
        }

        console.log(failLog + "\n");

        request(getOptions({
            method: "GET",
            url: baseUrl + "App/Default?Logout=true", //3.登出
        }), function (error, response, body) {
            console.log("账户已退出");
            console.log("########");
        });

    }, commonError);
}


function login(m_user) {
    var loginRequest = request(getOptions({
        method: "POST",
        url: baseUrl + "Login/", //2.登录
        form: extend(true, {}, {Force: true}, m_user)
    }));

    loginRequest.then(function (result) {
        var body = result.body;
        var jsonBody = JSON.parse(body);
        if (jsonBody.Success) {
            console.log("登录成功\n");
            getSignHidenParam();
        } else {
            console.log("登录失败\n" + unUnicode(body) + "\n");
        }

    }, commonError)
}

function preLogin(m_user) {
    var signRequest = request(getOptions({
        method: "GET",
        url: baseUrl + "App/?UUID=12196106344"//1.获取sessionId的cookies
    }));

    signRequest.then(function () {
        login(m_user);
    }, commonError)
}

//开始签到
var user = require("./user")
console.log("########");
console.log("账户" + user.UserID + "签到....\n");
preLogin(user);