//根据list转义传过来的sql
export function transSql (str,list) {
  var _str=JSON.parse(JSON.stringify(str));
  if(list.length==1){
    list.forEach((item,index)=>{
      for(let i in item){
        if(_str.indexOf("{#"+i+"#}")!=-1){
          var _reg=new RegExp("{#"+i+"#}",'g');
          _str=_str.replace(_reg,"'"+item[i]+"'")
        }
      }
    })
  }else if(list.length>1){
    var strList={};
    list.forEach((item,index)=>{
      for(let i in item){
        if(_str.indexOf("{#"+i+"#}")!=-1){
          if(strList[i]){
            strList[i].push(item[i]);
          }else{
            strList[i]=[item[i]];
          }

        }
      }
    });
    for(let a in strList){
      var _s='';
      if(_str.indexOf("{#"+a+"#}")!=-1){
        _s=JSON.parse(JSON.stringify(strList[a].join(",")));
        var _sreg=new RegExp(",",'g');
        _s=_s.replace(_sreg ,"','");
        _s="'"+_s+"'";
        var _reg=new RegExp("{#"+a+"#}",'g');
        _str=_str.replace(_reg,_s)
      }
    }
  }
  return _str;
}
