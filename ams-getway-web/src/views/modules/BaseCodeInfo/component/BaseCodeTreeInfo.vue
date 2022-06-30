<template>
<div style="height:600px;">
  <div class="tree">
      <BaseCodeTree  @treeQuery="treeQuery"></BaseCodeTree>
    </div>
    <div tyle="width:100%">
      <BaseCodeTreeList ref="listData" @BaseCodeMethod="BaseCodeMethod"></BaseCodeTreeList>
  </div>
</div>
</template>
<script>
import BaseCodeTree from './BaseCodeTree.vue';
import BaseCodeTreeList from './BaseCodeTreeList.vue';
export default {
  components: {
    BaseCodeTree,
    BaseCodeTreeList
  },
  mounted: function() {
    this.$refs.listData.query();
  },
  methods:{
    treeQuery(){
        this.$refs.listData.query();
    },
    BaseCodeMethod(){
       this.$emit('BaseCodeOne');
    },
  }
};
</script>
<style scoped>
.tree {
  width: 300px;
  float: left;
  margin-right: 10px;
  /* height: 95%; */
}
.divContent {
  width: 78%;
  float: right;
}
</style>
