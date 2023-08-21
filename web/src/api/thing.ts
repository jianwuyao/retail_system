import { get, post } from '/@/utils/http/axios';

enum URL {
  thingList = '/api/thing/list',
  search = '/api/thing/search',
  suggestion = '/api/thing/suggestion',
  create = '/api/thing/create',
  update = '/api/thing/update',
  delete = '/api/thing/delete',
  detail = '/api/thing/detail',
}

const listApi = async (params: any) => get<any>({ url: URL.thingList, params: params, data: {}, headers: {} });
const searchApi = async (data: any) =>
  post<any>({ url: URL.search, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const suggestionApi = async (params: any) => get<any>({ url: URL.suggestion, params: params, headers: {} });
const createApi = async (data: any) =>
  post<any>({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const updateApi = async (data: any) =>
  post<any>({ url: URL.update, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });
const detailApi = async (params: any) => get<any>({ url: URL.detail, params: params, headers: {} });

export { listApi, searchApi, suggestionApi, createApi, updateApi, deleteApi, detailApi };
