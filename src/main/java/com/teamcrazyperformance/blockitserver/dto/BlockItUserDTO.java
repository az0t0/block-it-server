package com.teamcrazyperformance.blockitserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlockItUserDTO {
    private String Id;              // Firebase UID (유저 ID)
    private String Nickname;        // 유저 네임
    private int PlayCount;          // 플레이 횟수
    private int WinCount;           // 승리 횟수
}
