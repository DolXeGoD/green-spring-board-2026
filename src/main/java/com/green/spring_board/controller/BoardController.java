package com.green.spring_board.controller;

import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.entity.Boards;
import com.green.spring_board.service.BoardService;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<List<Boards>> getBoards(){
        return ResponseEntity.ok(
            boardService.getAllBoards()
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Boards> getBoardDetail(@PathVariable int id){
        Boards board = boardService.getBoard(id);
        if(board == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(board);
    }

    // 삽입
    @PostMapping
    public ResponseEntity<Void> createBoard(@RequestBody BoardCreateRequest boardCreateRequest) {
        int newBoardId = boardService.createBoard(boardCreateRequest);
        if(newBoardId == -1){
            return ResponseEntity.badRequest().build();
        }

        URI location = URI.create("/api/board/" + newBoardId);

        return ResponseEntity.created(location).build();
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable int id,
            @RequestBody BoardCreateRequest boardCreateRequest
    ){
        int code = boardService.updateBoard(id, boardCreateRequest);
        if(code == -1){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id){
        int code = boardService.deleteBoard(id);
        if(code == -1){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
