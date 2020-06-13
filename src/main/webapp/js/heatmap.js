function addHeatmapLayer(){
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
    map.addLayer(heatmap);
}

function removeHeatmapLayer(){
    map.removeLayer(heatmap);
}