package com.green.spring_board.service;

import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.entity.Board;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BoardService {
    private BoardRepository boardRepository;

    // 전체 조회
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 상세 조회
    public Board getBoard(int id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(optionalBoard.isEmpty()) {
            // 요청한 게시글을 찾지 못한 경우
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
        Board board = optionalBoard.get();

        board.setHits(board.getHits() + 1);
        boardRepository.save(board);
        return board;
    }

    public int createBoard(BoardCreateRequest boardCreateRequest) {
        if(boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()){
            throw new UserRequestException("잘못된 입력값 입니다.");
        }
        if(boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()){
            throw new UserRequestException("잘못된 입력값 입니다.");
        }

        Board board = new Board();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());

        Board savedBoard = boardRepository.save(board);

        return savedBoard.getId();
    }

    public void updateBoard(int id, BoardCreateRequest boardCreateRequest) {
        Optional<Board> optionalBoards = boardRepository.findById(id);
        if(optionalBoards.isEmpty()) {
            // 게시글을 못 찾은 경우
            throw new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
        }
        Board board = optionalBoards.get();

        if(boardCreateRequest.getTitle() != null && !boardCreateRequest.getTitle().isBlank()) {
            board.setTitle(boardCreateRequest.getTitle());
        }
        if(boardCreateRequest.getContent() != null && !boardCreateRequest.getContent().isBlank()) {
            board.setContent(boardCreateRequest.getContent());
        }
        boardRepository.save(board);
    }

    public void deleteBoard(int id) {
        boolean isExist = boardRepository.existsById(id);
        if(!isExist) {
            throw new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
        }
        boardRepository.deleteById(id);
    }
}
