package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.impl.SetmealServiceImpl;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealServiceImpl setmealServiceImpl;

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealServiceImpl.pageQuery(setmealPageQueryDTO);
       return Result.success(pageResult);
    }

    @ApiOperation("起售停售套餐" )
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
       log.info("起售停售套餐：{},{}",status,id);
       setmealServiceImpl.startOrStop(status,id);
       return Result.success();
    }

    @ApiOperation("新增套餐" )
    @PostMapping()
    public Result saveWithDish(@RequestBody SetmealDTO setmealDTO){
       log.info("新增套餐：{}",setmealDTO);
       //TODO
        setmealServiceImpl.saveWithDish(setmealDTO);
       return Result.success();
    }

    @ApiOperation("根据id查询套餐信息" )
    @GetMapping("/{id}")
    public Result<SetmealVO> detById(@PathVariable Long id){
        log.info("根据id查询套餐信息：{}",id);
        SetmealVO setmealVO=setmealServiceImpl.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    @ApiOperation("修改套餐" )
    @PutMapping()
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐：{}",setmealDTO);
        setmealServiceImpl.updateWithDish(setmealDTO);
        return Result.success();
    }

    @ApiOperation("批量删除套餐")
    @DeleteMapping()
    public Result delete(@RequestParam  Long[] ids){
        log.info("批量删除套餐：{}",ids);
        setmealServiceImpl.delete(ids);
        return Result.success();
    }
}
