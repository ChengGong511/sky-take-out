package com.sky.service;

import com.sky.entity.GeoLocation;
import com.sky.entity.RidingResult;

public interface BaiduMapService {

    // 根据地址获取经纬度
    public GeoLocation getGeoLocation(String address);

    RidingResult getRidingRouteByCoord(GeoLocation origin, GeoLocation destination);
}
