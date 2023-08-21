<template>
  <div>
    <Header />
    <div class="pay-content">
      <div class="title">订单提交成功</div>
      <div class="time-text time-margin">
        <span>请在 </span>
        <span class="time">{{ ddlTime }}</span>
        <span> 之前付款，超时订单将自动取消</span>
      </div>
      <div class="text">支付金额</div>
      <div class="price">
        <span class="num">{{ amount }}</span>
        <span> 元</span>
      </div>
      <div class="pay-choose-view" style="">
        <div class="pay-choose-box flex-view">
          <div class="choose-box choose-box-active">
            <img :src="WxPayIcon" />
            <span>微信支付</span>
          </div>
          <div class="choose-box">
            <img :src="AliPayIcon" />
            <span>支付宝</span>
          </div>
        </div>
        <div class="tips">请选择任意一种支付方式</div>
        <button class="pay-btn pay-btn-active" @click="handlePay()">确认支付</button>
      </div>
    </div>
  </div>
</template>

<script setup>
  import Header from '/@/views/index/components/header.vue';
  import { message } from 'ant-design-vue';
  import WxPayIcon from '/@/assets/images/wx-pay-icon.svg';
  import AliPayIcon from '/@/assets/images/ali-pay-icon.svg';
  import { useUserStore } from '/@/store';
  import { updateApi } from '/@/api/order';
  import { deleteByUserIdApi } from '/@/api/shoppingCart';

  const router = useRouter();
  const route = useRoute();
  const userStore = useUserStore();

  let ddlTime = ref();
  let amount = ref();
  let orderId = ref();

  onMounted(() => {
    amount.value = route.query.amount;
    orderId.value = route.query.orderId;
    ddlTime.value = formatDate(new Date().getTime(), 'YY-MM-DD hh:mm:ss');
    // 禁止浏览器回退
    history.pushState(null, null, document.URL);
    window.addEventListener(
      'popstate',
      function (e) {
        history.pushState(null, null, document.URL);
      },
      false,
    );
  });

  const handlePay = () => {
    let userId = userStore.user_id;
    if (userId) {
      deleteByUserIdApi({ userId: userId })
        .then((res) => {
          const formData = new FormData();
          formData.append('id', orderId.value);
          formData.append('status', '2');
          updateApi({}, formData)
            .then((res) => {
              message.success('支付成功！');
              router.push({ name: 'portal' });
            })
            .catch((err) => {
              console.log(err.msg);
            });
        })
        .catch((err) => {
          console.log(err.msg);
        });
    }
  };
  const formatDate = (time, format = 'YY-MM-DD hh:mm:ss') => {
    const date = new Date(time);

    const year = date.getFullYear(),
      month = date.getMonth() + 1,
      day = date.getDate() + 1,
      hour = date.getHours(),
      min = date.getMinutes(),
      sec = date.getSeconds();
    const preArr = Array.apply(null, Array(10)).map(function (elem, index) {
      return '0' + index;
    });

    const newTime = format
      .replace(/YY/g, year)
      .replace(/MM/g, preArr[month] || month)
      .replace(/DD/g, preArr[day] || day)
      .replace(/hh/g, preArr[hour] || hour)
      .replace(/mm/g, preArr[min] || min)
      .replace(/ss/g, preArr[sec] || sec);
    return newTime;
  };
</script>

<style scoped lang="less">
  .flex-view {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
  }

  .pay-content {
    position: relative;
    margin: 120px auto 0;
    width: 800px;
    background: #fff;
    overflow: hidden;

    .title {
      color: #152844;
      font-weight: bold;
      font-size: 28px;
      line-height: 40px;
      height: 40px;
      text-align: center;
      margin-bottom: 20px;
    }

    .time-text {
      height: 28px;
      line-height: 28px;
      font-size: 20px;
      text-align: center;
      color: #152844;
    }

    .time-margin {
      margin: 11px 0 24px;
    }

    .time {
      color: #f62a2a;
    }

    .text {
      height: 28px;
      line-height: 28px;
      font-size: 20px;
      font-weight: bold;
      text-align: center;
      color: #152844;
    }

    .price {
      color: #f53535;
      font-weight: bold;
      font-size: 28px;
      height: 40px;
      line-height: 40px;
      text-align: center;

      .num {
        font-size: 28px;
      }
    }

    .pay-choose-view {
      margin-top: 24px;

      .choose-box {
        width: 140px;
        height: 126px;
        border: 1px solid #cedce4;
        border-radius: 4px;
        text-align: center;
        cursor: pointer;
      }

      .pay-choose-box {
        -webkit-box-pack: justify;
        -ms-flex-pack: justify;
        justify-content: space-between;
        max-width: 300px;
        margin: 0 auto;

        img {
          height: 40px;
          margin: 24px auto 16px;
          display: block;
        }
      }

      .choose-box-active {
        border: 1px solid #4684e2;
      }

      .tips {
        color: #6f6f6f;
        font-size: 20px;
        line-height: 22px;
        height: 22px;
        text-align: center;
        margin: 28px 0 28px;
      }

      .pay-btn {
        cursor: pointer;
        background: #c3c9d5;
        border-radius: 32px;
        width: 150px;
        height: 40px;
        line-height: 40px;
        border: none;
        outline: none;
        font-size: 20px;
        color: #fff;
        text-align: center;
        display: block;
        margin: 0 auto;
      }

      .pay-btn-active {
        background: #4684e2;
      }
    }
  }
</style>
