package com.itheima.reiji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reiji.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper        //继承并指定泛型
public interface EmployeeMapper extends BaseMapper<Employee>{
}
