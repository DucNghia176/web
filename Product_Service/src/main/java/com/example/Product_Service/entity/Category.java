package com.example.Product_Service.entity;

import com.example.Product_Service.emun.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.hc.core5.reactor.IOSession;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "CATEGORY")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_seq", allocationSize = 1)
    @Column(name = "CATEGORY_ID")
    Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    String name;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "PARENT_ID")
    Long parentId;

    @Enumerated(EnumType.STRING) // Lưu Enum dưới dạng String
    @Column(name = "STATUS", nullable = false)
    CategoryStatus status = CategoryStatus.ACTIVE;

}

