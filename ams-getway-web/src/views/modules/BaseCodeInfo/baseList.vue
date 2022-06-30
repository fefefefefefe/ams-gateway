 <!-- 字典管理 -->
<template>
    <div class="baseList">
        <el-card class="box-card">
            <div slot="header" class="clearfix">
                <span class="title">查询条件</span>
                <div class="el-icon-top" @click="test()" v-if="disp"></div>
                <div class="el-icon-bottom" @click="test()" v-if="!disp"></div>
            </div>
            <!-- <el-collapse-transition> -->
                <div class="select-div" v-show="disp">
                    <el-form :inline="true" :model="form">
                    <el-form-item prop="dataSortName" label="代码类别名称：">
                        <el-input v-model="form.dataSortName"></el-input>
                    </el-form-item>
                    <el-form-item prop="dataSortValue" label="代码类别编码">
                        <el-input v-model="form.dataSortValue"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="clearForm()">重置</el-button>
                        <el-button type="primary" @click="query()">查询</el-button>
                    </el-form-item>
                    </el-form>
                 </div>
            <!-- </el-collapse-transition> -->
        </el-card>
        <!-- <div class="down-content"> -->
        <el-card class="box-card">
            <div slot="header" class="clearfix">
                <span class="title">查询结构列表</span>
            </div>
        <div>
          <el-table
            ref="multipleTable"
            :data="tableData"
            tooltip-effect="dark"
            @selection-change="handleSelectionChange"
            :height="tableHeight - 80"
            border
          >
            <el-table-column type="selection" width="45"></el-table-column>
            <el-table-column label="序号" type="index" align="center" width="50"></el-table-column>
            <el-table-column prop="dataSortName" label="代码类别名称" width="300">
              <template slot-scope="scope">
                <el-button type="text"
                  @click="toBaseInfo(scope.row)"
                  :underline="false"
                >{{scope.row.dataSortName}}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="dataSortValue" label="代码类别编码" width="180"></el-table-column>
            <el-table-column prop="dataSortDesc" label="代码类别描述"></el-table-column>
            <el-table-column prop="extendTag" label="展现形式" width="180">
              <template slot-scope="scope">
                <div v-if="scope.row.extendTag =='1'">树状</div>
                <div v-if="scope.row.extendTag =='0'">列表</div>
              </template>
            </el-table-column>
          </el-table>
          <div class="operate-div">
            <DataSortOperate
              ref="dataSortOperate"
              :dataOperate="multipleSelection"
              @closeMain="query()"
            ></DataSortOperate>
            <div class="divBtn">
              <el-button
                type="primary"
                class="el_header_button"
                @click="saveBaseData"
              >添加</el-button>
              <el-button
                type="primary"
                class="el_header_button"
                @click="editBaseData"
                v-if="this.editTag!=1"
              >修改</el-button> 
              <!-- <el-button
                type="primary"
                size="mini"
                class="el_header_button"
                @click="delBaseData"
                v-if="this.editTag!=1"
              >删除</el-button>  -->
              <el-button type="primary" class="el_header_button" @click="resetForm">重置</el-button>
            </div>
          </div>
          <el-dialog title="代码类别详情" :close-on-click-modal="false" width="80%" :modal-append-to-body="false" :visible.sync="addOrUpdateVisible">
             <BaseCodeInfo v-if="addOrUpdateVisible" @close="closeImportDrawer" @ok="ImportSucceed" :dataSortIds="dataSortIds" :dataSortNames="dataSortNames" :editTags="editTags"></BaseCodeInfo>
          </el-dialog>
           <el-dialog title="代码类别详情" :close-on-click-modal="false" append-to-body width="80%" :modal-append-to-body="false" :visible.sync="BaseTreeInfoVisible">
             <BaseCodeTreeInfo v-if="BaseTreeInfoVisible" @close="closeImportDrawer" @ok="ImportSucceed"  @BaseCodeOne="BaseCodeOne" :dataSortId="dataSortIds" :dataSortName="dataSortNames" :editTag="editTags"></BaseCodeTreeInfo>
          </el-dialog>
        </div>
      </el-card>
      
    </div>
