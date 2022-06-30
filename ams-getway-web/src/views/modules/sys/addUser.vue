<template>
  <div class="mod-user">
    <el-form
      :inline="true"
      :model="dataForm"
      @keyup.enter.native="getDataList()"
    >
      <el-form-item label="用户姓名：">
        <el-input
          v-model="dataForm.userName"
          placeholder="请输入搜索内容"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-select
          v-model="dataForm.sex"
          filterable
          clearable
          placeholder="全部"
          size="small"
        >
          <el-option
            v-for="(item, index) in sex"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="手机号码：">
        <el-input
          v-model="dataForm.userName"
          placeholder="请输入搜索内容"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="用户状态：">
        <el-select
          v-model="dataForm.state"
          filterable
          clearable
          placeholder="全部"
          size="small"
        >
          <el-option
            v-for="(item, index) in state"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getDataList()">查询</el-button>
        <el-button @click="reset()">重置</el-button>
      </el-form-item>
      <el-form-item style="float:right">
        <el-button type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button
          type="primary"
          @click="addOrUpdateHandle()"
          :disabled="
            dataListSelections.length <= 0 || dataListSelections.length > 1
          "
          >修改</el-button
        >
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
        prop="username"
        header-align="center"
        align="center"
        label="用户姓名"
      >
      </el-table-column>
      <el-table-column
        prop="email"
        header-align="center"
        align="center"
        label="性别"
      >
      </el-table-column>
      <el-table-column
        prop="mobile"
        header-align="center"
        align="center"
        label="创建日期"
      >
      </el-table-column>
      <el-table-column
        prop="mobile"
        header-align="center"
        align="center"
        label="手机号码"
      >
      </el-table-column>
      <el-table-column
        prop="status"
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
  </div>
</template>

<script>
export default {
  name: "addUser",
  data() {
    return {
      dataForm: {
        userName: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      sex: [
        { id: "1", name: "男" },
        { id: "2", name: "女" }
      ],
      state: [
        { id: "1", name: "正常" },
        { id: "2", name: "已删除" }
      ],
      title: "",
      id: ""
    };
  },
  methods: {
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/sys/user/list"),
        method: "get",
        params: this.$http.adornParams({
          page: this.pageIndex,
          limit: this.pageSize,
          username: this.dataForm.userName
        })
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.page.list;
          this.totalPage = data.page.totalCount;
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
    // 新增/修改
    addOrUpdateHandle() {
      this.addOrUpdateVisible = true;
      if (this.dataListSelections.length > 0) {
        this.title = "新增用户";
      } else {
        this.title = "修改用户";
        this.id = this.dataListSelections[0];
      }
    },
    //关闭新增弹窗
    closeAddDrawer() {
      this.addOrUpdateVisible = false;
    },
    addSucceed() {
      this.addOrUpdateVisible = false;
    },
    // 删除
    deleteHandle(id) {
      var userIds = id
        ? [id]
        : this.dataListSelections.map(item => {
            return item.userId;
          });
      this.$confirm(
        `确定对[id=${userIds.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/user/delete"),
            method: "post",
            data: this.$http.adornData(userIds, false)
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
    }
  }
};
</script>
