<template>
  <div class="mod-home home-new">

  </div>
</template>

<script>
  export default {
    components:{

    },
    data(){
      return{

      }
    },
    computed: {

    },
    watch: {

    },
    activated () {

    },
    methods: {

    }
  }
</script>

<style lang="scss" scoped>
  /* 4个模块的公共样式 */
  .projectInfo,.todoList,.collectionList,.dataList{
    font-size: 12px;
    border: 1px solid #eaedf1;
    border-radius: 20px;
    padding: 10px;
    overflow: hidden;
    /*模块标题 */
    .cardTitle{
      /*border-left: 5px solid #aa0f17;*/
      font-size: 14px;
      font-weight: 400;
      color: #1f2f3d;
      line-height: 16px;
      height: 16px;
      margin-bottom: 10px;
      .cardMore{
        float: right;
        font-size: 12px;
        /*color: #aa0f17;*/
        cursor: pointer;
      }
    }
  }
  .projectInfo,.todoList{
    height: 41vh;
  }
  .collectionList,.dataList{
    height: 38vh;
  }

  /*项目详情样式 */
  .projectInfo{
    .restore_content{
      padding-bottom: 8px;
      width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      .restore-label{
        margin-right: 10px;
        margin-left: 8px;
      }
    }
    .restore_desc{
      // width: 100%;
      //overflow: hidden;
      //text-overflow: ellipsis;
      // white-space: nowrap;
      padding-bottom: 8px;
      margin-left: 8px;
      display: inline-block;
      height: 106px;
      overflow: hidden;
      word-break:break-all;
      .restore-label{
        margin-right: 10px;
      }
      .restore-project-desc{
        line-height: 22px;
      }
    }
    .el-table{
      border-radius: 0;
    }
    /deep/ .el-table thead tr, /deep/ .el-table thead th {
      /*background: #aa0f17 !important;*/
      border-radius: 0;
      color: #ffffff;
    }
  }

  /*el-table样式 */
  .el-table{
    border: 0;
    padding: 0;
    font-size: 12px;
    .el-table__row {
      height: 30px !important;
    }
    /deep/ td, /deep/ th {
      padding: 3px;
    }
  }
  /*待办样式 */
  .todoList{
    /deep/ .el-table .el-table__header-wrapper {
      padding-top: 0px;
    }
    /deep/ .el-tabs__item {
      padding: 0 10px;
      height: 30px;
      line-height: 25px;
      font-size: 12px;
    }
  }
  /* 采集监控样式*/
  .collectionList,.dataList{
    /deep/ .el-button--mini, .el-button--mini.is-round,.el-button--text {
      padding: 5px 6px;
      border: 0;
      margin: 0;
    }
    .projectTable {
      .el-button--text {
        padding: 5px 2px;
      }
    }
    .el-button--text,.el-button--text:focus, .el-button--text:hover {
      /*color: #aa0f17;*/
      font-size: 11px ;
    }
    .cardMore .el-button:focus,.cardMore .el-button:hover {
      /*color: #aa0f17;*/
      font-weight: 400;
      background: #f7edf0;
    }
    .el-button--danger{
      /*color: #aa0f17;*/
      font-weight: 400;
      background: #f7edf0;
    }
    /deep/ .el-progress__text{
      font-size: 12px !important;
    }
  }

  .mod-home {
    margin-top: 0;
  }

</style>

