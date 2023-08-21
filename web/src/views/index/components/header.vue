<template>
  <div class="main-bar-view">
    <div class="logo">
      <img :src="logoImage" class="search-icon" @click="$router.push({ name: 'portal' })" />
    </div>
    <span class="header-title">小型门店零售系统</span>
    <div class="search-entry">
      <a-space :size="0">
        <a-auto-complete
          class="search-input"
          placeholder="输入关键词"
          v-model:value="keywordRef"
          :options="options"
          @search="searchChange"
        />
        <a-button class="search-btn" @click="search"><img :src="SearchIcon" class="search-icon" style="height: 24px" /></a-button>
      </a-space>
    </div>
    <div class="right-view">
      <template v-if="userStore.user_token">
        <a-dropdown>
          <a class="ant-dropdown-link" @click="(e) => e.preventDefault()">
            <template v-if="userStore.user_avatar">
              <img :src="BASE_URL + '/api/file/download/avatar?name=' + userStore.user_avatar + '&' + Math.random()" class="self-img" />
            </template>
            <template v-else>
              <img :src="AvatarIcon" class="self-img" />
            </template>
          </a>
          <template #overlay>
            <a-menu>
              <a-menu-item style="font-size: 18px; font-weight: bold; margin: 10px">
                <a @click="goUserCenter('orderView')">订单中心</a>
              </a-menu-item>
              <a-menu-item style="font-size: 18px; font-weight: bold; margin: 10px">
                <a @click="goUserCenter('userInfoEditView')">个人设置</a>
              </a-menu-item>
              <a-menu-item style="font-size: 18px; font-weight: bold; margin: 10px">
                <a @click="quit()">退出</a>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </template>
      <template v-else>
        <button class="login btn hidden-sm" @click="goLogin()">登录</button>
      </template>

      <div class="right-icon" v-if="userStore.user_id" @click="handleOpenShoppingCart">
        <img :src="ShoppingCartIcon" style="width: 32px; height: 32px" />
      </div>
      <div>
        <a-drawer title="购物车" placement="right" :closable="true" :maskClosable="true" :visible="shoppingCartVisible" @close="onClose">
          <a-spin :spinning="loading" style="min-height: 200px">
            <div class="list-content">
              <div class="notification-view">
                <div class="list">
                  <div class="notification-item flex-view" v-for="item in shoppingCartData">
                    <div class="content-box" style="">
                      <div class="content">
                        <p
                          >{{ item.title }} <span style="color: #f00">￥{{ item.price }}</span></p
                        >
                      </div>
                      <a-input-number
                        v-model:value="item.count"
                        :min="1"
                        style="height: 45px"
                        @change="
                          (value) => {
                            handleUpdateShoppingCart(item.id, value);
                          }
                        "
                      />
                      <img
                        @click="handleDeleteShoppingCart(item.id)"
                        :src="DeleteIcon"
                        style="width: 30px; height: 30px; margin-left: 10px; margin-top: 5px; cursor: pointer"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </a-spin>
          <div
            style="
              position: absolute;
              border-top: 1px #eee dashed;
              background-color: #fff;
              display: flex;
              flex-direction: row;
              width: 90%;
              height: 44px;
              bottom: 1px;
            "
          >
            <div style="flex: 1; line-height: 44px; font-weight: normal">商品总额 {{ amount }} 元</div>
            <div
              @click="handleConfirm"
              style="line-height: 44px; background-color: #4684e2; color: #fff; padding: 0px 16px; font-weight: bold; cursor: pointer"
            >
              提交订单
            </div>
          </div>
        </a-drawer>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useUserStore } from '/@/store';
  import { BASE_URL } from '/@/store/constants';
  import logoImage from '/@/assets/images/k-logo.png';
  import SearchIcon from '/@/assets/images/search.png';
  import AvatarIcon from '/@/assets/images/avatar.jpg';
  import ShoppingCartIcon from '/@/assets/images/shoppingCart.png';
  import DeleteIcon from '/@/assets/images/ic-delete.png';
  import { listApi, deleteByIdApi, updateCountApi } from '/@/api/shoppingCart';
  import { suggestionApi } from '/@/api/thing';

  const router = useRouter();
  const route = useRoute();
  const userStore = useUserStore();
  const keywordRef = ref('');
  let loading = ref(false);
  let shoppingCartVisible = ref(false);
  let shoppingCartData = ref([]);
  let amount = ref(0);
  const options = ref<MockVal[]>([]);

  interface MockVal {
    value: string;
  }
  const mockVal = (str: string, repeat = 1): MockVal => {
    return {
      value: str.repeat(repeat),
    };
  };

  onMounted(() => {
    getShoppingCartList();
  });

  const handleUpdateShoppingCart = (id, value) => {
    updateCountApi({ id: id, count: value })
      .then((res) => {})
      .catch((err) => {
        console.log(err.msg);
      });
  };
  const handleDeleteShoppingCart = (id) => {
    deleteByIdApi({ id: id })
      .then((res) => {
        shoppingCartData.value.forEach((item, index) => {
          if (item.id === id) {
            shoppingCartData.value.splice(index, 1);
          }
        });
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };
  const handleOpenShoppingCart = () => {
    shoppingCartVisible.value = true;
    getShoppingCartList();
  };
  const getShoppingCartList = () => {
    loading.value = true;
    shoppingCartData.value = [];
    let userId = userStore.user_id;
    listApi({ userId: userId })
      .then((res) => {
        shoppingCartData.value = res.data;
      })
      .catch((err) => {
        console.log(err.msg);
      });
    watchEffect(async () => {
      // 计算总额
      let sum = 0;
      shoppingCartData.value.forEach((item) => {
        sum = sum + parseFloat(item.price) * item.count;
      });
      amount.value = sum;
      loading.value = false;
    });
  };
  const search = () => {
    const keyword = keywordRef.value;
    if (keyword != null && keyword != '') {
      let text = router.resolve({ name: 'search', query: { keyword: keyword } });
      window.open(text.href, '_blank');
    }
  };
  const searchChange = () => {
    options.value = [];
    const keyword = keywordRef.value;
    if (keyword != null && keyword != '') {
      suggestion(keyword);
    }
  };
  const suggestion = (keyword) => {
    suggestionApi({ key: keyword })
      .then((res) => {
        options.value = [];
        res.data.forEach((item) => {
          options.value.push(mockVal(item));
        });
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };
  const goLogin = () => {
    router.push({ name: 'login' });
  };
  const goUserCenter = (menuName) => {
    router.push({ name: menuName });
  };
  const quit = () => {
    userStore.logout().then((res) => {
      router.push({ name: 'portal' });
    });
  };
  const onClose = () => {
    shoppingCartVisible.value = false;
  };
  const handleConfirm = () => {
    if (shoppingCartData.value.length > 0) {
      router.push({
        name: 'confirm',
        query: {},
      });
    }
  };
</script>

<style scoped lang="less">
  .main-bar-view {
    position: fixed;
    top: 0;
    left: 0;
    height: 56px;
    width: 100%;
    background: #fff;
    border-bottom: 1px solid #cedce4;
    padding-left: 48px;
    z-index: 16;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;

    .header-title {
      margin-right: 20px;
      font-size: 28px;
      font-weight: bold;
      text-align: center;
    }
  }

  .logo {
    margin-right: 16px;
    img {
      width: 32px;
      height: 32px;
      cursor: pointer;
    }
  }

  .search-entry {
    .search-input {
      width: 300px;
      font-size: 18px;
      margin-left: 15px;
    }

    .search-btn {
      background-color: #4684e2;
    }
  }

  .right-view {
    padding-right: 36px;
    flex: 1;
    display: flex;
    flex-direction: row;
    gap: 20px;
    justify-content: flex-end;

    .username {
      height: 32px;
      line-height: 32px;
      text-align: center;
    }

    button {
      outline: none;
      border: none;
      cursor: pointer;
    }

    img {
      cursor: pointer;
    }

    .right-icon {
      position: relative;
      width: 24px;
      margin: 4px 0 0 4px;
      cursor: pointer;
      display: inline-block;
      font-size: 0;

      span {
        position: absolute;
        right: -15px;
        top: -3px;
        font-size: 12px;
        color: #fff;
        background: #4684e2;
        border-radius: 8px;
        padding: 0 4px;
        height: 16px;
        line-height: 16px;
        font-weight: 600;
        min-width: 20px;
        text-align: center;
      }
    }

    .self-img {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      vertical-align: middle;
      cursor: pointer;
    }

    .btn {
      background: #4684e2;
      font-size: 20px;
      color: #fff;
      border-radius: 4px;
      text-align: center;
      width: 80px;
      height: 40px;
      line-height: 40px;
      vertical-align: middle;
      margin-left: 32px;
    }
  }

  .content-list {
    flex: 1;

    .list-title {
      color: #152844;
      font-weight: 600;
      font-size: 18px;
      height: 48px;
      margin-bottom: 4px;
      border-bottom: 1px solid #cedce4;
    }
  }

  .notification-item {
    padding-top: 16px;
    padding-bottom: 10px;
    border-bottom: 1px solid #e9e9e9;

    .content-box {
      display: flex;
      flex-direction: row;
    }

    .content {
      margin-top: 2px;
      flex: 1;
      font-size: 20px;
      font-weight: bold;
      line-height: 40px;
    }
  }
</style>
