<template>
  <div class="site-wrapper site-page--login">
    <div class="site-content__wrapper">
      <div class="site-content">
        <div class="brand-info">
          <!--<h2 class="brand-info__text">人保管理平台</h2>-->
          <!--<p class="brand-info__intro">renren-fast-vue基于vue、element-ui构建开发，实现renren-fast后台管理前端功能，提供一套更优的前端解决方案。</p>-->
        </div>
        <div class="login-main-div">
          <div class="login-main">
            <!--<div class="login-main-left">-->
              <!--<div class="lm-left-title">欢迎登录</div>-->
              <!--<div class="lm-left-title-line"></div>-->
              <!--<div class="lm-left-img">-->
                <!--<img src="../../assets/img/lm-left-img.png" alt="">-->
              <!--</div>-->
            <!--</div>-->
            <div class="login-main-left">
              <div class="lm-left-title-new">
                <!--<img src="../../assets/img/login-logo.png" alt="">-->
                <div></div>
              </div>
            </div>
            <div class="login-main-right">
              <h3 class="login-title"></h3>
              <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" status-icon>
                <el-form-item prop="userName" class="lm-right-input">
                  <div>用户名</div>
                  <el-input prefix-icon="el-icon-user-solid" v-model="dataForm.userName" placeholder="帐号"></el-input>
                </el-form-item>
                <el-form-item prop="password" class="lm-right-input">
                  <div>密码</div>
                  <el-input prefix-icon="el-icon-s-goods" v-model="dataForm.password" type="password" placeholder="密码"></el-input>
                </el-form-item>
                <!--<el-form-item prop="captcha">-->
                <!--<el-row :gutter="20">-->
                <!--<el-col :span="14">-->
                <!--<el-input v-model="dataForm.captcha" placeholder="验证码">-->
                <!--</el-input>-->
                <!--</el-col>-->
                <!--<el-col :span="10" class="login-captcha">-->
                <!--<img :src="captchaPath" @click="getCaptcha()" alt="">-->
                <!--</el-col>-->
                <!--</el-row>-->
                <!--</el-form-item>-->
                <el-form-item>
                  <el-button class="login-btn-submit" type="primary" @click="dataFormSubmit()">登录</el-button>
                </el-form-item>
              </el-form>
            </div>

          </div>
        </div>
      </div>
    </div>
    <!-- 弹窗, 修改密码 -->
    <update-password v-if="updatePassowrdVisible" ref="updatePassowrd"></update-password>
  </div>
</template>

<script>
  import UpdatePassword from '../main-navbar-update-password'
  // 引入js
  import hex_md5 from '../common/md5.js';
  import { getUUID } from '@/utils'
  export default {
    components: {
      UpdatePassword
    },
    data () {
      return {
        updatePassowrdVisible:false,
        currdatetime:'',
        dataForm: {
          userName: '',
          password: '',
          uuid: '',
          captcha: ''
        },
        dataRule: {
          userName: [
            { required: true, message: '帐号不能为空', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '密码不能为空', trigger: 'blur' }
          ],
          captcha: [
            { required: true, message: '验证码不能为空', trigger: 'blur' }
          ]
        },
        captchaPath: ''
      }
    },
    created () {
      // this.getCaptcha()
    },
    methods: {
      // 提交表单
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl('sys/login'),
              method: 'post',
              data: this.$http.adornData({
                'username': this.dataForm.userName,
                // 'password':this.dataForm.password,
                'password': hex_md5(this.dataForm.password),
                // 'checkKey':  this.dataForm.uuid,
                // 'captcha': this.dataForm.captcha
              })
            }).then(({data}) => {
              if (data && data.code === 200) {
                this.$cookie.set('token', data.result.token);
                if(this.dataForm.password=='111111'){
                  this.updatePassowrdVisible = true;
                  this.$store.commit('user/updateLoginName', this.dataForm.userName)
                  this.$store.commit('user/updateId', data.result.userId)
                  this.$nextTick(() => {
                    this.$refs.updatePassowrd.init('login')
                  })
                  return false;
                }
                this.$router.replace({ name: 'home' });
              } else {
                this.getCaptcha();
                this.$message.error(data.message);
              }
            })
          }
        })
      },
      // 获取验证码
      getCaptcha () {
        this.dataForm.uuid = getUUID();
        // this.captchaPath = this.$http.adornUrl(`/captcha.jpg?uuid=${this.dataForm.uuid}`)
        this.currdatetime = new Date().getTime();
        this.$http({
          url: this.$http.adornUrl(`/login/randomImage/${ this.dataForm.uuid}`),
          method: 'get',
        }).then(({data}) => {
          this.captchaPath =data.result;
        })
      }
    }
  }
</script>

