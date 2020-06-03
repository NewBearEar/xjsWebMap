//基本底图
var envstr = '';
var extent = [73.44696044921875,6.318641185760498,135.08583068847656,53.557926177978516];
var osmtile;
var urlAdr = 'http://47.94.150.127:8080/geoserver/xjs/wms';   //8080是Tomcat9那个服务器，需要在云端运行tomcat9
//var layerName = 'xjs:BoundaryChn2_4p,xjs:BoundaryChn2_4l,xjs:BoundaryChn1_4l';
var layerName = 'xjs:BoundaryChn2_4p,xjs:BoundaryChn2_4l,xjs:BoundaryChn1_4l';
var tiled;   //Tile WMS
var untiled;   //WMS服务
var provinceAnotation; //省注记
var dbVecLayer;  //查询图层
var map; //地图
var dbSourceVec;   //数据库矢量数据源
var initMap = function(){
    //设置地图范围

    var mlayers = initLayers();

    var overviewMapControl = new ol.control.OverviewMap({
        //className:'ol-overviewmap ol-custom-overviewmap',  //控制鹰眼图样式
        layers:mlayers,
        view: new ol.View({
            projection: 'EPSG:4326',
            center: [115, 39],
            zoom: 2
        }),
    });
    /*
    controls: ol.control.defaults({
        attributionOptions: {
            collapsible: true
        }
    })*/

    //定义地图对象//地图对象
    map = new ol.Map({
        controls:ol.control.defaults().extend([overviewMapControl]),
        layers: mlayers,
        target: 'map',
        view: new ol.View({
            projection: 'EPSG:4326',
            center: [115, 39],
            zoom: 4
        }),


    });



    initOlTools(mlayers); //初始化工具

}

//初始化所有图层，返回图层数组
var initLayers = function (){
    //OSM图像图层
    osmtile =  new ol.layer.Tile({
        visible:true,
        source: new ol.source.OSM({
            maxZoom:4
        })
    });
    //瓦片图层
    tiled = new ol.layer.Tile({
        visible:true,
        source: new ol.source.TileWMS({
            url: urlAdr,  //URL

            params: {  //请求参数
                'LAYERS': layerName,
                'TILED': false,  //对TileWMS无影响
                'env':'',   //ogc:function env动态设置地图颜色，空字符表示默认采用SLD文件配置的值
            },
            serverType: 'geoserver'
        })
    });

    //注记图层
    provinceAnotation = new ol.layer.Image({
        visible:true,
        source: new ol.source.ImageWMS({
            ratio: 1,
            url: urlAdr,
            params: {
                "LAYERS": 'xjs:ProvinceAnotation',
                'TILED': false,
            },
            serverType: 'geoserver'
        })
    });

    //JSON图层
    var jsonUrl = "data/GeoJSON_HB.json" ;   //   "data/GeoJSON_HB.json";  //相对路径取决于调用js的网页的location，而不取决于js文件的项目路径和url
    console.log(document.location.toString());  //查看当前url
    //var testServletUrl = "getGeoJson";
    //Json读取
    var jsonVecLayer = new ol.layer.Vector({
        title: 'add Layer',
        source: new ol.source.Vector({
            projection: 'EPSG:4326',
            url:  jsonUrl, // testServletUrl,   //GeoJSON的文件路径
            // 注意，当servlet正确输出时，这里也可以直接填写servlet的url，不需要利用(ajax)请求返回数据，再用readFeatures方法来加载json对象了
            format: new ol.format.GeoJSON()
        })
    });

    //用于创建图层的默认Json字符，没实际意义
    var defaultGeoJsonStr = "{\"type\" : \"FeatureCollection\", \"features\" : [{\"type\": \"Feature\", \"geometry\": {\"type\":\"Point\",\"coordinates\":[122.53233,52.968872]}, \"properties\": {\"gid\": 1, \"area\": 0.000000000000000, \"perimeter\": 0.000000000000000, \"cntypt_\": 1, \"cntypt_id\": 31, \"name\": \"漠河县\", \"pyname\": \"Mohe Xian\", \"class\": \"AI\", \"id\": 1031, \"pn\": 1, \"adcode93\": 232723}}]}";
    var defaultGeoJsonObj = eval('('+defaultGeoJsonStr+')');
    //数据库查询图层
    dbVecLayer = new ol.layer.Vector({
        visible: false,   //默认不可见，查询之后改为可见
        title: 'db vevtor Layer',
        source: new ol.source.Vector({
            projection: 'EPSG:4326',
            features: (new ol.format.GeoJSON().readFeatures(defaultGeoJsonObj)),
        })
    });

    /*
    dbVecLayer = new ol.layer.Vector({
        title: 'db vevtor Layer',
        source: new ol.source.Vector({
            projection: 'EPSG:4326',

        })
    })*/

    var mlayers = [osmtile,tiled,provinceAnotation];  //dbVecLayer];  //图层数组
    return mlayers;
}



var initOlTools = function(mlayers){
    //自适应地图view
   // map.getView().fit(extent, map.getSize());
    //map.getView().setZoom(4);
    //添加比例尺控件
    map.addControl(new ol.control.ScaleLine());
    //添加缩放滑动控件
    map.addControl(new ol.control.ZoomSlider());
    //添加全屏控件
    map.addControl(new ol.control.FullScreen());
    //添加鼠标定位控件
    map.addControl(new ol.control.MousePosition({
            undefinedHTML: 'outside',
            projection: 'EPSG:4326',
            target:$("#location")[0],
            coordinateFormat: function(coordinate) {
                return ol.coordinate.format(coordinate, '{x}, {y}', 4);
            }
        })
    );

}