<template>
    <div class="rule-tree-box">
        <el-input
            class="filter-text"
            placeholder="输入关键字进行过滤"
            v-if="isShowSearch"
            v-model="filterText">
        </el-input>
        <el-tree
            v-loading="treeLoading"
            ref="ruleTreeRoot"
            class="treeClass"
            :data="treeData"
            :show-checkbox="isShowCheckBox"
            :filter-node-method="filterNode"
            :props="layoutTreeProps"
            :expand-on-click-node="false"
            :check-strictly="isRelation"
            node-key="folderId"
            @check-change="callCheckChange"
            highlight-current
            @node-click="nodeClick"
            :default-expand-all="ifExpand"
            >
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span v-if="!isShowIcon" :class="isShowEdit ? 'cut-width custom-tree-label tree-label' : 'custom-tree-label tree-label' " :title="node.label">{{ node.label }}</span>
                <span v-if="isShowIcon" class="tree-label">
                    <span class="folder-icon" :title="node.label"></span>
                    <span :title="node.label" :class="isShowEdit ? 'cut-width custom-tree-label' : 'custom-tree-label' "> {{node.label}}</span>
                </span>
                <span class="tree-btn" v-if="isShowEdit">
                    <el-button
                        type="text"
                        size="mini"
                        @click="() => append(data,node,'add')">
                        <i class="el-icon-circle-plus-outline" title="新增"></i>
                    </el-button>
                    <el-button
                    v-if="data.folderParentId"
                    type="text"
                    size="mini"
                    @click="() => append(data,node,'edit')">
                    <i class="el-icon-edit" title="编辑"></i>
                    </el-button>
                    <el-button
                        v-if="data.folderParentId  && !data.children"
                        type="text"
                        size="mini"
                        @click="() => remove(node, data)">
                        <i class="el-icon-delete" title="删除"></i>
                    </el-button>
                </span>
            </span>
        </el-tree>
        <el-dialog :title="treeTitle" :visible.sync="treeVisible"  :close-on-press-escape="false"  :close-on-click-modal="false">
            <el-form :model="treeForm" :rules="rulesTreeForm" ref="treeForm">
                <el-form-item label="分类名称" prop="folderName">
                    <el-input v-model="treeForm.folderName" autocomplete="off" maxlength="30" show-word-limit></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="treeVisible = false">取 消</el-button>
                <el-button type="primary" @click="ruleSubmit('treeForm')" :loading="btnLoading">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
