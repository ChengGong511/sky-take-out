package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page <SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);
        //封装分页结果
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal= Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        //属性拷贝
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //向套餐表插入数据 1条
        setmealMapper.insert(setmeal);
        Long setmealId=setmeal.getId();

        //向套餐菜品表插入数据 多条
        List<SetmealDish> Dishes=setmealDTO.getSetmealDishes();
        if(Dishes !=null && !Dishes.isEmpty()){
            Dishes.forEach(item-> item.setSetmealId(setmealId));
            //批量插入套餐菜品关联数据
            setmealDishMapper.insertBatch(Dishes);
        }
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        SetmealVO setmealVO=new SetmealVO();
        //获取套餐基本信息
        Setmeal setmeal=setmealMapper.selectById(id);
        //获取套餐关联的菜品信息
        List<SetmealDish> setmealDishes=setmealDishMapper.getBySetmealId(id);
        //属性拷贝
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        //属性拷贝
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //更新套餐基本信息
        setmealMapper.update(setmeal);
        //删除套餐对应的菜品信息
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        //添加套餐对应的菜品信息
        List<SetmealDish>setmealDishes=setmealDTO.getSetmealDishes();
        if(setmealDishes !=null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(item-> item.setSetmealId(setmeal.getId()));
            //批量插入套餐菜品关联数据
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }

    @Override
    public void delete(Long[] ids) {
        //判断当前套餐是否可以删除 查询当前套餐是否处于启售状态
        for (Long id : ids) {
            Setmeal setmeal=setmealMapper.selectById(id);
            if(setmeal.getStatus().equals(StatusConstant.ENABLE)){
                //如果是起售状态 则抛出业务异常
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        setmealMapper.deleteByIds(ids);
        //批量删除菜品口味表中的数据
        setmealDishMapper.deleteBySetmealIds(ids);

    }
}
