package com.green.spring_board.service;

import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.entity.Board;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BoardService {
    private BoardRepository boardRepository;
    private UserRepository userRepository;

    // 전체 조회
    public List<BoardResponse> getAllBoards() {
        // List<Board> -> List<BoardResponse> 형태로 변환 후 반환
        List<Board> boards = boardRepository.findAll();

        // 1. List<BoardResponse> 형태의 빈 리스트 생성
        List<BoardResponse> boardResponses = new ArrayList<>();

        // 2. Board 개수만큼 반복하며 new BoardResponse 생성
        for (Board board : boards) {
            // 3. 1번에서 만든 리스트에 추가
            boardResponses.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }
        return boardResponses;


    }

    // 상세 조회
    public BoardResponse getBoard(int id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(optionalBoard.isEmpty()) {
            // 요청한 게시글을 찾지 못한 경우
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }
        Board board = optionalBoard.get();

        User user = board.getUser();
        System.out.println(user.getNickname());
        board.setHits(board.getHits() + 1);
        boardRepository.save(board);
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getHits(),
                board.getUser().getId(),
                board.getUser().getNickname(),
                board.getCreatedDatetime(),
                board.getUpdatedDatetime()
        );
    }

    public int createBoard(BoardCreateRequest boardCreateRequest, Integer userId) {
        if(boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()){
            throw new UserRequestException("잘못된 입력값 입니다.");
        }
        if(boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()){
            throw new UserRequestException("잘못된 입력값 입니다.");
        }

        // userId 유효성 체크 (해당 userId의 유저가 정상적으로 존재하는지)
        // TODO :: 이후 삭제/탈퇴 유저에 대한 검증도 추가 필요
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UnauthenticatedException("로그인한 사용자를 찾을 수 없습니다.");
        }

        Board board = new Board();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        board.setUser(user.get());

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

        if(boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) {
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
