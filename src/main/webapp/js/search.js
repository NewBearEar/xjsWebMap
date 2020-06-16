var container = document.getElementById('popup');
var content = $('#popup-content');
var closer = document.getElementById('popup-closer');
var popup = new ol.Overlay({
    element: container,
    positioning: 'bottom-center',
    autoPan: true,
    //The animation options used to pan the overlay into view.
    //This animation is only used when autoPan is enabled.
    autoPanAnimation: {
        //动画持续时间
        duration:250
    },
    stopEvent: false,
    offset: [0, -50]
});
var cityNameIdDist = {};  //城市名与district_geocode的字典
$.ajax({  //AJAX请求json,取出城市名对应的district_geocode
    url: 'data/weather_district_id.json', // url不带/就是当前路径（因为我这个html放在webapp目录下），而且在页面上访问时已经包含application context，相当于/xjs/getGeoJson
    dataType: "json",   //传输json
    type: "get",
    success: function (districtIdArray) {  //回调函数
        if($.isEmptyObject(districtIdArray)){   //判断返回对象是否为空对象
            alert("未获取到城市对应的LocationId");
        }else{ //非空
            //console.log(districtIdArray)
            for(x in districtIdArray){
                var district = districtIdArray[x].district;
                var district_geocode = districtIdArray[x].district_geocode;
                cityNameIdDist[district] = district_geocode;
            }
            //console.log(cityNameIdDist);
        }
    },
    error: function () {  //请求失败的回调方法
        alert("请求失败，请重试");
    }
});

function addInterEvent() {  //必须要在引入ol的文件的页面内调用一下，而且map为什么没有on方法
    map.addEventListener('click', function(evt) {
        var feature = map.forEachFeatureAtPixel(evt.pixel,
            function(feature) {
                return feature;
            });
        if (feature) {
            var coordinates = feature.getGeometry().getCoordinates();
            //console.log(feature.get('name'));
            var cityName = feature.get('name');
            var district_geocode1 = cityNameIdDist[cityName];  //获取对应的id号
            //console.log(cityName.slice(0, cityName.length-1));
            var district_geocode2 = cityNameIdDist[cityName.slice(0,cityName.length-1)];  //对于某些名字去掉“县”等描述，取id
            var district_geocode;
            if(district_geocode1){  //取有值的那个
                district_geocode = district_geocode1;
            }else if(district_geocode2){
                district_geocode = district_geocode2;
            }
            //console.log(district_geocode);
            var forecastText = cityName+':';
            var forecastTextArray = [forecastText];
            //popup.setPosition(coordinates);
            popup.setPosition(coordinates);
            //将内容div的内容清空
            content.empty();

            if(!district_geocode){  //判断是否能够找到对应的geocode
                forecastTextArray.push("未能找到相应城市的天气预报信息");
                //添加要素信息
                //addFeatureInfo(featureInfo);
                for(i in forecastTextArray){
                    var text = forecastTextArray[i];
                    content.append("<p id='text-'"+i+">"+text+"</p>");
                }
                //console.log(evt.pixel);
            }else {
                //ajax请求百度天气预报API  //百度天气api不知为何，不能够用jsonp实现跨域（callback参数没有反应），改用高德
                //var ak = 'ZbyTqH1pdREnbxin0Z0Gx4peoHZjGnIv'; //my ak
                //高德key
                var key = 'ab69453690f77bca25d70d353a09e501';
                var weatherapiUrl = 'https://restapi.amap.com/v3/weather/weatherInfo?key='+key+'&city='+district_geocode+'&extensions=all';
                //参数all表示预报
                $.ajax({  //AJAX请求天气预报  //ajax执行顺序可能和代码顺序不同，比如这里，ajax请求才会执行，但是其外的内容会继续执行下去
                    //调用百度api
                    //url: 'http://api.map.baidu.com/weather/v1/?district_id='+district_geocode+'&data_type=all&ak='+ ak,
                    //调用高德天气API
                    url: weatherapiUrl,
                    dataType: "jsonp",   //传输jsonp,跨域请求 ,
                    type: "get",
                    jsonpCallback:"weather",
                    success: function (forecastObj) {  //回调函数
                        //console.log(forecastObj);
                        if ($.isEmptyObject(forecastObj) || forecastObj.status != 1) {   //判断返回对象是否为空对象
                            forecastTextArray.push("未能找到相应城市的天气预报信息");

                        } else { //非空
                            //console.log(districtIdArray)
                            var forecasts = forecastObj.forecasts;
                            var forecast = forecasts[0];  //只有一个城市
                            var casts = forecast.casts;
                            for (x in casts) {
                                //console.log(casts[x]);
                                var text_day = casts[x].dayweather;
                                var text_night = casts[x].nightweather;
                                var temperature_high = casts[x].daytemp;
                                var temperature_low = casts[x].nighttemp;
                                var date = casts[x].date;
                                var week = casts[x].week;
                                var temp_text = date + "  星期" + week + " : " + "气温:" + temperature_high + "-" + temperature_low + "," +
                                    "白天:" + text_day + "," + "夜间:" + text_night ;
                                forecastTextArray.push(temp_text);
                            }
                        }
                        //console.log(forecastTextArray);
                        //添加要素信息  //ajax回调函数再调用一次，不会和外面的重复
                        //addFeatureInfo(featureInfo);
                        for(i in forecastTextArray){
                            var text = forecastTextArray[i];
                            content.append("<p id='text-'"+i+">"+text+"</p>");
                        }
                        //console.log(evt.pixel);

                    },
                    error: function () {  //请求失败的回调方法
                        alert("请求失败，请重试");
                        forecastTextArray.push("未能找到相应城市的天气预报信息");
                    }
                });
            }


        } else {
            //将内容div的内容清空
            content.empty();
            popup.setPosition(undefined);//Set the position for this overlay. If the position is undefined the overlay is hidden.
        }
    });
    
    // 鼠标移动的事件
    map.addEventListener('pointermove', function (evt) {
        if (evt.dragging) {   //如果是拖动地图造成的鼠标移动，则不作处理
            return;
        }
        var pixel = map.getEventPixel(evt.originalEvent);
        var hit = map.hasFeatureAtPixel(pixel);
        map.getTargetElement().style.cursor = hit ? 'pointer' : '';
    });

}

