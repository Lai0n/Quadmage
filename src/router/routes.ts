import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('src/pages/AppIndex.vue') }],
  },

  {
    path: '/:catchAll(.*)*',
    component: () => import('src/pages/Error-404.vue'),
  },
];

export default routes;
