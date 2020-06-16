var heatmap = new ol.layer.Heatmap({
    source: new ol.source.Vector({
        url: 'heatmapTestData',  //重点
        projection: 'EPSG:3857',
        format: new ol.format.GeoJSON(),
        /*
        new ol.format.KML({
        extractStyles: false
    })
         */
    }),
    blur: 30,
    radius: 25,
    weight:function (feature) {
        var name = feature.get('name');
        var cityCounts = feature.get('value');
        //console.log(cityCounts);
        return cityCounts;  //weight在0-1之间，在服务器端归一化
    }
});

function heatmapEvent(){
    var isHeatmapLayerOpen = false;
    $("#open-heatmap").click(function () {
        console.log(isHeatmapLayerOpen);
        if(!isHeatmapLayerOpen){
            addHeatmapLayer();
            isHeatmapLayerOpen = true;

        }else {
            removeHeatmapLayer();
            isHeatmapLayerOpen = false;
        }
    })
}



function addHeatmapLayer(){

    map.addLayer(heatmap);
    //此时移除鼠标移动的箭头效果，防止热力图闪烁
    console.log('1')
    map.removeEventListener('pointermove',pmove);
}

function removeHeatmapLayer(){
    map.removeLayer(heatmap);
    //移除热力图图层之后，重新添加事件
    map.addEventListener('pointermove', pmove);
}

function pmove(evt) {  //回调函数
    if (evt.dragging) {   //如果是拖动地图造成的鼠标移动，则不作处理
        return;
    }
    var pixel = map.getEventPixel(evt.originalEvent);
    var hit = map.hasFeatureAtPixel(pixel);
    map.getTargetElement().style.cursor = hit ? 'pointer' : '';
}