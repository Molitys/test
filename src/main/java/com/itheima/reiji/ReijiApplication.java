package com.itheima.reiji;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
//@ComponentScan(basePackages = {"com.itheima.reiji.controller",})
//@MapperScan
@SpringBootApplication
@ServletComponentScan

public class ReijiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReijiApplication.class, args);
        log.info("项目运行成功。。。");
    }
}
