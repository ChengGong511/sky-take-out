package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public List<ShoppingCart> list() {
        //获取当前用户的id
        Long userId= BaseContext.getCurrentId();
       List<ShoppingCart> shoppingCartList = shoppingCartMapper.listById(userId);
       return shoppingCartList;
    }

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //获取当前用户的id
        Long userId=BaseContext.getCurrentId();

        //添加购物车的业务逻辑

        //先查看购物车中是否已经存在该菜品或套餐,存在就数量加1,不存在就添加一条新的记录
        //根据用户id、菜品id、套餐id、口味查询购物车
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList!=null && shoppingCartList.size()>0) {
            //说明购物车中已经存在该菜品或套餐
            ShoppingCart cart = shoppingCartList.get(0);
            cart.setNumber(cart.getNumber() + 1);//update shopping_cart set number=number+1 where id=?
            shoppingCartMapper.updateNumberById(cart);
        }
        else{
            //不存在该菜品或套餐
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId!=null) {
                //菜品数据
                Dish dish= dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            }else{
                Long setmealId = shoppingCartDTO.getSetmealId();
                //套餐数据
                Setmeal setmeal= setmealMapper.selectById(setmealId);

                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
           //插入购物车表
            shoppingCartMapper.insert(shoppingCart);
        }
    }
}
