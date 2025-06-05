package com.sunseed.entity;

import com.sunseed.enums.PageType;
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
@ToString
@Builder
@Table(name = "static_pages")
public class StaticPages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

//    private String articleImagePath;

    private String description;
    private String summary;

    @Enumerated(EnumType.STRING)
    private PageType pageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserProfile createdBy;

    @Builder.Default
    private Boolean hide = Boolean.TRUE;

    //  private String link;
    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;


}