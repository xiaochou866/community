package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoggerTests {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        logger.debug("debug log");
        logger.info("info log"); // 在以后执行一些线程池 定时任务的时候可能会出问题 所以需要记录一些信息
        logger.warn("warn log");
        logger.error("error log"); // 在try catch 捕获到一个错误以后 肯定要对日志进行记录
    }
}
