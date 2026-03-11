package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "{supplier.name.required}")
    @Setter
    private String name;

    @Column(name = "phone_number")
    @Pattern(regexp = "^$|\\d{10,11}", message = "{supplier.phone.invalid}")
    @Setter
    private String phoneNumber;

    @Column(name = "address")
    @Setter
    private String address;

    @Column(name = "district")
    @Setter
    private String district;

    @Column(name = "city")
    @Setter
    private String city;

    @Column(name = "created_at")
    @CreatedDate
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private OffsetDateTime updatedAt;
}

