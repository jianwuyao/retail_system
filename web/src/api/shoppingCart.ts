import { get, post } from '/@/utils/http/axios';

enum URL {
  shoppingCartList = '/api/shoppingCart/list',
  add = '/api/shoppingCart/add',
  updateCount = '/api/shoppingCart/updateCount',
  deleteById = '/api/shoppingCart/deleteById',
  deleteByUserId = '/api/shoppingCart/deleteByUserId',
}

const listApi = async (params: any) => get<any>({ url: URL.shoppingCartList, params: params, data: {}, headers: {} });
const addApi = async (data: any) =>
  post<any>({ url: URL.add, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const updateCountApi = async (data: any) =>
  post<any>({ url: URL.updateCount, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const deleteByIdApi = async (params: any) => post<any>({ url: URL.deleteById, params: params, headers: {} });
const deleteByUserIdApi = async (params: any) => post<any>({ url: URL.deleteByUserId, params: params, headers: {} });

export { listApi, addApi, updateCountApi, deleteByIdApi, deleteByUserIdApi };
