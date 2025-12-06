package com.sky.service;

import com.sky.entity.AddressBook;
import com.sky.vo.DeliveryInfoVO;

import java.util.List;

public interface AddressBookService {

    List<AddressBook> list(AddressBook addressBook);

    void save(AddressBook addressBook);

    AddressBook getById(Long id);

    void update(AddressBook addressBook);

    void setDefault(AddressBook addressBook);

    void deleteById(Long id);

    DeliveryInfoVO checkDeliveryRange(Long addressId);
}
