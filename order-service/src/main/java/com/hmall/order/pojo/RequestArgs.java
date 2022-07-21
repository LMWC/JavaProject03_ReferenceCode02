package com.hmall.order.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestArgs {
    Integer num ;
    Integer paymentType;
    Integer addressId;
    Long itemId;
}
