package com.sky.dto;

import lombok.Data;

@Data
public class EmployeeEditPasswordDTO {

    Long employeeId;
    String oldPassword;
    String newPassword;
}
