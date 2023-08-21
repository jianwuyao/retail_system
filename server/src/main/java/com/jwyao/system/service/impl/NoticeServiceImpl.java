package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.entity.Notice;
import com.jwyao.system.mapper.NoticeMapper;
import com.jwyao.system.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public List<Notice> getNoticeList() {
        return noticeMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public void createNotice(Notice notice) {
        noticeMapper.insert(notice);
    }

    @Override
    public void deleteNotice(String id) {
        noticeMapper.deleteById(id);
    }

    @Override
    public void updateNotice(Notice notice) {
        noticeMapper.updateById(notice);
    }

}
