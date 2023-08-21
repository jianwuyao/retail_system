<template>
  <div class="content-list">
    <div class="list-title">我的消息</div>
    <a-spin :spinning="loading" style="min-height: 200px">
      <div class="list-content">
        <div class="notification-view">
          <div class="list">
            <div class="notification-item flex-view" v-for="item in msgData">
              <div class="content-box">
                <div class="header">
                  <img :src="MessageIcon" class="avatar" />
                  <span class="title-txt">《{{ item.title }}》</span>
                  <span class="time">{{ item.createTime }}</span>
                </div>
                <div class="content">
                  <p>{{ item.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
  import { listApi } from '/@/api/notice';
  import MessageIcon from '/@/assets/images/message.png';

  let loading = ref(false);
  let msgData = ref([]);

  onMounted(() => {
    getMessageList();
  });

  const getMessageList = () => {
    loading.value = true;
    listApi()
      .then((res) => {
        msgData.value = res.data;
        loading.value = false;
      })
      .catch((err) => {
        console.log(err.msg);
        loading.value = false;
      });
  };
</script>
<style scoped lang="less">
  progress {
    vertical-align: baseline;
  }

  .flex-view {
    display: flex;
  }

  input,
  textarea {
    outline: none;
    border-style: none;
  }

  button {
    padding: 0;
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

  .notification-item {
    padding-top: 16px;

    .content-box {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      border-bottom: 1px dashed #e9e9e9;
      padding: 4px 0 12px;
    }

    .header {
      margin-bottom: 16px;
    }

    .avatar {
      width: 40px;
      height: 40px;
      margin-right: 5px;
      margin-bottom: 6px;
    }

    .title-txt {
      color: #315c9e;
      font-weight: bold;
      font-size: 24px;
      line-height: 30px;
      height: 30px;
    }

    .time {
      color: #a1adc5;
      font-size: 18px;
      margin-left: 10px;
      line-height: 30px;
      height: 30px;
    }

    .content {
      margin-top: 4px;
      color: #484848;
      font-size: 20px;
      line-height: 20px;
      margin-left: 60px;
    }
  }
</style>
