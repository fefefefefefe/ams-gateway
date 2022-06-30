<template>
  <div> 
    <div class="title-div">
      <span>操作</span>
    </div>
    <div>
      <el-form :model="formCodeData" :rules="rules" label-width="150px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="类型名称" prop="codeName" label-width="150px">
              <el-input v-model="formCodeData.codeName" class="input"></el-input>
            </el-form-item>
            <el-form-item label="uuid" prop="codeId" v-if="show=false">
              <el-input v-model="formCodeData.codeId" class="input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型编码" prop="codeValue" label-width="150px">
              <el-input
                v-model="formCodeData.codeValue"
                placeholder="请输入4-10位的数字"
                class="input"
                @change="number()"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item label="类型描述" prop="codeDesc" label-width="150px">
            <el-input type="textarea" v-model="formCodeData.codeDesc"></el-input>
          </el-form-item>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      formCodeData: {
        codeId: "",
        codeIndex: "",
        codeName: "",
        codeDesc: "",
        codeValue: "",
        parentCodeId: "",
        dataSortId: "",
        delTag: "0"
      },
      editCodeValue: "",
      rules: {
        codeName: [
          { required: true, message: "请输入类型名称", trigger: "blur" }
        ],
        codeValue: [
          { required: true, message: "请输入类型编码", trigger: "blur" }
        ],
        codeDesc: [
          { required: true, message: "请填写类型描述", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    // 校验编码格式
    number() {
      var data = this.formCodeData.codeValue;
      let codeValue = new RegExp("^[0-9]{4,10}$").test(data);
      if (!codeValue) {
        this.$notify.error({
          title: "错误",
          message: "请输入4-10位的数字",
          position: "bottom-right"
        });
        this.formCodeData.codeValue = "";
      }
    },
    //重置
    resetForm() {
      this.formCodeData = {
        codeId: "",
        codeIndex: "",
        codeName: "",
        codeDesc: "",
        codeValue: "",
        parentCodeId: "",
        dataSortId: "",
        delTag: ""
      };
    },
    // 添加
    saveCedeData(sortId, parentCodeId) {
      if (
        this.formCodeData.codeName == undefined ||
        this.formCodeData.codeName == null ||
        this.formCodeData.codeName == ""
      ) {
        alert("请输入类型名称");
        return false;
      }
      if (
        this.formCodeData.codeValue == undefined ||
        this.formCodeData.codeValue == null ||
        this.formCodeData.codeValue == ""
      ) {
        alert("请输入类型编码");
        return false;
      }
      if (
        this.formCodeData.codeDesc == undefined ||
        this.formCodeData.codeDesc == null ||
        this.formCodeData.codeDesc == ""
      ) {
        alert("请输入类型描述");
        return false;
      }
      if (sortId === "" || sortId === null || sortId === undefined) {
        sortId = parentCodeId;
      }
      this.formCodeData.dataSortId = sortId;
      this.formCodeData.parentCodeId = parentCodeId;
      this.$http({
            url:this.$http.adornUrl("/baseCodeInfo/add"),
            method: 'post',
            data: this.$http.adornData({
                codeId: this.formCodeData.codeId,
                codeIndex:this.formCodeData.codeIndex,
                codeName:this.formCodeData.codeName,
                codeDesc: this.formCodeData.codeDesc,
                codeValue: this.formCodeData.codeValue,
                parentCodeId: this.formCodeData.parentCodeId,
                dataSortId: this.formCodeData.dataSortId,
                delTag: "0"
            })
      }).then(({data}) => {
        if (data.code === 200) {
            this.$message({
                message: '添加成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                    this.$emit("closeMain");
                    this.$emit('BaseCodeTreeTwo')
                    this.resetForm();
                }
            })
        } else {
            this.$message.error(data.message)
        }
        }) .catch(function(error) {
          console.log(error);
        });
    },
    // 修改
    editCodeData(sortId, parentCodeId) {
      if (
        this.formCodeData.codeName == undefined ||
        this.formCodeData.codeName == null ||
        this.formCodeData.codeName == ""
      ) {
        alert("请输入类型名称");
        return false;
      }
      if (
        this.formCodeData.codeDesc == undefined ||
        this.formCodeData.codeDesc == null ||
        this.formCodeData.codeDesc == ""
      ) {
        alert("请输入类型描述");
        return false;
      }
      this.$confirm(
        "不可以修改类型编码，编码将保持不变，是否确认修改其他信息?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
         this.$http({
            url:this.$http.adornUrl("/baseCodeInfo/updateByUuId"),
            method: 'post',
            data: this.$http.adornData({
                codeId: this.formCodeData.codeId,
                codeIndex:this.formCodeData.codeIndex,
                codeName:this.formCodeData.codeName,
                codeDesc: this.formCodeData.codeDesc,
                dataSortId: sortId,
                parentCodeId: parentCodeId,
                delTag: "0",
                codeValue:this.editCodeValue
            })
      }).then(({data}) => {
        if (data.code === 200) {
            this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                    this.$emit("closeMain");
                    this.$emit('BaseCodeTreeTwo')
                    this.resetForm();
                }
            })
        } else {
            this.$message.error(data.message)
        }
        }) .catch(function(error) {
          console.log(error);
        });
      });
    }
  },
  props: ["codeOperate"],
  watch: {
    codeOperate(data) {
      if (data.length === 1) {
        this.formCodeData.codeId = data[0].codeId;
        this.formCodeData.codeName = data[0].codeName;
        this.formCodeData.codeDesc = data[0].codeDesc;
        this.formCodeData.codeValue = data[0].codeValue;
        this.editCodeValue = data[0].codeValue;
      } else {
        this.resetForm();
      }
    }
  },
  computed: {
    tableHeight: {
      get() {
        return this.$store.state.common.tableHeight;
      }
    }
  },
};
</script>
<style scoped>
.title-div {
  text-align: left;
}
.input {
  width: 52%;
  float: left;
}
</style>