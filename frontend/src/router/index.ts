import Vue from 'vue'
import Router from 'vue-router'

import WorkbenchHome from '../views/WorkbenchHome.vue'

Vue.use(Router)

export default new Router({
  mode: 'hash',
  routes: [
    {
      path: '/',
      name: 'workbench-home',
      component: WorkbenchHome,
      meta: {
        title: '任务总览',
      },
    },
  ],
})
