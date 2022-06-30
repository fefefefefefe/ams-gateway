<template>
  <div class="boxTree">
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
          border 
        >
          <el-table-column type="selection" width="45"></el-table-column>
          <el-table-column type="index" label="序号" width="60"></el-table-column>
          <el-table-column prop="codeName" :label="dataSortName+'类型名称'" width="300"></el-table-column>
          <el-table-column prop="codeValue" :label="dataSortName+'类型编码'" width="180"></el-table-column>
          <el-table-column prop="codeDesc" :label="dataSortName+'类型描述'"></el-table-column>
        </el-table>
        <div class="operate-div">
          <BaseCodeTreeOperate
            ref="baseCodeOperate"
            :codeOperate="seleteLengthData"
            @closeMain="query" @BaseCodeTreeTwo="BaseCodeTreeTwo"
          ></BaseCodeTreeOperate>
          <div class="divBtn">
            <el-button
              type="primary"
              class="el_divBtn_button"
              @click="saveCodeData"
              v-if="this.editTag!=1"
            >添加</el-button>
            <el-button
              type="primary"
              class="el_divBtn_button"
              @click="editCodeData"
              v-if="this.editTag!=1"
            >修改</el-button>
            <el-button
              type="primary"
              class="el_divBtn_button"
              @click="delCodeData"
              v-if="this.editTag!=1"
            >删除</el-button>
            <el-button
              type="primary"
              class="el_divBtn_button"
              @click="resetForm"
              v-if="this.editTag!=1"
            >重置</el-button>
            <el-button type="primary" class="el_divBtn_button" @click="returnList">返回</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapState } from "vuex";
import BaseCodeTreeOperate from "./BaseCodeTreeOperate.vue";
export default {
  components: {
    BaseCodeTreeOperate
  },
  data() {
    return {
      dataSortId: "",
      dataSortName: "",
      codeId: "",
      parentCodeId: "",
      tableData: [],
      seleteLengthData: [],
      editTag: ""
    };
  },
  computed: {
    // ...mapState({
    //   baseCodes: state => state.datasort.baseCodes
    // }),
    baseCode(){
          return this.$store.state.datasort.baseCodes
    },
    tableHeight: {
      get() {
        return this.$store.state.common.tableHeight;
      }
    }
  },
  methods: {
    BaseCodeTreeTwo(){

    },
    handleSelectionChange(val) {
      this.seleteLengthData = val;
    },
    saveCodeData() {
      // 调用子路由方法
      var sortId = this.dataSortId;
      var parentCodeId = this.parentCodeId;
      this.$refs.baseCodeOperate.saveCedeData(sortId, parentCodeId);
    },
    // 修改
    editCodeData() {
      var sortId = this.dataSortId;
      var parentCodeId = this.parentCodeId;
      if (this.seleteLengthData.length != 1) {
        this.$notify.error({
          title: "错误",
          message: "请选中一条数据进行修改",
          position: "bottom-right"
        });
        return false;
      }
      this.$refs.baseCodeOperate.editCodeData(sortId, parentCodeId);
    },
    // 删除
    delCodeData() {
      if (this.seleteLengthData.length < 1) {
        this.$notify.error({
          title: "错误",
          message: "请选中一条以上数据进行删除",
          position: "bottom-right"
        });
        return false;
      }
      this.$http({
          url:this.$http.adornUrl('/baseCodeInfo/updateBatchById'),
          method: 'delete',
          data:this.seleteLengthData
      }).then(({data}) => {
         if (data && data.code === 200) {
              this.$message({
                message: "删除成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.query();
                  // this.$emit('loadnode')
                }
              });
            } else {
              this.$message.error(data.msg);
            }
      })
    },
    // 查询列表
    query() {
    let baseCode = this.$store.state.datasort.baseCodes;
    this.parentCodeId = baseCode.codeId;
    this.dataSortName = baseCode.codeName;
    this.dataSortId = baseCode.dataSortId;
       this.$http({
          url:this.$http.adornUrl('/baseCodeInfo/selectPage'),
          method: 'get',
          params: this.$http.adornParams({
              parentCodeId: this.parentCodeId,
          })
      }).then(({data}) => {
        this.tableData = data.result.records
        this.dataListLoading = false;
      }).catch(function(error) {
        console.log(error);
      });
    },
    resetForm() {
      this.$refs.baseCodeOperate.resetForm();
    },
    returnList() {
      this.$router.push({
        path: `/baseList`
      });
      // this.$emit('BaseCodeMethod')
    }
  },
  mounted(){
    //字段设置
    let baseCode = this.$store.state.datasort.baseCodes;
    baseCode = baseCode;
    this.dataSortName = baseCode.codeName;
    this.dataSortId = baseCode.dataSortId;
    this.parentCodeId = baseCode.codeId;
    this.editTag = this.$route.query.editTag;;
  },
  watch: {
    baseCode:{
      handler: function(newval,old) {
        var baseCode = newval;
        this.dataSortName = baseCode.codeName;
        this.dataSortId = baseCode.dataSortId;
        this.parentCodeId = baseCode.codeId;
        this.editTag = this.$route.query.editTag;;
        this.query();
      },
      deep: true
    }
  }
};
</script>

<style scoped>
.operate-div {
  margin-top: 20px;
}
.divBtn {
  text-align: center;
}
.title {
  font-family: PingFangSC-Medium;
  font-size: 14px;
  color: #42444d;
  font-weight: 600;
}
/* .el_divBtn_button {
  background: #6c6d70;
  height: 24px;
  padding: 0px 15px;
  line-height: 23px;
  border-radius: 4px;
  border-color: #909399;
} */
.boxTree{
    height: 550px;
    min-height: 550px;
    overflow: auto;
}
</style>