package com.example.product_service1.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Bảng lưu danh mục sản phẩm, giúp phân loại sản phẩm.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_seq", allocationSize = 1)
    @Column(name = "CATEGORY_ID")
    Long id; // ID danh mục

    @Column(name = "NAME", nullable = false, unique = true, length = 100)
    String name; // Tên danh mục

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    Category parent; // Danh mục cha (nếu có)
}