var isSearchTextComplete = 0;  //搜索文字是否被补全，补全时采用精准查询，直接搜索使用模糊查询 ，0代表未补全，默认值为0；1代表补全
//按钮单击事件
$('#btn').click(function ajaxConfirm() {  //函数必须放在btn的click里面否则会一直调用
    $("#loadgif").show();
    //console.log($('#form2').serialize());  为什么这个函数得不到数据
    //获取表单对话框内容
    var searchTxtValue = $('#searchTxt').val();   //val()而不是value()
    //console.log(searchTxtValue);


    $.ajax({  //AJAX向Servlet发送get请求，请求后台数据库数据
        url: "getGeoJson", // url不带/就是当前路径（因为我这个html放在webapp目录下），而且在页面上访问时已经包含application context，相当于/xjs/getGeoJson
        dataType: "json",   //传输json
        data:  {
            searchTxt:searchTxtValue,
            "isSearchTextComplete":isSearchTextComplete
        },   //$('#form2').serialize(),  //按键值对形式
        type: "get",
        success: function (dbGeoJson) {  //回调函数，更新map
            $("#loadgif").hide();  //隐藏加载动画
            isSearchTextComplete = 0;  //补全值置零
            $("#left-panel").css("height","850px");//left-panel显示
            //console.log(dbGeoJson);  //这里已经传回一个json对象
            if($.isEmptyObject(dbGeoJson)){   //判断返回对象是否为空对象
                alert("未查询到相关内容");
            }else {
                //添加到到图层
                //dbVecLayer.source.addFeature((new ol.format.GeoJSON()).readFeatures(dbGeoJson));
                //改到initMap定义图层，现在只需要重新加载数据源

                //调用函数更新图层要素
                updateDbVeclayerFeatures(dbGeoJson);
                //调用函数添加list
                addShowCardAndPoiList(dbGeoJson);
                //最后判断图层是否可见，不可见则设置为可见
                if(!dbVecLayer.getVisible()){
                    dbVecLayer.setVisible(true);
                }
                //console.log(map.getLayers());
                //map.addLayer(dbVecLayer);
            }
        },
        error: function () {  //请求失败的回调方法
            $("#loadgif").hide();
            alert("请求失败，请重试");
            isSearchTextComplete = 0;  //补全值置零
        }
    });
});

