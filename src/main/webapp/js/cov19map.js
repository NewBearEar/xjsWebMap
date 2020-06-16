
//避免ES6的用法
//导入ECharts
//import echarts from 'echarts';
//import ADLayer from 'openlayers_echart';
function cov19mapEvent(){
    var isCov19MapOpen = false;
    $("#open-cov19layer").click(function () {
        if(!isCov19MapOpen){
            opencov19mapBox();
            isCov19MapOpen = true;
        }

    })
    $("#close-cov19map").click(function () {
        if(isCov19MapOpen){
            closecov19mapBox();
            isCov19MapOpen = false;
        }

    });

    function closecov19mapBox() {  //关闭统计图

        $("#inner-cov19map").empty();  //清空统计图
        $("#cov19map-wrapper").hide();
    }
    function opencov19mapBox() {  //打开统计图
        $("#cov19map-wrapper").show();
        //添加内嵌统计图
        $("#inner-cov19map").append("<iframe src=\"html/cov19ScatterMap.html\" frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" id=\"cov19mapiframe\"  scrolling=\"0\"  width=\"100%\" height=\"100%\"></iframe>");
    }
}








