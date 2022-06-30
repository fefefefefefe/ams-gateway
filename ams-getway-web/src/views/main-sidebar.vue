<template>
  <aside class="site-sidebar" :class="'site-sidebar--' + sidebarLayoutSkin">
    <div class="site-sidebar__inner">
      <el-menu
        :default-active="menuActiveName || 'home'"
        :collapse="sidebarFold"
        :collapseTransition="false"
        class="site-sidebar__menu">
        <el-menu-item index="home" @click="$router.push({ name: 'home' })">
          <icon-svg name="shouye" class="site-sidebar__menu-icon"></icon-svg>
          <span slot="title">首页</span>
        </el-menu-item>
        <!--<el-submenu index="sq">-->
          <!--<template slot="title">-->
            <!--<icon-svg name="shoucang" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span>授权管理</span>-->
          <!--</template>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'authoriza' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">功能授权</span>-->
          <!--</el-menu-item>-->
        <!--</el-submenu>-->
        <!--<el-submenu index="sj">-->
          <!--<template slot="title">-->
            <!--<icon-svg name="shoucang" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span>数据管理</span>-->
          <!--</template>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'rule' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">规则管理</span>-->
          <!--</el-menu-item>-->
        <!--</el-submenu>-->
        <!--<el-menu-item index="lx" @click="$router.push({ name: 'projectList' })">-->
          <!--<icon-svg name="shouye" class="site-sidebar__menu-icon"></icon-svg>-->
          <!--<span slot="title">立项管理</span>-->
        <!--</el-menu-item>-->
        <!--<el-menu-item index="lx" @click="$router.push({ name: 'codemirror' })">-->
          <!--<icon-svg name="shouye" class="site-sidebar__menu-icon"></icon-svg>-->
          <!--<span slot="title">codemirror Demo</span>-->
        <!--</el-menu-item>-->

        <!--<el-submenu index="dataFirstExplore">-->
          <!--<template slot="title">-->
            <!--<icon-svg name="shoucang" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span>数据初探</span>-->
          <!--</template>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'ruleConfig' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">初探规则配置</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'ruleMoniter' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">初探规则监控</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'detailExport' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">结果明细导出</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'reportExport' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">初探报告导出</span>-->
          <!--</el-menu-item>-->
        <!--</el-submenu>-->
        <!--<el-submenu index="sc">-->
          <!--<template slot="title">-->
            <!--<icon-svg name="shoucang" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span>二次数据筛查</span>-->
          <!--</template>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'auditRuleConfig' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">审核规则配置</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'auditRuleMonitoring' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">审核执行监控</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'resultDetailsExport' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">结果明细导出</span>-->
          <!--</el-menu-item>-->
        <!--</el-submenu>-->

        <!--<el-menu-item index="lx" @click="$router.push({ name: 'projectList' })">-->
          <!--<icon-svg name="shouye" class="site-sidebar__menu-icon"></icon-svg>-->
          <!--<span slot="title">立项管理</span>-->
        <!--</el-menu-item>-->
        <!--<el-submenu index="demo">-->
          <!--<template slot="title">-->
            <!--<icon-svg name="shoucang" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span>demo</span>-->
          <!--</template>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'demo-echarts' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">echarts</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-ueditor" @click="$router.push({ name: 'demo-ueditor' })">-->
            <!--<icon-svg name="editor" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">ueditor</span>-->
          <!--</el-menu-item>-->
        <!--</el-submenu>-->
        <!--<el-submenu index="sign">-->
          <!--<template slot="title">-->
            <!--<icon-svg name="log" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span>台账管理</span>-->
          <!--</template>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'railway' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">铁路集装箱</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'importedOre' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">进口矿</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'importedOre' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">喷吹煤</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'sinterCoal' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">烧结煤</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'coke' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">焦炭</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'scrapIron' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">外购废铁</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'limestone' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">灰石</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'waterSlag' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">水渣</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'steelSlag' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">钢渣</span>-->
          <!--</el-menu-item>-->
          <!--<el-menu-item index="demo-echarts" @click="$router.push({ name: 'steel' })">-->
            <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
            <!--<span slot="title">钢材</span>-->
          <!--</el-menu-item>-->
        <!--</el-submenu>-->
        <!--<el-menu-item index="home" @click="$router.push({ name: 'summary' })">-->
          <!--<icon-svg name="tubiao" class="site-sidebar__menu-icon"></icon-svg>-->
          <!--<span slot="title">汇总表</span>-->
        <!--</el-menu-item>-->
        <sub-menu
          v-for="menu in menuList"
          :key="menu.menuId"
          :menu="menu"
          :dynamicMenuRoutes="dynamicMenuRoutes">
        </sub-menu>
      </el-menu>
      <div class="new-zhedie"  @click="sidebarFold = !sidebarFold">
        <i :class="!sidebarFold?'el-icon-arrow-left':'el-icon-arrow-right'"></i>
      </div>
    </div>
  </aside>
</template>

<script>
  import SubMenu from './main-sidebar-sub-menu'
  import {isURL} from '@/utils/validate'

  export default {
    data() {
      return {
        dynamicMenuRoutes: []
      }
    },
    components: {
      SubMenu
    },
    computed: {

      sidebarLayoutSkin: {
        get() {
          return this.$store.state.common.sidebarLayoutSkin
        }
      },
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold },
        set (val) { this.$store.commit('common/updateSidebarFold', val) }
      },
      menuList: {
        get() {
          return this.$store.state.common.menuList
        },
        set(val) {
          this.$store.commit('common/updateMenuList', val)
        }
      },
      menuActiveName: {
        get() {
          return this.$store.state.common.menuActiveName
        },
        set(val) {
          this.$store.commit('common/updateMenuActiveName', val)
        }
      },
      mainTabs: {
        get() {
          return this.$store.state.common.mainTabs
        },
        set(val) {
          this.$store.commit('common/updateMainTabs', val)
        }
      },
      mainTabsActiveName: {
        get() {
          return this.$store.state.common.mainTabsActiveName
        },
        set(val) {
          this.$store.commit('common/updateMainTabsActiveName', val)
        }
      }
    },
    watch: {
      $route: 'routeHandle'
    },
    created() {
      this.menuList = JSON.parse(sessionStorage.getItem('menuList') || '[]')
      this.dynamicMenuRoutes = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
      this.routeHandle(this.$route)
    },
    methods: {
      // 路由操作
      routeHandle(route) {
        if (route.meta.isTab) {
          // tab选中, 不存在先添加
          var tab = this.mainTabs.filter(item => item.name === route.name)[0]
          if (!tab) {
            if (route.meta.isDynamic) {
              route = this.dynamicMenuRoutes.filter(item => item.name === route.name)[0]
              if (!route) {
                return console.error('未能找到可用标签页!')
              }
            }
            tab = {
              menuId: route.meta.menuId || route.name,
              name: route.name,
              title: route.meta.title,
              type: isURL(route.meta.iframeUrl) ? 'iframe' : 'module',
              iframeUrl: route.meta.iframeUrl || '',
              params: route.params,
              query: route.query
            }
            this.mainTabs = this.mainTabs.concat(tab)
          }
          this.menuActiveName = tab.menuId + ''
          this.mainTabsActiveName = tab.name
        }
      }
    }
  }
</script>
