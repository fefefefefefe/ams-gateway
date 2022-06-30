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
                @change="number()" :disabled="show" 
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
    // 添加
    saveCedeData(sortUuid) {
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
        alert( "请输入类型描述");
        return false;
      }
      this.formCodeData.dataSortId = sortUuid;
       this.$http({
            url:this.$http.adornUrl("/baseCodeInfo/add"),
            method: 'post',
            data: this.$http.adornData({
                dataSortId: this.formCodeData.dataSortId,
                codeId:this.formCodeData.codeId,
                codeIndex: this.formCodeData.codeIndex,
                codeName: this.formCodeData.codeName,
                codeDesc: this.formCodeData.codeDesc,
                codeValue: this.formCodeData.codeValue,
                parentCodeId: this.formCodeData.parentCodeId,
                delTag: "0"
            })
            //   this.formOpera
      }).then(({data}) => {
        if (data.code === 200) {
            this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                    this.reset();
                    this.$emit("closeMain");
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
    editCodeData(sortUuid) {
      if (
        this.formCodeData.codeName == undefined ||
        this.formCodeData.codeName == null ||
        this.formCodeData.codeName == ""
      ) {
        alert(2, "请输入类型名称");
        return false;
      }
      if (
        this.formCodeData.codeDesc == undefined ||
        this.formCodeData.codeDesc == null ||
        this.formCodeData.codeDesc == ""
      ) {
        alert(2, "请输入类型描述");
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
        this.formCodeData.dataSortId = sortUuid;
        this.formCodeData.codeValue = this.editCodeValue;
        this.$http({
          url:this.$http.adornUrl("/baseCodeInfo/updateByUuId"),
          method: 'post',
          data: this.$http.adornData({
              dataSortId: this.formCodeData.dataSortId,
              codeId:this.formCodeData.codeId,
              codeIndex: this.formCodeData.codeIndex,
              codeName: this.formCodeData.codeName,
              codeDesc: this.formCodeData.codeDesc,
              codeValue: this.formCodeData.codeValue,
              parentCodeId: this.formCodeData.parentCodeId,
              delTag: "0"
              })
          }).then(({data}) => {
            if (data.code === 200) {
                this.$message({
                    message: '修改成功',
                    type: 'success',
                    duration: 1500,
                    onClose: () => {
                        this.$emit("closeMain");
                        this.reset();
                    }
                })
            } else {
                this.$message.error(data.message)
            }
          }) .catch(function(error) {})
      });
    },
    reset() {
      this.formCodeData = {
        codeId: "",
        codeIndex: "",
        codeName: "",
        codeDesc: "",
        codeValue: "",
        parentCodeId: "",
        dataSortId: "",
        delTag: "0"
      };
    }
  },
  props: ["codeOperate"],
  mounted: function() {
    //异步加载
    if (this.codeOperate.length != 0) {
      this.formCodeData.codeId = this.codeOperate[0].codeId;
      this.formCodeData.codeName = this.codeOperate[0].codeName;
      this.formCodeData.codeDesc = this.codeOperate[0].codeDesc;
      this.formCodeData.codeValue = this.codeOperate[0].codeValue;
      this.editCodeValue = this.codeOperate[0].codeValue;
    }
  },
  watch: {
    codeOperate(data) {
      if (data.length === 1) {
        this.formCodeData.codeId = data[0].codeId;
        this.formCodeData.codeName = data[0].codeName;
        this.formCodeData.codeDesc = data[0].codeDesc;
        this.formCodeData.codeValue = data[0].codeValue;
        this.editCodeValue = this.codeOperate[0].codeValue;
      } else {
        this.formCodeData = {
          codeId: "",
          codeName: "",
          codeDesc: "",
          codeValue: ""
        };
      }
    }
  }
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