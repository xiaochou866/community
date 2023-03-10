package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//这样写 就是只会去扫描带有Controller注解的bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger= LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 这个方法的方法名可以随便取
        logger.error("服务器发生异常:" + e.getMessage());
        for (StackTraceElement element: e.getStackTrace()){
            logger.error(element.toString());
        }

        // 怎么判断一个请求是同步请求 还是一个异步请求呢?
        String xRequestedWidth = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(xRequestedWidth)){
            // 只有异步请求才希望你返回XML 当然你也可以返回JSON 或者其他类型的数据
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));
        }else{
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
