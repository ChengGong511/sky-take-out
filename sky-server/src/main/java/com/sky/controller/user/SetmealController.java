package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.service.impl.SetmealServiceImpl;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;



    @ApiOperation("根据id查询套餐菜品" )
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> dishList(@PathVariable Long id){
        log.info("根据id查询套餐菜品：{}",id);
        List<DishItemVO> dishItemVOList=setmealService.getDishItemById(id);
        return Result.success(dishItemVOList);
    }

    @ApiOperation("根据分类ID查询套餐列表")
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Long categoryId){
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);

        List<Setmeal> list = setmealService.list(setmeal);
        return Result.success(list);
    }

    }

