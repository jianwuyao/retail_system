<template>
  <div class="content-list">
    <div class="list-title">我的评论</div>
    <div class="list-content">
      <div class="comment-view">
        <a-spin :spinning="loading" style="min-height: 200px">
          <div class="comment-list">
            <div class="comment-item flex-view" v-for="item in commentData">
              <img :src="item.cover" class="avatar" />
              <div class="infos">
                <div class="name flex-view">
                  <h3></h3>
                  <h3 @click="handleClickTitle(item)">评论商品：{{ item.title }}</h3>
                </div>
                <div class="time">评论时间：{{ item.createTime }}</div>
                <div class="content">评论内容：{{ item.content }}</div>
              </div>
            </div>
          </div>
        </a-spin>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { useUserStore } from '/@/store';
  import { listUserCommentsApi } from '/@/api/comment';
  import { BASE_URL } from '/@/store/constants';

  const router = useRouter();
  const userStore = useUserStore();

  const loading = ref(false);
  const commentData = ref([]);

  onMounted(() => {
    getCommentList();
  });

  const handleClickTitle = (record) => {
    let text = router.resolve({ name: 'detail', query: { id: record.thingId } });
    window.open(text.href, '_blank');
  };

  const getCommentList = () => {
    loading.value = true;
    let userId = userStore.user_id;
    listUserCommentsApi({ userId: userId })
      .then((res) => {
        res.data.forEach((item) => {
          item.cover = BASE_URL + '/api/file/download/thing?name=' + item.cover;
        });
        commentData.value = res.data;
        loading.value = false;
      })
      .catch((err) => {
        message.error(err.msg || '网络异常');
        loading.value = false;
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
    flex: 1;

    .list-title {
      color: #152844;
      font-weight: bold;
      font-size: 20px;
      height: 48px;
      margin-top: 10px;
      border-bottom: 1px solid #cedce4;
    }
  }

  .comment-view {
    overflow: hidden;

    .comment-list {
      margin: 8px auto;
    }

    .comment-item {
      padding: 15px 0;

      .avatar {
        width: 80px;
        height: 80px;
        border-radius: 2px;
        margin-right: 30px;
        border: 1px solid rgba(100, 100, 100, 0.3);
      }

      .infos {
        position: relative;
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
      }

      .name {
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
        cursor: pointer;
      }

      h3 {
        color: #152844;
        font-weight: bold;
        font-size: 15px;
        margin: 2px 0 2px 0;
      }

      .time {
        color: #484848;
        font-size: 15px;
        margin-bottom: 6px;
      }

      .content {
        color: #484848;
        font-weight: bold;
        font-size: 18px;
        line-height: 22px;
      }
    }
  }
</style>
