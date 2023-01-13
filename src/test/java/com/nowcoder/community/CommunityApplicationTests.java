package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    void testApplicationContext() {
        System.out.println(applicationContext);
        // 获取已经进行自动装配的bean
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());
        // 为MyBatis设置了优先级别之后 在某一块代码还想使用Hibernate怎么办?
        alphaDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
        System.out.println(alphaDao.select());
    }

    @Test
    public void testBeanManagement(){
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
        // 被Spring容器管理的Bean是单例模式 只会进行依次初始化过程 也就是在输出的时候只进行一次
        alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }

    @Test
    public void testBeanConfig(){
        SimpleDateFormat simpleDateFormat =
                applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    // 在卡法中获取bean的方式
    @Autowired
    @Qualifier("alphaHibernate") // 指定具体的bean 在一个接口有多个实现类的情况下可以进行使用
    private AlphaDao alphaDao;
    @Autowired
    private AlphaService alphaService;
    @Autowired
    private SimpleDateFormat simpleDateFormat;
    @Test
    public void testDI(){
        System.out.println(alphaDao);
        System.out.println(alphaService);
        System.out.println(simpleDateFormat);
    }
}
