package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.entity.GeoLocation;
import com.sky.entity.RidingResult;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import com.sky.service.BaiduMapService;
import com.sky.vo.DeliveryInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private BaiduMapService baiduMapService;

    String shopAddress="湖北省武汉市江夏区东湖新技术开发区光谷二路29号";
    /**
     * 条件查询
     *
     * @param addressBook
     * @return
     */
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    /**
     * 新增地址
     *
     * @param addressBook
     */
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        return addressBook;
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     */
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    @Transactional
    public void setDefault(AddressBook addressBook) {
        //1、将当前用户的所有地址修改为非默认地址 update address_book set is_default = ? where user_id = ?
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        //2、将当前地址改为默认地址 update address_book set is_default = ? where id = ?
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }


    public DeliveryInfoVO checkDeliveryRange(Long addressId) {
        AddressBook addressBook = addressBookMapper.getById(addressId);
        if (addressBook == null) {
            return DeliveryInfoVO.builder()
                    .deliverable(false)
                    .errorMsg("地址不存在")
                    .build();
        }

        String userAddress = addressBook.getProvinceName()
                + addressBook.getCityName()
                + addressBook. getDistrictName()
                + addressBook.getDetail();

        GeoLocation userLocation = baiduMapService.getGeoLocation(userAddress);
        GeoLocation shopLocation = baiduMapService.getGeoLocation(shopAddress);

        RidingResult ridingResult = baiduMapService.getRidingRouteByCoord(shopLocation, userLocation);

        if (ridingResult == null || ridingResult.getDistance() > 10000) {
            return DeliveryInfoVO.builder()
                    .deliverable(false)
                    .distanceKm(ridingResult != null ? ridingResult.getDistanceInKm() : null)
                    .errorMsg("超出配送范围（仅支持10公里内配送）")
                    .build();
        }

        int estimatedMinutes = (int) Math.ceil(ridingResult.getDurationInMinutes()) + 15; // 加15分钟出餐时间

        return DeliveryInfoVO.builder()
                .deliverable(true)
                .distanceKm(ridingResult. getDistanceInKm())
                . estimatedMinutes(estimatedMinutes)
                .estimatedTimeDesc("预计" + estimatedMinutes + "分钟送达")
                . build();
    }

}
