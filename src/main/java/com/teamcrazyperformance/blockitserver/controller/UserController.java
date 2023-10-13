package com.teamcrazyperformance.blockitserver.controller;

import com.teamcrazyperformance.blockitserver.dto.BlockItUserDTO;
import com.teamcrazyperformance.blockitserver.entity.User;
import com.teamcrazyperformance.blockitserver.service.UserProfileImgService;
import com.teamcrazyperformance.blockitserver.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/blockit")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserProfileImgService userProfileImgService;

    /* 회원 가입 시 회원 정보 DB에 등록 */
    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody BlockItUserDTO blockItUserDTO) {
        try {
            User user = new User();
            user.setId(blockItUserDTO.getId());
            user.setName(blockItUserDTO.getNickname());
            log.info("Firebase GID '" + blockItUserDTO.getId() + "'(닉네임: " + blockItUserDTO.getNickname() + ")가 회원 가입 요청");

            userService.addUser(user);
            String response = "Firebase GID '" + blockItUserDTO.getId() + "'(닉네임: " + blockItUserDTO.getNickname() + ")가 회원 가입 성공";
            log.info(response);
            return ResponseEntity.ok(response);  // 200 OK
        } catch (Exception e) {
            log.error("Firebase GID '" + blockItUserDTO.getId() + "가 가입 실패. 예외명: " + e);
            return ResponseEntity.badRequest().body("가입 실패했습니다: " + e);  // 400 Bad Request
        }
    }

    /* 회원 정보 가져오기 */
    @PostMapping("/user")
    public ResponseEntity<BlockItUserDTO> getUserData(@RequestBody BlockItUserDTO blockItUserDTO) {
        try {
            // 데이터베이스에서 userId에 해당하는 사용자 조회
            User user = userService.findUserById(blockItUserDTO.getId());

            // 사용자가 존재하지 않을 경우
            if (user == null) {
                log.error("Firebase GID '" + blockItUserDTO.getId() + "에 맞는 유저 정보가 존재하지 않습니다.");
                return ResponseEntity.badRequest().body(null);
            }

            // 유저 정보 객체에 집어 넣음
            BlockItUserDTO userData = new BlockItUserDTO(
                    blockItUserDTO.getId(),
                    user.getName(),
                    user.getPlayCount(),
                    user.getWinCount());

            // 유저 정보 반환
            log.info("Firebase GID '" + userData.getId() + "'(닉네임: " + userData.getNickname() + ")가 정보 요청");
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /* 닉네임 변경 요청 */
    @PostMapping("/user/update/nickname")
    public ResponseEntity<String> changeUserName(@RequestBody BlockItUserDTO blockItUserDTO) {
        try {
            // 중복 체크
            User userWithSameNickname = userService.findUserByName(blockItUserDTO.getNickname());
            if (userWithSameNickname != null) {
                log.info("Firebase GID '" + blockItUserDTO.getId() + "'(닉네임: " + blockItUserDTO.getNickname() + ")가 닉네임 변경 실패, 닉네임 중복");
                return ResponseEntity.ok("Failed");
            }

            // 유저 존재 여부 체크
            User user = userService.findUserById(blockItUserDTO.getId());
            if(user == null) {
                log.error("Firebase GID '" + blockItUserDTO.getId() + "에 맞는 회원 정보가 존재하지 않습니다.");
                return ResponseEntity.badRequest().body("회원 정보 없음");
            }

            // 체크 통과 시 유저 닉네임 변경
            user.setName(blockItUserDTO.getNickname());
            userService.saveUser(user);

            log.info("Firebase GID '" + blockItUserDTO.getId() + "'(닉네임: " + blockItUserDTO.getNickname() + ")가 닉네임 변경 완료");
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            log.info("Firebase GID '" + blockItUserDTO.getId() + "'(닉네임: " + blockItUserDTO.getNickname() + ")가 닉네임 변경 실패, 예외명: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("닉네임 변경 실패, 예외명: " + e);
        }
    }

    /* 이미지 변경 요청 */
    @PostMapping("/user/update/img")
    public ResponseEntity<String> uploadProfile(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        try {
            String fileName = userProfileImgService.storeFile(file, userId);  // 파일 저장 후 파일명 반환

            User user = userService.findUserById(userId);
            user.setProfileImgPath(fileName);
            userService.saveUser(user);

            log.info("Firebase GID '" + user.getId() + "'(닉네임: " + user.getName() + ")가 프로필 사진 업로드 성공");
            return ResponseEntity.ok("프로필 사진이 업로드되었습니다.");
        } catch (Exception e) {
            log.info("Firebase GID '" + userId + "'가 프로필 사진 업로드 실패, 예외명: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 사진 업로드 실패, 예외명: " + e);
        }
    }

    /* 이미지 로딩 */
    @GetMapping("/user/img/{userId}")
    public ResponseEntity<byte[]> getProfile(@PathVariable String userId) {
        User user = userService.findUserById(userId);
        if (user != null && user.getProfileImgPath() != null) {
            try {
                log.info("Firebase GID '" + user.getId() + "'(닉네임: " + user.getName() + "가 프로필 사진 요청");
                byte[] file = userProfileImgService.loadFileAsBytes(user.getProfileImgPath());
                return ResponseEntity.ok(file);
            } catch (Exception e) {
                log.info("Firebase GID '" + user.getId() + "'(닉네임: " + user.getName() + "가 프로필 사진 요청 실패, 예외명: " + e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            if (user != null) {
                log.info("Firebase GID '" + user.getId() + "'(닉네임: " + user.getName() + "가 프로필 사진 요청 실패, 파일 존재하지 않음");
            } else {
                log.info("프로필 사진 요청 실패, 해당 유저가 존재하지 않음");
            }
            return ResponseEntity.notFound().build();
        }
    }
}
