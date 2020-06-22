var isRouteToolOpen = false;
var focusinput = 0;   //焦点1代表起点，2代表终点,0代表没有聚焦blur
//路网原始图层
var roadLayerName = 'road_wgs84_net';
roadLayer = new ol.layer.Tile({
    visible:true,
    source: new ol.source.TileWMS({
        url: urlAdr,  //URL
        params: {  //请求参数
            'LAYERS': roadLayerName,
        },
        serverType: 'geoserver'
    })
});
//路径查询图层源
var routeTileSource = new ol.source.TileWMS();     //改用wms

$("#open-road-search").click(function () {  //管理按钮
    $("#right-panel").toggle();  //打开规划面板
    //最后判断图层是否可见，不可见则设置为可见
    if(!markerLayer.getVisible()){
        markerLayer.setVisible(true);
        routeLayer.setVisible(true);
    }else {
        markerLayer.setVisible(false);
        routeLayer.setVisible(false);
    }
    //console.log(isRouteToolOpen)
    if(isRouteToolOpen){
        //若打开了，就关闭
        isRouteToolOpen = false;
        map.removeLayer(roadLayer);
        focusinput = 0;  //重置焦点
        //关闭时清空图层source
        routeLayer.setSource(null);  //清空路径查询Tile图层source
        //清空起始点终止点输入框
        $("#road-route-start input").val("");
        $("#road-route-end input").val("");
    }else {
        isRouteToolOpen = true;
        map.getLayers().insertAt(6,roadLayer);  //按顺序插入图层
        //打开时
        //清空起始点终止点等所有icon
        markerLayer.getSource().clear(); //获取icon数据源所有feature
    }
});

$("#road-route-start input").focus(function () {  //填充在mapTool.js里
    focusinput = 1;
});
$("#road-route-end input").focus(function () {  //填充在mapTool.js里
    focusinput = 2;
});



$("#route-search-begin").click(function () {
    if($("#road-route-start input").val() && $("#road-route-end input").val()){
        var startpt = $("#road-route-start input").val().split(",");
        var endpt = $("#road-route-end input").val().split(",");
        var viewParams = {
            "x1":startpt[0],
            "y1":startpt[1],
            "x2":endpt[0],
            "y2":endpt[1]
        }
    }else {
        alert("起始点或终止点不能为空");
    }
    console.log(viewParams);

    routeTileSource = new ol.source.TileWMS({
        url:urlAdr,
        params: {
            "LAYERS":'xjs:road_fromAtoB',
            "TILED":true,
            'viewparams': 'x1:' + viewParams.x1+ ';y1:' + viewParams.y1 + ';x2:' + viewParams.x2+ ';y2:' + viewParams.y2
        }
    });
    routeLayer.setSource(routeTileSource);

    //getRoutingByAjax(viewParams);
});
// 通过wfs失败
/*
function getRoutingByAjax(viewParams) {   //viewParams包括x1，y1,x2,y2
    var typeName ='xjs:road_fromAtoB';
    var data = {
        'service': 'wfs',
        'version': '1.0.0',
        'request': 'GetFeature',
        'typeName': typeName,
        'outputFormat': 'application/json',
        'viewparams': 'x1:' + viewParams.x1+ ';y1:' + viewParams.y1 + ';x2:' + viewParams.x2+ ';y2:' + viewParams.y2
    };
    var wfsurl = geoserverUrl + '/wfs';
    $.ajax({
        type: "get",
        url: wfsurl,
        data: data
    }).then(function(response){
        var features = new ol.format.GeoJSON().readFeatures(response);
        routeSource.addFeatures(features);
    })
}
*/
