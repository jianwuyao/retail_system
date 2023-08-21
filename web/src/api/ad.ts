import { get, post } from '/@/utils/http/axios';

enum URL {
  adList = '/api/advertisement/list',
  create = '/api/advertisement/create',
  update = '/api/advertisement/update',
  delete = '/api/advertisement/delete',
}

const listApi = async () => get<any>({ url: URL.adList, params: {}, data: {}, headers: {} });
const createApi = async (data: any) =>
  post<any>({
    url: URL.create,
    params: {},
    data: data,
    headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' },
  });
const updateApi = async (data: any) =>
  post<any>({
    url: URL.update,
    data: data,
    headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' },
  });
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });

export { listApi, createApi, updateApi, deleteApi };
