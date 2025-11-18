package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Override
    @Transactional
    public void saveWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();

        //属性拷贝
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入数据 1条
        dishMapper.insert(dish);

        Long dishId = dish.getId();
        //向菜品口味表插入数据 多条
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors !=null && !flavors.isEmpty()){
            flavors.forEach(item-> item.setDishId(dishId));

            dishFlavorMapper.insertBatch(flavors);

        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        //执行分页查询
        Page <DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        //封装分页结果
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());

        return pageResult;
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {

        //TODO 后期优化，如果ids里面有一部分菜品不能删除，返回给用户，其他可以删除的菜品继续删除

        //判断当前菜品是否可以删除   是否在售卖  是否关联了套餐
        //根据ids查询菜品数据
        for (Long id : ids) {
            Dish dish =dishMapper.getById(id);
            if(dish.getStatus().equals(StatusConstant.ENABLE)){
                //如果是起售状态 则抛出业务异常
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && !setmealIds.isEmpty()){
            //说明关联了套餐 不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        dishMapper.deleteByIds(ids);
        //批量删除菜品口味表中的数据
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /*
        * 根据id查询菜品信息和对应的口味信息
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = dishMapper.getById(id);

        //查询对应的口味信息
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        //  组装成DishVO对象并返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //删除原有口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors !=null && !flavors.isEmpty()){
            flavors.forEach(item-> item.setDishId(dishDTO.getId()));

            dishFlavorMapper.insertBatch(flavors);

        }

    }

    @Override
    public void startOrStop(Integer status, long id) {
        Dish dish= Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.update(dish);
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


}
