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
var jsonUrl;
var jsonVecLayer;
var dbSourceVec;   //数据库矢量数据源
var initMap = function(){
    //设置地图范围

    var mlayers = initLayers();  //里面的图层其实不应该使用全局变量，但先用着
    /*
    controls: ol.control.defaults({
        attributionOptions: {
            collapsible: true
        }
    })*/

    //定义地图对象//地图对象
    map = new ol.Map({
        controls:ol.control.defaults({
            //删除右下角商标栏
            attribution:false,
            rotate:false
        }),
        layers: mlayers,
        target: 'map',
        view: new ol.View({
            projection: 'EPSG:4326',
            //center: [115, 39],
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
            //maxZoom:4
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
    jsonUrl = "data/GeoJSON_HB.json" ;   //   "data/GeoJSON_HB.json";  //相对路径取决于调用js的网页的location，而不取决于js文件的项目路径和url
    console.log(document.location.toString());  //查看当前url
    //var testServletUrl = "getGeoJson";
    //Json读取
    jsonVecLayer = new ol.layer.Vector({
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

    var mlayers = [osmtile,tiled,jsonVecLayer,dbVecLayer,provinceAnotation];  //图层数组
    return mlayers;
}



var initOlTools = function(mlayers){
    //自适应地图view
    map.getView().fit(extent, map.getSize());
    //map.getView().setZoom(4);
    //添加比例尺控件
    map.addControl(new ol.control.ScaleLine());
    //添加缩放滑动控件
    map.addControl(new ol.control.ZoomSlider());
    //添加全屏控件
    //map.addControl(new ol.control.FullScreen());
    //添加鼠标定位控件
    map.addControl(new ol.control.MousePosition({
            className: "ol-mouse-position ol-custom-mouse-position",    //为了更改样式，必须在默认类名基础上增加一个
            undefinedHTML: 'outside',
            projection: 'EPSG:4326',
            //target:$("#wrapper-loc")[0],
            coordinateFormat: function(coordinate) {
                return ol.coordinate.format(coordinate, '{x}, {y}', 4);
            }
        })
    );
    var overviewMapControl = new ol.control.OverviewMap({
        className:'ol-overviewmap ol-custom-overviewmap',  //控制鹰眼图样式
        //这个控件相当于独立的map了，需要重新添加新图层，设置新view
        view: new ol.View({
            projection: 'EPSG:4326',
            //zoom: 8
        }),
        collapseLabel: '\u00BB',            //鹰眼展开时功能按钮上的标识
        label: '\u00AB',                    //鹰眼控件折叠时功能按钮标识
        collapsed:false,                     //初始为展开显示方式
        layers: [
            new ol.layer.Tile({
                visible: true,
                source: new ol.source.OSM({
                    //maxZoom:4
                })
            }),
            //瓦片图层
            new ol.layer.Tile({
                visible: true,
                source: new ol.source.TileWMS({
                    url: urlAdr,  //URL
                    params: {  //请求参数
                        'LAYERS': layerName,
                        'TILED': false,  //对TileWMS无影响
                    },
                    serverType: 'geoserver'
                })
            }),
        ] //离谱的事情就这么轻易发生，如果使用mlayers.slice(0,2)就会闪烁×××  -----不是这个原因，但这里要注意尽量避免使用其他相同图层
        // 其实并不是这个原因，我测试之后发现问题出在osm身上，只要重复使用OSM的layer就会闪烁，在主地图和缩略图间交替闪烁。仔细想想，ol.Map和ol.control.OverviewMap应该都是map，而且缩放程度不同，
        // OSM又是一个综合的数据源，他们不能同时请求同一个缩放程度不同的OSM。当然，这只是我根据官网API文档介绍的推测。
        //同源的矢量图层ol.layer.Vector和图片图层ol.layer.Image也会受到一定程度的影响，ol.layer.Vector可能会因为缩放而导致显示问题，如果没有缩略图，只会在极小比例尺突然放大到较大比例尺，网络未能及时加载资源的的情况下出现这种情况，但如果缩略图使用了同源矢量图层，导致主图比例尺大时，缩略图仍然处在小比例尺，两幅图显示会出现混乱问题，从而影响主图的观感）
        //这个问题在平移时尤为明显，不能忍受
        //而图片图层ol.layer.Image的问题则是直接闪烁，闪烁似乎与OSM的如出一辙，但原因是否一样就不得而知了
        //当然，如果再生成（new）一些数据源相同（例如相同url的WMS瓦片）的图层作为缩略图的图层，也不会存在上述问题。
        // 因此，可以总结一下，这个问题（地图闪烁，矢量图显示异常）的核心就是“与相同图层有关，与相同数据源无关”。
        //对地图瓦片ol.layer.Tile（指ol.source.TileWMS）似乎影响不大，但为了避免以后不必要的问题，这里还是建议不要在缩略图（以及其他map中）使用相同图层
    });
    map.addControl(overviewMapControl);  //添加鹰眼图
}