/** 工作台应用入口，加载基础组件、显式交互能力和统一样式。 */
import Vue from 'vue'

import App from './App.vue'
import router from './router'
import './plugins/element'
import './plugins/workbench-interactions'
import './styles/index.css'
import './styles/workbench-compact.css'

new Vue({
  router,
  render: (createElement) => createElement(App),
}).$mount('#app')
