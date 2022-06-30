var commonDate = {

  formatDate:function(row, column, cellValue, index) {
        if(cellValue==null || cellValue=="") return "";

        var date=new Date(cellValue);
        var year=date.getFullYear();

        var month= date.getMonth()+1<10 ? "0"+(date.getMonth()+1) : date.getMonth()+1;
        var day=date.getDate()<10 ? "0"+date.getDate() : date.getDate();
        var hours=date.getHours()<10 ? "0"+date.getHours() : date.getHours();
        var minutes=date.getMinutes()<10 ? "0"+date.getMinutes() : date.getMinutes();
        var seconds=date.getSeconds()<10 ? "0"+date.getSeconds() : date.getSeconds();
        // 拼接
        // alert(year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds);
        // return year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
        return year+"-"+month+"-"+day;
    },
  formatTime:function(row, column,cellValue) {
        if(cellValue==null || cellValue=="") return "";
        let date= new Date(cellValue);
        let year=date.getFullYear();

        let month= date.getMonth()+1<10 ? "0"+(date.getMonth()+1) : date.getMonth()+1;
        let day=date.getDate()<10 ? "0"+date.getDate() : date.getDate();
        let hours=date.getHours()<10 ? "0"+date.getHours() : date.getHours();
        let minutes=date.getMinutes()<10 ? "0"+date.getMinutes() : date.getMinutes();
        let seconds=date.getSeconds()<10 ? "0"+date.getSeconds() : date.getSeconds();
        // 拼接
        return year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
    },
    getDate : function () {
      let date= new Date();
      let year=date.getFullYear();

      let month= date.getMonth()+1<10 ? "0"+(date.getMonth()+1) : date.getMonth()+1;
      let day=date.getDate()<10 ? "0"+date.getDate() : date.getDate();
      let hours=date.getHours()<10 ? "0"+date.getHours() : date.getHours();
      let minutes=date.getMinutes()<10 ? "0"+date.getMinutes() : date.getMinutes();
      let seconds=date.getSeconds()<10 ? "0"+date.getSeconds() : date.getSeconds();
      // 拼接
      return year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
    },
    getTime : function (cellValue) {
      let date = new Date(); 
      let hours=date.getHours()<10 ? "0"+date.getHours() : date.getHours();
      let minutes=date.getMinutes()<10 ? "0"+date.getMinutes() : date.getMinutes();
      let seconds=date.getSeconds()<10 ? "0"+date.getSeconds() : date.getSeconds();
      // 拼接
      return hours+":"+minutes+":"+seconds;
    },
  clearTime(cellValue){
    return cellValue.replace(/.[0-9]*$/,'')
  },
  //中文符号转换问题
  chineseCharToElishChar (chineseChar){
    // 将单引号‘’都转换成'，将双引号“”都转换成"
    var str = chineseChar.replace(/\’|\‘/g,"'").replace(/\“|\”/g," ");
    // 将中括号【】转换成[]，将大括号｛｝转换成{}
    str = str.replace(/\【/g,"[").replace(/\】/g,"]").replace(/\｛/g,"{").replace(/\｝/g,"}");
    // 将逗号，转换成,，将：转换成:
    str = str.replace(/，/g,",").replace(/：/g,":");
    return str;
  }
}
export default commonDate;
