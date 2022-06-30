<template>
  <div class="centerDiv">
    <el-card class="box-card">
      <div class="text item treeDiv">
        <el-tree
          ref="tree"
          :data="data"
          lazy
          node-key="id"
          check-strictly
          :load="loadnode"
          :props="defaultProps"
          @node-click="nodeclick" :default-expand-all="ifExpand"
        ></el-tree>
      </div>
    </el-card>
  </div>
</template>
<script>
// import { stringify } from "querystring";
export default {

  data() {
    return {
      data: [],
      defaultProps: {
        children: "children",
        label: "codeName"
      },
      treeFistNode: "",
      dataSortId: "",
      dataSortName: "",
      baseCode: {},
      ifExpand:true,
    };
  },  
  computed: {
    tableHeight: {
      get() {
        return this.$store.state.common.tableHeight;
      }
    }
    },
  methods: {
    loadnode(node, resolve) {
      //如果展开第一级节点，从后台加载一级节点列表
      if (node.level == 0) {
        this.loadfirstnode(resolve);
      }
      //如果展开其他级节点，动态从后台加载下一级节点列表
      if (node.level >= 1) {
        this.loadchildnode(node, resolve);
      }
    },
    //加载第一级节点 -默认加载树结构
    loadfirstnode(resolve) {
      var dataSortId = this.$store.state.datasort.dataSortId;
      var dataSortName = this.$store.state.datasort.dataSortName;
      var data = [
        {
          codeId: dataSortId,
          codeName: dataSortName,
        }
      ];
      resolve(data);
      this.baseCode.codeId = dataSortId;
      this.baseCode.codeName = dataSortName;
      //传递值
      this.$store.dispatch("setBaseCode", this.baseCode);
    },
    //刷新树组件
    refreshtree() {
      this.axios.get("http://localhost:6060/loadtype").then(function(resp) {
        this.data = resp.data;
      });
    },
    //加载根节点的子节点集合
    loadchildnode(node, resolve) {
      if (node.level === 1) {
        this.treeLoading = true;
        this.$http({
          url:this.$http.adornUrl('/baseCodeInfo/selectPage'),
          method: 'get',
          params: this.$http.adornParams({
              dataSortId:  node.data.codeId,
              parentCodeId: node.data.codeId
          })
          }).then(({data}) => {
            var jsonStr = JSON.stringify(data.result.records);
            var label = eval("(" + jsonStr + ")");
            this.ifExpand=false;
            resolve(label);
          })
      } else {
        this.treeLoading = true;
        this.$http({
          url:this.$http.adornUrl('/baseCodeInfo/selectPage'),
          method: 'get',
          params: this.$http.adornParams({
              parentCodeId: node.data.codeId
          })
          }).then(({data}) => {
            var jsonStr =  JSON.stringify(data.result.records)
            var label = eval("(" + jsonStr + ")");
            this.ifExpand=false;
            // this.$emit('query',true)
            resolve(label);
          })
      }
      //   var str = "" ;
      //   for(var i=0;i<data.length;i++){
      //        var a =  data[i].codeName;
      //        str+= "{'label':'"+a+"'},";
      //   }
      //  str =  str.substring(0,str.length-1);
      //  str = "["+str+"]";
      //   alert(str)
      //   var label = eval('('+str+')') ;
      //   alert(label)
    },
    //点击节点上触发的事件，传递三个参数，数据对象使用第一个参数
    nodeclick(data) {
    //   alert(data.label+",id="+data.id);
      this.baseCode.codeId = data.codeId;
      this.baseCode.codeName = data.codeName;
      this.baseCode.dataSortId = data.dataSortId;
      // 传递值
      this.$store.dispatch("setBaseCode", this.baseCode);
      this.$emit('treeQuery')
    }
  }
};
</script>

<style lang="scss" scoped>
.treeDiv {
  height: 550px;
  min-height:550px;
  overflow: auto;
}
</style>