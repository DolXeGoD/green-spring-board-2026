package com.green.spring_board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    private BoardRepository boardRepository;

    public BoardController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // 전체 조회
    @GetMapping
    public List<Boards> getBoards(){
        return boardRepository.findAll();
    }

    // 상세 조회
    @GetMapping("/{id}")
    public Boards getBoardDetail(@PathVariable int id){
        return boardRepository.findById(id).get();
    }

    // 삽입
    @PostMapping
    public void createBoard(@RequestBody BoardCreateRequest boardCreateRequest) {
        System.out.println(boardCreateRequest.getTitle());
        System.out.println(boardCreateRequest.getContent());

        Boards board = new Boards();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());

        boardRepository.save(board);
    }

    // 수정
    @PatchMapping("/{id}")
    public void updateBoard(
            @PathVariable int id,
            @RequestBody BoardCreateRequest boardCreateRequest
    ){
        Boards board = boardRepository.findById(id).get();

        if(boardCreateRequest.getTitle() != null) {
            board.setTitle(boardCreateRequest.getTitle());
        }
        if(boardCreateRequest.getContent() != null) {
            board.setContent(boardCreateRequest.getContent());
        }
        boardRepository.save(board);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable int id){
        boardRepository.deleteById(id);
    }
}
