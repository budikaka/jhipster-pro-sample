import BasicLayout from '@/layouts/BasicLayout.vue';
import { RouteConfig } from 'vue-router';
// jhipster-needle-add-client-root-folder-router-to-business-router-import - JHipster will import entities to the client root folder router here

export const entitiesRoute: RouteConfig = {
  path: '/entities',
  name: 'entities',
  component: BasicLayout,
  meta: { authorities: ['ROLE_ADMIN'], title: '业务' },
  children: [
    // jhipster-needle-add-client-root-folder-router-to-business-router - JHipster will import entities to the client root folder router here
  ],
};
