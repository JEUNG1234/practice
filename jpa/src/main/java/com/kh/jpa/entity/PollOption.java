package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "POLL_OPTION")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTION_ID")
    private Long id; // DB 자동 생성 ID

    @Column(name = "OPTION_TEXT", nullable = false, length = 255)
    private String text;

    @Column(name = "VOTES", nullable = false)
    @Builder.Default
    private Integer votes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLL_ID", nullable = false)
    private Poll poll;
}