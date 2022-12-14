package com.example.canya.heart.service;

import com.example.canya.board.entity.Board;
import com.example.canya.board.repository.BoardRepository;
import com.example.canya.heart.entity.Heart;
import com.example.canya.heart.repository.HeartRepository;
import com.example.canya.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public ResponseEntity<?> heartCreate(Long boardId, Member member) {
        Optional<Board> board = boardRepository.findById(boardId);

        if (board.isEmpty()) {
            return new ResponseEntity<>("해당 게시글이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (heartRepository.existsByBoardAndMember_MemberId(board.get(), member.getMemberId())) {

            heartRepository.deleteByBoardAndMember(board.get(),member);
            board.get().updateHeartCount(false);
            return new ResponseEntity<>("좋아요 삭제 완료.", HttpStatus.OK);
        } else {
            Heart heart = new Heart(member, board.get());
            heartRepository.save(heart);
            board.get().updateHeartCount(true);
            boardRepository.save(board.get());
            return new ResponseEntity<>("좋아요 생성 완료.", HttpStatus.OK);
        }
    }
}
