package org.skyme.util;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
/**
 * @author:Skyme
 * @create: 2023-09-05 16:29
 * @Description:
 */
public class EmailUtil {
    public static boolean sendEmail(String to, String subject,String content) {
        SimpleEmail email = new SimpleEmail();
        email.setHostName("imap.qq.com");
        // 设置账号和密码
        email.setAuthentication("46559677@qq.com","ocgeghgdzotwbjfh");
        try {
            email.setFrom("46559677@qq.com","业务员");
            email.addTo(to); // 发送对象
            email.setSubject(subject);  // 设置主题
            email.setMsg(content);  //设置内容
            email.send();
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
            return false;
        }
    }
}
