<template>
  <div class="detail">
    <Header />
    <div class="detail-content">
      <div class="detail-content-top">
        <div style="position: relative">
          <div class="thing-infos-view">
            <div class="thing-infos">
              <div class="thing-img-box">
                <img :src="detailData.cover" />
              </div>
              <div class="thing-info-box">
                <div class="thing-state">
                  <span class="state hidden-sm">商品详情</span>
                </div>
                <h1 class="thing-name">{{ detailData.title }}</h1>
                <div class="thing-classification">
                  <span class="name" style="color: #98c355">{{ detailData.classification }}</span>
                </div>
                <span style="color: #f00; font-size: 24px; font-weight: bold">￥{{ detailData.price }}</span>
                <button class="buy-btn" @click="handleOrder(detailData)">
                  <img :src="AddIcon" />
                  <span>添加购物车</span>
                </button>
              </div>
              <div class="thing-count-box">
                <div class="count-text">
                  <img :src="CountIcon" />
                  <span>浏览数： {{ detailData.browseCount }}</span>
                </div>
                <div class="count-text">
                  <img :src="CountIcon" />
                  <span>心愿数： {{ detailData.wishCount }}</span>
                </div>
                <div class="count-text">
                  <img :src="CountIcon" />
                  <span>收藏数： {{ detailData.collectCount }}</span>
                </div>
              </div>
            </div>
            <div class="thing-counts hidden-sm">
              <div class="count-item flex-view pointer" @click="addToWish()">
                <div class="count-img">
                  <img :src="WantIcon" />
                </div>
                <div class="count-box flex-view">
                  <div class="count-text-box">
                    <span class="count-title">加入心愿单</span>
                  </div>
                </div>
              </div>
              <div class="count-item flex-view pointer" @click="collect()">
                <div class="count-img">
                  <img :src="RecommendIcon" />
                </div>
                <div class="count-box flex-view">
                  <div class="count-text-box">
                    <span class="count-title">收藏</span>
                  </div>
                </div>
              </div>
              <div class="count-item flex-view" @click="share()">
                <div class="count-img">
                  <img :src="ShareIcon" />
                </div>
                <div class="count-box flex-view">
                  <div class="count-text-box">
                    <span class="count-title">分享</span>
                  </div>
                  <div class="count-num-box">
                    <span class="num-text"></span>
                    <img :src="WeiboShareIcon" class="mg-l" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="detail-content-bottom">
        <div class="thing-content-view flex-view">
          <div class="main-content">
            <div class="order-view">
              <div class="title">简介</div>
              <div class="thing-intro">
                <p class="text" style="">{{ detailData.description }}</p>
              </div>
              <div class="title">评论</div>
              <div class="thing-comment">
                <div class="title">发表新的评论</div>
                <div class="publish flex-view">
                  <template v-if="userStore.user_avatar">
                    <img
                      :src="BASE_URL + '/api/file/download/avatar?name=' + userStore.user_avatar + '&' + Math.random()"
                      class="mine-img"
                    />
                  </template>
                  <template v-else>
                    <img :src="AvatarIcon" class="mine-img" />
                  </template>
                  <input placeholder="说点什么..." class="content-input" ref="commentRef" />
                  <button class="send-btn" @click="sendComment()">发送</button>
                </div>
                <div class="tab-view flex-view">
                  <div class="count-text">共有 {{ commentData.length }} 条评论</div>
                  <div class="tab-box flex-view" v-if="commentData.length > 0">
                    <span :class="sortIndex === 0 ? 'tab-select' : ''" @click="sortCommentList('recent')">最新</span>
                    <div class="line"></div>
                    <span :class="sortIndex === 1 ? 'tab-select' : ''" @click="sortCommentList('hot')">热门</span>
                  </div>
                </div>
                <div class="comments-list">
                  <div class="comment-item" v-for="item in commentData">
                    <div class="flex-item flex-view">
                      <template v-if="userStore.user_avatar">
                        <img
                          :src="BASE_URL + '/api/file/download/avatar?name=' + item.userId + '.jpeg&' + Math.random()"
                          class="avator"
                        />
                      </template>
                      <template v-else>
                        <img :src="AvatarIcon" class="avator" />
                      </template>
                      <div class="person">
                        <div class="name">{{ item.username }}</div>
                        <div class="time">{{ item.createTime }}</div>
                      </div>
                      <div class="float-right">
                        <span @click="like(item.id)">推荐</span>
                        <span class="num">{{ item.likeCount }}</span>
                      </div>
                    </div>
                    <p class="comment-content">{{ item.content }}</p>
                  </div>
                  <div class="infinite-loading-container">
                    <div class="infinite-status-prompt" style="">
                      <div slot="no-results" class="no-results">
                        <div></div>
                        <p>没有更多了</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="recommend" style="">
            <div class="title">热门推荐</div>
            <div class="things">
              <div class="thing-item thing-item" v-for="item in recommendData" @click="handleDetail(item)">
                <div class="img-view"> <img :src="item.cover" /></div>
                <div class="info-view">
                  <h3 class="thing-name">{{ item.title.substring(0, 12) }}</h3>
                  <span style="font-weight: bold; font-size: 18px">￥{{ item.price }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>
<script setup>
  import { message } from 'ant-design-vue';
  import Header from '/@/views/index/components/header.vue';
  import Footer from '/@/views/index/components/footer.vue';
  import AddIcon from '/@/assets/images/add.svg';
  import WantIcon from '/@/assets/images/wish-hover.svg';
  import RecommendIcon from '/@/assets/images/recommend-hover.svg';
  import ShareIcon from '/@/assets/images/share-icon.svg';
  import WeiboShareIcon from '/@/assets/images/wb-share.svg';
  import AvatarIcon from '/@/assets/images/avatar.jpg';
  import CountIcon from '/@/assets/images/count.svg';
  import { detailApi, listApi as listThingList } from '/@/api/thing';
  import { listThingCommentsApi, createApi as createCommentApi, likeApi } from '/@/api/comment';
  import { addApi as addShoppingCart } from '/@/api/shoppingCart';
  import { wishApi } from '/@/api/thingWish';
  import { collectApi } from '/@/api/thingCollect';
  import { BASE_URL } from '/@/store/constants';
  import { useRoute, useRouter } from 'vue-router/dist/vue-router';
  import { useUserStore } from '/@/store';

  const router = useRouter();
  const route = useRoute();
  const userStore = useUserStore();

  let thingId = ref('');
  let detailData = ref({});
  let commentData = ref([]);
  let recommendData = ref([]);
  let sortIndex = ref(0);
  let commentOrder = ref('recent');
  let commentRef = ref();

  const shoppingCart = reactive({
    id: undefined,
    title: undefined,
    userId: undefined,
    count: undefined,
    price: undefined,
  });

  onMounted(() => {
    thingId.value = route.query.id.trim();
    getThingDetail();
    getRecommendThing();
    getCommentList();
  });

  const getThingDetail = () => {
    detailApi({ id: thingId.value })
      .then((res) => {
        detailData.value = res.data;
        detailData.value.cover = BASE_URL + '/api/file/download/thing?name=' + detailData.value.cover;
      })
      .catch((err) => {
        message.error('获取详情失败');
      });
  };
  const addToWish = () => {
    let userId = userStore.user_id;
    if (userId) {
      wishApi({ thingId: thingId.value, userId: userId })
        .then((res) => {
          message.success(res.msg);
          getThingDetail();
        })
        .catch((err) => {
          console.log('操作失败');
        });
    } else {
      message.warn('请先登录！');
    }
  };
  const collect = () => {
    let userId = userStore.user_id;
    if (userId) {
      collectApi({ thingId: thingId.value, userId: userId })
        .then((res) => {
          message.success(res.msg);
          getThingDetail();
        })
        .catch((err) => {
          console.log('收藏失败');
        });
    } else {
      message.warn('请先登录！');
    }
  };
  const share = () => {
    let shareHref = 'https://weibo.com/';
    window.open(shareHref);
  };
  const handleOrder = (detailData) => {
    let userId = userStore.user_id;
    if (userId) {
      const formData = new FormData();
      formData.append('thingId', detailData.id);
      formData.append('classificationId', detailData.classificationId);
      formData.append('title', detailData.title);
      formData.append('userId', userId);
      formData.append('price', detailData.price);
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
  const getRecommendThing = () => {
    listThingList({ sort: 'recommend' })
      .then((res) => {
        res.data.forEach((item) => {
          if (item.cover) {
            item.cover = BASE_URL + '/api/file/download/thing?name=' + item.cover;
          }
        });
        recommendData.value = res.data.slice(0, 5);
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };
  const handleDetail = (item) => {
    // 跳转新页面
    let text = router.resolve({ name: 'detail', query: { id: item.id } });
    window.open(text.href, '_blank');
  };
  const sendComment = () => {
    let text = commentRef.value.value.trim();
    if (text.length <= 0) {
      return;
    }
    commentRef.value.value = '';
    let userId = userStore.user_id;
    if (userId) {
      let exist = false;
      commentData.value.forEach((item) => {
        if (item.content === text) {
          exist = true;
          like(item.id);
        }
      });
      if (!exist) {
        createCommentApi({ content: text, thingId: thingId.value, userId: userId })
          .then((res) => {
            getCommentList();
          })
          .catch((err) => {
            console.log(err.msg);
          });
      }
    } else {
      message.warn('请先登录！');
    }
  };
  const like = (commentId) => {
    likeApi({ id: commentId })
      .then((res) => {
        getCommentList();
      })
      .catch((err) => {
        message.warn('请先登录！');
        console.log(err.msg);
      });
  };
  const getCommentList = () => {
    listThingCommentsApi({ thingId: thingId.value, order: commentOrder.value })
      .then((res) => {
        commentData.value = res.data;
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };
  const sortCommentList = (sortType) => {
    if (sortType === 'recent') {
      sortIndex.value = 0;
    } else {
      sortIndex.value = 1;
    }
    commentOrder.value = sortType;
    getCommentList();
  };
</script>
<style scoped lang="less">
  .hide {
    display: none;
  }

  .detail-content {
    display: flex;
    flex-direction: column;
    width: 1100px;
    margin: 4px auto;
  }

  .flex-view {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
  }

  .hidden-lg {
    display: none !important;
  }

  .thing-infos-view {
    display: flex;
    margin: 90px 0 25px 0;
    overflow: hidden;

    .thing-infos {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      display: flex;
    }

    .thing-img-box {
      -webkit-box-flex: 0;
      -ms-flex: 0 0 200px;
      flex: 0 0 200px;
      margin: 0 50px 0 0;

      img {
        width: 210px;
        height: 210px;
        display: block;
        background-size: cover;
        object-fit: cover;
        border: 1px solid rgba(100, 100, 100, 0.3);
      }
    }

    .thing-info-box {
      text-align: left;
      -webkit-box-flex: 0;
      -ms-flex: 0 0 350px;
      flex: 0 0 350px;
    }

    .thing-count-box {
      text-align: left;
      -webkit-box-flex: 0;
      -ms-flex: 0 0 180px;
      flex: 0 0 180px;
      margin-top: 52px;
      .count-text {
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 7px;
      }
      .count-text img {
        width: 28px;
        height: 28px;
        margin-right: 10px;
      }
    }

    .thing-state {
      height: 36px;
      line-height: 36px;

      .state {
        font-weight: bold;
        color: #4684e2;
        background: rgba(70, 132, 226, 0.1);
        border-radius: 2px;
        padding: 8px 8px 8px 8px;
        margin-right: 16px;
      }
    }

    .thing-name {
      line-height: 32px;
      margin: 16px 0 4px 0;
      color: #0f1111 !important;
      font-size: 24px !important;
      font-weight: bold !important;
      font-style: normal !important;
      text-transform: none !important;
      text-decoration: none !important;
    }

    .thing-classification {
      color: #152844;
      font-size: 18px;
      margin-bottom: 6px;
    }

    .thing-counts {
      -webkit-box-flex: 0;
      -ms-flex: 0 0 255px;
      flex: 0 0 255px;
      margin-left: 20px;
    }

    .pointer {
      cursor: pointer;
    }

    .count-item {
      height: 64px;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      cursor: pointer;
    }

    .count-img {
      -webkit-box-flex: 0;
      -ms-flex: 0 0 32px;
      flex: 0 0 32px;
      margin-right: 20px;
      font-size: 0;

      img {
        width: 100%;
        display: block;
      }
    }

    .count-box {
      position: relative;
      border-bottom: 1px solid #cedce4;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      -webkit-box-pack: justify;
      -ms-flex-pack: justify;
      justify-content: space-between;
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      height: 100%;
    }

    .count-text-box {
      font-size: 0;

      .count-title {
        color: #152844;
        font-weight: 600;
        font-size: 20px;
        line-height: 20px;
        display: block;
        height: 20px;
      }
    }

    .count-num-box {
      font-weight: 600;
      font-size: 20px;
      line-height: 24px;
      color: #152844;
    }
  }

  .buy-btn {
    cursor: pointer;
    display: block;
    background: #4684e2;
    border-radius: 4px;
    text-align: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    height: 40px;
    line-height: 20px;
    width: 150px;
    outline: none;
    border: none;
    margin-top: 6px;
  }

  .buy-btn img {
    width: 20px;
    margin-right: 5px;
    vertical-align: middle;
  }

  .buy-btn span {
    vertical-align: middle;
  }

  .thing-content-view {
    margin-top: 20px;
    padding-bottom: 50px;
  }

  .main-content {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;

    .text {
      color: #484848;
      font-size: 18px;
      line-height: 26px;
      padding-left: 12px;
      margin: 20px 0 60px 0;
      white-space: pre-wrap;
    }
  }

  .order-view {
    position: relative;
    color: #6c6c6c;
    margin-right: 30px;

    .title {
      font-weight: bold;
      font-size: 20px;
      line-height: 26px;
      color: #152844;
      margin-bottom: 12px;
    }

    .thing-intro {
      border-top: 1px solid #cedce4;
    }
  }

  .recommend {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 235px;
    flex: 0 0 235px;
    margin-left: 20px;

    .title {
      font-weight: bold;
      font-size: 20px;
      line-height: 26px;
      color: #152844;
      margin-bottom: 12px;
    }

    .things {
      border-top: 1px solid #cedce4;

      .thing-item {
        min-width: 255px;
        max-width: 255px;
        position: relative;
        flex: 1;
        margin-right: 20px;
        height: fit-content;
        overflow: hidden;
        margin-top: 20px;
        margin-bottom: 20px;
        border-bottom: 1px #e1e1e1 solid;
        display: flex;

        .img-view {
          height: 100px;
          width: 100px;
          margin-bottom: 24px;

          img {
            height: 100px;
            width: 100px;
            overflow: hidden;
            margin: 0 auto;
            background-size: cover;
            object-fit: cover;
            border: 1px solid rgba(100, 100, 100, 0.3);
          }
        }

        .info-view {
          overflow: hidden;
          padding: 0 16px;
          .thing-name {
            line-height: 24px;
            margin-top: 12px;
            color: #0f1111 !important;
            font-size: 22px !important;
            font-weight: bold !important;
            font-style: normal !important;
            text-transform: none !important;
            text-decoration: none !important;
          }
        }
      }
    }
  }

  .flex-view {
    display: flex;
  }

  .thing-comment {
    border-top: 1px solid #cedce4;
    .title {
      font-weight: bold;
      font-size: 18px;
      line-height: 22px;
      height: 22px;
      color: #152844;
      margin: 24px 0 18px;
    }

    .publish {
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;

      .mine-img {
        -webkit-box-flex: 0;
        -ms-flex: 0 0 50px;
        flex: 0 0 50px;
        margin-right: 12px;
        border-radius: 50%;
        width: 50px;
        height: 50px;
      }

      .content-input {
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
        background: #f6f9fb;
        border-radius: 4px;
        height: 45px;
        line-height: 30px;
        font-size: 18px;
        color: #484848;
        padding: 5px 12px;
        white-space: nowrap;
        outline: none;
        border: 0px;
      }

      .send-btn {
        margin-left: 10px;
        background: #4684e2;
        border-radius: 4px;
        -webkit-box-flex: 0;
        -ms-flex: 0 0 80px;
        flex: 0 0 80px;
        color: #fff;
        font-size: 20px;
        text-align: center;
        height: 40px;
        line-height: 40px;
        outline: none;
        border: 0px;
        cursor: pointer;
      }
    }

    .tab-view {
      -webkit-box-pack: justify;
      -ms-flex-pack: justify;
      justify-content: space-between;
      font-size: 18px;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      margin: 20px 0 10px 0;

      .count-text {
        color: #484848;
        float: left;
      }

      .tab-box {
        color: #5f77a6;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;

        .tab-select {
          color: #152844;
        }

        span {
          cursor: pointer;
        }
      }

      .line {
        width: 1px;
        height: 12px;
        margin: 0 12px;
        background: #cedce4;
      }
    }
  }

  .comments-list {
    .comment-item {
      .flex-item {
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
        padding-top: 16px;

        .avator {
          -webkit-box-flex: 0;
          -ms-flex: 0 0 40px;
          flex: 0 0 40px;
          width: 40px;
          height: 40px;
          margin-right: 12px;
          border-radius: 50%;
          cursor: pointer;
        }

        .person {
          -webkit-box-flex: 1;
          -ms-flex: 1;
          flex: 1;
        }

        .name {
          color: #152844;
          font-weight: 600;
          font-size: 18px;
          line-height: 22px;
          height: 22px;
          cursor: pointer;
        }

        .time {
          color: #5f77a6;
          font-size: 14px;
          line-height: 16px;
          height: 16px;
          margin-top: 4px;
        }

        .float-right {
          color: #4684e2;
          font-size: 18px;
          float: right;

          span {
            margin-left: 19px;
            cursor: pointer;
          }

          .num {
            color: #152844;
            margin-left: 6px;
            cursor: auto;
          }
        }
      }
    }
  }

  .comment-content {
    margin-top: 8px;
    color: #484848;
    font-size: 18px;
    line-height: 22px;
    padding-bottom: 16px;
    border-bottom: 1px dashed #cedce4;
    margin-left: 52px;
    overflow: hidden;
    word-break: break-word;
  }

  .infinite-loading-container {
    clear: both;
    text-align: center;
  }
</style>
