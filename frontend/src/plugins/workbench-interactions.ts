/** 注册工作台显式交互指令，保持菜单与基础组件行为不变。 */
import Vue from 'vue'
import { workbenchTable } from '../directives/workbench-table'
import { workbenchDialog } from '../directives/workbench-dialog'

Vue.directive('workbench-table', workbenchTable)
Vue.directive('workbench-dialog', workbenchDialog)
