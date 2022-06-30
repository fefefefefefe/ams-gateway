<template>
  <!-- eslint-disable vue/no-v-html -->
  <div class="vqb-rule card">
    <div class="form-inline">
      <label class="mr-5">{{ rule.label }}</label>

      <!-- List of operands (optional) -->
      <select
        v-if="typeof rule.operands !== 'undefined'"
        v-model="query.operand"
        class="form-control mr-2"
      >
        <option
          v-for="operand in rule.operands"
          :key="operand"
        >
          {{ operand }}
        </option>
      </select>

      <!-- List of operators (e.g. =, !=, >, <) -->
      <!-- 2022年3月11日 18:10:34 ZhangSiWeiG修改 支持显示值和真实值-->
      <select
        v-if="typeof rule.operators !== 'undefined' && rule.operators.length >= 1"
        v-model="query.operator"
        class="form-control mr-2"
      >
        <option
          v-for="operator in rule.operators"
          :key="operator.id"
          :value="operator.id"
        >
          {{ operator.label}}
        </option>
      </select>

      <!-- Basic text input -->
      <input
        v-if="rule.inputType === 'text'"
        v-model="query.value"
        class="form-control"
        type="text"
        :placeholder="labels.textInputPlaceholder"
      >

      <!-- Basic number input -->
      <input
        v-if="rule.inputType === 'number'"
        v-model="query.value"
        class="form-control"
        type="number"
      >

      <!-- Datepicker -->
      <input
        v-if="rule.inputType === 'date'"
        v-model="query.value"
        class="form-control"
        type="date"
      >

      <!-- Custom component input -->
      <div
        v-if="isCustomComponent"
        class="vqb-custom-component-wrap"
      >
        <component
          :is="rule.component"
          :value="query.value"
          @input="updateQuery"
        />
      </div>

      <!-- Checkbox input -->
      <template
        v-if="rule.inputType === 'checkbox'"
      >
        <div
          v-for="choice in rule.choices"
          :key="choice.value"
          class="form-check form-check-inline"
        >
          <input
            :id="'depth' + depth + '-' + rule.id + '-' + index + '-' + choice.value"
            v-model="query.value"
            type="checkbox"
            :value="choice.value"
            class="form-check-input"
          >
          <label
            class="form-check-label"
            :for="'depth' + depth + '-' + rule.id + '-' + index + '-' + choice.value"
          >
            {{ choice.label }}
          </label>
        </div>
      </template>

      <!-- Radio input -->
      <template
        v-if="rule.inputType === 'radio'"
      >
        <div
          v-for="choice in rule.choices"
          :key="choice.value"
          class="form-check form-check-inline"
        >
          <input
            :id="'depth' + depth + '-' + rule.id + '-' + index + '-' + choice.value"
            v-model="query.value"
            :name="'depth' + depth + '-' + rule.id + '-' + index"
            type="radio"
            :value="choice.value"
            class="form-check-input"
          >
          <label
            class="form-check-label"
            :for="'depth' + depth + '-' + rule.id + '-' + index + '-' + choice.value"
          >
            {{ choice.label }}
          </label>
        </div>
      </template>

      <!-- Select without groups -->
      <select
        v-if="rule.inputType === 'select' && !hasOptionGroups"
        v-model="query.value"
        class="form-control"
        :multiple="rule.type === 'multi-select'"
      >
        <option
          v-for="option in selectOptions"
          :key="option.value"
          :value="option.value"
        >
          {{ option.label }}
        </option>
      </select>

      <!-- Select with groups -->
      <select
        v-if="rule.inputType === 'select' && hasOptionGroups"
        v-model="query.value"
        class="form-control"
        :multiple="rule.type === 'multi-select'"
      >
        <optgroup
          v-for="(option, option_key) in selectOptions"
          :key="option_key"
          :label="option_key"
        >
          <option
            v-for="sub_option in option"
            :key="sub_option.value"
            :value="sub_option.value"
          >
            {{ sub_option.label }}
          </option>
        </optgroup>
      </select>

      <!-- 添加输入框加下拉框 -->
      <el-autocomplete
        v-if="rule.inputType === 'inputselect'"
        ref="input"
        class="inline-input"
        v-model="query.value"
        :fetch-suggestions="querySearch"
        placeholder="请输入内容"
        style="width:500px" >
        <!-- <i
          class="el-icon-arrow-down el-input__icon"
          slot="suffix"
          @click="handleIconClick"
        ></i> -->
      </el-autocomplete>

      <!-- Remove rule button -->
      <button
        type="button"
        class="close ml-auto"
        @click="remove"
        v-html="labels.removeRule"
      >
      </button>
    </div>
  </div>
</template>

<script>
import QueryBuilderRule from '../../components/vue-query-builder/QueryBuilderRule.vue';

export default {
  extends: QueryBuilderRule
}
</script>
