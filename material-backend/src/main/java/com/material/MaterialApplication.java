package com.material;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 物料管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.material.modules.**.mapper")
public class MaterialApplication {


    public static void main(String[] args) {
        SpringApplication.run(MaterialApplication.class, args);
        System.out.println("物料管理系统启动成功！");
    }
}
