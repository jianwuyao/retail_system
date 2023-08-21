package com.jwyao.system.interceptor;

import com.google.gson.Gson;
import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.BaseContext;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.OperationLog;
import com.jwyao.system.entity.User;
import com.jwyao.system.permission.Access;
import com.jwyao.system.permission.AccessLevel;
import com.jwyao.system.service.OperationLogService;
import com.jwyao.system.service.UserService;
import com.jwyao.system.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义请求拦截器：1.接口验权 2.记录操作日志
 */
@Slf4j
@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    private static OperationLogService operationLogService;

    private static UserService userService;

    private static RedisTemplate redisTemplate;

    // 解决先@Component后@Autowired失效的方案
    @Autowired
    public void setOpLogService(OperationLogService service) {
        AccessInterceptor.operationLogService = service;
    }

    @Autowired
    public void setUserService(UserService userService) {
        AccessInterceptor.userService = userService;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        AccessInterceptor.redisTemplate = redisTemplate;
    }

    /**
     * 接口授权
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("operationStartTime", System.currentTimeMillis());
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Access access = method.getAnnotation(Access.class);
        // 如果注解为null，说明不需要拦截，直接放过
        if (access == null) {
            return true;
        }
        // 管理员
        if (access.level().getCode() == AccessLevel.ADMIN.getCode()) {
            String token = request.getHeader("ADMINTOKEN");
            User user = userService.getUserByToken(token);
            if (user != null && user.getRole().equals(User.ADMIN_USER)) {
                return true;
            } else {
                APIResponse apiResponse = new APIResponse(ResponseCode.FAIL, "无操作权限");
                writeResponse(response, apiResponse);
                return false;
            }
        }
        // 普通用户
        if (access.level().getCode() == AccessLevel.LOGIN.getCode()) {
            String token = request.getHeader("TOKEN");
            String key = "user::" + token;
            User user = (User) redisTemplate.opsForValue().get(key);
            if (user != null && user.getRole().equals(User.NORMAL_USER)) {
                Long userId = user.getId();
                BaseContext.setCurrentId(userId);
                return true;
            } else {
                APIResponse apiResponse = new APIResponse(ResponseCode.FAIL, "未登录");
                writeResponse(response, apiResponse);
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /**
     * 记录操作日志
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long endTime = System.currentTimeMillis();
        Long startTime = (Long) request.getAttribute("operationStartTime");
        Long diff = (endTime - startTime);

        OperationLog opLog = new OperationLog();
        opLog.setReIp(IpUtils.getIpAddr(request));
        opLog.setReMethod(request.getMethod());
        opLog.setReUrl(request.getRequestURI());
        opLog.setReUa(request.getHeader(HttpHeaders.USER_AGENT));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        opLog.setReTime(formatter.format(new Date()));
        opLog.setAccessTime(String.valueOf(diff));
        operationLogService.createOpLog(opLog);
    }

    public void writeResponse(HttpServletResponse response, APIResponse apiResponse) throws IOException {
        response.setStatus(200);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(apiResponse);
        response.getWriter().println(jsonStr);
        response.getWriter().flush();
    }

}
