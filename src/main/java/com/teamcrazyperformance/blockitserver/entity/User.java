package com.teamcrazyperformance.blockitserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private int playCount = 0;

    @Column
    private int winCount = 0;

    @Column
    private int consecWinCount = 0;

    @Column
    private String profileImgPath = null;
}
