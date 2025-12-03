package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(*) from orders where status = #{confirmed}")
    Integer countByStatus(Integer confirmed);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndTimeout(Integer status, LocalDateTime orderTime);


    @Select("select sum(amount) from orders where order_time >= #{begin} and order_time <= #{end} and status = #{status}")
    Double sumByMap(Map map);

    @Select("select count(*) from orders where status = #{completed} and order_time >= #{beginTime} and order_time <= #{endTime}")
    Long countOrdersByStatus(Integer completed, LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select count(*) from orders where order_time >= #{beginTime} and order_time <= #{endTime}")
    Long countAllOrders(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select id from orders where order_time between #{beginTime} and #{endTime}")
    List<Long> listOrdersByDate(LocalDateTime beginTime, LocalDateTime endTime);
}
