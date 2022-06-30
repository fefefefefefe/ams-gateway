import Vue from 'vue'
import router from '@/router'
import store from '@/store'

/**
 * 获取uuid
 */
export function getUUID () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    return (c === 'x' ? (Math.random() * 16 | 0) : ('r&0x3' | '0x8')).toString(16)
  })
}

/**
 * 是否有权限
 * @param {*} key
 */
export function isAuth (key) {
  return JSON.parse(sessionStorage.getItem('permissions') || '[]').indexOf(key) !== -1 || false
}

/**
 * 树形数据转换
 * @param {*} data
 * @param {*} id
 * @param {*} pid
 */
export function treeDataTranslate (data, id = 'id', pid = 'parentId') {
  var res = []
  var temp = {}
  for (var i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (var k = 0; k < data.length; k++) {
    if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
      if (!temp[data[k][pid]]['children']) {
        temp[data[k][pid]]['children'] = []
      }
      if (!temp[data[k][pid]]['_level']) {
        temp[data[k][pid]]['_level'] = 1
      }
      data[k]['_level'] = temp[data[k][pid]]._level + 1
      temp[data[k][pid]]['children'].push(data[k])
    } else {
      res.push(data[k])
    }
  }
  return res
}

/**
 * 清除登录信息
 */
export function clearLoginInfo () {
  Vue.cookie.delete('token')
  store.commit('resetStore')
  router.options.isAddDynamicMenuRoutes = false
}

// 图片转base64
export function getBase64(file) {
  return new Promise(function (resolve, reject) {
    let reader = new FileReader()
    let imgResult = ''
    reader.readAsDataURL(file)
    reader.onload = function () {
      imgResult = reader.result
    }
    reader.onerror = function (error) {
      reject(error)
    }
    reader.onloadend = function () {
      resolve(imgResult)
    }
  })
}

Array.prototype.indexOf = function(val) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == val) return i;
  }
  return -1;
};
Array.prototype.remove = function(val) {
  var index = this.indexOf(val);
  if (index > -1) {
    this.splice(index, 1);
  }
};

export function PxSocket(options) {
  this.options = options;
  this.ws = null;
  this.timeout=options.heartTime||50000;
  this.timeoutObj=null;
  this.serverTimeoutObj=null;
}

PxSocket.prototype = {
  connect: function () {
    var _this = this;
    try {
      if ('WebSocket' in window) {
        console.log(_this.options.url)
        _this.ws = new WebSocket(_this.options.url);
        // console.log( _this.ws)
        // console.log(http_url.Socket_url+options.id)
      } else {
        alert("您的浏览器不支持websocket");
      }
      _this.ws.onerror = function () {
        _this.reconnect(_this.ws);
        console.log("send error！");
      };
      _this.ws.onopen = function () {
        _this.heartCheck();      //心跳检测重置
        // console.log(_this.options.name +"  "+ new Date().toUTCString());
        console.log("connection success！")
      };
      _this.ws.onmessage = function (event) {
        _this.heartCheck();      //拿到任何消息都说明当前连接是正常的
        // console.log("llws收到消息啦:" + event.data);
        // console.log(event.data)
        if (event.data) {
          _this.options.succ(event.data);
        } else {
          // console.log(_this.options.name + " is " + event.data);
        }
      };
      _this.ws.onclose = function () {
        // _this.reconnect(_this.ws);
        console.log("closed websockettttttt!")
      }
    } catch (e){
      _this.reconnect(_this.ws);

    }
    return _this;
  },
  reconnect: function () {
    var _this=this,
      lockReconnect = false;
    if (lockReconnect) return;
    lockReconnect = true;
    setTimeout(function () {     //没连接上会一直重连，设置延迟避免请求过多
      _this.connect();
      lockReconnect = false;
    }, 2000);
  },
  heartCheck: function () {
    var self = this;
    clearTimeout(self.timeoutObj);
    clearTimeout(self.serverTimeoutObj);
    self.timeoutObj = setTimeout(function () {
      //这里发送一个心跳，后端收到后，返回一个心跳消息，
      //onmessage拿到返回的心跳就说明连接正常

      if(self.ws.readyState!=3){
        console.log("传参了")
        self.ws.send("ping");
        // console.log("ping!");
        self.serverTimeoutObj = setTimeout(function () {//如果超过一定时间还没重置，说明后端主动断开了
          self.ws.close();     //如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
        }, self.timeout)
      }

    }, this.timeout)
  },
  close: function () {

    this.ws.close(3000, "强制关闭");
  }
}

export function randomString(len) {
  len = len || 32;
  let timestamp = new Date().getTime();
  let $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
  let maxPos = $chars.length;
  let randomStr = '';
  for (let i = 0; i < len; i++) {
    randomStr += $chars.charAt(Math.floor(Math.random() * maxPos));
  }
  return randomStr + timestamp;
}
export function getDate () {
  var nowDate = new Date();
  let date = {
      year: nowDate.getFullYear(),
      month: nowDate.getMonth() + 1,
      date: nowDate.getDate(),
  }
  if (date.month>= 1 && date.month <= 9) {
    date.month = "0" + date.month;
  }
  if (date.date>= 1 && date.date <= 9) {
    date.date = "0" + date.date;
  }
  return date.year+"-"+date.month+"-"+date.date;
}
// 处理二级制文件
export function exportZip (response, fileName) {
  let data = response.data;
  let fileReader = new FileReader();
  fileReader.onload = function() {
      try {
          let jsonData = JSON.parse(this.result);  // 说明是普通对象数据，后台转换失败
      if (jsonData.code) {
          that.$message.error(jsonData.message)
      }
      } catch (err) {   // 解析成对象失败，说明是正常的文件流
          const blob = new Blob([response.data], {type: 'application/zip'});
          const filename = response.headers['content-disposition'];
          const downloadElement = document.createElement('a');
          const href = window.URL.createObjectURL(blob); //创建下载的链接
          downloadElement.href = href;
          [downloadElement.download] = [fileName + '.zip'];
          document.body.appendChild(downloadElement);
          downloadElement.click(); //点击下载
          document.body.removeChild(downloadElement); //下载完成移除元素
          window.URL.revokeObjectURL(href); //释放blob对
      }
  };
  fileReader.readAsText(data)
}