export default {
    props: {
        isShowSearch: { // 是否显示规则搜索
            type: Boolean,
            default: false
        },
        isShowCheckBox: { // 是否显示多选框
            type: Boolean,
            default: false
        },
        isShowEdit: { // 规则是否可编辑
            type: Boolean,
            default: false
        },
        parentGetTreeData: { // 父组件的获取规则树id的方法名
            type: String,
            default: 'getTreeId'
        },
        parentCheckChange : {
            type: Function,
        },
        isParent: { // 判断调用组件是否为当前组件的父组件
            type: Boolean,
            default: true
        },
        isRelation: {
            type: Boolean, // 父子节点是否关联,是否单选
            default: false
        },
        folderSorts: { // 规则树查询参数
            default: ''
        },
        isShowIcon: {
            type: Boolean, // 是否显示文件夹
            default: true
        },
        // folderTypes: { // 规则树查询参数
        //     default: ''
        // }

    },
    computed: {
      projectId: {
        get () {
          if(this.$store.state.common.projectId){
            return this.$store.state.common.projectId
          }else{
            this.$http({
              url: this.$http.adornUrl("/xmProject/selectProjectByUserId"),
              method: "get",
              params: this.$http.adornParams()
            }).then(({ data }) => {
              if (data && data.code === 200) {
                return data.result && data.result.projectId && data.result.projectId || '';
              }
            });
          }

        }
      },
    },
    data () {
        return {
            ifExpand:true,
            filterText: '',
            treeVisible: false,
            treeTitle: '',
            treeLoading: false,
            treeForm: {
                folderName: ''
            },
            rulesTreeForm: {
                folderName: [
                    { required: true, message: '请输入分类名称', trigger: 'blur'},
                ],
            },
            treeData: [],
            layoutTreeProps: {
                label(data, node) {
                    const config = data.__config__ || data
                    return  config.label || config.folderName
                },
            },
            editRuleItem: {},
            btnLoading: false,
            optionType: 'add',
            editRuleItemNode: {},
            parentId: [],

        }
    },
    mounted () {
      if(this.treeData.length==0){
        this.getRuleFolder();
      }
    },
    // activated () {
    //   if(this.treeData==[]){
    //     this.getRuleFolder();
    //   }
    // },
    methods: {
        // 获取规则树
        getRuleFolder (callBack,from) {
            this.treeLoading = true;
            this.$http({
                isLoading:false,
                url: this.$http.adornUrl('/ruleFolder/getRuleFolder'),
                method: 'get',
                params:  this.$http.adornParams({folderTypes: this.folderSorts,projectId:this.projectId}, false)
                // params:  this.$http.adornParams({}, false)
            }).then(({data}) => {
                this.treeLoading = false;

                if (data.code == 200) {
                    this.$nextTick(() => {
                      this.treeData = data.result;
                      this.ifExpand=false;
                    });
                    if (callBack) {
                        callBack(this.treeData)
                    }

                    this.$emit("getTreeData", this.treeData);
                }
            }).catch(() => {
                this.treeLoading = false
            })


        },
        filterNode(value, data) {
            if (!value) return true
            return data.folderName.indexOf(value) !== -1;
        },
        ruleSubmit (formName) {
            if (this.optionType == 'add') {
                // 新增提交
                this.addRuleFolder(formName)
            } else {
                // 编辑提交
                this.editRuleFolder(formName)
            }
        },
        getParent (node, type) {
            const checkedNodes = [];
            const traverse = function(node) {
                if (node.parent.data.folderId) {
                    checkedNodes.push(node.parent.data.folderId);
                    traverse(node.parent);
                }
            };
            traverse(node)
            if (type == 'add') {
                checkedNodes.unshift(node.data.folderId)
            }
            return checkedNodes;
        },
        addRuleFolder (formName) {
            let folderPath =  this.getParent(this.editRuleItemNode, 'add');
            folderPath = folderPath.reverse()
            let addRuleFolderdata = {
                folderName: this.treeForm.folderName, // 规则树名称
                folderLevel: this.editRuleItem.folderLevel && this.editRuleItem.folderLevel || '', // 级别
                folderParentId: this.editRuleItem.folderId, // 父节点id
                folderPath: folderPath.length>0 && folderPath.join('\\') || '', // 路径  // 所有父节点的id拼接
                folderSort: this.editRuleItemNode.childNodes.length>0 ? this.editRuleItemNode.childNodes.length+1 : 1, // 顺序 // 子集长度加1
            }

            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.btnLoading = true;
                    this.$http({
                        isLoading:false,
                        url: this.$http.adornUrl('ruleFolder/add'),
                        method: 'post',
                        data:  this.$http.adornData(addRuleFolderdata, false)
                    }).then(({data}) => {
                        this.btnLoading = false;
                        if (data.code == 200) {
                            this.treeVisible = false;

                            this.getRuleFolder(null,'self')
                            this.$bus.$emit('updateRuleData');
                        } else {
                            this.btnLoading = false;
                            this.$message({
                                message: "添加失败",
                                type: "error",
                            });
                        }
                    }).catch(() => {
                        this.btnLoading = false;
                        this.$message({
                            message: "添加失败",
                            type: "error",
                        });
                    })
                }
            });

        },
        editRuleFolder (formName) {
            // let folderPath =  this.getParent(this.editRuleItemNode, 'edit');
            let editRuleFolderdata = {
                folderId: this.editRuleItem.folderId,
                folderName: this.treeForm.folderName, // 规则树名称
                // folderLevel: this.editRuleItem.folderLevel && this.editRuleItem.folderLevel || '', // 级别
                // folderParentId: this.editRuleItem.folderParentId, // 父节点id
                // folderPath: folderPath.length>0 && folderPath.join('\\') || '', // 路径 // 父节点id集合
                // folderSort: this.editRuleItem.folderSort, // 顺序
            }
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.btnLoading = true;
                    this.$http({
                        isLoading:false,
                        url: this.$http.adornUrl('ruleFolder/updateByUuId'),
                        method: 'post',
                        data:  this.$http.adornData(editRuleFolderdata, false)
                    }).then(({data}) => {
                        this.btnLoading = false;
                        if (data.code == 200) {
                            this.treeVisible = false;
                          this.getRuleFolder(null,'self')

                            this.$bus.$emit('updateRuleData');
                        } else {
                            this.btnLoading = false;
                            this.$message({
                                message: "添加失败",
                                type: "error",
                            });
                        }
                    }).catch(() => {
                        this.btnLoading = false;
                        this.$message({
                            message: "编辑失败",
                            type: "error",
                        });

                    })
                }
            });

        },
        append (data,node, type) {
            this.treeTitle=type=='add'?'填写分类信息':'编辑分类信息';
            this.treeVisible=true;
            this.treeForm.folderName=type=='add'?'':data.folderName; // 类型名称赋值
            this.editRuleItem = data; // 获取本条数据
            this.editRuleItemNode = node; // 获取点击node
            this.optionType = type; // 编辑或新增
        },
        // 获取数据列表
        getDataList (data, callBack) {
            // 如何改规则节点有子节点的话folderId为空
            let dataForm = {
                folderPath: data.folderPath,
                folderId: data.folderId,
            }
            if (data.children) {
                dataForm.folderId = '';
            }
            this.$http({
            isLoading: false,
            url: this.$http.adornUrl('/rule/selectPage'),
            method: 'get',
            params: this.$http.adornParams({
                'folderPath': dataForm.folderPath,
                'folderId': dataForm.folderId,
            })
            }).then(({data}) => {
                callBack(data)
            })
        },
        remove (node, data) {
            // 删除校验改规则下是否有规则列表
            this.getDataList(data, (res) => {
                if (res.result.records.length>0) {
                    return this.$message({message: '此规则分类下存在规则，请先删除规则再删除规则分类！',type: 'warning'});
                }
                this.$confirm(`确认要该条规则吗?`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                    }).then(() => {
                        this.$http({
                            isLoading:false,
                            url: this.$http.adornUrl(`ruleFolder/delete/${data.folderId}`),
                            method: 'post',
                        }).then(({data}) => {
                            this.btnLoading = false;
                            if (data.code == 200) {
                                this.$message({
                                    message: '操作成功',
                                    type: 'success',
                                })
                              this.getRuleFolder(null,'self')
                                this.$bus.$emit('updateRuleData');
                            } else {
                                this.$message.error(data.msg)

                            }
                        }).catch(() => {
                            this.btnLoading = false;
                        })

                }).catch(() => {})

            })

        },
        nodeClick (data, node) {
            // 调用父组件的获取规则树id的方法
            if (this.isParent) {
                this.$parent.$parent[this.parentGetTreeData](data,node, this.treeData);
            } else {
                this.$emit("getTreeId", data,node, this.treeData);
            }
        },
        callCheckChange (data, checked) {
            if(this.isRelation) {
                this.$emit("checkChange", data, checked, this.treeData);
                if (checked) {
                    this.$refs.ruleTreeRoot.setCheckedNodes([data]);
                } else {
                    this.$refs.ruleTreeRoot.setCheckedNodes([]);
                }
            }
        },
        // 父组件通过data 设置复选框为勾选状态
        setCheckedByData (data) {
            this.$refs.ruleTreeRoot.setCheckedNodes([data]);
        },
        // 清楚选中的规则列表
        clearCheckedKeys () {
            this.$refs.ruleTreeRoot.setCheckedKeys([]);
            this.$refs.ruleTreeRoot.setCurrentKey(null);
        }

    },
    watch: {
      filterText(val) {
        this.$refs.ruleTreeRoot.filter(val.trim());
      },
      'treeVisible'(nval, oval) {
          if (!nval) {
            if (this.$refs['treeForm']) {
                this.$refs['treeForm'].resetFields()
            }
            this.treeForm.folderName = ''
          }

        },
        projectId (nval, oval) {
            if (nval) {
                this.getRuleFolder()
                // this.nodeClick()
            }
        }

    },

}
</script>
<style scoped lang="scss">
.rule-tree-box {
    width: 100%;
    /deep/ .filter-text {
        width: 80%;
        margin: 10px 0 10px 10px;
    }
    .custom-tree-node {
        width: 100%;
        display: flex;
        // display: inline-block;
        align-items: center;
    }
    .tree-label {
        max-width: 180px;
        display: inline-block;
        overflow:hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        -o-text-overflow:ellipsis;
    }
    .folder-icon {
        background: url(../../assets/img/folder.png);
        background-size: 100% 100%;
        background-repeat: no-repeat;
        display: inline-block;
        width: 13px;
        height: 16px;
    }
    /deep/ .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
        background-color: #e3edfa;
    }
    /deep/ .el-tree-node__children {
        overflow:visible;
    }
}

</style>
