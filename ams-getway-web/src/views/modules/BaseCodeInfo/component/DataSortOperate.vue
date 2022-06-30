<template>
  <div>
    <div class="title-div">
      <span>操作</span>
    </div>
    <div>
      <el-form :model="formOpera" :rules="rules" ref="formOpera" label-width="150px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="代码类型名称" prop="dataSortName" label-width="150px">
              <el-input v-model="formOpera.dataSortName" class="input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码类型编码" prop="dataSortValue" label-width="150px">
              <el-input
                v-model="formOpera.dataSortValue"
                placeholder="请输入4-10位的数字"
                class="input"
                @change="number()" 
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否树形显示" prop="extendTag" label-width="150px">
              <el-select v-model="formOpera.extendTag" class="input" :disabled="show">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="0"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item label="代码类型描述" prop="dataSortDesc" label-width="150px">
            <el-input type="textarea" v-model="formOpera.dataSortDesc" class="textarea"></el-input>
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
      show: false,
      formOpera: {
        dataSortId: "",
        dataSortName: "",
        dataSortValue: "",
        dataSortDesc: "",
        extendTag: "0",
        editTag: "0"
      },
      editDataSortValue: "",
      rules: {
        dataSortName: [
          { required: true, message: "请输入类型名称", trigger: "blur" }
        ],
        dataSortValue: [
          { required: true, message: "请输入类型编码", trigger: "blur" }
        ],
        dataSortDesc: [
          { required: true, message: "请输入类型描述", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    // 校验编码格式
    number() {
      var data = this.formOpera.dataSortValue;
      let dataSortValue = new RegExp("^[0-9]{4,10}$").test(data);
      if (!dataSortValue) {
        this.$notify.error({
          title: "错误",
          message: "请输入4-10位的数字",
          position: "bottom-right"
        });
        this.formOpera.dataSortValue = "";
      }
    },
    saveBaseData() {
      if (
        this.formOpera.dataSortName == undefined ||
        this.formOpera.dataSortName == null ||
        this.formOpera.dataSortName == ""
      ) {
        alert("请输入代码类型名称");
        return false;
      }
      if (
        this.formOpera.dataSortValue == undefined ||
        this.formOpera.dataSortValue == null ||
        this.formOpera.dataSortValue == ""
      ) {
        alert("请输入代码类型编码");
        return false;
      }
      if (
        this.formOpera.dataSortDesc == undefined ||
        this.formOpera.dataSortDesc == null ||
        this.formOpera.dataSortDesc == ""
      ) {
        alert("请输入代码类型描述");
        return false;
      }
      this.$http({
            url:this.$http.adornUrl("/baseDataSortInfo/add"),
            method: 'post',
            data: this.$http.adornData({
                dataSortId: this.formOpera.dataSortId,
                dataSortName:this.formOpera.dataSortName,
                dataSortValue:this.formOpera.dataSortValue,
                dataSortDesc: this.formOpera.dataSortDesc,
                extendTag: this.formOpera.extendTag,
                editTag: "0"
            })
            //   this.formOpera
      }).then(({data}) => {
        if (data.code === 200) {
            this.$message({
                message: '操作成功',
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
        }) .catch(function(error) {
          console.log(error);
        });
    },
    editBaseData() {
      if (
        this.formOpera.dataSortName == undefined ||
        this.formOpera.dataSortName == null ||
        this.formOpera.dataSortName == ""
      ) {
        this.$alert(2, "请输入代码类型名称");
        return false;
      }
      if (
        this.formOpera.dataSortDesc == undefined ||
        this.formOpera.dataSortDesc == null ||
        this.formOpera.dataSortDesc == ""
      ) {
        alert(2, "请输入代码类型描述");
        return false;
      }
      this.$confirm(
        "不可以修改代码类型编码，编码将保持不变，是否确认修改其他信息?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        this.formOpera.dataSortValue = this.editDataSortValue;
        this.$http({
            url:this.$http.adornUrl("/baseDataSortInfo/updateByUuId"),
            method: 'post',
            data: this.$http.adornData({
                dataSortId: this.formOpera.dataSortId,
                dataSortName:this.formOpera.dataSortName,
                dataSortValue:this.formOpera.dataSortValue,
                dataSortDesc: this.formOpera.dataSortDesc,
                extendTag: "0",
                editTag: "0"
            })
        }).then(({data}) => {
            if(data.code === 200){
                this.$message({
                    message: '修改成功',
                    type: 'success',
                    duration: 1500,
                    onClose: () => {
                        this.reset();
                        this.$emit("closeMain");
                    }
                })
              }
          })
          .catch(function(error) {
            console.log(error);
          });
      });
    },
    // 重置
    reset() {
      this.formOpera = {
        dataSortName: "",
        dataSortValue: "",
        dataSortDesc: "",
        extendTag: "0",
        editTag: "0"
      };
      this.show = false;
    },
    checkTag() {
      if (this.formOpera.dataSortId != null) {
        this.show = true;
      }
    }
  },
  props: ["dataOperate"],
  mounted(){
    //异步加载
    if (this.dataOperate.length != 0) {
      this.formOpera.dataSortId = this.dataOperate[0].dataSortId;
      this.formOpera.dataSortName = this.dataOperate[0].dataSortName;
      this.formOpera.dataSortValue = this.dataOperate[0].dataSortValue;
      this.formOpera.dataSortDesc = this.dataOperate[0].dataSortDesc;
      this.formOpera.extendTag = this.dataOperate[0].extendTag;
      this.editDataSortValue = this.dataOperate[0].dataSortValue;
      this.checkTag();
    }
  },
  watch: {
    dataOperate() {
      //数据监视
      if (this.dataOperate.length != 0) {
        this.formOpera.dataSortId = this.dataOperate[0].dataSortId;
        this.formOpera.dataSortName = this.dataOperate[0].dataSortName;
        this.formOpera.dataSortValue = this.dataOperate[0].dataSortValue;
        this.formOpera.dataSortDesc = this.dataOperate[0].dataSortDesc;
        this.formOpera.extendTag = this.dataOperate[0].extendTag;
        this.editDataSortValue = this.dataOperate[0].dataSortValue;
         this.show = true;
    //     this.checkTag();
    }else{
       this.show = true;
       this.formOpera = {
        dataSortId: "",
        dataSortName: "",
        dataSortValue: "",
        dataSortDesc: "",
        extendTag: "0",
        editTag: "0"
      }
    }
    }
  }
};
</script>
<style scoped>
.title-div {
  text-align: left;
}
.selected {
  float: left;
}
.textarea {
  width: 80%;
  float: left;
}
.input {
  width: 52%;
  float: left;
}
</style>