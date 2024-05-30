package com.itheima.reiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reiji.entity.Employee;
import com.itheima.reiji.mapper.EmployeeMapper;
import com.itheima.reiji.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
