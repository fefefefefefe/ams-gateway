<template>
    <div class="batch-list-box">
        <el-input
            class="filter-text"
            placeholder="输入关键字进行过滤"
            v-model="filterText">
        </el-input>
         <el-tree :data="batchTreeList" highlight-current :default-expand-all="true" v-loading="batchLoading" node-key="id" ref="batchList" :props="layoutTreeProps" @node-click="nodeClick" :filter-node-method="filterNode">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                    <span :title="node.label" class="batch-label">
                        {{node.label}}
                    </span>
                    <span class="batch-btn">
                        <el-button
                            v-if="!data.children"
                            type="text"
                            size="mini"
                            @click="() => remove(node, data)">
                            <i class="el-icon-delete" title="删除"></i>
                        </el-button>
                    </span>
                </span>
        </el-tree>
    </div>
</template>
<script>
export default {
    props: {
        batchTreeList: { // 批次列表
            default: []
        },
        isParent: { // 判断调用组件是否为当前组件的父组件
            type: Boolean,
            default: true
        },
        parentGetTreeData: { // 父组件的获取选中的批次数据的方法名
            default: 'getbatchData'
        },
        batchLoading: { // 批次列表加载状态
            type: Boolean,
            default: false
        }
    },
    data () {
        return {
            filterText: '',
            layoutTreeProps: {
                label(data, node) {
                    const config = data.__config__ || data
                    return  config.label || config.batchName
                },
            }

        }
    },
    mounted () {
    },
    activated () {
        // this.getRuleFolder();
    },
    methods: {
        nodeClick (data, node) {
            // 调用父组件的获取规则树id的方法
            if (this.isParent) {
                this.$parent.$parent[this.parentGetTreeData](data,node);
            } else {
                this.$emit("getbatchData", data,node);
            }

        },
        remove (node, data) {
            this.$confirm(`确认要该条批次吗?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
                }).then(() => {
                    this.$http({
                        isLoading:false,
                        url: this.$http.adornUrl('batch/removeBatchByIds/'),
                        method: 'DELETE',
                        data: this.$http.adornData( [data.batchId], false)
                    }).then(({data}) => {
                        if (data.code == 200) {
                            this.$message({
                                message: '删除成功',
                                type: 'success',
                            })
                            // 手动清空选中的批次id
                            this.nodeClick()
                            // 更新批次列表
                            this.$emit('refreshBitchData');
                            //更新规则列表
                            this.$emit('refreshRuleData')

                        } else {
                            this.$message.error(data.message)
                        }
                    }).catch(() => {
                    })

            }).catch(() => {})




        },
        filterNode(value, data) {
            if (!value) return true
            if (data.batchName) {
                return data.batchName.indexOf(value) !== -1;
            } else {
                return data.label.indexOf(value) !== -1;
            }
        },
        // 清楚选中的规则列表
        clearCheckedKeys () {
            this.$refs.batchList.setCheckedKeys([]);
            this.$refs.batchList.setCurrentKey(null);
        }
    },
    watch: {
        filterText(val) {
            this.$refs.batchList.filter(val.trim());
        },
    }

}
</script>
<style scoped lang="scss">
.batch-list-box {
    width: 100%;
    height: 100%;
    overflow: auto;
    /deep/ .filter-text {
        width: 80%;
        margin: 10px 0 10px 10px;
    }
    /deep/ .el-tree {
        height: 100%;
    }
    /deep/ .el-tree-node__children .el-tree-node {
        height: 35px;
        line-height: 35px;
    }
    /deep/ .is-current {
        position: relative;
        .el-icon-delete {
            color:#AF0F16;
        }
    }
    /deep/ .is-current::before {
        position: absolute;
        content: '';
        width: 2px;
        height: 25px;
        display: inline-block;
        background: #AF0F16;

        margin-left: 3px;
    }

    /deep/ .el-tree-node__children .custom-tree-node{
        display: flex;
    }

    /deep/ .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
        color: #AF0F16 !important;
        background: #fff !important;
    }
    /deep/ .el-tree-node__content:hover{
        background: #fff;
    }

    .batch-label {
        max-width: 180px;
        display: inline-block;
        overflow:hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        -o-text-overflow:ellipsis;
    }
    .batch-btn {
        padding-left: 10px;
    }
}

</style>
