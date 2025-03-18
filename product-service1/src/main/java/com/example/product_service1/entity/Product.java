package com.example.product_service1.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bảng lưu thông tin sản phẩm.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "PRODUCT")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    @Column(name = "PRODUCT_ID")
    Long id; // ID của sản phẩm

    @Column(name = "NAME", nullable = false, length = 255)
    String name; // Tên sản phẩm

    @Lob
    @Column(name = "DESCRIPTION")
    String description; // Mô tả sản phẩm

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    BigDecimal price; // Giá sản phẩm

    @Column(name = "STOCK", nullable = false)
    Integer stock; // Số lượng tồn kho

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    Category category; // Danh mục của sản phẩm

    @Column(name = "CREATED_AT", nullable = false)
    LocalDateTime createdAt = LocalDateTime.now(); // Ngày tạo sản phẩm
}
