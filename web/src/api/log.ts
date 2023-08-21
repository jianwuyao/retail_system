import { get, post } from '/@/utils/http/axios';

enum URL {
  loginLogList = '/api/opLog/loginLogList',
  opLogList = '/api/opLog/list',
  errorLogList = '/api/errorLog/list',
}

const listLoginLogApi = async () => get<any>({ url: URL.loginLogList, params: {}, data: {}, headers: {} });
const listOpLogListApi = async () => get<any>({ url: URL.opLogList, params: {}, data: {}, headers: {} });
const listErrorLogListApi = async () => get<any>({ url: URL.errorLogList, params: {}, data: {}, headers: {} });

export { listLoginLogApi, listOpLogListApi, listErrorLogListApi };
