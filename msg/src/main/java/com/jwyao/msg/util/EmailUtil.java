package com.jwyao.msg.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;


@Component
public class EmailUtil {

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送纯文本的邮件信息
     *
     * @param receiver  接收方
     * @param subject   邮件主题
     * @param content   邮件内容（发送内容）
     */
    public void sendMessage(String receiver, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sender);
        msg.setTo(receiver);
        msg.setSubject(subject);
        msg.setText(content);
        mailSender.send(msg);
    }

    /**
     * 发送带附件的邮件信息
     *
     * @param receiver  接收方
     * @param subject   邮件主题
     * @param content   邮件内容（发送内容）
     * @param files     文件数组 // 可发送多个附件
     */
    public void sendMessageCarryFiles(String receiver, String subject, String content, File[] files) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject(subject);
            helper.setText(content);
            if (files != null && files.length > 0) {
                for (File file : files) {
                    helper.addAttachment(file.getName(), file);
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(mimeMessage);
    }

}
