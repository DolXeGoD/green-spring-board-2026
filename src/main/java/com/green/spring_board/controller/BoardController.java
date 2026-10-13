package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.service.BoardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/board")
@AllArgsConstructor
public class BoardController {

    private final BoardService boardService;
    // 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardResponse>>> getBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String order

    ){
        return ResponseEntity.ok(
            ApiResponse.ok(boardService.getAllBoards(page, size, order))
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoardDetail(
            @PathVariable int id
    ){
        BoardResponse board = boardService.getBoard(id);
        return ResponseEntity.ok(
                ApiResponse.ok(board)
        );
    }

    @GetMapping("/my-boards")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getMyBoards(){
        List<BoardResponse> response = boardService.getMyBoards();

        return ResponseEntity.ok(
                ApiResponse.ok(response)
        );
    }

    // 삽입
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest
    ) {
            int newBoardId = boardService.createBoard(boardCreateRequest);
            URI location = URI.create("/api/board/" + newBoardId);
            return ResponseEntity.created(location).body(ApiResponse.ok());
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(
            @PathVariable int id,
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest
    ) {
        boardService.updateBoard(id, boardUpdateRequest);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable int id
    ) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<Void>> likeBoard(
            @PathVariable int id
    ){
        boardService.pressLike(id);
        return ResponseEntity.ok(ApiResponse.ok());

        // 내가 이 게시글 좋아요 눌렀는지
    }

    @GetMapping("/like/{id}")
    public ResponseEntity<ApiResponse<LikeDetailResponse>> viewLikeDetails(
            @PathVariable int id
    ){

        // 이 게시글에 좋아요 누른 유저들의 유저명
        LikeDetailResponse response = boardService.getLikeDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> searchPosts(
            @RequestParam String keyword
    ) {
        List<BoardResponse> response = boardService.search(keyword);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
