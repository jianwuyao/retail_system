<template>
  <div class="xtx-carousel" @mouseenter="stop()" @mouseleave="start()">
    <img class="carousel-btn prev" @click="scrollFun(-1)" :src="LeftIcon" alt="" />
    <img class="carousel-btn next" @click="scrollFun(1)" :src="RightIcon" alt="" />
    <ul class="carousel-body" ref="carousel">
      <!-- 重复部分-无限滚动必须 -->
      <li class="carousel-item" v-for="(item, i) in imageList" :key="i">
        <img :src="item.imageUrl" @click="handleLink(item.link)" alt="" style="width: 320px; height: 120px" />
      </li>
      <!-- 默认内容 -->
      <li class="carousel-item" v-for="(item, i) in imageList" :key="i">
        <img :src="item.imageUrl" @click="handleLink(item.link)" alt="" style="width: 320px; height: 120px" />
      </li>
      <!-- 重复部分-无限滚动必须 -->
      <li class="carousel-item" v-for="(item, i) in imageList" :key="i">
        <img :src="item.imageUrl" @click="handleLink(item.link)" alt="" style="width: 320px; height: 120px" />
      </li>
    </ul>
  </div>
</template>

<script lang="ts" setup>
  import { listApi } from '/@/api/ad';
  import { BASE_URL } from '/@/store/constants';
  import LeftIcon from '/@/assets/images/left.png';
  import RightIcon from '/@/assets/images/right.png';

  const props = defineProps({
    duration: {
      type: Number,
      default: 2,
    },
    autoplay: {
      type: Boolean,
      default: false,
    },
  });

  let imageList = ref([]);
  const carousel = ref();
  let timer;
  let width = 1080;
  let itemWidth = 360;
  let left = 0;

  const autoplayFn = () => {
    clearInterval(timer);
    timer = setInterval(() => {
      scrollFun(1);
    }, props.duration * 1000);
  };

  const scrollFun = (step) => {
    if (!carousel.value) return;
    if (step == -1) {
      left += itemWidth;
    } else {
      left -= itemWidth;
    }
    if (left <= -width * 2) {
      setTimeout(() => {
        left = -width;
        carousel.value.style.transitionDuration = '0s';
        carousel.value.style.transform = `translateX(${left}px)`;
      }, 500);
    } else if (left >= -width) {
      setTimeout(() => {
        left = -width * 2;
        carousel.value.style.transitionDuration = '0s';
        carousel.value.style.transform = `translateX(${left}px)`;
      }, 500);
    } else {
      carousel.value.style.transitionDuration = '0.5s';
    }
    carousel.value.style.transform = `translateX(${left}px)`;
  };

  onMounted(() => {
    getAdList();
    timer = setInterval(() => {
      scrollFun(1);
    }, props.duration * 1000);
  });

  onUnmounted(() => {
    clearInterval(timer);
    timer = null;
  });

  const stop = () => {
    if (timer) clearInterval(timer);
  };

  const start = () => {
    autoplayFn();
  };

  const getAdList = () => {
    listApi()
      .then((res) => {
        res.data.forEach((item) => {
          item.imageUrl = BASE_URL + '/api/file/download/advertisement?name=' + item.image;
        });
        imageList.value = res.data;
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };

  const handleLink = (link) => {
    window.open(link);
  };
</script>

<style scoped lang="scss">
  .xtx-carousel {
    white-space: nowrap;
    overflow: hidden;
    width: 1080px;
    align-self: flex-start;
    display: flex;
    position: relative;

    &:hover {
      .carousel-btn {
        opacity: 1;
      }
    }

    .carousel-body {
      white-space: nowrap;
      padding: 0;
      transition: all 0.5s;
    }

    .carousel-item {
      width: 320px;
      height: 120px;
      display: inline-block;
      border-radius: 8px;
      overflow: hidden;
      margin: 0 20px;
    }

    .carousel-btn {
      width: 50px;
      height: 50px;
      background: rgba(0, 0, 0, 0.2);
      color: #fff;
      border-radius: 50%;
      position: absolute;
      top: 40%;
      transform: translateY(-40%);
      z-index: 2;
      text-align: center;
      line-height: 50px;
      opacity: 0;
      transition: all 0.5s;

      &.prev {
        left: 20px;
      }

      &.next {
        right: 20px;
      }
    }
  }
</style>
