import Vue from 'vue'

import App from './App.vue'
import router from './router'
import './plugins/element'
import './styles/index.css'

new Vue({
  router,
  render: (createElement) => createElement(App),
}).$mount('#app')
