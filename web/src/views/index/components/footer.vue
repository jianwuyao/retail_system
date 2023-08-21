<template>
  <div class="footer-view">
    <carouselPlay :speed="2" :direction="'left'" />
    <div class="foot-link-box flex-view">
      <a href="" class="foot-link" target="_blank">GitHub</a>
      <div class="link-split"></div>
      <a href="/admin" class="foot-link" target="_blank">后台管理</a>
    </div>
  </div>
</template>

<script>
  import carouselPlay from '/@/views/index/components/carousel.vue';
  export default {
    components: {
      carouselPlay,
    },
    data() {
      return {};
    },
  };
</script>

<style scoped lang="less">
  .flex-view {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
  }

  .footer-view {
    max-width: 1200px;
    margin: 0 auto;
    padding: 10px 0 20px 54px;

    .foot-link-box {
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      overflow: hidden;
      -webkit-box-pack: center;
      -ms-flex-pack: center;
      justify-content: center;
      .foot-link {
        margin: 0 30px;
        color: #2a4f88;
      }
      a {
        background-color: transparent;
        text-decoration: none;
        color: var(--mid-blue);
      }
    }
  }
</style>
