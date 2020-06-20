/****点选添加点记录工具*****/
function addClickDialogEvent() {
    //信息提示面板
    $("#message-center").click(function (){
        //console.log("11")
        $("#message-center p").toggle();
    })

    $("#dialog-form").dialog({
        autoOpen: false,
        height: 700,
        width: 550,
        modal: false,
        buttons: {
            "创建新纪录": function () {
                var bValid = false;
                console.log($( "#name" ).val());
                var name = $( "#name" ).val(),
                    pinyin = $( "#pinyin" ).val(),
                    intro = $( "#intro" ).val(),
                    adcode93 = $( "#adcode93" ).val(),
                    longitude = $( "#longitude" ).val(),
                    latitude = $( "#latitude" ).val(),
                    type = $( "#type" ).val();
                var allFields = $( [] ).add($( "#name" )).add($( "#pinyin" )).add($( "#intro" )).add($( "#adcode93" ));
                allFields.removeClass("ui-state-error");
                if(""==name || ""==pinyin || ""==adcode93 || ""==intro || ""==longitude || ""==latitude || ""==type){
                    //任意一条为空都不可
                    updateTips("全部内容均为必填项，不能为空");
                }else{
                    if(! (/^([0-9])+$/.test($("#adcode93").val()))){
                        //正则表达式测试adcode93是否为数字，如果不是则
                        $("#adcode93").addClass("ui-state-error");
                        updateTips("adcode93只允许输入数字");
                    }else if(! (/^([a-zA-Z])+$/.test($("#pinyin").val()))){
                        //正则表达式测试pinyin是否为字幕，如果不是则
                        $("#pinyin").addClass("ui-state-error");
                        updateTips("拼音只允许输入字母");
                    }else {
                        bValid = true;  //填完之后
                    }
                }
                //alert("1");
                if (bValid) {
                    //alert("1");
                    var addInfoFormData = $("#add-info").serialize();//打包FromData  这里序列化会把中文处理成%23%12%83这样的字符
                    var addInfoFormDataJson = JSON.parse(DataDeal.formToJsonStr(addInfoFormData));
                    addInfoFormDataJson.name = $( "#name" ).val();  //重新录入中文
                    console.log(addInfoFormDataJson);

                    //ajax向服务器提交内容
                    $("#loadgif").show();
                    $.ajax({  //AJAX向Servlet发送get请求，请求后台数据库数据
                        url: "addInfo", // url不带/就是当前路径（因为我这个html放在webapp目录下），而且在页面上访问时已经包含application context，相当于/xjs/getGeoJson
                        dataType: "json",   //传输对于jsp页面，只能要求text数据？ 若写json，则即使是200请求成功，也会调用error
                        data: addInfoFormDataJson,  //不能是json格式的对象，而必须是非json格式的对象，否则后端无法直接从名字读取
                        type: "post",
                        success: function (addStatus) {  //回调函数，更新map
                            $("#loadgif").hide();
                            console.log(addStatus);
                            if($.isEmptyObject(addStatus)) {   //判断返回对象是否为空对象
                                alert("添加失败！");
                            }else {
                                if(1==addStatus.statusCode){
                                    alert("添加成功！");
                                    var gid = addStatus.gid;
                                    allFields.val("");  //清空内容
                                    $(this).dialog("close");  //关闭窗口
                                    location.href = "jsp/showInfo.jsp?gid="+gid;
                                }else {
                                    alert("添加失败！");
                                }
                            }

                        },
                        error: function () {  //请求失败的回调方法
                            $("#loadgif").hide();
                            alert("请求失败，请重试");
                        }
                    });

                    //关闭窗口之前置空
                    $(this).dialog("close");
                }
            }
        },
        Cancel: function () {
            $(this).dialog("close");
        },
        close: function () {
            //allFields.val( "" ).removeClass( "ui-state-error" );
        }
    });
    $("#add-click-tool")
        .button()
        .click(function () {
            $("#dialog-form").dialog("open");
        });
}

//设置全局变量，存储点击位置
var iconFeature_start =  new ol.Feature();
var iconFeature_end =  new ol.Feature();
var iconFeature_somewhere = new ol.Feature();

