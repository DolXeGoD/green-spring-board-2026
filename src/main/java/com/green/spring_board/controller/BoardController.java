package com.green.spring_board.controller;

import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.entity.Board;
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
    public ResponseEntity<List<Board>> getBoards(){
        return ResponseEntity.ok(
            boardService.getAllBoards()
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardDetail(@PathVariable int id){
        try {
            Board board = boardService.getBoard(id);
            if(board == null){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(board);
        } catch (ResourceNotFoundException e){
            // 게시글을 못 찾았을 때 (404)
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            // 위에도 아니면, 무조건 Java 아니면 DB 에러로 서버 에러 (500)
            return ResponseEntity.internalServerError().build();
        }
    }

    // 삽입
    @PostMapping
    public ResponseEntity<Void> createBoard(@RequestBody BoardCreateRequest boardCreateRequest) {
        try{
            int newBoardId = boardService.createBoard(boardCreateRequest);
            URI location = URI.create("/api/board/" + newBoardId);

            return ResponseEntity.created(location).build();
        } catch (UserRequestException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable int id,
            @RequestBody BoardCreateRequest boardCreateRequest
    ){
        try{
            boardService.updateBoard(id, boardCreateRequest);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id){
        try{
            boardService.deleteBoard(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
