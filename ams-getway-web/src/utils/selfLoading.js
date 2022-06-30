const SelfLoading={};
/**
 * options参
 * @param {loadingClass} String 遮罩层的自定义class
 * @param {loadingText} String 显示的加载文字
 * @param {loadingTextClass} String 显示的加载文字自定义class
 * @param {buttonText} String 操作按钮的文字
 * @param {showBtn}  Boolean 是否显示showBtn 默认true
 * @param {buttonClass}  String 操作按钮的自定义class
 * @param {buttonFun}  Function 操作按钮的点击回调函数
 *
 */

SelfLoading.show=function (options) {
  var dom=document.createElement("div");
  dom.style.width='100vw';
  dom.style.height='100vh';
  dom.style.position='fixed';
  dom.style.zIndex='99999999999999';
  dom.style.overflow='hidden';
  dom.style.top='0';
  dom.style.left='0';
  dom.style.background='rgba(0,0,0,.3)';
  dom.className="SelfLoading-div";
  addClass(dom,options&&options.loadingClass);
  var _loadingText=document.createElement("div");
  _loadingText.innerHTML=options&&options.loadingText||'努力加载中...';
  _loadingText.style.width='100%';
  _loadingText.style.textAlign ='center';
  _loadingText.style.color ='#fff';
  _loadingText.style.marginTop ='40vh';
  _loadingText.className="SelfLoading-loadingText";
  addClass(_loadingText,options&&options.loadingTextClass);
  dom.appendChild(_loadingText);
  if(options&&options.showBtn===false){

  }else{
    var _btn=document.createElement("button");
    _btn.innerHTML=options&&options.buttonText||'操作按钮';
    _btn.style.margin='10px auto';
    _btn.style.display='block';
    _btn.className="SelfLoading-buttonText";
    _btn.onclick=function () {

      options&&options.buttonFun?options.buttonFun():console.log("点击了");
    };
    addClass(_btn,options&&options.buttonClass);
    dom.appendChild(_btn);
  }
  document.body.append(dom);
};
SelfLoading.hide=function () {
  var cm_complete=document.getElementsByClassName("SelfLoading-div");
  if(cm_complete[0]){
    cm_complete[0].parentNode.removeChild(cm_complete[0])
  }
};
function addClass(el, className){
  if(hasClass(el, className)){
    return
  }
  var newClass = el.className.split(' ');
  newClass.push(className);
  el.className = newClass.join(' ');
}

function hasClass(el, className){
  var reg = new RegExp('(^|\\s)' + className + '(\\s|$)');
  return reg.test(el.className)
}
export default SelfLoading
