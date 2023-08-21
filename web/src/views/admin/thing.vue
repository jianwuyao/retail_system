<template>
  <div>
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">新增</a-button>
          <a-button @click="handleBatchDelete">批量删除</a-button>
          <a-input-search addon-before="名称" enter-button @search="onSearch" @change="onSearchChange" />
        </a-space>
      </div>
      <a-table
        size="middle"
        rowKey="id"
        :loading="data.loading"
        :columns="columns"
        :data-source="data.dataList"
        :scroll="{ x: 'max-content' }"
        :row-selection="rowSelection"
        :pagination="{
          size: 'default',
          current: data.page,
          pageSize: data.pageSize,
          onChange: (current) => (data.page = current),
          showSizeChanger: false,
          showTotal: (total) => `共 ${total} 条数据`,
        }"
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'coverUrl'">
            <img :src="text" style="width: 60px; height: 60px" />
          </template>
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>

    <!--弹窗区域-->
    <div>
      <a-modal
        :visible="modal.visible"
        :forceRender="true"
        :title="modal.title"
        width="880px"
        ok-text="确认"
        cancel-text="取消"
        @cancel="handleCancel"
        @ok="handleOk"
      >
        <div>
          <a-form ref="myform" :label-col="{ style: { width: '100px' } }" :model="modal.form" :rules="modal.rules">
            <a-row :gutter="24">
              <a-col span="24">
                <a-form-item label="商品名称" name="title">
                  <a-input placeholder="请输入" v-model:value="modal.form.title" />
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="分类" name="classificationId">
                  <a-select
                    placeholder="请选择"
                    allowClear
                    :options="modal.classificationData"
                    :field-names="{ label: 'title', value: 'id' }"
                    v-model:value="modal.form.classificationId"
                  />
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="标签">
                  <a-select mode="multiple" placeholder="请选择" allowClear v-model:value="modal.form.tags">
                    <template v-for="item in modal.tagData">
                      <a-select-option :value="item.id">{{ item.title }}</a-select-option>
                    </template>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="封面" name="imageFile">
                  <a-upload-dragger
                    name="file"
                    accept="image/*"
                    :multiple="false"
                    :before-upload="beforeUpload"
                    v-model:file-list="fileList"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.coverUrl">
                        <img :src="modal.form.coverUrl" style="width: 80px; height: 80px" />
                      </template>
                      <template v-else>
                        <file-image-outlined />
                      </template>
                    </p>
                    <p class="ant-upload-text"> 请选择要上传的封面图片 </p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="内容简介">
                  <a-textarea placeholder="请输入" v-model:value="modal.form.description" />
                </a-form-item>
              </a-col>
              <a-col span="8">
                <a-form-item label="定价" name="price">
                  <a-input-number placeholder="请输入" :min="0" v-model:value="modal.form.price" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col span="8">
                <a-form-item label="状态" name="status">
                  <a-select placeholder="请选择" allowClear v-model:value="modal.form.status">
                    <template v-for="item in modal.statusData">
                      <a-select-option :value="item.id">{{ item.title }}</a-select-option>
                    </template>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col span="8">
                <a-form-item label="库存" name="repertory">
                  <a-input-number placeholder="请输入" :min="0" v-model:value="modal.form.repertory" style="width: 100%" />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
      </a-modal>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { FormInstance, message } from 'ant-design-vue';
  import { createApi, listApi, updateApi, deleteApi } from '/@/api/thing';
  import { listApi as listClassificationApi } from '/@/api/classification';
  import { listApi as listTagApi } from '/@/api/tag';
  import { BASE_URL } from '/@/store/constants';
  import { FileImageOutlined } from '@ant-design/icons-vue';

  const columns = reactive([
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      align: 'center',
      width: 60,
    },
    {
      title: '图片',
      dataIndex: 'coverUrl',
      key: 'coverUrl',
      align: 'center',
      width: 150,
    },
    {
      title: '名称',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
    },
    {
      title: '简介',
      dataIndex: 'description',
      key: 'description',
      customRender: ({ text }) => (text ? text.substring(0, 10) + '...' : '--'),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      customRender: ({ text }) => (text === 0 ? '上架' : '下架'),
    },
    {
      title: '库存',
      dataIndex: 'repertory',
      key: 'repertory',
    },
    {
      title: '操作',
      dataIndex: 'action',
      key: 'operation',
      align: 'center',
      fixed: 'right',
      width: 140,
    },
  ]);

  const beforeUpload = (file: File) => {
    // 改文件名
    fileList.value = [];
    const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
    const copyFile = new File([file], fileName);
    modal.form.imageFile = copyFile;
    return false;
  };

  // 文件列表
  const fileList = ref<any[]>([]);

  // 页面数据
  const data = reactive({
    dataList: [],
    loading: false,
    keyword: '',
    selectedRowKeys: [] as any[],
    pageSize: 10,
    page: 1,
  });

  // 弹窗数据源
  const modal = reactive({
    visible: false,
    editFlag: false,
    title: '',
    classificationData: [],
    tagData: [{}],
    statusData: [
      {
        id: 0,
        title: '上架',
      },
      {
        id: 1,
        title: '下架',
      },
    ],
    form: {
      id: undefined,
      title: undefined,
      classificationId: undefined,
      tags: [],
      cover: undefined,
      coverUrl: undefined,
      imageFile: undefined,
      description: undefined,
      price: undefined,
      status: undefined,
      repertory: undefined,
    },
    rules: {
      title: [{ required: true, message: '请输入名称', trigger: 'change' }],
      classificationId: [{ required: true, message: '请选择分类', trigger: 'change' }],
      price: [{ required: true, message: '请输入定价', trigger: 'change' }],
      status: [{ required: true, message: '请选择状态', trigger: 'change' }],
      repertory: [{ required: true, message: '请输入库存', trigger: 'change' }],
    },
  });

  const myform = ref<FormInstance>();

  onMounted(() => {
    getDataList();
    getClassificationDataList();
    getTagDataList();
  });

  const getDataList = () => {
    data.loading = true;
    listApi({
      keyword: data.keyword,
      backstage: 8848,
    })
      .then((res) => {
        data.loading = false;
        res.data.forEach((item: any, index: any) => {
          item.index = index + 1;
          if (item.cover) {
            item.coverUrl = BASE_URL + '/api/file/download/thing?name=' + item.cover;
          }
        });
        data.dataList = res.data;
      })
      .catch((err) => {
        data.loading = false;
        console.log(err.msg);
      });
  };

  const getClassificationDataList = () => {
    listClassificationApi().then((res) => {
      modal.classificationData = res.data;
    });
  };
  const getTagDataList = () => {
    listTagApi().then((res) => {
      res.data.forEach((item, index) => {
        item.index = index + 1;
      });
      modal.tagData = res.data;
    });
  };

  const onSearchChange = (e: Event) => {
    data.keyword = e?.target?.value;
    console.log(data.keyword);
  };

  const onSearch = () => {
    getDataList();
  };

  const rowSelection = ref({
    onChange: (selectedRowKeys: (string | number)[], selectedRows: DataItem[]) => {
      console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      data.selectedRowKeys = selectedRowKeys;
    },
  });

  const handleAdd = () => {
    resetModal();
    modal.visible = true;
    modal.editFlag = false;
    modal.title = '新增';
    // 重置
    for (const key in modal.form) {
      modal.form[key] = undefined;
    }
    modal.form.cover = undefined;
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

  const confirmDelete = (record: any) => {
    console.log('delete', record);
    deleteApi({ ids: record.id })
      .then((res) => {
        getDataList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };

  const handleBatchDelete = () => {
    console.log(data.selectedRowKeys);
    if (data.selectedRowKeys.length <= 0) {
      console.log('hello');
      message.warn('请勾选删除项');
      return;
    }
    deleteApi({ ids: data.selectedRowKeys.join(',') })
      .then((res) => {
        message.success('删除成功');
        data.selectedRowKeys = [];
        getDataList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };

  const handleOk = () => {
    myform.value
      ?.validate()
      .then(() => {
        const formData = new FormData();
        if (modal.editFlag) {
          formData.append('id', modal.form.id);
        }
        formData.append('title', modal.form.title);
        formData.append('classificationId', modal.form.classificationId);
        if (modal.form.tags) {
          formData.append('tags', modal.form.tags);
        }
        if (modal.form.cover) {
          formData.append('cover', modal.form.cover);
        }
        if (modal.form.imageFile) {
          formData.append('imageFile', modal.form.imageFile);
        }
        if (modal.form.description) {
          formData.append('description', modal.form.description);
        }
        formData.append('price', modal.form.price);
        if (modal.form.repertory === 0) {
          formData.append('status', '1');
        } else {
          formData.append('status', modal.form.status);
        }
        formData.append('repertory', modal.form.repertory);
        if (modal.editFlag) {
          updateApi(formData)
            .then((res) => {
              hideModal();
              getDataList();
            })
            .catch((err) => {
              console.log(err.msg);
              message.error(err.msg || '操作失败');
            });
        } else {
          createApi(formData)
            .then((res) => {
              hideModal();
              getDataList();
            })
            .catch((err) => {
              console.log(err.msg);
              message.error(err.msg || '操作失败');
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
    fileList.value = [];
  };

  // 关闭弹窗
  const hideModal = () => {
    modal.visible = false;
  };
</script>

<style scoped lang="less">
  .page-view {
    min-height: 100%;
    background: #fff;
    padding: 24px;
    display: flex;
    flex-direction: column;
  }

  .table-operations {
    margin-bottom: 16px;
    text-align: right;
  }

  .table-operations > button {
    margin-right: 8px;
  }
</style>
