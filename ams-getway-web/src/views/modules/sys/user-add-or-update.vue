<template>
  <el-dialog
    width="70%"
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="100px">
      <div class="user-left inline-block">
        <el-form-item label="用户账号" prop="userLoginName">
          <el-input v-model="dataForm.userLoginName" :readonly="dataForm.id&&dataForm.id!=0" placeholder="用户账号(15字以内)" maxlength="15"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="userPassword" :class="{ 'is-required': !dataForm.id }">
          <el-input class="no-autofill-pwd" v-model="dataForm.userPassword" type="text" placeholder="密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="comfirmPassword" class="no-autofill-pwd" :class="{ 'is-required': !dataForm.id }">
          <el-input class="no-autofill-pwd" v-model="dataForm.comfirmPassword" type="text" placeholder="确认密码"></el-input>
        </el-form-item>
        <el-form-item label="用户姓名" prop="userName">
          <el-input v-model="dataForm.userName" placeholder="用户姓名（30字以内）" maxlength="30"></el-input>
        </el-form-item>
        <el-form-item label="部门及职务">
          <el-input v-model="dataForm.userNumber" placeholder="部门及职务"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="userPhone">
          <el-input v-model="dataForm.userPhone" placeholder="手机号"></el-input>
        </el-form-item>
        <!--<el-form-item label="角色" size="mini" prop="roleIdList">-->
        <!--<el-checkbox-group v-model="dataForm.roleIdList">-->
        <!--<el-checkbox v-for="role in roleList" :key="role.roleId" :label="role.roleId">{{ role.roleName }}</el-checkbox>-->
        <!--</el-checkbox-group>-->
        <!--</el-form-item>-->
        <el-form-item label="性别" size="mini" prop="userSex">
          <el-radio-group v-model="dataForm.userSex	">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <!--<el-form-item label="状态" size="mini" prop="status">-->
        <!--<el-radio-group v-model="dataForm.status">-->
        <!--<el-radio :label="0">禁用</el-radio>-->
        <!--<el-radio :label="1">正常</el-radio>-->
        <!--</el-radio-group>-->
        <!--</el-form-item>-->
      </div>
      <div class="user-right inline-block">
        <el-form-item size="mini" label="授权">
          <el-tree
            :default-checked-keys="selTree"
            :data="menuList"
            :props="menuListTreeProps"
            node-key="menuId"
            ref="menuListTree"
            :default-expand-all="false"
            :check-strictly="isCheck"
            @check-change="dataForm.id?isCheck=true:isCheck=false"
            show-checkbox>
          </el-tree>
        </el-form-item>
      </div>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { isEmail, isMobile } from '@/utils/validate'
  import { treeDataTranslate } from '@/utils'
  export default {
    data () {
      var validatePassword = (rule, value, callback) => {
        if (!this.dataForm.id && !/\S/.test(value)) {
          callback(new Error('密码不能为空'))
        } else {
          callback()
        }
      }
      var validateComfirmPassword = (rule, value, callback) => {
        if (!this.dataForm.id && !/\S/.test(value)) {
          callback(new Error('确认密码不能为空'))
        } else if (this.dataForm.userPassword !== value) {
          callback(new Error('确认密码与密码输入不一致'))
        } else {
          callback()
        }
      }
      var validateEmail = (rule, value, callback) => {
        if (!isEmail(value)) {
          callback(new Error('邮箱格式错误'))
        } else {
          callback()
        }
      }
      var validateMobile = (rule, value, callback) => {
        if (!isMobile(value)) {
          callback(new Error('手机号格式错误'))
        } else {
          callback()
        }
      }
      return {
        selTree: [],
        isCheck: true,
        tempKey: -666666 ,// 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
        menuList: [],
        menuListTreeProps: {
          label: 'menuName',
          children: 'children'
        },
        visible: false,
        roleList: [],
        dataForm: {
          id: 0,
          userLoginName: '',
          userPassword: '',
          comfirmPassword: '',
          userName: '',
          userNumber: '',
          userPhone: '',
          userSex:1,
        },
        dataRule: {
          userLoginName: [
            { required: true, message: '用户账号不能为空', trigger: 'blur' }
          ],
          userPassword: [
            { validator: validatePassword, trigger: 'blur' }
          ],
          comfirmPassword: [
            { validator: validateComfirmPassword, trigger: 'blur' }
          ],
          userName: [
            { required: true, message: '用户姓名不能为空', trigger: 'blur' }
          ],
          userNumber: [
            { required: true, message: '工号不能为空', trigger: 'blur' }
          ],
          email: [
            { required: true, message: '邮箱不能为空', trigger: 'blur' },
            { validator: validateEmail, trigger: 'blur' }
          ],
          userPhone: [
            { required: true, message: '手机号不能为空', trigger: 'blur' },
            { validator: validateMobile, trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      cleanMsg(){
        this.dataForm={
            id: 0,
            userLoginName: '',
            userPassword: '',
            comfirmPassword: '',
            userName: '',
            userNumber: '',
            userPhone: '',
            userSex:1,
        };
      },
      //验证唯一性
      verification(val,msg,name){
        if(val){
          this.$http({
            url: this.$http.adornUrl("/user/add"),
            method: "post",
            isLoading:false,
            params: this.$http.adornParams({
              userLoginName: this.dataForm.userLoginName,
              userNumber:  this.dataForm.userNumber,
            })
          }).then(({ data }) => {
            if (data && data.code === 200) {
              if(data.result===false){
                this.$message.error(msg);
                this.$forceUpdate();
                this.dataForm.project[name]='';
                this.$set(this.dataForm,this.dataForm)
              }
            }
          });
        }
      },
      init (id) {
        this.cleanMsg();
        this.visible = true;
        this.dataForm.id=id;
        this.selTree = [];
        if(!this.dataForm.id){
          this.isCheck=false;
        }
        this.$http({
          url: this.$http.adornUrl('/menu/getMenuList'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.menuList = treeDataTranslate(data.result, 'menuId','menuParentId')
        }).then(() => {
          this.visible = true
          this.$nextTick(() => {
            this.$refs['dataForm'].resetFields();

            // this.$refs.menuListTree.setCheckedKeys([])
          });
        }).then(() => {
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/user/selectByUuid/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 200) {
                var user=data.result;
                this.dataForm.userLoginName = user.userLoginName
                this.dataForm.userPassword = user.userPassword
                this.dataForm.comfirmPassword = user.comfirmPassword
                this.dataForm.userName = user.userName
                this.dataForm.userNumber = user.userNumber
                this.dataForm.userPhone = user.userPhone
                this.dataForm.userSex = user.userSex
                this.selTree  = user.menuIds?user.menuIds.split(","):[];
                this.$nextTick(() => {
                  //因为我是根据数据id来判断选中所以使用setCheckedKeys，具体可以查看element官网api
                  this.$refs.menuListTree.setCheckedKeys(this.selTree);//给树节点赋值
                  this.isCheck= false //重点： 赋值完成后 设置为false
                })
              }
            })
          }
        })


      },
      // 表单提交
      dataFormSubmit () {
        var _menuList=[].concat(this.$refs.menuListTree.getCheckedKeys(), [this.tempKey], this.$refs.menuListTree.getHalfCheckedKeys());
        // console.log(_menuList)
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/user/${!this.dataForm.id ? 'add' : 'updateByUuId'}`),
              method: 'post',
              data: this.$http.adornData({
                'userId': this.dataForm.id || undefined,
                'userLoginName': this.dataForm.userLoginName,
                'userPassword': this.dataForm.userPassword,
                'userName': this.dataForm.userName,
                'userNumber': this.dataForm.userNumber,
                'userPhone': this.dataForm.userPhone,
                'userSex': this.dataForm.userSex,
                'menuIds': _menuList.join(","),
              })
            }).then(({data}) => {
              if (data && data.code === 200) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.message)
              }
            })
          }
        })
      }
    }
  }
</script>
<style scoped>
  >>>.no-autofill-pwd .el-input__inner{
    -webkit-text-security:disc!important;
  }
  .user-left,.user-right{
    width: 49%;
    vertical-align: top;
  }
  .user-right{
    height: auto;
    overflow-y: auto;
    max-height: 70vh;
  }
</style>
