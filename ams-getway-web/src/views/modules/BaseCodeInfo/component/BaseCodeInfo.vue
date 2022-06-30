<template>
  <div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span class="title">查询结构列表</span>
      </div>
      <div>
        <el-table
          :data="tableData"
          tooltip-effect="dark"
          ref="multipleCheck"
          @selection-change="checkData"
          border
        >
          <el-table-column type="selection" width="45"></el-table-column>
          <el-table-column type="index" label="序号" width="60"></el-table-column>
          <el-table-column prop="codeUuid" v-if="show=false"></el-table-column>
          <el-table-column prop="codeName" :label="dataSortName+'类型名称'" width="300"></el-table-column>
          <el-table-column prop="codeValue" :label="dataSortName+'类型编码'" width="200"></el-table-column>
          <el-table-column prop="codeDesc" :label="dataSortName+'类型描述'"></el-table-column>
        </el-table>
        <div class="operate-div">
          <BaseCodeOperate
            ref="baseCodeOperate"
            @closeMain="query()"
            :codeOperate="seleteLengthData"
          ></BaseCodeOperate>
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
import BaseCodeOperate from './BaseCodeOperate.vue';
export default {
  components: {
    BaseCodeOperate
  },
  props: { 
    dataSortIds: { type: String } ,
    dataSortNames:{type: String},
    editTags:{type: Number}
  },
  data() {
    return {
      dataSortId: "",
      dataSortName: "",
      parentCodeUuid: "",
      editTag: "",
      tableData: [],
      seleteLengthData: [],
      dataListLoading:false
    };
  },
  methods: {
    checkData(value) {
      this.seleteLengthData = value;
    },
    // 添加
    saveCodeData() {
      // 调用子路由方法
      var sortUuid = this.dataSortId;
      this.$refs.baseCodeOperate.saveCedeData(sortUuid);
    },
    // 修改
    editCodeData() {
      var sortUuid = this.dataSortId;
      if (this.seleteLengthData.length != 1) {
        this.$notify.error({
          title: "错误",
          message: "请选中一条数据进行修改",
          position: "bottom-right"
        });
        return false;
      }
      this.$refs.baseCodeOperate.editCodeData(sortUuid);
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
                }
              });
            } else {
              this.$message.error(data.msg);
            }
      })
    },
    // 查询列表
    query() {
      this.dataListLoading = true;
      this.$http({
          url:this.$http.adornUrl('/baseCodeInfo/selectPage'),
          method: 'get',
          params: this.$http.adornParams({
              dataSortId: this.dataSortId,
          })
      }).then(({data}) => {
        this.tableData = data.result.records
        this.dataListLoading = false;
      }).catch(function(error) {
        console.log(error);
      });
    },
    resetForm() {
      // 调用子路由
      this.$refs.baseCodeOperate.reset();
    },
    returnList() {
        this.$emit('close')
      // this.$router.push({
      //   path: `/baseList`
      // });
    }
  },
  mounted: function() {
    this.dataSortId = this.dataSortIds;
    this.dataSortName = this.dataSortNames;
    this.parentCodeUuid = this.dataSortIds;
    this.editTag = this.editTags;
    this.query();
  },
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
</style>