import Vue from 'vue'
import App from '@/App'
import router from '@/router'                 // api: https://github.com/vuejs/vue-router
import store from '@/store'                   // api: https://github.com/vuejs/vuex
import VueCookie from 'vue-cookie'            // api: https://github.com/alfhen/vue-cookie
import '@/element-ui'                         // api: https://github.com/ElemeFE/element
import '@/icons'                              // api: http://www.iconfont.cn/
import '@/element-ui-theme'
// import '@/assets/chinaSoftScss/index.scss' //中软样式
import '@/assets/scss/index.scss' //人保样式
import httpRequest from '@/utils/httpRequest' // api: https://github.com/axios/axios
import { isAuth } from '@/utils'
import cloneDeep from 'lodash/cloneDeep'
import moment from 'moment'
// 引入bus
import { bus } from '@/utils/bus.js'
Vue.prototype.$bus = bus;

// 引入jshint用于实现js自动补全提示

import jshint from "jshint";
//使用gojs
// import gojs from 'gojs';
// import gojs from '@/utils/go.js';
// Vue.prototype.go = gojs;
//使用codemirror
window.JSHINT = jshint.JSHINT;
import VueCodemirror from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'
// import base style
import 'codemirror/lib/codemirror.css'
// import more codemirror resource...
Vue.use(VueCodemirror, /* {
  options: { theme: 'base16-dark', ... },
  events: ['scroll', ...]
} */)
Vue.use(VueCookie)
Vue.config.productionTip = false

// 非生产环境, 适配mockjs模拟数据                 // api: https://github.com/nuysoft/Mock
if (process.env.NODE_ENV !== 'production') {
  require('@/mock')
}

Vue.prototype.$tableHeight = document.documentElement.clientHeight-270;

// Vue.prototype.$tableHeight = window.innerHeight-270;

// window.addEventListener('resize', () => {
//   Vue.prototype.$tableHeight = window.innerHeight-270;
//   console.log(Vue.prototype.$tableHeight)
// }, false);
Vue.prototype.$moment = moment
// 挂载全局
Vue.prototype.$http = httpRequest // ajax请求方法
Vue.prototype.isAuth = isAuth     // 权限方法

// 保存整站vuex本地储存初始状态
window.SITE_CONFIG['storeState'] = cloneDeep(store.state)

// 日期格式化过滤器注册
Vue.filter('dateformat', function(dataStr, pattern = 'YYYY-MM-DD') {
  if (dataStr) {
    return moment(dataStr).format(pattern)
  } else {
    return ''
  }
})
Vue.filter('monthdateformat', function(dataStr, pattern = 'YYYY-MM') {
  if (dataStr) {
    return moment(dataStr).format(pattern)
  } else {
    return ''
  }
})

Vue.filter('formatMoney', function(num) {
  return utils.formatMoney(num, 4)
})
Vue.filter('datetimeformat', function(
  dataStr,
  pattern = 'YYYY-MM-DD HH:mm:ss'
) {
  if (dataStr) {
    return moment(dataStr).format(pattern)
  } else {
    return ''
  }
})
Vue.filter('numberToChinese', function(integer) {
  return utils.numberToChinese(integer)
})
Vue.filter('stateTranslate', function(code) {
  return utils.stateTranslate(code)
})
Vue.filter('datetoMonth', function(date) {
  return utils.datetoMonth(date)
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
