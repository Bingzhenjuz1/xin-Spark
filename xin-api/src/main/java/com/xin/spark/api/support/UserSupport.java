package com.xin.spark.api.support;


import com.xin.spark.domain.exception.ConditionException;
import com.xin.spark.service.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserSupport {
    public Long getCurrentUserId(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = requestAttributes.getRequest().getHeader("token");
        Long userId = TokenUtil.VerifyToken(token);
        if (userId < 0){
            throw new ConditionException("非法用户!");
        }
        return userId;
    }
}
