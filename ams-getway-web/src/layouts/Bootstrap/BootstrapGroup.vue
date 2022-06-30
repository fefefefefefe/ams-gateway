<template>
  <!-- eslint-disable vue/no-v-html -->
  <div
    class="vqb-group card"
    :class="'depth-' + depth.toString()"
  >
    <div class="vqb-group-heading card-header">
      <div class="match-type-container form-inline">
        <label
          class="mr-2"
          for="vqb-match-type"
        >
          {{ labels.matchType }}
        </label>

        <select
          style="margin-top: 5px"
          id="vqb-match-type"
          v-model="query.logicalOperator"
          class="form-control"
        >
          <option
            v-for="label in labels.matchTypes"
            :key="label.id"
            :value="label.id"
          >
            {{ label.label }}
          </option>
        </select>

        <button
          v-if="depth > 1"
          type="button"
          class="close ml-auto"
          @click="remove"
          v-html="labels.removeGroup"
        >
        </button>
      </div>
    </div>

    <div class="vqb-group-body card-body">
      <div class="rule-actions form-inline">
        <div class="form-group">
          <select
            v-model="selectedRule"
            class="form-control mr-2"
          >
            <option
              v-for="rule in rules"
              :key="rule.id"
              :value="rule"
            >
              {{ rule.label }}
            </option>
          </select>

          <button
            type="button"
            class="btn btn-secondary mr-2"
            @click="addRule"
          >
            {{ labels.addRule }}
          </button>

          <button
            v-if="depth < maxDepth"
            type="button"
            class="btn btn-secondary"
            @click="addGroup"
          >
            {{ labels.addGroup }}
          </button>
        </div>
      </div>

      <query-builder-children v-bind="$props" />
    </div>
  </div>
</template>

<script>
import QueryBuilderGroup from '../../components/vue-query-builder/QueryBuilderGroup';
import QueryBuilderRule from './BootstrapRule.vue';

export default {
  name: "QueryBuilderGroup",

  components: {
    // eslint-disable-next-line vue/no-unused-components
    'QueryBuilderRule': QueryBuilderRule
  },

  extends: QueryBuilderGroup,
}
</script>

<style scoped>
  >>>.form-control{
    margin-left: 10px;
    height: 34px;
    padding: 6px 12px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #555;
    background-color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
    -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
    -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
  }
  >>>.btn{
    margin: 5px;
    display: inline-block;
    padding: 6px 12px;
    margin-bottom: 0;
    font-size: 14px;
    font-weight: normal;
    line-height: 1.42857143;
    text-align: center;
    white-space: nowrap;
    vertical-align: middle;
    -ms-touch-action: manipulation;
    touch-action: manipulation;
    cursor: pointer;
    border: 1px solid transparent;
    border-radius: 4px;
    margin-top: -3px;
    margin-left: 9px;
  }
  >>>.close{
    float: right;
    font-size: 1.5rem;
    font-weight: 700;
    line-height: 1;
    color: #000;
    text-shadow: 0 1px 0 #fff;
    opacity: .5;
    margin-right: 15px;
  }
  >>>.el-input__inner{
    margin-left: 10px;
  }
  >>>.form-group{
    margin-top: 10px;
  }
  >>>.form-inline{
    padding-top: 10px;
    padding-left: 30px;
  }
  >>>.vqb-rule{
    border-left: 2px solid #8bc34a;
    margin-top: 15px;
    background-color: #efefef;
    height: 60px;
  }
</style>
