package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron="0 * * * * ?") //每分钟执行一次
    public void processTimeoutOrder(){
        log.info("定时处理超时订单任务，{}",LocalDateTime.now());

        //select * from orders where status='待支付' and 下单时间 < 当前时间 - 15分钟
        List<Orders> ordersList =orderMapper.getByStatusAndTimeout(Orders.PENDING_PAYMENT,LocalDateTime.now().minusMinutes(15));
        //查询订单明细列表并删除

        if(ordersList!=null && !ordersList.isEmpty()){
            for(Orders order:ordersList){
                order.setStatus(Orders.CANCELLED); //设置订单状态为已取消
                order.setCancelReason("超时未支付，系统自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);

                log.info("超时订单已取消，订单id：{}",order.getId());
            }
        }


    }

    @Scheduled(cron="0 0 1 * * ?") //每天凌晨1点执行一次
    public void processDeliveryOrder(){
        log.info("定时处理待收货订单任务，{}",LocalDateTime.now());

        List<Orders>ordersList=orderMapper.getByStatusAndTimeout(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.now().minusDays(60));

        if(ordersList!=null && !ordersList.isEmpty()){
            for(Orders order:ordersList){
                order.setStatus(Orders.COMPLETED); //设置订单状态为已完成
                order.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(order);

                log.info("待收货订单已自动确认收货，订单id：{}",order.getId());
            }
        }
    }
}
