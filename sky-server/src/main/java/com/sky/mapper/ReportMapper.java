package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReportMapper {

    @Select("select amount from orders where order_time between #{begin} and #{end}")
    List<Double> getTurnover(String begin, String end);
}
