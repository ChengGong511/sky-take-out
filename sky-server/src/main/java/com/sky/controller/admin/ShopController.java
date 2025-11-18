package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import com.sky.service.impl.ShopServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags="店铺管理")
public class ShopController {

    @Autowired
    private ShopService shopService;
    @ApiOperation(value = "修改店铺状态")
    @PutMapping("/{status}")
    public Result updateShopStatus(@PathVariable Integer status) {
        log.info("修改店铺状态为：{}", status == 1 ? "营业中" : "休息中");
        shopService.updateShopStatus(status);
        return Result.success();
    }

    @ApiOperation(value = "查看店铺状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus() {

        Integer status =shopService.getShopStatus(); // 假设1表示营业中，0表示休息中
        log.info("查看店铺状态为：{}", status == 1 ? "营业中" : "休息中");
        return Result.success(status);
    }
}
