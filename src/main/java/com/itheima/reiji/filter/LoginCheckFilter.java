package com.itheima.reiji.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reiji.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 *检查用户是否完成登录的过滤器
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")//过滤器名字以及拦截哪些请求路径，拦截所有请求
@Slf4j
public class LoginCheckFilter implements Filter {

    //专门用来进行路径比较，工具类，路径匹配器，支持通配符的写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;//强转
        HttpServletResponse response = (HttpServletResponse) servletResponse;//强转

        //1、获取本次请求的URI即路径
        String requestURI = request.getRequestURI();//已经得到request对象

        log.info("拦截到请求：{}",requestURI);

        String[] urls = new String[]{//把不需要处理的请求放进去
                "/employee/login",//登录路径,说明点的是登录的按钮
                "/employee/logout",//退出
                //一些静态资源,backend下面的一些页面，提问：不登录应该看不到菜单页面，页面没关系，我们只是重点控制数据，获取数据查数据库会发送动态请求
                "/backend/**",//通配符的方式
                "/front/**"//移动端的页面
        };

        //2、判断本次请求是否需要处理/检查登录状态，可专门封装一个方法来判断
        boolean check = check(urls,requestURI);

        //3、如果不需要处理,则直接放行
        if(check){//方行
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4、判断登录状态,如果已登录,则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            //if不成立，这次请求需要处理，则需要判断一下是否完成登录，则需要从session里获取用户
            //不为空，说明已经完成登录了，直接放行
            log.info("用户已登录。。。用户id为：{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录。。。。");
        //5、如果未登录则返回未登录结果，通过输出流的方式，把数据写回去，data.code data.msg即R对象转成的json
        //通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));//msg与beckend.js里的msg对应
        //把R对象转成JSON，然后通过输出流把它写回去


        log.info("拦截到请求：{}",request.getRequestURI());//{}:占位符，后面跟一个参数，参数输出到花括号的位置，输出request。getURI
        return;
    }

    /**
     * 路基匹配，检查本次请求是否需要放行
     * 需要把urls传进来，遍历，看是否有匹配的
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return  true;
            }
        }
        return false;
    }
}
