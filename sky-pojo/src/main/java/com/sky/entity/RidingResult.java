package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RidingResult {
    private Integer distance; // 距离（米）
    private Integer duration; // 时间（秒）
    private String originAddress;
    private String destinationAddress;
    private GeoLocation originLocation;
    private GeoLocation destinationLocation;

    public RidingResult(Integer distance, Integer duration, String originAddress, String destinationAddress) {
        this.distance = distance;
        this.duration = duration;
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
    }

    /**
     * 获取距离（公里）
     */
    public Double getDistanceInKm() {
        return distance != null ? distance / 1000.0 : 0.0;
    }

    /**
     * 获取时间（分钟）
     */
    public Double getDurationInMinutes() {
        return duration != null ? duration / 60.0 : 0.0;
    }

    /**
     * 获取格式化信息
     */
    public String getFormattedInfo() {
        return String.format("距离: %.2f公里, 时间: %.1f分钟", getDistanceInKm(), getDurationInMinutes());
    }

    public boolean isValid() {
        return distance != null && distance > 0 && duration != null && duration > 0;
    }
}
