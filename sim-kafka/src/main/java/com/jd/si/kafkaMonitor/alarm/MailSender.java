package com.jd.si.kafkaMonitor.alarm;

import com.jd.si.kafkaMonitor.common.SystemConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * 发送邮件
 * Created by lilianglin on 2016/8/26.
 */
public class MailSender implements Sender{

    private static final Log logger = LogFactory.getLog(MailSender.class);

    public void send(String title,String content){
        boolean isSSL = true;
        String host = SystemConfig.mailSmtp;
        int port = SystemConfig.mailPort;
        String from = SystemConfig.mailFrom;
        String to = SystemConfig.mailTo;

        String toStr[] = to.split(",");
        Address[] addrs = new Address[toStr.length];
        for(int i=0;i<toStr.length;i++){
            try {
                addrs[i] = new InternetAddress(toStr[i]);
            } catch (AddressException e) {
                logger.error(e);
            }
        }
        boolean isAuth = true;
        final String username = SystemConfig.mailUsername;
        final String password = SystemConfig.mailPassword;

        Properties props = new Properties();
        props.put("mail.smtp.ssl.enable", isSSL);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", isAuth);

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipients(Message.RecipientType.TO, addrs);
            message.setSubject(title);
            message.setContent(content, "text/html;charset=utf-8");
            Transport.send(message);
        } catch (AddressException e) {
            logger.error("邮件发送失败",e);
        } catch (MessagingException e) {
            logger.error("邮件发送失败", e);
        }
    }

    public static void main(String[] args) throws MessagingException, GeneralSecurityException {
        new MailSender().send("关于昨天一批机器GC满问题的答复",
                "小志！昨天那批机器GC问题是由于我们这边上线导致的，这边今天已经解决了，谢谢。");
    }

}
