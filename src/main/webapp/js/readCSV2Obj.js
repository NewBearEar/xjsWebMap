
function csvToObject(csvString){
    var csvarry = csvString.split("\r\n");
    var datas = [];
    var headers = csvarry[0].split(",");
    for(var i = 1;i<csvarry.length;i++){
        var data = {};
        var temp = csvarry[i].split(",");
        for(var j = 0;j<temp.length;j++){
            data[headers[j]] = temp[j];
        }
        datas.push(data);
    }
    return datas;
}

function FuncCSVInport() {
    $("#csvFileInput").val("");
    $("#csvFileInput").click();
}

function readCSVFile(obj) {
    var reader = new FileReader();
    reader.readAsText(obj.files[0]);
    reader.onload = function () {
        var data = csvToObject(this.result);
        console.log(data);//data为csv转换后的对象
    }
}
