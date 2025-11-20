package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.impl.DishServiceImpl;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishServiceImpl dishServiceImpl;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("新增菜品")
    @PostMapping
    @Cacheable(value = "dishCache",key = "'dish_'+#dishDTO.categoryId")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavors(dishDTO);

        return Result.success();
    }

    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("菜品批量删除")
    @DeleteMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}", ids);
        dishService.delete(ids);

        return Result.success();
    }

    @ApiOperation("根据id查询菜品信息")
    @GetMapping("/{Id}")
    public Result<DishVO> getById(@PathVariable Long Id){
        log.info("根据id查询菜品信息：{}", Id);
        DishVO dishVO=dishService.getByIdWithFlavor(Id);
        return Result.success(dishVO);
    }

    @ApiOperation("修改菜品信息")
    @PutMapping
    @CachePut(value = "dishCache",key = "'dish_'+#dishDTO.categoryId")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品")
    @CacheEvict(value = "dishCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status,long id){
        log.info("启用禁用菜品：{},{}",status,id);
        dishService.startOrStop(status,id);
        return Result.success();
    }

    @GetMapping("list")
    @ApiOperation("根据分类ID查询菜品列表")
    public Result<List<DishVO>> list(Long categoryId){
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        List<DishVO> list = dishService.listWithFlavor(dish);
        return Result.success(list);
    }


    private void clearCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}

