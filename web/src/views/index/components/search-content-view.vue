<template>
  <div class="content-margin">
    <div class="search-name-box">
      <span>与 </span>
      <span class="strong">{{ searchRequestParams.key }}</span>
      <span> 相关的内容</span>
    </div>
    <div class="content-list">
      <div class="top-select-view flex-view">
        <div class="order-view" style="font-size: 20px">
          <span class="title"></span>
          <span
            class="tab"
            :class="tData.selectTabIndex === index ? 'tab-select' : ''"
            v-for="(item, index) in tData.tabData"
            :key="index"
            @click="selectTab(index)"
          >
            {{ item }}
          </span>
          <span :style="{ left: tData.tabUnderLeft + 'px', width: '30px' }" class="tab-underline"></span>
        </div>
      </div>
      <div class="thing-list">
        <a-spin :spinning="tData.loading" style="min-height: 200px">
          <div class="things flex-view">
            <div class="thing-item item-column-4" v-for="item in tData.pageData">
              <div class="img-view"> <img :src="item.cover" @click="handleDetail(item)" /></div>
              <div class="info-view">
                <div class="thing-name">{{ item.title.substring(0, 12) }}</div>
                <div class="thing-price" style="color: #f00; font-size: 20px; font-weight: bold">￥{{ item.price }}</div>
                <button class="buy-btn" @click="handleOrder(item)"> <img :src="AddIcon" /> </button>
              </div>
            </div>
          </div>
        </a-spin>
        <div class="page-view" style="">
          <a-pagination
            v-model:value="searchRequestParams.page"
            size="small"
            @change="changePage"
            :hideOnSinglePage="true"
            :defaultPageSize="searchRequestParams.size"
            :total="tData.total"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import AddIcon from '/@/assets/images/add.svg';
  import { searchApi } from '/@/api/thing';
  import { addApi as addShoppingCart } from '/@/api/shoppingCart';
  import { BASE_URL } from '/@/store/constants';
  import { useUserStore } from '/@/store';
  import { message } from 'ant-design-vue';

  const userStore = useUserStore();
  const router = useRouter();
  const route = useRoute();

  const tData = reactive({
    loading: false,
    pageData: [],
    total: 0,

    tabData: ['价格', '浏览数'],
    selectTabIndex: 0,
    tabUnderLeft: 12,
  });

  const searchRequestParams = reactive({
    key: '',
    page: 1,
    size: 8,
    sort: 'price',
  });

  onMounted(() => {
    search();
  });
  const search = () => {
    searchRequestParams.page = 1;
    searchRequestParams.key = route.query.keyword.trim();
    if (searchRequestParams.key != null && searchRequestParams.key !== '') {
      searchThingList();
    }
  };
  // 分页事件
  const changePage = (page) => {
    searchRequestParams.page = page;
    searchThingList();
  };
  const handleDetail = (item) => {
    // 跳转新页面
    let text = router.resolve({ name: 'detail', query: { id: item.id } });
    window.open(text.href, '_blank');
  };
  const searchThingList = () => {
    tData.loading = true;
    const formData = new FormData();
    formData.append('key', searchRequestParams.key);
    formData.append('page', searchRequestParams.page);
    formData.append('size', searchRequestParams.size);
    formData.append('sortBy', searchRequestParams.sort);
    searchApi(formData)
      .then((res) => {
        res.data.things.forEach((item) => {
          if (item.cover) {
            item.cover = BASE_URL + '/api/file/download/thing?name=' + item.cover;
          }
        });
        tData.pageData = res.data.things;
        tData.total = res.data.total;
        tData.loading = false;
      })
      .catch((err) => {
        console.log(err.msg);
        tData.loading = false;
      });
  };
  // 价格|浏览数
  const selectTab = (index) => {
    tData.selectTabIndex = index;
    tData.tabUnderLeft = 12 + 70 * index;
    searchRequestParams.sort = index === 0 ? 'price' : 'browse_count';
    searchThingList();
  };
  const handleOrder = (item) => {
    let userId = userStore.user_id;
    if (userId) {
      const formData = new FormData();
      formData.append('thingId', item.id);
      formData.append('classificationId', item.classificationId);
      formData.append('title', item.title);
      formData.append('userId', userId);
      formData.append('price', item.price);
      addShoppingCart(formData)
        .then((res) => {
          message.success('已添加到购物车');
        })
        .catch((err) => {
          console.log(err.msg);
        });
    } else {
      message.warn('请先登录！');
    }
  };
</script>
<style scoped lang="less">
  .content-margin {
    vertical-align: center;
    margin: 156px 0 100px;
  }

  .page-view {
    width: 100%;
    text-align: center;
    margin-top: 48px;
  }

  .search-name-box {
    background: #f5f9fb;
    height: 100px;
    line-height: 110px;
    font-size: 22px;
    font-weight: bold;
    color: #152844;
    text-align: center;
    position: fixed;
    top: 45px;
    left: 0;
    z-index: 1;
    width: calc(100% - 8px);

    .strong {
      color: #f53535;
      font-weight: 600;
      margin: 0 4px;
    }
  }

  .things {
    -ms-flex-wrap: wrap;
    flex-wrap: wrap;
  }

  .flex-view {
    display: flex;
  }

  .thing-item {
    min-width: 200px;
    max-width: 200px;
    position: relative;
    flex: 1;
    height: fit-content;
    overflow: hidden;
    margin: 26px 37.5px 20px;
    cursor: pointer;

    .img-view {
      height: 200px;
      width: 200px;

      img {
        height: 200px;
        width: 200px;
        margin: 0 auto;
        background-size: cover;
        object-fit: cover;
        border: 1px solid rgba(100, 100, 100, 0.3);
      }
    }

    .info-view {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      display: flex;
      padding: 5px 0 0 2px;

      .thing-name {
        -webkit-box-flex: 0;
        -ms-flex: 0 0 100px;
        flex: 0 0 100px;
        line-height: 30px;
        margin-right: 5px;
        color: #0f1111 !important;
        font-size: 20px !important;
        font-weight: bold !important;
        font-style: normal !important;
        text-transform: none !important;
        text-decoration: none !important;
        display: inline;
      }
      .thing-price {
        -webkit-box-flex: 0;
        -ms-flex: 0 0 60px;
        flex: 0 0 60px;
        line-height: 30px;
        display: inline;
      }
      .buy-btn {
        -webkit-box-flex: 0;
        -ms-flex: 0 0 30px;
        flex: 0 0 30px;
        cursor: pointer;
        background: #4684e2;
        border-radius: 4px;
        text-align: center;
        color: #fff;
        height: 30px;
        line-height: 30px;
        width: 30px;
        outline: none;
        border: none;
      }
      .buy-btn img {
        width: 15px;
        vertical-align: center;
        horiz-align: center;
      }
    }
  }

  .a-price-symbol {
    top: -0.5em;
    font-size: 12px;
  }

  .a-price {
    color: #0f1111;
    font-size: 21px;
  }

  .top-select-view {
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    height: 40px;
    line-height: 40px;
    margin-left: 30px;

    .order-view {
      position: relative;
      color: #6c6c6c;
      font-size: 14px;

      .title {
        margin-right: 8px;
      }

      .tab {
        color: #999;
        margin-right: 20px;
        cursor: pointer;
      }

      .tab-select {
        color: #152844;
      }

      .tab-underline {
        position: absolute;
        bottom: 0;
        left: 84px;
        width: 16px;
        height: 4px;
        background: #4684e2;
        -webkit-transition: left 0.3s;
        transition: left 0.3s;
      }
    }
  }
</style>
