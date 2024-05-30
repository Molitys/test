package com.itheima.reiji.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理，底层基于代理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//加了这两个注解的类就被这个类处理
@ResponseBody//一会儿写的一个方法要返回json数据，加注解把结果封装成json数据
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法，一旦抛这种异常，就会被拦截到
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//表示处理这种异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){//把异常注进来
        log.error(ex.getMessage());//异常信息

        //首先判断是否是重复的报错，Duplicate entry
        if(ex.getMessage().contains("Duplicate entry")){
            //动态截出zhangsan,到底是哪个账号重复了
            String[] split = ex.getMessage().split(" ");//根据空格分隔
            String msg = split[2] + "已存在。。。";//拼接
            return R.error(msg);//返回
        }

        return R.error("未知错误。。。");//不是上面这种情况，没法定位
    }
}
