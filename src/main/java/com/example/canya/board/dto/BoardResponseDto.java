package com.example.canya.board.dto;

import com.example.canya.board.entity.Board;
import com.example.canya.image.entity.Image;
import com.example.canya.rating.dto.RatingResponseDto;
import com.example.canya.rating.entity.Rating;
import com.example.canya.timestamp.Time;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardResponseDto {
    private Long boardId;
    private int heartCount;
    private int commentCount;
    private String boardTitle;
    private String boardContent;
    private String address;
    private String totalRating;
    private String memberNickname;
    private String memberProfileImage;
    private String imageUrl;
    private String addressId;
    private String date;
    private LocalDateTime createdAt;
    private Rating rating;
    private boolean isLiked;
    private boolean isLastPage;
    private List<Image> imageList;
    private List<BoardResponseDto> boardResponseDto;
    private List<String> highestRatings;

    public BoardResponseDto(Board board, RatingResponseDto dto, boolean isLiked) {
        this.memberProfileImage = board.getMember().getMemberProfileImage();
        this.memberNickname = board.getMember().getMemberNickname();
        this.address = board.getAddress();
        this.isLiked = isLiked;
        this.boardId = board.getBoardId();
        this.date = Time.calculateTime(board);
        this.boardTitle = board.getBoardTitle();
        this.boardContent = board.getBoardContent();
        this.commentCount = board.getCommentList().size();
        this.heartCount = board.getHeartList().size() != 0 ? board.getHeartList().size() : 0;
        this.totalRating = String.format("%.1f", dto.getTotalRating());
        this.imageUrl = board.getImageList().get(0).getImageUrl();
        this.highestRatings = dto.getHighestRatings();
    }

    public BoardResponseDto(List<BoardResponseDto> dto, int size, int boardNum, int pageNum) {
        this.isLastPage = pageNum == Math.ceil(boardNum / size) || size >= boardNum;
        this.boardResponseDto = dto;
    }

    public BoardResponseDto(Board board, boolean isLiked) {
        this.boardContent = board.getBoardContent();
        this.boardId = board.getBoardId();
        this.addressId = board.getAddressId();
        this.boardTitle = board.getBoardTitle();
        this.createdAt = board.getCreatedAt();
        this.rating = board.getRatingList().get(0);
        this.heartCount = board.getHeartCount();
        this.isLiked = isLiked;
        this.date = Time.calculateTime(board);
        this.memberNickname = board.getMember().getMemberNickname();
        this.memberProfileImage = board.getMember().getMemberProfileImage();
        this.address = board.getAddress();
        this.imageList = board.getImageList();
        this.imageUrl = board.getImageList().get(0).getImageUrl();
        this.totalRating = String.format("%.1f", rating.getTotalRating());
    }
}
