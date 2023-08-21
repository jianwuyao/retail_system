<template>
  <div class="content-list">
    <div class="list-title">
      <span>地址管理</span>
      <span class="add-new-btn" @click="handleAdd">新建地址</span>
    </div>
    <div class="list-content">
      <div class="address-item flex-view" v-for="item in pageData.addressData">
        <div class="infos">
          <div class="name-box">
            <span class="name">{{ item.name }}</span>
            <span class="tel">{{ item.mobile }}</span>
          </div>
          <p class="address-box">{{ item.path }}</p>
        </div>
        <div class="do-box">
          <div class="btns">
            <span class="edit" @click="handleEdit(item)">编辑</span>
            <a-popconfirm title="确定删除？" ok-text="是" cancel-text="否" @confirm="handleDelete(item)">
              <span class="delete">删除</span>
            </a-popconfirm>
          </div>
          <div class="default-box" v-if="item.isDefault === 1">
            <img :src="AddressIcon" />
            <span>默认地址</span>
          </div>
        </div>
      </div>
      <div class="no-data" style="display: none">暂无地址</div>
    </div>
  </div>
  <!--弹窗区域-->
  <div>
    <a-modal
      :visible="modal.visible"
      :forceRender="true"
      :title="modal.title"
      ok-text="确认"
      cancel-text="取消"
      @cancel="handleCancel"
      @ok="handleOk"
    >
      <div>
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
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { FormInstance } from 'ant-design-vue';
  import { listApi, deleteApi } from '/@/api/address';
  import { createApi, updateApi } from '/@/api/address';
  import { useUserStore } from '/@/store';
  import AddressIcon from '/@/assets/images/address-right-icon.svg';

  const userStore = useUserStore();

  // 页面数据
  const pageData = reactive({
    addressData: [],
  });

  // 弹窗数据源
  const modal = reactive({
    visible: false,
    editFlag: false,
    title: '',
    form: {
      id: undefined,
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

  const myform = ref<FormInstance>();

  onMounted(() => {
    listAddressData();
  });

  const listAddressData = () => {
    let userId = userStore.user_id;
    listApi({ userId: userId })
      .then((res) => {
        pageData.addressData = res.data;
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };

  const handleDelete = (item) => {
    deleteApi({ ids: item.id })
      .then((res) => {
        listAddressData();
      })
      .catch((err) => {
        console.log(err.msg);
      });
  };

  const handleAdd = () => {
    resetModal();
    modal.visible = true;
    modal.editFlag = false;
    modal.title = '新增';
    // 重置
    for (const key in modal.form) {
      modal.form[key] = undefined;
    }
  };
  const handleEdit = (record: any) => {
    resetModal();
    modal.visible = true;
    modal.editFlag = true;
    modal.title = '编辑';
    // 重置
    for (const key in modal.form) {
      modal.form[key] = record[key];
    }
  };

  const handleOk = () => {
    myform.value
      ?.validate()
      .then(() => {
        const formData = new FormData();
        formData.append('userId', userStore.user_id);
        formData.append('isDefault', modal.form.isDefault ? '1' : '0');
        if (modal.form.id) {
          formData.append('id', modal.form.id);
        }
        if (modal.form.name) {
          formData.append('name', modal.form.name);
        }
        if (modal.form.mobile) {
          formData.append('mobile', modal.form.mobile);
        }
        if (modal.form.path) {
          formData.append('path', modal.form.path);
        }

        if (modal.editFlag) {
          updateApi(formData)
            .then((res) => {
              hideModal();
              listAddressData();
            })
            .catch((err) => {
              console.log(err.msg);
            });
        } else {
          createApi(formData)
            .then((res) => {
              hideModal();
              listAddressData();
            })
            .catch((err) => {
              console.log(err.msg);
            });
        }
      })
      .catch((err) => {
        console.log('不能为空');
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
    modal.visible = false;
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
    .add-new-btn {
      color: #4684e2;
      font-size: 18px;
      float: right;
      font-weight: 400;
      cursor: pointer;
    }
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
      font-weight: 500;
      font-size: 18px;
      line-height: 22px;
      height: 22px;
    }

    .name {
      margin-right: 16px;
    }

    .address-box {
      color: #484848;
      font-size: 18px;
      line-height: 22px;
      height: 22px;
      margin-top: 15px;
    }

    .btns {
      font-size: 18px;
      cursor: pointer;
      height: 22px;
      line-height: 22px;
    }

    .edit {
      color: #4684e2;
      margin-right: 24px;
    }

    .delete {
      color: #f62a2a;
    }

    .default-box {
      margin-top: 15px;
      font-size: 18px;
      font-weight: bold;

      img {
        position: relative;
        top: -1px;
        width: 18px;
        height: 18px;
        margin-right: 6px;
        vertical-align: middle;
      }

      span {
        color: #6f6f6f;
        font-size: 18px;
        vertical-align: middle;
      }
    }
  }
</style>
