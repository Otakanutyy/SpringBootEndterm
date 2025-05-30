package com.example.apigateway.model;

public class OrderDto {
    private String productName;
    private Double orderTotal;
    private Long customerId;

    public OrderDto() {}

    public OrderDto(String productName, Double orderTotal, Long customerId) {
        this.productName = productName;
        this.orderTotal = orderTotal;
        this.customerId = customerId;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getProductName() {
        return productName;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
