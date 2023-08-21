<template>
  <div class="content-list">
    <div class="list-title">我的收藏</div>
    <div role="tablist" class="list-tabs-view flex-view"> </div>
    <div class="list-content">
      <div class="collect-thing-view">
        <div class="thing-list flex-view">
          <div class="thing-item item-column-3" v-for="(item, index) in pageData.collectData" :key="index">
            <div class="remove" @click="handleRemove(item)">移出</div>
            <div class="img-view" @click="handleClickItem(item)">
              <img :src="item.cover" />
            </div>
            <div class="info-view">
              <h3 class="thing-name">{{ item.title }}</h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { message } from 'ant-design-vue';
  import { userCollectListApi, unCollectApi } from '/@/api/thingCollect';
  import { BASE_URL } from '/@/store/constants';
  import { useUserStore } from '/@/store';

  const router = useRouter();
  const userStore = useUserStore();

  const pageData = reactive({
    collectData: [],
  });

  onMounted(() => {
    getCollectThingList();
  });

  const handleClickItem = (record) => {
    let text = router.resolve({ name: 'detail', query: { id: record.thingId } });
    window.open(text.href, '_blank');
  };
  const handleRemove = (record) => {
    unCollectApi({ id: record.id })
      .then((res) => {
        message.success('移除成功');
        getCollectThingList();
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };
  const getCollectThingList = () => {
    let userId = userStore.user_id;
    userCollectListApi({ userId: userId })
      .then((res) => {
        res.data.forEach((item) => {
          item.cover = BASE_URL + '/api/file/download/thing?name=' + item.cover;
        });
        pageData.collectData = res.data;
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };
</script>
<style scoped lang="less">
  .flex-view {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
  }

  .content-list {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;

    .list-title {
      color: #152844;
      font-weight: bold;
      font-size: 20px;
      height: 48px;
      margin-top: 10px;
      border-bottom: 1px solid #cedce4;
    }

    .list-tabs-view {
      position: relative;
      height: 12px;
      line-height: 42px;
    }
  }

  .thing-list {
    -ms-flex-wrap: wrap;
    flex-wrap: wrap;
    -webkit-box-pack: start;
    -ms-flex-pack: start;
    justify-content: flex-start;

    .thing-item {
      position: relative;
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      margin-right: 30px;
      min-width: 200px;
      max-width: 200px;
      height: fit-content;
      border-radius: 4px;
      overflow: hidden;
      margin-top: 16px;
      cursor: pointer;

      .remove {
        position: absolute;
        right: 8px;
        top: 8px;
        width: 60px;
        height: 24px;
        text-align: center;
        line-height: 24px;
        color: #fff;
        background: #0951ea;
        border-radius: 8px;
        cursor: pointer;
        font-size: 18px;
      }

      .img-view {
        background: #eaf1f5;
        font-size: 0;
        text-align: center;
        height: 200px;

        img {
          max-width: 100%;
          height: 100%;
          display: block;
          margin: 0 auto;
          border-radius: 4px;
          -webkit-box-sizing: border-box;
          box-sizing: border-box;
        }
      }

      .info-view {
        background: #f6f9fb;
        text-align: center;
        height: 60px;
        overflow: hidden;
        padding: 0 16px;

        h3 {
          color: #1c355a;
          font-weight: bold;
          font-size: 18px;
          line-height: 20px;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          margin: 12px 0 8px;
        }
      }
    }
  }
</style>
