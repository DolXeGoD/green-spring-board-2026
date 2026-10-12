package com.green.spring_board.controller;

import com.green.spring_board.dto.ApiResponse;
import com.green.spring_board.dto.CommentCreateRequest;
import com.green.spring_board.dto.CommentResponse;
import com.green.spring_board.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/board/{id}/comment")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable int id,
            @RequestBody CommentCreateRequest commentCreateRequest
    ){
        commentService.createComment(commentCreateRequest, id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/board/{id}/comment")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> readComments(
            @PathVariable int id
    ){
        return ResponseEntity.ok(
                ApiResponse.ok(commentService.readComments(id))
        );
    }

    @PatchMapping("/comment/{id}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @RequestBody CommentCreateRequest commentCreateRequest,
            @PathVariable int id
    ){
        commentService.updateComment(commentCreateRequest, id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable int id
    ){
        commentService.deleteComment(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok());
    }
}
