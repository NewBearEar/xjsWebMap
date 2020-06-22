function statisticWidgetEvent(){
    var isStaWidgetOpen = false;
    $("#open-adcode-histogram").click(function () {
        if(!isStaWidgetOpen){
            openHistogramBox();
            isStaWidgetOpen = true;
        }

    })
    $("#close-histogram").click(function () {
        if(isStaWidgetOpen){
            closeHistogramBox();
            isStaWidgetOpen = false;
        }

    });

    function closeHistogramBox() {  //关闭统计图

        $("#inner-histogram").empty();  //清空统计图
        $("#inner-pie").empty();
        $("#histogram-wrapper").hide();
    }
    function openHistogramBox() {  //打开统计图
        $("#histogram-wrapper").show();
        //添加内嵌统计图
        $("#inner-histogram").append("<iframe src=\"html/adcodeHistogram.html\" frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" " +
            "id=\"prodcutHistDetailSrc\"  scrolling=\"0\"  width=\"100%\" height=\"100%\"></iframe>");
        $("#inner-pie").append("<iframe src=\"html/adclassPie.html\" frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" " +
            "id=\"prodcutPieDetailSrc\"  scrolling=\"0\"  width=\"100%\" height=\"100%\"></iframe>");
    }
}
function switchStatisticTabEvent(){
    var $form_modal = $("#histogram-wrapper");
    var $form_modal_tab = $('.cd-switcher'),
        $tab_hist = $form_modal_tab.children('li').eq(0).children('a'),
        $tab_pie = $form_modal_tab.children('li').eq(1).children('a'),
        $hist = $form_modal.find("#inner-histogram"),
        $pie = $form_modal.find('#inner-pie');

    //切换表单
    $form_modal_tab.on('click', function(event) {
        event.preventDefault();
        ( $(event.target).is( $tab_hist ) ) ? hist_selected() : signup_selected();
    });
    function hist_selected(){
        $hist.addClass('is-selected');
        $pie.removeClass('is-selected');
        $tab_hist.addClass('selected');
        $tab_pie.removeClass('selected');
    }

    function signup_selected(){
        $hist.removeClass('is-selected');
        $pie.addClass('is-selected');
        $tab_hist.removeClass('selected');
        $tab_pie.addClass('selected');
    }
}