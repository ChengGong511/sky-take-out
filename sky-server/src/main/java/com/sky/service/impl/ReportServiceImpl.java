package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;

import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO getTurnoverReport( LocalDate begin, LocalDate end) {

        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
            //日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的营业额数据，营业额是指：状态为“已完成”的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // select sum(amount) from orders where order_time > beginTime and order_time < endTime and status = 5
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        //封装返回结果
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
            //日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的新增用户数
        List<Long> userCountList = new ArrayList<>();
        //存放总用户数
        List<Long> totalUserCountList = new ArrayList<>();

        for(LocalDate date:dateList){
            //查询date日期对应的新增用户数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // select count(*) from user where create_time > beginTime and create_time < endTime
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Long userCount = userMapper.countNewUserByMap(map);
            userCount = userCount == null ? 0L : userCount;
            userCountList.add(userCount);

            //查询date日期对应的总用户数
            // select count(*) from user where create_time < endTime
            Map totalMap = new HashMap();
            totalMap.put("end", endTime);
            Long totalUserCount = userMapper.countTotalUserByMap(totalMap);
            totalUserCount = totalUserCount == null ? 0L : totalUserCount;
            totalUserCountList.add(totalUserCount);
        }
       //封装返回结果
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(userCountList, ","))
                .totalUserList(StringUtils.join(totalUserCountList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrderReport(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
            //日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的总订单数
        List<Long> orderCountList = new ArrayList<>();
        //存放有效订单数列表
        List<Long> validOrderCountList = new ArrayList<>();
        for(LocalDate date:dateList){
            //查询date日期对应的订单数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // select count(*) from orders where create_time > beginTime and create_time < endTime
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            //拿到有效订单数
            Long orderValidCount = orderMapper.countOrdersByStatus(Orders.COMPLETED, beginTime, endTime);
            //拿到总订单数
            Long orderCount = orderMapper.countAllOrders(beginTime, endTime);
            orderValidCount = orderValidCount == null ? 0L : orderValidCount;
            orderCount = orderCount == null ? 0L : orderCount;
            orderCountList.add(orderCount);
            validOrderCountList.add(orderValidCount);
        }
        //拿到总订单总数
        Integer orderTotalCount = orderCountList.stream().collect(Collectors.summingInt(Long::intValue));
        //拿到有效订单数
        Integer orderTotalValidCount = validOrderCountList.stream().collect(Collectors.summingInt(Long::intValue));
        Double orderCompletionRate= orderTotalCount == 0 ? 0.0 :
                (orderTotalValidCount.doubleValue() / orderTotalCount.doubleValue()) ;
        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(orderTotalCount)
                .validOrderCount(orderTotalValidCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10ReportVO(LocalDate begin, LocalDate end) {


        //存放top10菜品名字
        List<String> nameList=new ArrayList<>();
        //存放top10菜品销量
        List<String> salesList=new ArrayList<>();

        //select count(*) as sales, name from order_detail where create_time > beginTime and create_time < endTime group by name order by sales desc limit 10
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        //先把这一时间段的订单id全部搜索出来
        List<Long> idsList = orderMapper.listOrdersByDate(beginTime, endTime);
        //根据订单Id，拿到这一时间段内销量前十的菜品
        List<Map> top10List = orderDetailMapper.listTop10ByDate(idsList);
        for(Map map:top10List){
            String name = (String) map.get("name");
            String sales = map.get("value") != null ? map.get("value").toString() : "0";
            nameList.add(name);
            salesList.add(sales);
        }
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(salesList, ","))
                .build();
        return salesTop10ReportVO;
    }
}
