package com.example.rent2gojavaproject.services.dtos.responses.customerResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCustomerResponse {

    private int id;

    private String name;

    private String surname;

    private String phoneNumber;

    private String email;

    private String nationalityId;

    private String city;

    private String district;

    private String address;
}
