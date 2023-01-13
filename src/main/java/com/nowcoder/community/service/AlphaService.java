package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype") // 将单例模式改为多例的一个状态 每次在获取bean的时候重新初始化实例
public class AlphaService {

    @Autowired
    AlphaDao alphaDao;


    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct // 在构造器方法运行之后
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destory(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}
