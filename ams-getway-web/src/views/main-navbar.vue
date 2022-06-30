<template>
  <nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
    <div class="site-navbar__header">
      <div class="navbar_header_new">
        <div class="inline-block navbar_header_new-title">
          <div class="inline-block navbar_header_new-img"></div>
          <!--<img src="../assets/img/home-logo.png" alt="">-->
          <!--<div class="navbar_header_new-titlename inline-block"><img src="../assets/img/home-logo-2.png" alt=""></div>-->
          <div class="navbar_header_new-titlename inline-block"></div>
        </div>
      </div>
      <!--<h1 class="site-navbar__brand" @click="$router.push({ name: 'home' })">-->
        <!--<a class="site-navbar__brand-lg" href="javascript:;">人保健康飞检系统</a>-->
        <!--&lt;!&ndash;<a class="site-navbar__brand-mini" href="javascript:;">人保</a>&ndash;&gt;-->
        <!--<a class="site-navbar__brand-mini" href="javascript:;">-->
          <!--<img class="mnb-img" src="../assets/img/mainNavBarLogo.png" alt="">-->
        <!--</a>-->
      <!--</h1>-->
    </div>
    <div class="site-navbar__body clearfix">
      <!--<el-menu-->
        <!--class="site-navbar__menu"-->
        <!--mode="horizontal">-->
        <!--<el-menu-item class="site-navbar__switch" index="0" @click="sidebarFold = !sidebarFold">-->
          <!--<icon-svg name="zhedie"></icon-svg>-->
        <!--</el-menu-item>-->
      <!--</el-menu>-->
      <!--<el-switch-->
        <!--class="btn-magnifier"-->
        <!--v-model="useMagnifier"-->
        <!--active-text="放大镜">-->
      <!--</el-switch>-->
      <el-menu
        class="site-navbar__menu site-navbar__menu--right"
        mode="horizontal">
        <!--<el-menu-item index="1" @click="$router.push({ name: 'theme' })">-->
          <!--<template slot="title">-->
            <!--<el-badge value="new">-->
              <!--<icon-svg name="shezhi" class="el-icon-setting"></icon-svg>-->
            <!--</el-badge>-->
          <!--</template>-->
        <!--</el-menu-item>-->
        <!--<el-menu-item index="2">-->
          <!--<el-badge value="hot">-->
            <!--<a href="https://www.renren.io/" target="_blank">官方社区</a>-->
          <!--</el-badge>-->
        <!--</el-menu-item>-->
        <!--<el-submenu index="3">-->
          <!--<template slot="title">Git源码</template>-->
          <!--<el-menu-item index="2-1"><a href="https://github.com/renrenio/renren-fast-vue" target="_blank">前端</a></el-menu-item>-->
          <!--<el-menu-item index="2-2"><a href="https://gitee.com/renrenio/renren-fast" target="_blank">后台</a></el-menu-item>-->
          <!--<el-menu-item index="2-3"><a href="https://gitee.com/renrenio/renren-generator" target="_blank">代码生成器</a></el-menu-item>-->
        <!--</el-submenu>-->

        <el-menu-item class="site-navbar__avatar" index="3">
          <el-dropdown :show-timeout="0" placement="bottom">
            <span class="el-dropdown-link">
              <img style="border-radius: 50%;border:1px solid #666" src="~@/assets/img/jindin.png" :alt="userName">{{ userName }}
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="updatePasswordHandle()">修改密码</el-dropdown-item>
              <el-dropdown-item @click.native="logoutHandle()">退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>
      </el-menu>
    </div>
    <!-- 弹窗, 修改密码 -->
    <update-password v-if="updatePassowrdVisible" ref="updatePassowrd"></update-password>
  </nav>
</template>

<script>
  import UpdatePassword from './main-navbar-update-password'
  import { clearLoginInfo } from '@/utils'
  export default {
    data () {
      return {
        updatePassowrdVisible: false,
        chatVisible: false,
        // projectCode: '',
        userId: sessionStorage.getItem("userId"),//当前用户id
      }
    },
    components: {
      UpdatePassword
    },
    computed: {
      projectCode:{
        get () {
          return this.$store.state.common.projectId||''
        },
        set (val) { this.$store.commit('common/updateProjectId', val) }
      },
      navbarLayoutType: {
        get () { return this.$store.state.common.navbarLayoutType }
      },
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold },
        set (val) { this.$store.commit('common/updateSidebarFold', val) }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      },
      userName: {
        get () { return this.$store.state.user.name }
      },
      projectList: {
        get () { return this.$store.state.common.projectList }
      },
      useMagnifier: {
        get () { return this.$store.state.common.useMagnifier },
        set (val) { this.$store.commit('common/updateUseMagnifier', val) }
      }
    },
    created () {
    },
    mounted(){},
    methods: {
      // 修改密码
      updatePasswordHandle () {
        this.updatePassowrdVisible = true
        this.$nextTick(() => {
          this.$refs.updatePassowrd.init()
        })
      },
      // 退出
      logoutHandle () {
        this.$confirm(`确定进行[退出]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/sys/logout'),
            method: 'post',
            data: this.$http.adornData()
          }).then(({data}) => {
            if (data && data.code === 200) {
              clearLoginInfo()
              this.$router.push({ name: 'login' })
            }
          })
        }).catch(() => {})
      },
    }
  }
</script>
<style scoped>
  .mychat{
    font-size: 30px;
    font-weight: bold;
    line-height: 50px;
    cursor: pointer;
    float: right;
  }
  .project-box {
    height: 50px;
    line-height: 50px;
    float: right;
    width:40%
  }
  .project-box .el-select{
    width: 100%;

  }
  .project-box >>>.el-input input{
    border:none;
    text-align: right;
  }
  .btn-magnifier{
    height: 50px;
  }
  .mnb-img{
    width: 40px;
    height:40px;
    margin-top: 5px;
  }
  .project-select >>>.el-select-dropdown__list,.project-select >>>.el-select-dropdown{
    width: 300px!important;
  }
</style>
