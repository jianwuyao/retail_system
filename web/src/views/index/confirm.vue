<template>
  <div>
    <Header />
    <section class="cart-page flex-view">
      <div class="left-flex">
        <div class="title flex-view">
          <h3>订单明细</h3>
        </div>
        <div class="cart-list-view">
          <div class="list-th flex-view">
            <span class="line-1">名称</span>
            <span class="line-2">价格</span>
            <span class="line-5">数量</span>
          </div>
          <div class="list">
            <div class="items flex-view" v-for="item in pageData.shoppingCartData">
              <div class="book flex-view">
                <h2>{{ item.title }}</h2>
              </div>
              <div class="pay">¥{{ item.price }}</div>
              <a-input-number
                v-model:value="item.count"
                :min="1"
                @change="
                  (value) => {
                    handleUpdateShoppingCart(item.id, value);
                  }
                "
              />
            </div>
          </div>
        </div>

        <div class="title flex-view">
          <h3>备注</h3>
        </div>
        <textarea v-model="pageData.remark" placeholder="输入备注信息，100字以内" class="remark"> </textarea>
      </div>
      <div class="right-flex">
        <div class="title flex-view">
          <h3>收货地址</h3>
        </div>
        <div class="address-view">
          <div class="name" v-if="pageData.receiverName">收件人：{{ pageData.receiverName }}</div>
          <div class="phone" v-if="pageData.receiverPhone">电话：{{ pageData.receiverPhone }}</div>
          <div class="address" v-if="pageData.receiverAddress">地址：{{ pageData.receiverAddress }}</div>
          <div class="btns-view">
            <button class="btn back" @click="handleSelect" v-if="pageData.addressData.length > 0">选择地址</button>
            <button class="btn ensure" @click="handleAdd">新建地址</button>
          </div>
        </div>
        <div class="title flex-view">
          <h3>结算</h3>
          <span class="click-txt">价格</span>
        </div>
        <div class="price-view">
          <div class="price-item flex-view">
            <div class="item-name">商品总价</div>
            <div class="price-txt">¥{{ pageData.amount }}</div>
          </div>
          <div class="price-item flex-view">
            <div class="item-name">商品优惠</div>
            <div class="price-txt">¥0</div>
          </div>
          <div class="price-item flex-view">
            <div class="item-name">商品折扣</div>
            <div class="price-txt">¥0</div>
          </div>
          <div class="total-price-view flex-view">
            <span>合计</span>
            <div class="price">
              <span class="font-big">¥{{ pageData.amount }}</span>
            </div>
          </div>
          <div class="btns-view">
            <button class="btn back" @click="handleBack()">返回</button>
            <button class="btn ensure" @click="handlePay()">结算</button>
          </div>
        </div>
      </div>
    </section>

    <!--新建地址-->
    <div>
      <a-modal
        :visible="modal.visible_add"
        :forceRender="true"
        :title="modal.title"
        ok-text="确认"
        cancel-text="取消"
        @cancel="handleCancel"
        @ok="handleOk"
      >
        <a-form ref="myform" :label-col="{ style: { width: '100px' } }" :model="modal.form" :rules="modal.rules">
          <a-row :gutter="24">
            <a-col span="24">
              <a-form-item label="姓名" name="name">
                <a-input placeholder="请输入" v-model:value="modal.form.name" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col span="24">
              <a-form-item label="电话号码" name="mobile">
                <a-input placeholder="请输入" v-model:value="modal.form.mobile" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col span="24">
              <a-form-item label="送货地址" name="path">
                <a-input placeholder="请输入" v-model:value="modal.form.path" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col span="24">
              <a-form-item label="默认地址">
                <a-switch v-model:checked="modal.form.isDefault" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-modal>
    </div>
  </div>

  <!--选择地址-->
  <div>
    <a-modal :visible="modal.visible_select" :forceRender="true" :title="modal.title" footer="" @cancel="handleCancel">
      <div class="list-content">
        <div class="address-item flex-view" v-for="item in pageData.addressData">
          <div class="infos">
            <div class="name-box">
              <span class="name">{{ item.name }}</span>
              <span class="mobile">{{ item.mobile }}</span>
            </div>
            <div class="address-box">
              <span class="address">{{ item.path }}</span>
            </div>
          </div>
          <div class="do-box">
            <div class="selected" @click="handleSelected(item)">确定</div>
            <div class="default" v-if="item.isDefault === 1">
              <img :src="AddressIcon" />
              <span>默认地址</span>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { message } from 'ant-design-vue';
  import Header from '/@/views/index/components/header.vue';
  import AddressIcon from '/@/assets/images/address-right-icon.svg';
  import { useUserStore } from '/@/store';
  import { createApi as createOrderApi } from '/@/api/order';
  import { listApi as listAddressListApi, createApi as createAddressApi } from '/@/api/address';
  import { listApi as listShoppingCartApi, updateCountApi } from '/@/api/shoppingCart';

  const router = useRouter();
  const route = useRoute();
  const userStore = useUserStore();

  const pageData = reactive({
    remark: undefined,
    amount: undefined,
    receiverName: undefined,
    receiverPhone: undefined,
    receiverAddress: undefined,
    shoppingCartData: [],
    addressData: [],
  });

  // 弹窗数据
  const modal = reactive({
    visible_add: false,
    visible_select: false,
    title: '',
    form: {
      name: undefined,
      mobile: undefined,
      path: undefined,
      isDefault: undefined,
    },
    rules: {
      name: [{ required: true, message: '请输入姓名', trigger: 'change' }],
      mobile: [{ required: true, message: '请输入电话号码', trigger: 'change' }],
      path: [{ required: true, message: '请输入送货地址', trigger: 'change' }],
    },
  });

  const myform = ref();

  onMounted(() => {
    getShoppingCartList();
    listAddressData();
  });

  const getShoppingCartList = () => {
    let userId = userStore.user_id;
    listShoppingCartApi({ userId: userId })
      .then((res) => {
        pageData.shoppingCartData = res.data;
      })
      .catch((err) => {
        console.log(err.msg);
      });
    watchEffect(async () => {
      // 计算总额
      let amount = 0;
      pageData.shoppingCartData.forEach((item) => {
        amount = amount + item.price * item.count;
      });
      pageData.amount = amount;
    });
  };

  const handleUpdateShoppingCart = (id, value) => {
    updateCountApi({ id: id, count: value })
      .then((res) => {})
      .catch((err) => {
        console.log(err.msg);
      });
  };

  const handleAdd = () => {
    resetModal();
    modal.visible_add = true;
    modal.title = '新增地址';
    // 重置
    for (const key in modal.form) {
      modal.form[key] = undefined;
    }
  };

  const handleSelect = () => {
    modal.visible_select = true;
    modal.title = '选择地址';
  };

  const handleSelected = (item) => {
    hideModal();
    pageData.receiverName = item.name;
    pageData.receiverPhone = item.mobile;
    pageData.receiverAddress = item.path;
  };

  const handleOk = () => {
    if (!userStore.user_id) {
      message.warn('请先登录！');
      return;
    }
    myform.value
      ?.validate()
      .then(() => {
        const formData = new FormData();
        formData.append('userId', userStore.user_id);
        formData.append('isDefault', modal.form.isDefault ? '1' : '0');
        formData.append('name', modal.form.name);
        formData.append('mobile', modal.form.mobile);
        formData.append('path', modal.form.path);
        createAddressApi(formData)
          .then((res) => {
            hideModal();
            listAddressData();
          })
          .catch((err) => {
            message.error(err.msg || '新建失败');
          });
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };

  const handleCancel = () => {
    hideModal();
  };

  // 恢复表单初始状态
  const resetModal = () => {
    myform.value?.resetFields();
  };

  // 关闭弹窗
  const hideModal = () => {
    modal.visible_add = false;
    modal.visible_select = false;
  };

  const listAddressData = () => {
    let userId = userStore.user_id;
    listAddressListApi({ userId: userId })
      .then((res) => {
        if (res.data.length > 0) {
          pageData.addressData = res.data;
          pageData.receiverName = res.data[0].name;
          pageData.receiverPhone = res.data[0].mobile;
          pageData.receiverAddress = res.data[0].path;
          res.data.forEach((item) => {
            if (item.isDefault) {
              pageData.receiverName = item.name;
              pageData.receiverPhone = item.mobile;
              pageData.receiverAddress = item.path;
            }
          });
        }
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };

  const handleBack = () => {
    router.back();
  };

  const handlePay = () => {
    const formData = new FormData();
    let userId = userStore.user_id;
    if (!userId) {
      message.warn('请先登录！');
      return;
    }
    if (!pageData.receiverName) {
      message.warn('请选择地址！');
      return;
    }
    formData.append('userId', userId);
    formData.append('userName', userStore.user_name);
    if (pageData.remark) {
      formData.append('remark', pageData.remark);
    }
    formData.append('amount', pageData.amount);
    formData.append('receiverName', pageData.receiverName);
    formData.append('receiverPhone', pageData.receiverPhone);
    formData.append('receiverAddress', pageData.receiverAddress);
    createOrderApi(formData)
      .then((res) => {
        let msg = res.data;
        if (msg.indexOf('success') === 0) {
          message.success('请支付订单');
          router.push({
            name: 'pay',
            query: {
              amount: pageData.amount,
              orderId: msg.slice(2),
            },
          });
        } else {
          message.error(msg);
        }
      })
      .catch((err) => {
        message.error(err.msg || '失败');
      });
  };
</script>

<style scoped lang="less">
  .flex-view {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
  }

  .cart-page {
    width: 1024px;
    min-height: 50vh;
    margin: 100px auto;
  }

  .left-flex {
    -webkit-box-flex: 17;
    -ms-flex: 17;
    flex: 17;
    padding-right: 20px;
  }

  .title {
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;

    h3 {
      color: #152844;
      font-weight: bold;
      font-size: 20px;
      height: 26px;
      line-height: 26px;
      margin: 0;
    }
  }

  .cart-list-view {
    margin: 4px 0 40px;

    .list-th {
      height: 50px;
      line-height: 50px;
      border-bottom: 1px solid #cedce4;
      color: #152844;
      font-size: 18px;
      font-weight: bold;

      .line-1 {
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
        margin-right: 20px;
      }

      .line-2,
      .pc-style .cart-list-view .list-th .line-3,
      .pc-style .cart-list-view .list-th .line-4 {
        width: 80px;
        margin-right: 60px;
      }

      .line-5 {
        width: 40px;
        margin-right: 25px;
      }
    }
  }

  .items {
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    margin-top: 20px;

    .book {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      margin-right: 20px;
      cursor: pointer;

      h2 {
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
        font-size: 20px;
        line-height: 20px;
        color: #152844;
        margin: 0;
      }
    }

    .type {
      width: 85px;
      margin-right: 40px;
      color: #152844;
      font-size: 14px;
    }

    .pay {
      color: #ff8a00;
      font-weight: 600;
      font-size: 20px;
      width: 85px;
      margin-right: 20px;
    }

    .num-box {
      width: 80px;
      margin-right: 43px;
      border-radius: 4px;
      border: 1px solid #cedce4;
      -webkit-box-pack: justify;
      -ms-flex-pack: justify;
      justify-content: space-between;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      height: 32px;
      padding: 0 4px;
    }

    .delete {
      margin-left: 36px;
      width: 24px;
      cursor: pointer;
    }
  }

  .mb-24 {
    margin-bottom: 24px;
  }

  .show-txt {
    color: #ff8a00;
    font-size: 14px;
  }

  .remark {
    width: 100%;
    background: #f6f9fb;
    border: 0;
    border-radius: 4px;
    padding: 20px 20px;
    margin-top: 16px;
    resize: none;
    height: 100px;
    line-height: 24px;
    font-size: 20px;
  }

  .right-flex {
    -webkit-box-flex: 8;
    -ms-flex: 8;
    flex: 8;
    padding-left: 24px;
    border-left: 1px solid #cedce4;
  }

  .click-txt {
    color: #4684e2;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
  }

  .address-view {
    margin: 15px 0 50px 0;
    color: #152844;
    align-items: center;
    font-size: 20px;
    line-height: 35px;
  }

  .address-item {
    background: #f7f9fb;
    border-radius: 4px;
    margin-bottom: 12px;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    padding: 16px;

    .name-box {
      color: #152844;
      font-size: 18px;
      line-height: 22px;
      height: 22px;
      .name {
        margin-right: 20px;
      }
    }

    .address-box {
      color: #484848;
      font-size: 18px;
      line-height: 22px;
      height: 22px;
      margin-top: 15px;
    }

    .do-box {
      color: #484848;
      font-size: 18px;
      font-weight: 550;
      line-height: 22px;
      height: 22px;
      width: 200px;

      .selected {
        cursor: pointer;
        text-align: right;
      }

      .default {
        margin-top: 15px;
        text-align: right;
        img {
          position: relative;
          top: -1px;
          width: 18px;
          height: 18px;
          margin-right: 6px;
        }
      }
    }
  }

  .price-view {
    overflow: hidden;
    margin-top: 16px;

    .price-item {
      -webkit-box-pack: justify;
      -ms-flex-pack: justify;
      justify-content: space-between;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      margin-bottom: 8px;
      font-size: 20px;

      .item-name {
        color: #152844;
      }

      .price-txt {
        font-weight: 500;
        color: #ff8a00;
      }
    }

    .total-price-view {
      margin-top: 30px;
      border-top: 1px solid #cedce4;
      -webkit-box-pack: justify;
      -ms-flex-pack: justify;
      justify-content: space-between;
      -webkit-box-align: start;
      -ms-flex-align: start;
      align-items: flex-start;
      padding-top: 10px;
      color: #152844;
      font-weight: bold;

      .price {
        color: #ff8a00;
        font-size: 20px;
        height: 36px;
        line-height: 36px;
      }
    }
  }

  .btns-view {
    margin-top: 24px;
    text-align: right;

    .back {
      background: #fff;
      color: #4684e2;
    }

    .ensure {
      cursor: pointer;
      background: #4684e2;
      color: #fff;
    }

    .btn {
      cursor: pointer;
      width: 120px;
      height: 40px;
      line-height: 40px;
      margin-left: 24px;
      text-align: center;
      border-radius: 12px;
      border: 1px solid #4684e2;
      font-size: 20px;
      outline: none;
    }
  }
</style>
