package com.atguigu.gulimall.thirdparty.component;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SmsComponent
 *
 * @author fj
 * @date 2022/12/29 19:13
 */
@Data
@Component
@Slf4j
public class SmsComponent {
    @Value("${spring.cloud.alicloud.sms.regionId}")
    private String regionId;
    @Value("${spring.cloud.alicloud.access-key}")
    private String accessKey;
    @Value("${spring.cloud.alicloud.secret-key}")
    private String secret;


//    public void sendCode(String phone, String code) throws ClientException {
//        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, secret);
//        IAcsClient client = new DefaultAcsClient(profile);
//        SendSmsRequest request = new SendSmsRequest();
//        request.setSignName("你的验证码为：");
//        request.setTemplateCode("SMS_154950909");
//        request.setPhoneNumbers(phone);
//        request.setTemplateParam("{\"code\":\""+code+"\"}");
//
//        try {
//            SendSmsResponse response = client.getAcsResponse(request);
//        } catch (ServerException e) {
//            e.printStackTrace();
//        } catch (ClientException e) {
//            System.out.println("ErrCode:" + e.getErrCode());
//            System.out.println("ErrMsg:" + e.getErrMsg());
//            System.out.println("RequestId:" + e.getRequestId());
//        }
//
//    }
public void sendCode(String phone, String code){
    log.info("手机号：{}，验证码：{}",phone,code);
    System.out.println("手机号 => " + phone+"验证码=>"+code);
}

}
