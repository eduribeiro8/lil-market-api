package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "one_time_job_execution")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OneTimeJobExecution {

    @Id
    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @CreatedDate
    @Column(name = "executed_at", nullable = false, updatable = false)
    private OffsetDateTime executedAt;
}
