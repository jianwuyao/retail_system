<template>
  <div class="content">
    <div class="content-left">
      <div class="left-search-item">
        <h4>商品分类</h4>
        <a-tree
          :tree-data="contentData.classificationData"
          :selected-keys="contentData.selectedKeys"
          @select="onSelect"
          style="min-height: 220px"
        />
      </div>
      <div class="left-search-item">
        <h4>热门标签</h4>
        <div class="tag-view tag-flex-view">
          <span
            class="tag"
            :class="{ 'tag-select': contentData.selectTagId === item.id }"
            v-for="item in contentData.tagData"
            :key="item.id"
            @click="clickTag(item.id)"
            style="margin: 8px; height: 40px; line-height: 40px"
            >{{ item.title }}</span
          >
        </div>
      </div>
    </div>
    <div class="content-right">
      <div class="top-select-view flex-view">
        <div class="order-view" style="font-size: 20px">
          <span class="title"></span>
          <span
            class="tab"
            :class="contentData.selectTabIndex === index ? 'tab-select' : ''"
            v-for="(item, index) in contentData.tabData"
            :key="index"
            @click="selectTab(index)"
          >
            {{ item }}
          </span>
          <span :style="{ left: contentData.tabUnderLeft + 'px', width: '30px' }" class="tab-underline"></span>
        </div>
      </div>
      <a-spin :spinning="contentData.loading" style="min-height: 200px">
        <div class="pc-thing-list flex-view">
          <div v-for="item in contentData.pageData" :key="item.id" class="thing-item item-column-3">
            <div class="img-view"> <img :src="item.cover" @click="handleDetail(item)" /></div>
            <div class="info-view">
              <div class="thing-name">{{ item.title.substring(0, 12) }}</div>
              <div class="thing-price" style="color: #f00; font-size: 20px; font-weight: bold">￥{{ item.price }}</div>
              <button class="buy-btn" @click="handleOrder(item)"> <img :src="AddIcon" /> </button>
            </div>
          </div>
          <div v-if="contentData.pageData.length <= 0 && !contentData.loading" class="no-data" style="font-size: 20px">暂无数据</div>
        </div>
      </a-spin>
      <div class="page-view" style="width: 980px">
        <a-pagination
          v-model="contentData.page"
          size="large"
          @change="changePage"
          :hideOnSinglePage="true"
          :defaultPageSize="contentData.pageSize"
          :total="contentData.total"
          :showSizeChanger="false"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
  import AddIcon from '/@/assets/images/add.svg';
  import { listApi as listClassificationList } from '/@/api/classification';
  import { listApi as listTagList } from '/@/api/tag';
  import { listApi as listThingList } from '/@/api/thing';
  import { addApi as addShoppingCart } from '/@/api/shoppingCart';
  import { BASE_URL } from '/@/store/constants';
  import { useUserStore } from '/@/store';
  import { message } from 'ant-design-vue';

  const userStore = useUserStore();
  const router = useRouter();

  const contentData = reactive({
    selectX: 0,
    selectTagId: -1,
    classificationData: [],
    selectedKeys: [],
    tagData: [],
    loading: false,

    tabData: ['最新', '最热', '推荐'],
    selectTabIndex: 0,
    tabUnderLeft: 12,

    thingData: [],
    pageData: [],

    page: 1,
    total: 0,
    pageSize: 8,
  });

  onMounted(() => {
    initSider();
    getThingList({});
  });

  const initSider = () => {
    contentData.classificationData.push({ key: '-1', title: '全部' });
    listClassificationList().then((res) => {
      res.data.forEach((item) => {
        item.key = item.id;
        contentData.classificationData.push(item);
      });
    });
    listTagList().then((res) => {
      contentData.tagData = res.data;
    });
  };
  const getSelectedKey = () => {
    if (contentData.selectedKeys.length > 0) {
      return contentData.selectedKeys[0];
    } else {
      return -1;
    }
  };
  const onSelect = (selectedKeys) => {
    contentData.selectedKeys = selectedKeys;
    console.log(contentData.selectedKeys[0]);
    if (contentData.selectedKeys.length > 0) {
      getThingList({ classification: getSelectedKey() });
    }
    contentData.selectTagId = -1;
  };
  const clickTag = (index) => {
    contentData.selectedKeys = [];
    contentData.selectTagId = index;
    getThingList({ tag: contentData.selectTagId });
  };

  // 最新|最热|推荐
  const selectTab = (index) => {
    contentData.selectTabIndex = index;
    contentData.tabUnderLeft = 12 + 60 * index;
    console.log(contentData.selectTabIndex);
    let sort = index === 0 ? 'recent' : index === 1 ? 'hot' : 'recommend';
    const data = { sort: sort };
    if (contentData.selectTagId !== -1) {
      data['tag'] = contentData.selectTagId;
    } else {
      data['classification'] = getSelectedKey();
    }
    getThingList(data);
  };
  const handleDetail = (item) => {
    // 跳转新页面
    let text = router.resolve({ name: 'detail', query: { id: item.id } });
    window.open(text.href, '_blank');
  };
  // 分页事件
  const changePage = (page) => {
    contentData.page = page;
    let start = (contentData.page - 1) * contentData.pageSize;
    contentData.pageData = contentData.thingData.slice(start, start + contentData.pageSize);
  };
  const getThingList = (data) => {
    contentData.loading = true;
    listThingList(data)
      .then((res) => {
        contentData.loading = false;
        res.data.forEach((item, index) => {
          if (item.cover) {
            item.cover = BASE_URL + '/api/file/download/thing?name=' + item.cover;
          }
        });
        contentData.thingData = res.data;
        contentData.total = contentData.thingData.length;
        changePage(1);
      })
      .catch((err) => {
        console.log(err.msg);
        contentData.loading = false;
      });
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
  .content {
    display: flex;
    flex-direction: row;
    width: 1300px;
    margin: 80px auto;
  }

  .content-left {
    width: 160px;
    margin-right: 32px;
  }

  .left-search-item {
    overflow: hidden;
    border-bottom: 1px solid #cedce4;
    margin-top: 24px;
    padding-bottom: 24px;
  }

  h4 {
    color: #4d4d4d;
    font-weight: 600;
    font-size: 24px;
    line-height: 28px;
    height: 28px;
  }

  .category-item {
    cursor: pointer;
    color: #333;
    margin: 12px 0 0;
    padding-left: 16px;
  }

  ul {
    margin: 0;
    padding: 0;
  }

  ul {
    list-style-type: none;
  }

  li {
    margin: 4px 0 0;
    display: list-item;
    text-align: -webkit-match-parent;
  }

  .child {
    color: #333;
    padding-left: 16px;
  }

  .child:hover {
    color: #4684e2;
  }

  .select {
    color: #4684e2;
  }

  .flex-view {
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    display: flex;
  }

  .name {
    font-size: 14px;
  }

  .name:hover {
    color: #4684e2;
  }

  .count {
    font-size: 14px;
    color: #999;
  }

  .check-item {
    font-size: 0;
    height: 18px;
    line-height: 12px;
    margin: 12px 0 0;
    color: #333;
    cursor: pointer;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
  }

  .check-item input {
    cursor: pointer;
  }

  .check-item label {
    font-size: 14px;
    margin-left: 12px;
    cursor: pointer;
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
  }

  .tag-view {
    -ms-flex-wrap: wrap;
    flex-wrap: wrap;
    margin-top: 4px;
  }

  .tag-flex-view {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
  }

  .tag {
    background: #fff;
    border: 1px solid #a1adc6;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    border-radius: 16px;
    height: 30px;
    line-height: 30px;
    padding: 0 15px;
    margin: 8px 8px 0 0;
    cursor: pointer;
    font-size: 20px;
    color: #152833;
  }

  .tag:hover {
    background: #4684e3;
    color: #fff;
    border: 1px solid #4684e3;
  }

  .tag-select {
    background: #4684e3;
    color: #fff;
    border: 1px solid #4684e3;
  }

  .content-right {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    padding-top: 12px;

    .pc-search-view {
      margin: 0 0 24px;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;

      .search-icon {
        width: 20px;
        height: 20px;
        -webkit-box-flex: 0;
        -ms-flex: 0 0 20px;
        flex: 0 0 20px;
        margin-right: 16px;
      }

      input {
        outline: none;
        border: 0px;
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
        border-bottom: 1px solid #cedce4;
        color: #152844;
        font-size: 14px;
        height: 22px;
        line-height: 22px;
        -ms-flex-item-align: end;
        align-self: flex-end;
        padding-bottom: 8px;
      }

      .clear-search-icon {
        position: relative;
        left: -20px;
        cursor: pointer;
      }

      button {
        outline: none;
        border: none;
        font-size: 14px;
        color: #fff;
        background: #288dda;
        border-radius: 32px;
        width: 88px;
        height: 32px;
        line-height: 32px;
        margin-left: 2px;
        cursor: pointer;
      }

      .float-count {
        color: #999;
        margin-left: 24px;
      }
    }

    .flex-view {
      display: flex;
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

    .pc-thing-list {
      -ms-flex-wrap: wrap;
      flex-wrap: wrap;

      .thing-item {
        min-width: 260px;
        max-width: 260px;
        position: relative;
        flex: 1;
        height: fit-content;
        overflow: hidden;
        margin-top: 26px;
        margin-bottom: 10px;
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

      .no-data {
        height: 200px;
        line-height: 200px;
        text-align: center;
        width: 100%;
        font-size: 16px;
        color: #152844;
      }
    }

    .page-view {
      width: 100%;
      text-align: center;
      margin-top: 48px;
    }
  }

  :deep(.ant-tree) {
    font-size: 20px;
  }

  :deep(.ant-tree .ant-tree-node-content-wrapper) {
    line-height: 50px;
  }
</style>
