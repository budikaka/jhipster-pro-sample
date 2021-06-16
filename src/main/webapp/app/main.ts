// with polyfills
import 'core-js/stable';
import 'regenerator-runtime/runtime';

import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store/';
// import { VueAxios } from './utils/request';
import KFormDesign from '@/components/jhi-data-form';
// import dataTable from "@/components/dataTable";
// import 'k-form-design/lib/k-form-design.css';
import { UPLOAD_IMAGE_URL, UPLOAD_FILE_URL } from '@/constants';
const jwttoken = localStorage.getItem('jhi-authenticationToken') || sessionStorage.getItem('jhi-authenticationToken');
KFormDesign.setConfig({
  uploadFile: UPLOAD_FILE_URL, // 上传文件地址
  uploadImage: UPLOAD_IMAGE_URL, // 上传图片地址
  uploadFileName: 'file',
  uploadImageName: 'image',
  uploadFileHeaders: { Authorization: `Bearer ${jwttoken}` },
  uploadImageHeaders: { Authorization: `Bearer ${jwttoken}` },
});
import 'xe-utils';
import VXETable from 'vxe-table';
import 'vxe-table/lib/index.css';
import 'vxe-table-plugin-antd/dist/style.css';
Vue.use(VXETable);
import VXETablePluginAntd from 'vxe-table-plugin-antd';
import VXEAntdJhi from '@/components/vxe-select-list-modal';
VXETable.use(VXETablePluginAntd);
VXETable.use(VXEAntdJhi);
Vue.use(KFormDesign);
import SelectListModal from '@/components/select-list-modal';
Vue.use(SelectListModal);
import vcolorpicker from 'vcolorpicker';
Vue.use(vcolorpicker);
// mock
// WARNING: `mockjs` NOT SUPPORT `IE` PLEASE DO NOT USE IN `production` ENV.
// import './mock';
// import NProgress from 'nprogress'; // progress bar
// import '@/components/NProgress/nprogress.less'; // progress
import bootstrap from './core/bootstrap';
import './core/lazy_use';
// import './permission'; // permission control
import './utils/filter'; // global filter
import './components/global.less';
// @ts-ignore
Vue.component('v-chart', VueECharts);
import * as config from './shared/config/config';
import AlertService from '@/shared/alert/alert.service';
import AccountService from './account/account.service';
import TranslationService from '@/locale/translation.service';
import UserService from '@/shared/service/user.service';
import AuthorityService from '@/entities/system/authority/authority.service';
import Authority from '@/entities/system/authority';

Vue.use(Authority);
// jhipster-needle-add-entity-service-to-main-import - JHipster will import entities services here

config.initVueApp(Vue);
const i18n = config.initI18N(Vue);
const translationService = new TranslationService(store, i18n);
const viewPermissionService = new ViewPermissionService();
const alertService = new AlertService(store);
const accountService = new AccountService(store, translationService, router, viewPermissionService);
import User from '@/entities/system/user';
Vue.use(User);
Vue.config.productionTip = false;

// mount axios Vue.$http and this.$http
// Vue.use(VueAxios);
router.beforeEach((to, from, next) => {
  // NProgress.start(); // start progress bar
  if (!to.matched.length) {
    next('/404');
    // NProgress.done(); // if current page is login will not trigger afterEach hook, so manually handle it
  }

  if (to.meta && to.meta.authorities && to.meta.authorities.length > 0) {
    if (!accountService.hasAnyAuthority(to.meta.authorities)) {
      sessionStorage.setItem('requested-url', to.fullPath);
      next('/user/login');
      // NProgress.done(); // if current page is login will not trigger afterEach hook, so manually handle it
    } else {
      // 将路由数据转换为菜单加入到系统中。
      next();
    }
  } else {
    // no authorities, so just proceed
    next();
  }
});

router.afterEach(() => {
  // NProgress.done(); // finish progress bar
});

new Vue({
  el: '#app',
  components: { App },
  template: '<App/>',
  router,
  i18n,
  store,
  created: bootstrap,
  provide: {
    accountService: () => accountService,
    alertService: () => alertService,
    userService: () => new UserService(),
    authorityService: () => new AuthorityService(),
    // jhipster-needle-add-entity-service-to-main - JHipster will import entities services here

    translationService: () => translationService,
  },
});
