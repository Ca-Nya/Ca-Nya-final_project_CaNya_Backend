package com.example.canya.communityComment.service;

import com.example.canya.community.entity.Community;
import com.example.canya.community.repository.CommunityRepository;
import com.example.canya.communityComment.dto.CommunityCommentRequestDto;
import com.example.canya.communityComment.dto.CommunityCommentResponseDto;
import com.example.canya.communityComment.entity.CommunityComment;
import com.example.canya.communityComment.repository.CommunityCommentRepository;
import com.example.canya.member.entity.Member;
import com.example.canya.member.service.MemberDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository communityCommentRepository;

    @Transactional
    public ResponseEntity<?> createCommunityComment(Long communityId, CommunityCommentRequestDto communityCommentRequestDto, Member member) {
        Optional<Community> community = communityRepository.findById(communityId);

        if(community.isEmpty()) {
            return new ResponseEntity<>("해당 커뮤니티 글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        CommunityComment communityComment = new CommunityComment(communityCommentRequestDto, community.get(), member);
        communityCommentRepository.save(communityComment);

        return new ResponseEntity<>("댓글 생성이 완료되었습니다.", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> getcommunityCommentList(Long communityId) {
        List<CommunityComment> communityComments = communityCommentRepository.findByCommunity_CommunityId(communityId);
        if (communityComments == null) {
            return new ResponseEntity<>("본 커뮤니티에는 댓글이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        List<CommunityCommentResponseDto> communityCommentResponseDtoList = new ArrayList<>();
        for(CommunityComment communityCommentList : communityComments) {
            CommunityCommentResponseDto communityCommentResponseDto = new CommunityCommentResponseDto(communityCommentList);

            communityCommentResponseDtoList.add(communityCommentResponseDto);
        }

        return new ResponseEntity<>(communityCommentResponseDtoList, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> updateCommunityComment(Long communityCommentId, CommunityCommentRequestDto communityCommentRequestDto, MemberDetailsImpl memberDetails) {

        Optional<CommunityComment> communityComment = communityCommentRepository.findById(communityCommentId);
        if (communityComment.isEmpty()) {
            return new ResponseEntity<>("해당 댓글이 존재하지 않습니다", HttpStatus.BAD_REQUEST);
        }

        if (!(communityComment.get().getMember().getMemberId().equals(memberDetails.getMember().getMemberId()))) {
            return new ResponseEntity<>("댓글 작성자가 아닙니다", HttpStatus.BAD_REQUEST);
        }

        communityComment.get().update(communityCommentRequestDto);
        return new ResponseEntity<>("수정이 완료되었습니다", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteCommunityComment(Long communityCommentId, Member member) {

        Optional<CommunityComment> communityComment = communityCommentRepository.findById(communityCommentId);
        if (communityComment.isEmpty()) {
            return new ResponseEntity<>("해당 댓글이 존재하지 않습니다", HttpStatus.BAD_REQUEST);
        }

        if (!(communityComment.get().getMember().getMemberId().equals(member.getMemberId()))) {
            return new ResponseEntity<>("댓글 작성자가 아닙니다", HttpStatus.BAD_REQUEST);
        }

        communityCommentRepository.deleteById(communityCommentId);
        return new ResponseEntity<>("삭제가 완료되었습니다", HttpStatus.OK);
    }

}
