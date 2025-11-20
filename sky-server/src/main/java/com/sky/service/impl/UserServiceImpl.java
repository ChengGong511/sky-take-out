package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.sky.context.BaseContext.threadLocal;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    private static final String wx_LOGIN="https://api.weixin.qq.com/sns/jscode2session";
    private static final String GRANT_TYPE="authorization_code";
    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口，获取用户信息
        String openid = getOpenid(userLoginDTO.getCode());
        //判断openid是否存在，如果为空，则说明获取用户信息失败，抛出异常
        if(openid.isEmpty()){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否是新用户,根据openid查询用户信息
        User user=userMapper.getByOpenid(openid);
        //如果是新用户，则保存用户信息到数据库
        if(user==null){
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    /*
    * 调用微信接口，获取openid
     */

    private String getOpenid(String code){
        Map<String,String>map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type",GRANT_TYPE);
        String json = HttpClientUtil.doGet(wx_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }

}
