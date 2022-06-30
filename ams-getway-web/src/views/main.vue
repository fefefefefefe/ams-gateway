<template>
  <div :class="useMagnifier?'magnifier':''">
    <div
      class="site-wrapper"
      :class="{ 'site-sidebar--fold': sidebarFold }"
      v-loading.fullscreen.lock="loading"
      element-loading-text="拼命加载中">
      <!--<div-->
        <!--class="site-wrapper"-->
        <!--:class="{ 'site-sidebar&#45;&#45;fold': sidebarFold }"-->
       <!--&gt;-->
      <template v-if="!loading">
        <main-navbar />
        <main-sidebar />
        <div class="site-content__wrapper" :style="{ 'min-height': documentClientHeight + 'px' }">
          <main-content v-if="!$store.state.common.contentIsNeedRefresh" />
        </div>
      </template>
    </div>
  </div>
</template>

<script>
  import MainNavbar from './main-navbar'
  import MainSidebar from './main-sidebar'
  import MainContent from './main-content'
  export default {
    provide () {
      return {
        // 刷新
        refresh () {
          this.$store.commit('common/updateContentIsNeedRefresh', true)
          this.$nextTick(() => {
            this.$store.commit('common/updateContentIsNeedRefresh', false)
          })
        }
      }
    },
    data () {
      return {
        loading: true
      }
    },
    components: {
      MainNavbar,
      MainSidebar,
      MainContent
    },
    computed: {
      documentClientHeight: {
        get () { return this.$store.state.common.documentClientHeight },
        set (val) { this.$store.commit('common/updateDocumentClientHeight', val) }
      },
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold }
      },
      userId: {
        get () { return this.$store.state.user.id },
        set (val) { this.$store.commit('user/updateId', val) }
      },
      userName: {
        get () { return this.$store.state.user.name },
        set (val) { this.$store.commit('user/updateName', val) }
      },
      useMagnifier: {
        get () { return this.$store.state.common.useMagnifier }
      }
    },
    created () {
      this.getUserInfo()
    },
    mounted () {
      this.resetDocumentClientHeight();

    },
    methods: {
      // 重置窗口可视高度
      resetDocumentClientHeight () {
        this.documentClientHeight = document.documentElement['clientHeight']
        window.onresize = () => {
          this.documentClientHeight = document.documentElement['clientHeight']
        }
      },
      // 获取当前管理员信息
      getUserInfo () {
        this.$http({
          url: this.$http.adornUrl('/sys/currentUser'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.loading = false
            this.userId = data.result.userId;
            this.userName = data.result.userName;
            sessionStorage.setItem("userId",this.userId)
            sessionStorage.setItem("userName",this.userName)
          }
        })
      }
    }
  }
</script>