function mapClickRegist(){
    map.addEventListener('click', function(evt) {   //  地图单击事件
        var coordinate = evt.coordinate;  //获取点击坐标
        //var hdms = ol.coordinate.toStringHDMS(ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326'));  //不需要坐标转换否则就回到赤道了
        console.log(coordinate);
        var longitude = coordinate[0];
        var latitude = coordinate[1];
        $("#longitude").val(longitude);
        $("#latitude").val(latitude);

        //icon style
        var createLabelStyle = function (feature,imageurl) {
            var iconStyle = new ol.style.Style({
                //点的图片样式
                image: new ol.style.Icon({
                    //标注图片和文字之间的距离
                    anchor: [0.5, 46],
                    //x方向的单位
                    anchorXUnits: 'fraction',
                    //y方向的单位
                    anchorYUnits: 'pixels',
                    src: imageurl   //icon的url
                }),
                //文本样式
                text: new ol.style.Text({
                    //对其方式
                    textAlign: 'center',
                    //基准线
                    textBaseline: 'middle',
                    //文字样式
                    font: 'normal 14px 微软雅黑',
                    //文本内容
                    text: feature.get('name'),
                    //文本填充样式
                    fill: new ol.style.Fill({ color: '#aa2200' }),
                    //笔触
                    stroke: new ol.style.Stroke({ color: '#ffcc33', width: 1 })

                })
            });
            return iconStyle;
        }

        //为点击位置添加marker
        //重新加载图层数据源
        var markerSourceTemp = markerLayer.getSource(); //获取数据源
        if(1==focusinput){
            if(markerSourceTemp.hasFeature(iconFeature_start)){
                markerSourceTemp.removeFeature(iconFeature_start);
            }
            if(markerSourceTemp.hasFeature(iconFeature_somewhere)){
                markerSourceTemp.removeFeature(iconFeature_somewhere);
            }

        }else if(2==focusinput){
            if(markerSourceTemp.hasFeature(iconFeature_end)){
                markerSourceTemp.removeFeature(iconFeature_end);
            }
            if(markerSourceTemp.hasFeature(iconFeature_somewhere)){
                markerSourceTemp.removeFeature(iconFeature_somewhere);
            }

        }else{
            markerSourceTemp.forEachFeature(function (feature) {
                markerSourceTemp.removeFeature(feature);   //对每一个feature进行移除
            });
        }

        var imageurl = 'img/pticon.png';
        var iconName = "somewhere";



        //console.log(focusinput);
        //routeSearch中判断焦点的值
        var currentIconFeature;   //记录哪一个icon需要被添加
        if(1==focusinput){
            $("#road-route-start input").val(coordinate.join(","));
            imageurl = 'img/marker_start.png';
            iconName = "start";
            iconFeature_start = new ol.Feature({
                geometry:new ol.geom.Point(coordinate),
                name: iconName,
                type:'icon',
            });
            currentIconFeature = iconFeature_start;
        }else if(2==focusinput){
            $("#road-route-end input").val(coordinate.join(","));
            imageurl = 'img/marker_end.png';
            iconName = "end";
            iconFeature_end = new ol.Feature({
                geometry:new ol.geom.Point(coordinate),
                name: iconName,
                type:'icon',
            });
            currentIconFeature = iconFeature_end;
        }else{
            imageurl = 'img/pticon.png';
            iconName = "somewhere";
            iconFeature_somewhere = new ol.Feature({
                geometry:new ol.geom.Point(coordinate),
                name: iconName,
                type:'icon',
            });
            currentIconFeature = iconFeature_somewhere;
        }
        //icon添加
        currentIconFeature.set("name",iconName);
        currentIconFeature.setStyle(createLabelStyle(currentIconFeature,imageurl));
        //console.log(currentIconFeature);
        markerSourceTemp.addFeature(currentIconFeature);   //向数据源添加新feature

    });
}

function updateTips(text) {   //错误提示
    $( ".validateTips" )
        .text(text)
        .addClass( "ui-state-highlight" );
    setTimeout(function() {
        $( ".validateTips" ).removeClass( "ui-state-highlight", 1500 );
    }, 500 );
}
/****点选添加点记录工具*****/
//

//转换工具对象
var DataDeal = {
    //将从form中通过$('#form').serialize()获取的值转成json字符串
    formToJsonStr: function (data) {
        data=data.replace(/&/g,"\",\"");
        data=data.replace(/=/g,"\":\"");
        data="{\""+data+"\"}";
        return data;
    },
}


