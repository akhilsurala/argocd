package com.sunseed.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;

    @ManyToOne()
    @JoinColumn(name = "source_id", referencedColumnName = "user_profile_id")
    private UserProfile source;

    @ManyToOne()
    @JoinColumn(name = "destination_id", referencedColumnName = "user_profile_id")
    private UserProfile destination;

    private String link;

    @Builder.Default
    private Boolean isSuccess = Boolean.FALSE;
    @Builder.Default
    private Boolean markAsRead = Boolean.FALSE;



    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}