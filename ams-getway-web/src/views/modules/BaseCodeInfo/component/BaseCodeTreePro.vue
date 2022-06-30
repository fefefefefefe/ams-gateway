<template>
  <div class="centerDiv">
    <el-card class="box-card">
      <div class="text item treeDiv">
        <el-tree
          ref="tree"
          :data="data"
          node-key="codeValue"
          check-strictly
          :default-expanded-keys="['400801']"
          :props="defaultProps"
          @node-click="onclick"
        ></el-tree>
      </div>
    </el-card>
  </div>
</template>
<script>
// import { stringify } from "querystring";
export default {
  props: ["codeSortValue"],
  data() {
    return {
      data: [],
      defaultProps: {
        children: "children",
        label: "codeDesc"
      }
    };
  },
  mounted(){
    // debugger
     this.$http({
          url:this.$http.adornUrl('baseCodeInfo/selectWholeTree'),
          method: 'get',
          params: this.$http.adornParams({
              codeSortValue: this.codeSortValue,
          })
      }).then(({data}) => {
        this.data = data.result.records
      }).catch(function(error) {
        console.log(error);
      });
    // this.axios
    //       .post(
    //         "/audit/zhBaseCodeInfo/ps/zhBaseCodeInfos/selectWholeTree?codeSortValue=" + this.codeSortValue
    //       )
    //       .then(resp =>{
    //         this.data = resp.data.data;
    //       });
  },
  methods: {
    //点击节点上触发的事件，传递三个参数，数据对象使用第一个参数
    onclick(data) {
      this.$emit("node-click", data);
    },
    /**
     * 更新node的数字 map key(node的key), value(要更新的数字)
     * @param map
     */
    updateNode(map){
      this.receiveNode(this.data, node=>{
        console.log(node);
        var nodeTxt = map[node.codeValue];
        if(nodeTxt != null){
          node.codeDesc = nodeTxt;
        }
      })
    },
    receiveNode(arr, func){
      arr.forEach(data =>{
        func(data);
        var newArr = data.children;
        if(newArr!=null){
          this.receiveNode(newArr, func);
        }
      })
    },
  }


};
</script>

<style  scoped>
.treeDiv {
  height: 600px;
}
</style>