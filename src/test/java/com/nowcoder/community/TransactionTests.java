package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransactionTests {
    @Autowired
    AlphaService alphaService;

    @Test
    public void testSave1(){
        Object obj = alphaService.save2();
        System.out.println(obj);
    }
}
