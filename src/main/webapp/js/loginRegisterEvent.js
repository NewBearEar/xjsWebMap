

function check_login()
{
    var usernameValue=$("#user_name").val();
    var passwordValue=$("#password").val();
    var username = $("#user_name").attr("name").split('_').slice(-1);
    var password = $("#password").attr("name").split('_').slice(-1);
    var isLoginSuccess = false;
    var loginParamObj = {
        username:usernameValue,
        password:passwordValue
    };

    if(usernameValue=="" || passwordValue=="")
    {
        $("#login_form,.tenant-model-header").removeClass('shake_effect');
        setTimeout(function()
        {
            $("#login_form,.tenant-model-header").addClass('shake_effect');
            //alert("用户名或密码不能为空");
        },1);

    }
    else
    {
        $("#login_form,.tenant-model-header").removeClass('shake_effect');  //消除抖动class
        loginAjax(loginParamObj);

    }



}

function  check_logout(){
    if(isLogined){
        logoutAjax();  //简单的登出
    }
}

function check_register(){
    var name = $("#r_user_name").val();
    var pass = $("#r_password").val();
    var email = $("r_email").val();
    if(name!="" && pass=="" && email != "")
    {
        alert("注册成功！");
        $("#user_name").val("");
        $("#password").val("");
    }
    else
    {
        $("#login_form,.tenant-model-header").removeClass('shake_effect');
        setTimeout(function()
        {
            $("#login_form,.tenant-model-header").addClass('shake_effect')
        },1);
    }
}
function closeModalBox() {
    $(".text-input").val("");
    $(".tenant-model-box").hide();
}

function loginRegisterAddEvents() {
    $("#create").click(function(){
        check_register();
        return false;
    })
    $("#login").click(function(){
        check_login();  //登录事件
        return false;
    })
    $("#logout").click(function () {
        check_logout(); //登出事件
        return false;
    })
    $('.message a').click(function () {
        $('.login-form,.register-form').animate({  //精准到类，防止误伤搜索框
            height: 'toggle',
            opacity: 'toggle'
        }, 'slow');
    });
    $(".closeModel").click(function(){
        closeModalBox();
    });
    $(".avatar-abstract").click(function () {
        $(".htmleaf-container,.tenant-model-box").show();  //显示登录框
    })
}

function loginAjax(paramObj) {
    $("#loadgif").show();
    $.ajax({  //AJAX向Servlet发送get请求，请求后台数据库数据
        url: "login", // url不带/就是当前路径（因为我这个html放在webapp目录下），而且在页面上访问时已经包含application context，相当于/xjs/getGeoJson
        dataType: "json",   //传输json
        data:  paramObj,   //$('#form2').serialize(),  //按键值对形式
        type: "post",
        success: function (userProfile) {  //回调函数，更新map
            $("#loadgif").hide();
            if($.isEmptyObject(userProfile) || userProfile.statusCode == 0){   //判断返回对象是否为空对象
                $("#login_form,.tenant-model-header").removeClass('shake_effect');
                setTimeout(function()
                {
                    $("#login_form,.tenant-model-header").addClass('shake_effect');
                },1);
            }else if(userProfile.statusCode == 1) {
                //alert("登录成功！");
                //window.sessionStorage.setItem('username',paramObj.username);
                //window.sessionStorage.setItem('password',paramObj.password);
                //console.log(userProfile);
                $(".tenant-model-header").removeClass('shake_effect');
                isLogined = true;
                switchLoginoutWrapper(isLogined);
                showLoginedInfo(userProfile);
                closeModalBox();  //关闭登录框
            }else {
                $("#login_form,.tenant-model-header").removeClass('shake_effect');
                setTimeout(function()
                {
                    $("#login_form,.tenant-model-header").addClass('shake_effect')
                },1);
            }
        },
        error: function () {  //请求失败的回调方法
            $("#loadgif").hide();
            alert("请求失败，请重试");
        }
    });
}

function logoutAjax() { //
    $("#loadgif").show();
    $.ajax({  //AJAX向Servlet发送get请求，请求后台数据库数据
        url: "logout", // url不带/就是当前路径（因为我这个html放在webapp目录下），而且在页面上访问时已经包含application context，相当于/xjs/getGeoJson
        dataType: "json",   //传输json

        type: "post",
        success: function (logoutStatus) {  //回调函数，更新map
            $("#loadgif").hide();
            if($.isEmptyObject(logoutStatus) || logoutStatus.statusCode == 0) {   //判断返回对象是否为空对象
                //alert("登出成功！");
                isLogined = false;
                switchLoginoutWrapper(isLogined);
                hideLoginedInfo();
                closeModalBox();  //关闭登录框
                location.reload();
            }else {
                alert("登出失败！");
            }

        },
        error: function () {  //请求失败的回调方法
            $("#loadgif").hide();
            alert("请求失败，请重试");
        }
    });
}

function showLoginedInfo(userProfile){
    //登录之后显示nickname
    $("#welcom-nickname").text(userProfile.nickname);

    //显示所有登录后的界面变化
    $("#message-center").removeClass("center-hidden");
    $("#message-center").addClass("center-show");
    //显示工具栏
    $("#tool-center").removeClass("center-hidden")
    $("#tool-center").addClass("center-show");

    $("#user_name").val("");
    $("#password").val("");
    //显示头像
    $(".avatar-abstract").css("background-image","url('headImage')");

}
function hideLoginedInfo(){
    //登出后消除界面变化
    $("#message-center").removeClass("center-show");
    $("#message-center").addClass("center-hidden");
    //移除工具栏
    $("#tool-center").removeClass("center-show")
    $("#tool-center").addClass("center-hidden");

    $("#user_name").val("");
    $("#password").val("");
    $(".avatar-abstract").css("background-image","url('img/noneLogin.jpg')");
}

function switchLoginoutWrapper(isLogined) {
    if(isLogined){
        $("#login-wrapper").hide();
        $("#logout-wrapper").show();
    }else {
        $("#login-wrapper").show();
        $("#logout-wrapper").hide();
    }
}