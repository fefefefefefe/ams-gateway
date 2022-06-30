<!--授权用户-->
<template>
  <div class="mod-role" style="overflow-y:auto;height:400px">
    <div>
      <el-form
        :inline="true"
        :model="dataForm"
        @keyup.enter.native="getDataList()"
      >
        <el-form-item label="用户帐号：">
          <el-input
            v-model="dataForm.userNumber"
            placeholder="请输入搜索内容"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="用户名称：">
          <el-input
            v-model="dataForm.userName"
            placeholder="请输入搜索内容"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getDataList()">查询</el-button>
          <el-button @click="reset()">重置</el-button>
        </el-form-item>
        <el-form-item style="float:right">
          <el-button type="primary" @click="add()">新增用户</el-button>
          <el-button
            type="danger"
            @click="deleteHandle()"
            :disabled="dataListSelections.length <= 0"
            >删除</el-button
          >
        </el-form-item>
      </el-form>
      <el-table
        :data="dataList"
        border
        v-loading="dataListLoading"
        @selection-change="selectionChangeHandle"
        style="width: 100%;"
      >
        <el-table-column
          type="selection"
          header-align="center"
          align="center"
          width="50"
        >
        </el-table-column>
        <el-table-column
          prop="userNumber"
          header-align="center"
          align="center"
          label="用户帐号"
        >
        </el-table-column>
        <el-table-column
          prop="userName"
          header-align="center"
          align="center"
          label="用户名称"
        >
        </el-table-column>
        <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          label="状态"
        >
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" size="small" type="danger"
              >禁用</el-tag
            >
            <el-tag v-else size="small">正常</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="pageIndex"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper"
      >
      </el-pagination>
      <!-- 弹窗, 新增 / 修改 -->
      <el-dialog
        :visible.sync="showAddDialog"
        title="新增用户"
        append-to-body
        :close-on-click-modal="false"
        :modal-append-to-body="false"
        width="40%"
        :close-on-press-escape="false"
      >
        <AddUser
          @close="closeAddDrawer"
          @ok="addSucceed"
          v-if="showAddDialog"
        ></AddUser>
      </el-dialog>
    </div>
    <div class="itemBtn">
      <el-button type="primary" @click="authority()">确定</el-button>
      <el-button @click="close()">取消</el-button>
    </div>
  </div>
</template>

<script>
import AddUser from "./addUser.vue";
export default {
  name: "authorityUser",
  components: {
    AddUser
  },
  data() {
    return {
      dataForm: {
        roleName: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showAddDialog: false
    };
  },
  components: {
    // AddOrUpdate
  },
  created() {
    this.getDataList();
    this.token = this.$cookie.get("token");
  },
  methods: {
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/user/selectPage"),
        method: "get",
        params: this.$http.adornParams({
          pageNo: this.pageIndex,
          pageSize: this.pageSize,
          userName: this.dataForm.userName,
          userNumber: this.dataForm.userNumber
        })
      }).then(({ data }) => {
        if (data && data.code === 200) {
          this.dataList = data.result.records;
          this.totalPage = data.result.total;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    },
    //新增
    add() {
      this.showAddDialog = true;
    },
    //关闭新增/修改弹窗
    closeAddDrawer() {
      this.showAddDialog = false;
    },
    addSucceed() {
      this.closeAddDrawer();
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map(item => {
            return item.roleId;
          });
      //确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?
      this.$confirm(
        `删除该角色后,该角色下的成员将失去对应的功能权限,确认删除该角色吗?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/role/delete"),
            method: "post",
            data: this.$http.adornData(ids, false)
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.getDataList();
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        })
        .catch(() => {});
    },
    //确定
    authority() {},
    //取消
    close() {
      this.$emit("close");
    }
  }
};
</script>
