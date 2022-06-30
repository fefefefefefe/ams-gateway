import httpRequest from '@/utils/httpRequest'
export default {
//   namespaced: true,
  state: {
    //table高度
    dataSortName:'', //name
    dataSortId:'',//id
    editTag:'',
    baseCodes:''
  },
  mutations: {
     SET_DATA_SORTNAME (state, name) {
      state.dataSortName = name
    },
     SET_DATA_SORTID(state, data) {
      state.dataSortId = data
    },
    SET_BASE_CODE(state,list){
      state.baseCodes = list
      console.log(list)
    },
    SET_EDITAG(state,data){
      state.editTag = data
    }
    
  },
  actions: {
    setDataSortName({commit},status){
      commit('SET_DATA_SORTNAME', status)
    },
    setDataSortId({commit},status){
      commit('SET_DATA_SORTID', status)
    },
    setBaseCode({commit},status){
      commit('SET_BASE_CODE', status)
    },
    setEditTag({commit},status){
      commit('SET_EDITAG', status)
    }
  },
}
