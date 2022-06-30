<template>
  <transition name="fade">
    <router-view></router-view>
  </transition>
</template>

<script>
  export default {
    mounted(){

    }
  }
</script>
<style>
  .el-button:focus {outline:none;} /*for IE*/
  .el-button::-moz-focus-inner {border-color: transparent;} /*for mozilla*/
</style>
