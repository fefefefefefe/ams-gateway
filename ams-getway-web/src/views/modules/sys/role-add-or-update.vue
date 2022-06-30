<template>
  <div class="add">
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      label-width="120px"
      class="demo-ruleForm"
      v-loading="loading"
    >
      <el-form-item label="角色编码" prop="roleNumber" class="item-a">
        <el-input
          v-model="ruleForm.roleNumber"
          size="small"
          placeholder="请输入内容"
          clearable
          :readonly="readonly"
        ></el-input>
      </el-form-item>
      <el-form-item label="角色名称" prop="roleName" class="item-a">
        <el-input
          v-model="ruleForm.roleName"
          size="small"
          placeholder="请输入内容"
          clearable
          :readonly="readonly"
        ></el-input>
      </el-form-item>
      <el-form-item label="角色描述" prop="roleDes" class="item-b">
        <el-input
          type="textarea"
          v-model="ruleForm.roleDes"
          placeholder="请输入内容"
          :readonly="readonly"
        ></el-input>
      </el-form-item>
    </el-form>
    <div class="itemBtn" v-if="showBtn">
      <el-button type="primary" @click="submitForm('ruleForm')">确定</el-button>
      <el-button @click="resetForm('ruleForm')">重置</el-button>
      <el-button @click="close('ruleForm')">取消</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "addOrUpdate",
  props: {
    id: { type: String },
    showBtn: { type: Boolean },
    readonly: { type: Boolean }
  },
  data() {
    return {
      // form对象
      ruleForm: {
        roleNumber: "",
        roleName: "",
        roleDes: ""
      },
      //form校验
      rules: {
        roleNumber: [{ required: true, message: "请输入", trigger: "blur" }],
        roleName: [{ required: true, message: "请输入", trigger: "blur" }]
      },
      loading: false,
      //单位选择页是否显示
      showDetailDialog: false,
    };
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      if (this.id != null && this.id != "" && this.id != undefined) {
        this.loading = true;
        this.$nextTick(() => {
          this.$refs["ruleForm"].resetFields();
        });
        this.$http({
          url: this.$http.adornUrl(`/role/selectByUuid/${this.id}`),
          method: "get",
          params: this.$http.adornParams()
        }).then(({ data }) => {
          if (data && data.code === 200) {
            var role = data.result;
            this.ruleForm.roleNumber = role.roleNumber;
            this.ruleForm.roleName = role.roleName;
            this.ruleForm.roleDes = role.roleDes;
            this.loading = false;
          }
        });
      }
    },
    // 表单提交
    submitForm() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/role/${!this.id ? "add" : "updateByUuId"}`
            ),
            method: "post",
            data: this.$http.adornData({
              roleId: this.id || undefined,
              roleNumber: this.ruleForm.roleNumber,
              roleName: this.ruleForm.roleName,
              roleDes: this.ruleForm.roleDes
            })
          }).then(({ data }) => {
            if (data && data.code === 200) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.$emit("ok");
                }
              });
            } else {
              this.$message.error(data.message);
            }
          });
        }
      });
    },
    // 重置
    resetForm(formName) {
      this.ruleForm = {
        roleNumber: "",
        roleName: "",
        remark: ""
      };
      this.$refs[formName].resetFields();
    },
    // 关闭
    close(formName) {
      this.$refs[formName].resetFields();
      this.$emit("close");
    },
    //选择单位
    choose(data) {
      // 弹出单位选择页
      this.showDetailDialog = true;
    }
  }
};
</script>
