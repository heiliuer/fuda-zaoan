var user = require("./user")
/**
 * Created by heiliuer on 2016/12/11 0014.
 */
var baseUrl = "http://zao.fzu4.com/";

var request = require("request");
/*Cookies are disabled by default (else, they would be used in subsequent requests).
 To enable cookies, set jar to true (either in defaults or options).*/
request = request.defaults({jar: true})
// request.debug = true;

var USER_AGENT_WEIXIN = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1 wechatdevtools/0.7.0 MicroMessenger/6.3.9 Language/zh_CN webview/0";

var OPTIONS = {
    headers: {
        'User-Agent': USER_AGENT_WEIXIN,
        "Connection": "keep-alive"
    }
};

var extend = require("./extends");

function getOptions(options) {
    return extend(true, {}, OPTIONS, options);
}


function unUnicode(body) {
    return JSON.stringify(JSON.parse(body));
}
function sign(m_user) {
    console.log("########");
    console.log("账户" + user.UserID + "签到....\n");

    request(getOptions({
        method: "GET",
        url: baseUrl + "App/?UUID=7856c84d"//1.获取sessionId的cookies
    }), function (error, response, body) {
        request(getOptions({
            method: "POST",
            url: baseUrl + "Login/", //2.登录
            form: extend(true, {}, {Force: true}, m_user)
        }), function (error, response, body) {
            var jsonBody = JSON.parse(body);
            if (jsonBody.Success) {
                console.log("登录成功\n");
            } else {
                console.log("登录失败\n" + unUnicode(body) + "\n");
            }
            request(getOptions({
                method: "POST",
                url: baseUrl + "App/Account/User/Sign",//2.登录
                form: {
                    Second: "2.6",
                    Wrong: "1"
                }
            }), function (error, response, body) {
                try {
                    var jsonBody = JSON.parse(body);
                    if (jsonBody.Success) {
                        console.log("签到成功\n");
                        return;
                    }
                } catch (e) {
                }

                var failLog = "";

                // if (body.indexOf("Object moved") != -1) {
                //     failLog = "账户在别处已登录， ";
                // }

                try {
                    failLog += "签到失败，response body：\n" + unUnicode(body)
                } catch (e) {
                    var confirmerror = /confirmerror\('(.*)'\)/.exec("body");
                    if (confirmerror && confirmerror.length > 1) {
                        failLog += "签到失败，confirmerror：\n" + confirmerror[1];
                    } else {
                        var showerror = /show\('(.*)'\)/.exec("body");
                        if (showerror && showerror.length > 1) {
                            failLog += "签到结果未知，show：\n" + show[1];
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

            });
        });
    });
}

//开始签到
var user = require("./user")
sign(user);