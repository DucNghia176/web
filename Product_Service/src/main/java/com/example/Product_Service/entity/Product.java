package com.example.Product_Service.entity;

import com.example.Product_Service.emun.ProductStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCT")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    @Column(name = "PRODUCT_ID")
    Long id; // Mã sản phẩm

    @Column(name = "NAME", nullable = false, unique = true)
    String name; // Tên sản phẩm

    @Column(name = "DESCRIPTION")
    String description; // Mô tả sản phẩm

    @Column(name = "PRICE", nullable = false)
    BigDecimal price; // Giá sản phẩm

    @Column(name = "STOCK_QUANTITY", nullable = false)
    Integer stockQuantity; // Số lượng tồn kho

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    Category category; // Danh mục sản phẩm

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    ProductStatus status = ProductStatus.ACTIVE; // Trạng thái sản phẩm
}

