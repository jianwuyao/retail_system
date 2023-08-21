<template>
  <div class="content-list">
    <div class="list-title">我的积分</div>
    <div class="my-score-view">
      <div class="score-title">积分余额：{{ score }}</div>
    </div>
  </div>
</template>

<script setup>
  import { detailApi } from '/@/api/user';
  import { useUserStore } from '/@/store';

  const router = useRouter();
  const userStore = useUserStore();

  let score = ref(0);

  onMounted(() => {
    getUserInfo();
  });

  const getUserInfo = () => {
    let userId = userStore.user_id;
    detailApi({ userId: userId })
      .then((res) => {
        if (res.data) {
          score.value = res.data.score;
        }
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };
</script>
<style scoped lang="less">
  .flex-view {
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

  .my-score-view {
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    margin-top: 16px;
    margin-left: 16px;

    .score-title {
      color: #111111;
      font-size: 18px;
      font-weight: bold;
    }
  }
</style>