//jquery-ui自动补全，输入内容自动补全时，调用
var autoCompleteUrl = "autoComplete";
$("#searchTxt").autocomplete({
    maxResults:10,   //自定义属性，最多显示条数,设置默认最多补全10条
    source: function (request,response) {  //response是相应参数
        var mapResults = this.options.maxResults;//利用变量将外部dom对象的maxResults传进ajax
        $.ajax({
            url:autoCompleteUrl,
            dataType:"json",
            type:"post",    //get请求
            data: {
                searchTxt: request.term    //request.term传递当前参数
            },
            minLength:1,
            success:function (result) {
                response(result.slice(0,mapResults));  //向response响应输出内容  //slice筛选前十个
            }
        })
    },
    response: function (event,ui) {   //这里的response属性名,这是在搜索完成后菜单显示前触发
        //不需要这里在这里回掉
        //console.log(ui);   //ui就是包含上述10条内容的对象
    },
    select: function (event,ui) {  //某一条被选择时触发
        //这里由补全完成的内容，补全值置1
        isSearchTextComplete = 1;
        //尝试用补全值填充搜索栏
        $('#searchTxt').val(ui.item.value);   // ui只有一个对象item，item包括name和value
        //这里直接调用btn的click ，ajax提交
        $("#btn").click();
    },

});

