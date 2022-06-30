<template>
  <div class="box">
    <div class="left authoriza-left">
      <!--<el-input-->
      <!--placeholder="输入关键字进行过滤"-->
      <!--v-model="filterText">-->
      <!--</el-input>-->
      <!--<el-tree-->
      <!--class="filter-tree"-->
      <!--:data="dataLeft"-->
      <!--:props="defaultProps"-->
      <!--default-expand-all-->
      <!--:filter-node-method="filterNode"-->
      <!--@node-click="getRightData"-->
      <!--ref="tree">-->
      <!--</el-tree>-->
      <div class="user-title">用户列表: <el-input clearable @blur='queryData' @input="queryData" class="search-user-input" placeholder="搜索用户" v-model="selectVal"></el-input></div>
      <ul class="user-list">
        <li v-for="(item, index) in userList" :key="index">
          <div
            class="userList-div"
            :class="activeClass == item.userId ? 'active' : ''"
            @click="(item.checkd = false), getRightData(item.userId)"
          >
            {{ item.userName }}
          </div>
          <!--<el-checkbox  class="userlist-checkbox" v-model="item.checkd">勾选授权</el-checkbox>-->
          <el-checkbox
            @change="checkTree(item.userId, item)"
            class="userlist-checkbox"
            :checked="item.checkd"
            >勾选授权</el-checkbox
          >
        </li>
      </ul>
    </div>
    <div class="right">
      <div class="buttons inline-block" v-if="dataRight != ''">
        <el-button type="primary" @click="getCheckedNodes">保存</el-button>
        <!--<el-button @click="getCheckedKeys">通过 key 获取</el-button>-->
        <!--<el-button @click="setCheckedNodes">通过 node 设置</el-button>-->
        <!--<el-button @click="setCheckedKeys">通过 key 设置</el-button>-->
        <!--<el-button @click="resetChecked">清空</el-button>-->
      </div>
      <div style="margin-top: 5%">
        <el-tree
          :default-checked-keys="selTree"
          :data="dataRight"
          show-checkbox
          default-expand-all
          node-key="menuId"
          ref="tree"
          highlight-current
          :props="defaultProps"
          :check-strictly="isCheck"

          @node-click="isCheck=true"
        >
          <!--@check-change="isCheck=true"-->
        </el-tree>
      </div>
    </div>
  </div>
</template>

