package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Notice;
import com.jwyao.system.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通知管理
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Cacheable(value = "noticeList")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Notice> list = noticeService.getNoticeList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @CacheEvict(value = "noticeList", allEntries = true)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Notice notice) {
        noticeService.createNotice(notice);
        return new APIResponse(ResponseCode.SUCCESS, "创建成功");
    }

    @CacheEvict(value = "noticeList", allEntries = true)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            noticeService.deleteNotice(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @CacheEvict(value = "noticeList", allEntries = true)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Notice notice) {
        noticeService.updateNotice(notice);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

}