var updateDbVeclayerFeatures = function (dbGeoJson) {  //传入GeoJson对象
    var featureArray = dbGeoJson.features;
    //重新加载图层数据源
    var dbVecSourceTemp = dbVecLayer.getSource(); //获取数据源
    dbVecSourceTemp.forEachFeature(function (feature) {
        dbVecSourceTemp.removeFeature(feature);   //对每一个feature进行移除
    });
    map.addOverlay(popup);  //这句话将天气面板的popup添加进来
    var createLabelStyle = function (feature) {

        var iconStyle = new ol.style.Style({
            //点的图片样式
            image: new ol.style.Icon({
                //标注图片和文字之间的距离
                anchor: [0.5, 46],
                //x方向的单位
                anchorXUnits: 'fraction',
                //y方向的单位
                anchorYUnits: 'pixels',
                src: 'img/pticon.png'
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
    //icon添加
    var iconFeatures = [];
    for(var i=0;i<featureArray.length;i++){
        var searchedCityname = featureArray[i].properties.name;
        var searchedCitycoor = featureArray[i].geometry.coordinates;  //坐标数组
        var iconFeature = new ol.Feature({
            geometry:new ol.geom.Point(searchedCitycoor),
            name: searchedCityname,
            type:'icon',
        });
        iconFeature.setStyle(createLabelStyle(iconFeature));
        iconFeatures.push(iconFeature);
    }
    console.log(iconFeatures)
    dbVecSourceTemp.addFeatures(iconFeatures);   //向数据源添加新features  (new ol.format.GeoJSON()).readFeatures(dbGeoJson)
}

var addShowCardAndPoiList = function (dbGeoJson) {
    //向列表中添加card，并添加poilist
    if ($("#cards-level1").length != 0){
        $("#cards-level1").empty();   //清空既有元素
    }
    //获取Fetures数组
    var featureArray = dbGeoJson.features;
    //console.log(featureArray);
    //创建card
    $("#cards-level1").append("<li id='card-1'></li>" );
    //添加属性，先写个1试试
    var datafoldStr = "共找到"+ featureArray.length +"个搜索结果";
    $("#card-1").attr("class","card animated-card","data-fold",datafoldStr);
    //创建card里面显示poi的容器
    $("#card-1").append("<div id='show-wrapper' class='poi-wrapper'></div>");  //可以包含poilist在内的多种信息
    //创建展示的list
    $("#show-wrapper").append("<ul id='show-poilist' class='poilist'></ul>");



    //动态创建元素
    var dynamicId = 0;  //动态列表id号
    for(var i = 0;i < featureArray.length;i++){  //这里写死了显示的几个字段
        /*
        //取对象的一部分，可以如下操作：
        // 不需要的属性放前面，rest就是剩下的
        //var {b,c,...rest} = obj
        //console.log(rest)
        */
        dynamicId = i;
        var attr = featureArray[i].properties; //返回属性对象
        var name = attr.name;
        var pinyin = attr.pinyin;
        var intro = attr.intro;
        var gid = attr.gid;   //要素的唯一标识码
        if(attr.image == null){
            var image = "No Image";
        }else {
            var image = attr.image;   //暂定，待修改
        }
        var showAttributionArray = [dynamicId,name,pinyin,intro,image];
        showAttributionArray.push(gid); //需要显示信息的数组,gid永远添加在最后，所以push
        //console.log(showAttributionArray);
        if(i<5){
            addPoiLine("show-poilist",showAttributionArray,i);
        }
    }

    //为ul的每个li动态绑定事件，实现选中样式
    //console.log($("#show-poilist li"));
    $("#show-poilist li").on('mouseover',function () {
        $(this).addClass('show-poiline-mouseover');  //mouseover事件时添加类名到li元素，用于css显示
    }).on('mouseout',function () {
        $(this).removeClass('show-poiline-mouseover');
    }).on('click',function () {
        if(isLogined) {   //管理登录权限
            //添加点击事件
            $('.show-poiline-selected').removeClass('show-poiline-selected');
            $(this).addClass('show-poiline-selected');
            //获取搜索参数和值
            var searchParams = $(this).attr('name').split("-")
            var searchKey = searchParams[0];
            var searchValue = searchParams[1];
            console.log(searchKey + searchValue);
            window.location = 'jsp/showInfo.jsp?' + searchKey + '=' + searchValue;  //跳转页面
        }else {
            alert("请先登录再进行编辑")
        }
    })

    if(featureArray.length>5){
        //显示详细信息
        //跟ul show-poilist 同一级
        $("#show-poilist").append("<div class='poi-focuscase'>");
        $("#show-poilist").children(".poi-focuscase").append("<span class='focuscase-more'>"+"点击查看全部"+featureArray.length+"条结果"+"</span>");
    }

}
function addPoiLine(parentNodeId,showAttributionArray,rowIndex){
    //需要参数：待添加节点的id，需要显示信息的数组,行号
    $("#"+parentNodeId).append("<li id=poi-line-"+rowIndex+"></li>");
    //console.log(showAttributionArray.slice(-1)) ;
    $("#poi-line-"+rowIndex).attr("class","poi-linebox").attr("name","gid-"+showAttributionArray.slice(-1));    //slice取值 赋值gid(最后一个)给name

    //添加li下面的各种div
    //添加图片结构
    $("#poi-line-"+rowIndex).append("<div id=poi-imgbox-"+ rowIndex+" class='poi-imgbox'></div>");
    //$("#poi-imgbox-"+rowIndex).append("<span id=poi-img-"+rowIndex +" class='poi-img'></span>"); //试试

    //注意这里给了一个相对路径，其中含有xjs的虚拟路径
    $("#poi-imgbox-"+rowIndex).append("<img id=poi-img-"+rowIndex +" class='poi-img' src='../xjs/imageOut?gid="+showAttributionArray.slice(-1)+"' alt='暂无图片'>"); //试试=\"../img/defaultImg.jpg\"
    //$(#"poi-img-"+rowIndex).append("<img src=\"../img/defaultImg.jpg\">");    // 这个通过gid进行图片查询
    //$("#poi-img-"+rowIndex).attr("style","")
    //添加info结构
    $("#poi-line-"+rowIndex).append("<div id=poi-info-left-"+ rowIndex+" class='poi-info-left'></div>"); //加上box
    $("#poi-info-left-"+rowIndex).append("<h3 class='poi-title'>")
    $("#poi-info-left-"+rowIndex).children(".poi-title").append("<span id='poi-name-cn' class='poi-name' title="+showAttributionArray[1]+">"+(showAttributionArray[0]+1)+"."+showAttributionArray[1]+"</span>");
    $("#poi-info-left-"+rowIndex).children(".poi-title").append("<span id='poi-name-pinyin' class='poi-name' title="+showAttributionArray[2]+">"+showAttributionArray[2]+"</span>");
    //详细介绍
    $("#poi-info-left-"+rowIndex).append("<div class='poi-info'>");
    $("#poi-info-left-"+rowIndex).children(".poi-info").append("<p class='poi-introduction'>"+showAttributionArray[3] +"</p>");


}