<script>
import { treeDataTranslate } from "@/utils";
export default {
  data() {
    return {
      selectVal:'',
      isCheck:true,
      checkboxTree: 0,
      selTree: [],
      userIdList: [],
      userId: "",
      userList: [],
      userListCopy: [],
      filterText: "",
      dataLeft: [
        {
          id: 1,
          label: "一级 1",
          children: [
            {
              id: 4,
              label: "二级 1-1",
              children: [
                {
                  id: 9,
                  label: "三级 1-1-1"
                },
                {
                  id: 10,
                  label: "三级 1-1-2"
                }
              ]
            }
          ]
        },
        {
          id: 2,
          label: "一级 2",
          children: [
            {
              id: 5,
              label: "二级 2-1"
            },
            {
              id: 6,
              label: "二级 2-2"
            }
          ]
        },
        {
          id: 3,
          label: "一级 3",
          children: [
            {
              id: 7,
              label: "二级 3-1"
            },
            {
              id: 8,
              label: "二级 3-2"
            }
          ]
        }
      ],
      defaultProps: {
        children: "children",
        label: "menuName"
      },
      dataRight: [],
      datafake: [
        {
          id: 1,
          label: "一级 1",
          children: [
            {
              id: 4,
              label: "二级 1-1",
              children: [
                {
                  id: 9,
                  label: "三级 1-1-1"
                },
                {
                  id: 10,
                  label: "三级 1-1-2"
                }
              ]
            }
          ]
        },
        {
          id: 2,
          label: "一级 2",
          children: [
            {
              id: 5,
              label: "二级 2-1"
            },
            {
              id: 6,
              label: "二级 2-2"
            }
          ]
        },
        {
          id: 3,
          label: "一级 3",
          children: [
            {
              id: 7,
              label: "二级 3-1"
            },
            {
              id: 8,
              label: "二级 3-2"
            }
          ]
        }
      ],
      //点击样式
      activeClass: ""
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  activated() {
    this.getLeftTree();
    this.token = this.$cookie.get("token");
  },
  methods: {
    //搜索用户
    queryData(){
      //并没有输入关键字时，就不要再搜索了
      if(this.selectVal===''||this.selectVal==null){
        this.userList=JSON.parse(JSON.stringify(this.userListCopy));
        return;
      }
      //搜索
      let list=this.userList.filter(item=>item.userName.indexOf(this.selectVal)>=0);
      this.userList=list;

    },
    //勾选
    checkTree(id, item) {
      if (this.userIdList.length == 0) {
        this.getRightData(id);
        item.checkd = true;
      }
      if (this.userIdList.indexOf(id) == -1) {
        this.userIdList.push(id);
        item.checkd = true;
      } else {
        var i = this.userIdList.findIndex((item, index) => {
          item.userId == id;
        });
        this.userIdList.splice(i, 1);
        item.checkd = false;
      }
      this.$set(this.userList, this.userList);
      console.log(this.userIdList);
    },
    getLeftTree() {
      this.$http({
        url: this.$http.adornUrl("/user/getUser"),
        method: "get"
      }).then(({ data }) => {
        this.userList = data.result;
        this.userListCopy = data.result;
        this.userList.forEach(item => {
          item.checkd = false;
        });
      });
    },
    getRightData(data, node, obj) {
      this.activeClass = data;
      var id = data;
      // console.log(data, node, obj);
      // this.dataRight=this.datafake;
      this.userId = data;
      this.userIdList = [];
      this.selTree = [];
      this.$http({
        url: this.$http.adornUrl("/menu/getUserMenuList"),
        method: "get",
        params: this.$http.adornParams({ userId: id })
      }).then(({ data }) => {
        var datas = data.result;

        datas.forEach(item => {
          this.selTree.push(item.menuId);
        });
        this.$http({
          url: this.$http.adornUrl("/menu/getMenuList"),
          method: "get",
          params: this.$http.adornParams({ userId: id })
        }).then(({ data }) => {
          this.dataRight = treeDataTranslate(
            data.result,
            "menuId",
            "menuParentId"
          );
          this.$nextTick(() => {
            //因为我是根据数据id来判断选中所以使用setCheckedKeys，具体可以查看element官网api
            this.$refs.tree.setCheckedKeys(this.selTree);//给树节点赋值
            this.isCheck= false //重点： 赋值完成后 设置为false
          })
        });
      });
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    getCheckedNodes() {
      // console.log(this.$refs.tree.getCheckedNodes());
      var list = this.$refs.tree.getCheckedNodes().concat(this.$refs.tree.getHalfCheckedNodes()),
        dataList = [];
      if (this.userIdList.length > 0) {
        list.forEach(item => {
          var _s = [];
          this.userIdList.forEach(vtem => {
            var v = { userId: vtem, menuId: item.menuId };
            _s.push(v);
          });
          dataList = dataList.concat(_s);
        });
      }
      if (this.userIdList.length == 0) {
        list.forEach(item => {
          var v = { userId: this.userId, menuId: item.menuId };
          dataList.push(v);
        });
      }
      console.log(dataList);
      if (dataList.length > 0) {
        // console.log(JSON.stringify(dataList));
        // dataList=JSON.stringify(dataList);
        this.$http({
          url: this.$http.adornUrl(`/userMenuRel/menuEmpower`),
          method: "post",
          data: { userMenuListStr: dataList }
        }).then(({ data }) => {
          if (data && data.code === 200) {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {}
            });
          }
        });
      }
    },
    getCheckedKeys() {
      console.log(this.$refs.tree.getCheckedKeys());
    },
    setCheckedNodes() {
      this.$refs.tree.setCheckedNodes([
        {
          id: 5,
          label: "二级 2-1"
        },
        {
          id: 9,
          label: "三级 1-1-1"
        }
      ]);
    },
    setCheckedKeys() {
      this.$refs.tree.setCheckedKeys([3]);
    },
    resetChecked() {
      this.$refs.tree.setCheckedKeys([]);
    }
  }
};
</script>

<style scoped>
.box {
  display: flex;
  justify-content: space-between;
}
.box > div {
  height: 74vh;
  border: 1px solid #ddd;
  padding: 1%;
  overflow: auto;
}
.left {
  width: 40%;
  border-right: none;
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
}
.right {
  width: 60%;
  position: relative;
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
}
.buttons {
  text-align: right;
  position: fixed;
  right: 5%;
  z-index: 999;
}
.user-list {
  padding: 0;
  overflow: auto;
}
.user-list > li {
  cursor: pointer;
  border-bottom: 1px solid #ddd;
  list-style-type: none;
  padding: 2%;
}
.user-list > li:nth-child(odd) {
  background: #f2f2f2;
}
.userlist-checkbox {
  float: right;
}
.userList-div {
  display: inline-block;
  width: 70%;
}
.user-list{
  height:85%;
  overflow-y: auto;
}
.search-user-input{
  width: 60%;
  margin-bottom: 10px;
}
</style>
