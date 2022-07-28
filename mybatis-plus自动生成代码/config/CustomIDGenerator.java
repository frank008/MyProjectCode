package com.dfdk.common.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

import org.springframework.stereotype.Component;

import java.util.UUID;

/*
*  配置mysql 数据库 UUID生成
*
* */

@Component
public class CustomIDGenerator implements IdentifierGenerator {
    @Override
    public Long nextId(Object entity) {
        //可以将当前传入的class全类名来作为bizKey,或者提取参数来生成bizKey进行分布式Id调用生成.
        String bizKey = entity.getClass().getName();
        //根据bizKey调用分布式ID生成
        long id = (long) (Math.random()*900000000);
        //返回生成的id值即可.
        return id;
    }
    @Override
    public String nextUUID(Object entity) {
        return "hz-"+ UUID.randomUUID().toString().replace("-", "");
    }
}
