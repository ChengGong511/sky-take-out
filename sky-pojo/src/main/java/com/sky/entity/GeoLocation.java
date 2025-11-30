package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoLocation
{
    private Double lat;
    private Double lng;
    private String originalAddress;
    private Integer confidence;
    private Integer precise;
    public GeoLocation(Double lat, Double lng, String originalAddress, int confidence) {
        this.lat = lat;
        this.lng = lng;
        this.originalAddress = originalAddress;
        this.confidence = confidence;
    }

    public String getCoordString() {
        return lat + "," + lng;
    }
}
