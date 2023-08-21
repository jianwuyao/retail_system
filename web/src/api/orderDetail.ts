import { get, post } from '/@/utils/http/axios';

enum URL {
  create = '/api/orderDetail/create',
}

const createApi = async (data: any) =>
  post<any>({
    url: URL.create,
    params: {},
    data: data,
    headers: { 'Content-Type': 'application/json;charset=utf-8' },
  });

export { createApi };
