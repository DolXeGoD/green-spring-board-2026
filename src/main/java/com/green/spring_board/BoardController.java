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

    // 삭제

}
