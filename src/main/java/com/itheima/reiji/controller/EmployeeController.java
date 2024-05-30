package com.itheima.reiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reiji.common.R;
import com.itheima.reiji.entity.Employee;
import com.itheima.reiji.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")//前端发送的是post的请求,请求的url路径
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 处理逻辑如下:
         * 1、将页面提交的密码password进行md5加密处理
         * 2、根据页面提交的用户名username查询数据库
         * 3、如果没有查询到则返回登录失败结果
         * 4、密码比对,如果不一致则返回登录失败结果
         * 5、查看员工状态,如果为已禁用状态,则返回员工已禁用结果
         * 6、登录成功,将员工id存入Session并返回登录成功结果
         */

        //1、将页面提交的密码password进行md5加密处理

        String password = employee.getPassword();//将密码拿到
        password = DigestUtils.md5DigestAsHex(password.getBytes());//调用工具方法将密码转成工具数组，赋给本身

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();//包装一个查询对象
        queryWrapper.eq(Employee::getUsername,employee.getUsername());//添加查询条件，等值查询,根据用户名，传过来的用户名在employee这个对象里面,至此条件已封装好
        Employee emp = employeeService.getOne(queryWrapper);//为什么调用getOne方法，因为数据库里面已经对字段进行了唯一的约束--unique,在设计表的索引里

        //3、如果没有查询到则返回登录失败结果
        //①判断是否查到
        if(emp == null){//等于空，没查到
            return R.error("登陆失败。。。");//结果要封装成R对象 public R<Employee> 见上
        }

        //4、密码比对,如果不一致则返回登录失败结果
        //数据库里查到的密码和md5处理完的密码比对
        if(!emp.getPassword().equals(password)){//比对不成功
            return R.error("登陆失败。。。");
        }

        //5、查看员工状态,如果为已禁用状态,则返回员工已禁用结果
        if(emp.getStatus() == 0){//0表禁用，1表可用
            return R.error("账号已禁用。。。");
        }

        //6、登录成功,将员工id存入Session并返回登录成功结果
        //登录成功将用户的id放到session里
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    /**
     *员工退出
     * * @param request
     * @return
     */
    @PostMapping("/logout")//地址要和浏览器发出响应的地址一致
    public R<String> logout(HttpServletRequest request){//退出不需要返回详细的数据，泛型一个字符串即可,
                                                       // 要给个参数，一会要操作session的对象,此处给了login里request对象即可，页面退出的时候没有提交其他参数

        //清理session中保存的当前登录员工的id,通过request.getSession().removeAttribute把属性移除掉，放的时候放的是什么名字移除的时候就用这个名字
        request.getSession().removeAttribute("employee");//弹幕：sessionid在request里，post，get什么的都是前端写好的？？
        return R.success("退出成功。。。");//清理掉后返回成功信息
    }


    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping//里边的字符串不用写了，地址就到employee，这个在前面的requestmapping里面写了
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){//传过来是code，没有用到数据，因为传过来的是json对象
                                                                                      // 加一个@RequestBody注解，使能正常封装
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));//getBytes变成byte数组

        //设置employee中没有设置的参数
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获得当前用户的id，通过request来得到session的对象
        Long empId = (Long)request.getSession().getAttribute("employee");//通过employee这个key来get一个session
                                                                           // 放进去的时候（见登陆成功代码）是通过emp.getId，所以取出来肯定还是这个ID
                                                                           //getAttribute返回的是一个object类型，所以要把他强转一下，向下转型

        employee.setCreateUser(empId);//创建当前登录用户的id
        employee.setUpdateUser(empId);//最后的更新人

        employeeService.save(employee);//来自IService,EmployeeService继承于IService，直接调用

        return R.success("新增员工成功。。。");
    }
}