</template>
<script>
import DataSortOperate from './component/DataSortOperate.vue';
import BaseCodeInfo from './component/BaseCodeInfo.vue'
import BaseCodeTreeInfo from './component/BaseCodeTreeInfo.vue'
export default {
    name:'baseList',
    components: {
       DataSortOperate,BaseCodeInfo,BaseCodeTreeInfo
    },
    data() {
        return {
        disp: true,
        screenHeight: document.documentElement.clientHeight,
        form: {
            dataSortName: '',
            dataSortValue: ''
        },
        tableData: [],
        multipleSelection: '',
        editTag: "",
        addOrUpdateVisible: false,
        BaseTreeInfoVisible:false,
        dataSortIds:'',
        dataSortNames:'',
        editTags:'',
        };
    },
    computed:{
       tableHeight: {
        get () { return this.$store.state.common.tableHeight}
        }
    },
    methods:{
      BaseCodeOne(){
           this.BaseTreeInfoVisible = false
      },
        test() {
            this.disp = !this.disp;
        },
        handleSelectionChange(val) {
        this.multipleSelection = val;
        for(var i =0;i<this.multipleSelection.length;i++){
             this.editTag = this.multipleSelection[i].editTag;
        }
        // this.editTag = this.multipleSelection[0].editTag;
        },
        toBaseInfo(row) { 
            if (row.extendTag == 0) {
                this.addOrUpdateVisible = true,
                this.dataSortIds = row.dataSortId
                this.dataSortNames = row.dataSortName
                this.editTags = row.editTag
                // this.$router.push({
                //     path:`/component/BaseCodeInfo/${row.dataSortId}/${row.dataSortName}`,
                //     query: { editTag: row.editTag }
                // });
            } else {
                this.$store.dispatch("setDataSortId", row.dataSortId);
                this.$store.dispatch("setDataSortName", row.dataSortName);
                // this.$store.dispatch("setEditTag", row.editTag);
                // this.BaseTreeInfoVisible = true
                this.$router.push({
                path: "/component/BaseCodeTreeInfo",
                query: { editTag: row.editTag }
                });
            }
        },
        clearForm() {
            this.form = {};
        },
        // 查询
        query() {
            this.$http({
                url:this.$http.adornUrl('/baseDataSortInfo/selectPage'),
                method: 'get',
                params: this.$http.adornParams({
                    dataSortName: this.form.dataSortName,
                    dataSortValue: this.form.dataSortValue
                })
            }).then(({data}) => {
                this.tableData = data.result.records
                })
                .catch(function(error) {
                console.log(error);
                });
        },
        // 添加
        saveBaseData() {
            // 调用子路由方法
            this.$refs.dataSortOperate.saveBaseData();
        },
        // 修改
        editBaseData() {
            if (this.multipleSelection.length != 1) {
                this.$notify.error({
                title: "错误",
                message: "请选中一条数据进行修改",
                position: "bottom-right"
                });
                return false;
            }
            // 调用子路由方法
            this.$refs.dataSortOperate.editBaseData();
        },
            // 删除
            // delBaseData() {
            //   if (this.multipleSelection.length < 1) {
            //     this.$notify.error({
            //       title: "错误",
            //       message: "请选中一条以上数据进行删除",
            //       position: "bottom-right"
            //     });
            //     return false;
            //   }
            //   this.$confirm("此操作将永久删除, 是否继续?", "提示", {
            //     confirmButtonText: "确定",
            //     cancelButtonText: "取消",
            //     type: "warning"
            //   })
            //     .then(() => {
            //       this.axios
            //         .post(
            //           "/audit/zhBaseDataSortInfo/pd/zhBaseDataSortInfo/delete",
            //           this.multipleSelection
            //         )
            //         .then(response => {
            //           console.log(response);
            //           this.query();
            //         })
            //         .catch(function(error) {
            //           console.log(error);
            //         });
            //     })
            //     .catch(() => {
            //       this.$message({
            //         type: "info",
            //         message: "已取消删除"
            //       });
            //     });
            // },
        // 重置编辑页表单
        resetForm() {
          // 调用子路由
          this.$refs.dataSortOperate.reset();
        },
        //关闭弹框
        ImportSucceed(){this.closeImportDrawer() },
        closeImportDrawer(){this.addOrUpdateVisible = false},
    },
    mounted() {
      this.query();
    },

}
</script>
<style scoped>
/* .select-div {
  text-align: left;
} */
.down-content {
  margin-top: 50px;
}
.operate-div {
  margin-top: 10px;
}
.divBtn {
  text-align: center;
}
/* .el_header_button {
  background: #6c6d70;
  height: 24px;
  padding: 0px 15px;
  line-height: 23px;
  border-radius: 4px;
  border-color: #909399;
} */
.el_header_button_reset {
  background: #ffffff;
  color: #39475b;
}
.title {
  font-family: PingFangSC-Medium;
  font-size: 14px;
  color: #42444d;
  font-weight: 600;
}
</style>