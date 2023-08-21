package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.User;
import com.jwyao.system.permission.Access;
import com.jwyao.system.permission.AccessLevel;
import com.jwyao.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final String SALT = "luck123456";

    @Value("${file.upload-path}")
    private String uploadPath;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword) {
        List<User> list = userService.getUserList(keyword);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(Long userId) {
        User user = userService.getUserDetail(userId);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", user);
    }

    // 后台管理员登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResponse login(User user) {
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + SALT).getBytes()));
        User responseUser = userService.getAdminUser(user);
        if (responseUser != null) {
            return new APIResponse(ResponseCode.SUCCESS, "查询成功", responseUser);
        } else {
            return new APIResponse(ResponseCode.FAIL, "用户名或密码错误");
        }
    }

    // 后台管理员注册
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(User user) throws IOException {
        if (!StringUtils.isEmpty(user.getUsername()) || !StringUtils.isEmpty(user.getPassword())) {
            // 查重
            if (userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponseCode.FAIL, "用户名重复");
            }
            // 设置密码
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + SALT).getBytes());
            user.setPassword(md5Str);
            // 设置token
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + SALT).getBytes());
            user.setToken(md5Str);
            // 设置头像
            String avatar = saveAvatar(user);
            if (!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.createUser(user);
            return new APIResponse(ResponseCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponseCode.FAIL, "创建失败");
    }

    // 普通用户登录
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin(User user) {
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + SALT).getBytes()));
        User responseUser = userService.getNormalUser(user);
        String key = "user::" + responseUser.getToken();
        redisTemplate.opsForValue().set(key, responseUser);
        if (responseUser != null) {
            return new APIResponse(ResponseCode.SUCCESS, "查询成功", responseUser);
        } else {
            return new APIResponse(ResponseCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户注册（添加管理员权限，避免乱注册瞎搞）
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    public APIResponse userRegister(User user) throws IOException {
        if (!StringUtils.isEmpty(user.getUsername())
                && !StringUtils.isEmpty(user.getPassword())
                && !StringUtils.isEmpty(user.getRePassword())) {
            // 查重
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponseCode.FAIL, "用户名重复");
            }
            // 验证密码
            if(!user.getPassword().equals(user.getRePassword())) {
                return new APIResponse(ResponseCode.FAIL, "密码不一致");
            }
            // 设置密码
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + SALT).getBytes());
            user.setPassword(md5Str);
            // 设置token
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + SALT).getBytes());
            user.setToken(md5Str);
            // 设置头像
            String avatar = saveAvatar(user);
            if (!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            // 设置角色
            user.setRole(User.NORMAL_USER);
            // 设置状态
            user.setStatus(0);
            userService.createUser(user);
            return new APIResponse(ResponseCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponseCode.FAIL, "创建失败");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            userService.deleteUser(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(User user) throws IOException {
        // password不能改，故置空
        user.setPassword(null);
        String avatar = saveAvatar(user);
        if (!StringUtils.isEmpty(avatar)) {
            user.avatar = avatar;
        }
        userService.updateUser(user);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    public APIResponse updateUserInfo(User user) throws IOException {
        User tmpUser = userService.getUserDetail(user.getId());
        if (tmpUser.getRole().equals(User.NORMAL_USER)) {
            // username和password不能改，故置空
            user.setUsername(null);
            user.setPassword(null);
            user.setRole(User.NORMAL_USER);
            String avatar = saveAvatar(user);
            if (!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.updateUser(user);
            return new APIResponse(ResponseCode.SUCCESS, "更新成功");
        } else {
            return new APIResponse(ResponseCode.FAIL, "非法操作");
        }
    }

    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    public APIResponse updatePwd(Long userId, String password, String newPassword) {
        User user =  userService.getUserDetail(userId);
        if (user.getRole().equals(User.NORMAL_USER)) {
            String md5Pwd = DigestUtils.md5DigestAsHex((password + SALT).getBytes());
            if (user.getPassword().equals(md5Pwd)) {
                user.setPassword(DigestUtils.md5DigestAsHex((newPassword + SALT).getBytes()));
                userService.updateUser(user);
            } else {
                return new APIResponse(ResponseCode.FAIL, "原密码错误");
            }
            return new APIResponse(ResponseCode.SUCCESS, "更新成功");
        } else {
            return new APIResponse(ResponseCode.FAIL, "非法操作");
        }
    }

    // 保存头像
    public String saveAvatar(User user) throws IOException {
        MultipartFile file = user.getAvatarFile();
        String fileName = null;
        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();
            if (fileName != null) {
                String filePath = uploadPath + File.separator + "avatar" + File.separator + fileName.split("\\.")[0] + ".jpeg";
                File destFile = new File(filePath);
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                file.transferTo(destFile);
            }
        }
        if (!StringUtils.isEmpty(fileName)) {
            user.avatar = fileName;
        }
        return fileName;
    }
}
