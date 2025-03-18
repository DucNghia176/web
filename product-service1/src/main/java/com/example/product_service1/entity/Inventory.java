package com.example.product_service1.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Bảng lưu thông tin kho hàng của từng sản phẩm.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "INVENTORY")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_seq")
    @SequenceGenerator(name = "inventory_seq", sequenceName = "inventory_seq", allocationSize = 1)
    @Column(name = "INVENTORY_ID")
    Long id; // ID của bản ghi kho hàng

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    Product product; // Sản phẩm liên quan đến kho hàng

    @Column(name = "WAREHOUSE_LOCATION", nullable = false, length = 255)
    String warehouseLocation; // Vị trí kho hàng

    @Column(name = "QUANTITY", nullable = false)
    Integer quantity; // Số lượng sản phẩm có trong kho

    @Column(name = "LAST_UPDATED", nullable = false)
    java.time.LocalDateTime lastUpdated = java.time.LocalDateTime.now(); // Thời gian cập nhật gần nhất
}
