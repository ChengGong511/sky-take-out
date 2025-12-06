package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfoVO implements Serializable {
    // 是否在配送范围内
    private Boolean deliverable;
    // 配送距离（公里）
    private Double distanceKm;
    // 预计配送时间（分钟）
    private Integer estimatedMinutes;
    // 格式化的预计送达时间描述，如 "预计30分钟送达"
    private String estimatedTimeDesc;
    // 错误信息（超出范围时显示）
    private String errorMsg;
}