<!--<style lang="scss">-->
  <!--.site-wrapper.site-page&#45;&#45;login {-->
    <!--position: absolute;-->
    <!--top: 0;-->
    <!--right: 0;-->
    <!--bottom: 0;-->
    <!--left: 0;-->
    <!--/*background-color: rgba(38, 50, 56, .6);*/-->
    <!--overflow: hidden;-->
    <!--&:before {-->
      <!--position: fixed;-->
      <!--top: 0;-->
      <!--left: 0;-->
      <!--z-index: -1;-->
      <!--width: 100%;-->
      <!--height: 100%;-->
      <!--content: "";-->
      <!--background-image: url(~@/assets/img/bg-div.png);-->
      <!--/*background-size:  100% 100%;*/-->
      <!--background-size: 100% 100%;-->
    <!--}-->
    <!--.site-content__wrapper {-->
      <!--position: absolute;-->
      <!--top: 0;-->
      <!--right: 0;-->
      <!--bottom: 0;-->
      <!--left: 0;-->
      <!--padding: 0;-->
      <!--margin: 0;-->
      <!--overflow-x: hidden;-->
      <!--overflow-y: auto;-->
      <!--background-color: transparent;-->
    <!--}-->
    <!--.site-content {-->
      <!--min-height: 100%;-->
      <!--padding: 30px 500px 30px 30px;-->
    <!--}-->
    <!--.brand-info {-->
      <!--margin: 220px 100px 0 90px;-->
      <!--color: #000;-->
      <!--text-shadow:5px 2px 6px #fff;-->
    <!--}-->
    <!--.brand-info__text {-->
      <!--margin:  0 0 22px 0;-->
      <!--font-size: 50px;-->
      <!--font-weight: bold;-->
      <!--text-transform : uppercase;-->
    <!--}-->
    <!--.brand-info__intro {-->
      <!--margin: 10px 0;-->
      <!--font-size: 16px;-->
      <!--line-height: 1.58;-->
      <!--opacity: .6;-->
    <!--}-->
    <!--.login-main-div{-->
      <!--width: 100%;-->
      <!--position: absolute;-->
      <!--top: 0;-->
    <!--}-->
    <!--.login-main {-->
      <!--margin: auto;-->
      <!--margin-top: 10vh;-->
      <!--padding: 50px;-->
      <!--width: 826px;-->
      <!--height: 509px;-->
      <!--background-image: url(~@/assets/img/bg1.png);-->
      <!--background-size:  100% 100%;-->
      <!--border-radius: 10px;-->
      <!--display: flex;-->
      <!--justify-content: space-between;-->
    <!--}-->
    <!--.login-main>div{-->
      <!--width: 50%;-->
      <!--display: inline-block;-->
      <!--position: relative;-->
    <!--}-->
    <!--.login-main-right{-->
      <!--padding:0 40px;-->
      <!--box-sizing: border-box;-->
    <!--}-->
    <!--.lm-left-title{-->
      <!--font-size: 36px;-->
      <!--font-family: Microsoft YaHei;-->
      <!--font-weight: 400;-->
      <!--color: #304155;-->
    <!--}-->
    <!--.lm-left-title-line{-->
      <!--width: 117px;-->
      <!--height: 2px;-->
      <!--background: #AF0F16;-->
      <!--margin-top: 13px;-->
    <!--}-->
    <!--.lm-left-img{-->
      <!--width: 100%;-->
      <!--position: absolute;-->
      <!--bottom: 0;-->
      <!--left: 0;-->
    <!--}-->
    <!--.lm-left-img>img{-->
      <!--width: 100%;-->
      <!--height: auto;-->
    <!--}-->
    <!--.site-content__wrapper{-->
      <!--height: 100%;-->
      <!--overflow: hidden;-->
    <!--}-->
    <!--.site-content{-->
      <!--padding: 0!important;-->
      <!--position: static;-->
      <!--height: 100vh;-->
      <!--overflow: hidden;-->
    <!--}-->
    <!--.login-title {-->
      <!--font-size: 17px;-->
      <!--font-family: Microsoft YaHei;-->
      <!--font-weight: bold;-->
      <!--color: #AF0F16;-->
      <!--line-height: 35px;-->
      <!--margin-bottom: 40px;-->
    <!--}-->
    <!--.lm-right-input {-->
      <!--margin-bottom: 10px;-->
      <!--.el-input__inner{-->
        <!--border-color: #AF0F16;-->
        <!--border-radius: 20px;-->
      <!--}-->
      <!--.el-input__icon{-->
        <!--color: #AF0F16;-->
      <!--}-->
    <!--}-->
    <!--.login-captcha {-->
      <!--overflow: hidden;-->
      <!--&gt; img {-->
        <!--width: 100%;-->
        <!--cursor: pointer;-->
      <!--}-->
    <!--}-->
    <!--.login-btn-submit {-->
      <!--width: 100%;-->
      <!--margin-top: 38px;-->
      <!--border-radius: 20px;-->
    <!--}-->
    <!--.lm-left-title-new{-->
      <!--margin: auto;-->
      <!--margin-top: 35%;-->
    <!--}-->
    <!--.lm-left-title-new>img{-->
      <!--width: 80%;-->
      <!--height: auto;-->
    <!--}-->
  <!--}-->
<!--</style>-->
