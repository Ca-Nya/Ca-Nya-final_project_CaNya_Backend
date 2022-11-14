package com.example.canya.Board.Service;

import com.example.canya.Board.Dto.BoardRequestDto;
import com.example.canya.Board.Dto.BoardResponseDto;
import com.example.canya.Board.Entity.Board;
import com.example.canya.Board.Repository.BoardRepository;

import com.example.canya.Image.Entity.Image;
import com.example.canya.Image.Repository.ImageRepository;
import com.example.canya.Member.Entity.Member;
import com.example.canya.Member.Repository.MemberRepository;
import com.example.canya.Member.Service.MemberDetailsImpl;
import com.example.canya.Rating.Dto.RatingRequestDto;
import com.example.canya.Rating.Dto.RatingResponseDto;
import com.example.canya.Rating.Entity.Rating;
import com.example.canya.Rating.Repository.RatingRepository;
import com.example.canya.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final RatingRepository ratingRepository;

    private final S3Uploader s3Uploader;


    public ResponseEntity<?> saveBoard( MemberDetailsImpl memberDetails){
        Board board = boardRepository.save(new Board(memberDetails.getMember()));
        ratingRepository.save(new Rating(board));
        return new ResponseEntity<>(board.getBoardId(), HttpStatus.OK);
    }

    public ResponseEntity<?> editBoard(BoardRequestDto dto , List<MultipartFile> images,
                                       Long boardId, Member member) throws IOException{
        Board board = boardRepository.findById(boardId).orElse(null);
        assert board != null;
        if(member.getMemberId().equals(board.getMember().getMemberId())){
            board.update(dto);
        }
        return new ResponseEntity<>("수정이 완료되었습니다.",HttpStatus.OK);
    }


    public ResponseEntity<?> cancelBoard(Long boardId, Member member){
        Optional<Board> board = boardRepository.findById(boardId);
        if(board.isEmpty()){
            return new ResponseEntity<>("게시글이 존재 하지 않습니다",HttpStatus.BAD_REQUEST);
        }
        if(board.get().getMember() != member){
            return new ResponseEntity<>("작성자가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
        }
        boardRepository.deleteById(boardId);
        return new ResponseEntity<>("작성이 취소 되었습니다.", HttpStatus.OK);
    }


    public ResponseEntity<?> getBoards(){
        // createdAt desc, first image for each board, highest two ratings

        List<Board> createdAtBoards = boardRepository.findAllByOrderByCreatedAtAsc();
        List<BoardResponseDto> boardsDto = new ArrayList<>();

        for (Board boards : createdAtBoards) {
            List<Image> imageList = boards.getImageList();
            Rating ratingList = ratingRepository.findRatingByBoardAndMemberId(boards,boards.getMember().getMemberId()).orElse(null);

            Map<String, Long> ratingMap = new HashMap<String, Long>();
            ratingMap.put("coffeeRating",ratingList.getCoffeeRate());
            ratingMap.put("dessertRating",ratingList.getDessertRate());
            ratingMap.put("kindnessRating",ratingList.getKindnessRate());
            ratingMap.put("moodRating",ratingList.getMoodRate());
            ratingMap.put("parkingRating",ratingList.getParkingRate());
            ratingMap.put("priceRating",ratingList.getPriceRate());

            Map.Entry<String,Long> maxEntry = null;
            Map.Entry<String,Long> secondMaxEntry = null;
            for (Map.Entry<String, Long> entry : ratingMap.entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            }
            ratingMap.values().remove(maxEntry.getValue());
            for (Map.Entry<String, Long> entry : ratingMap.entrySet()){
                if(secondMaxEntry == null || entry.getValue() > secondMaxEntry.getValue()){
                    secondMaxEntry = entry;
                }
            }
            System.out.println("second max : "+secondMaxEntry);
            System.out.println("max : " +maxEntry);

            RatingResponseDto responseDto = new RatingResponseDto(maxEntry, secondMaxEntry);

            if(imageList.size()!=0) {
                String image = imageList.get(0).getImageUrl();
                boardsDto.add(new BoardResponseDto(boards, image,responseDto));
            }
            boardsDto.add(new BoardResponseDto(boards,null,responseDto));
        }
        System.out.println(createdAtBoards);

        return new ResponseEntity<>(boardsDto,HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> confirmBoard( BoardRequestDto dto , List<MultipartFile> images,
                                         Long boardId) throws IOException {
        Board board = boardRepository.findById(boardId).orElse(null);
        assert board != null;
        board.update(dto);

        if(images!=null){
            for (MultipartFile image : images) {
                imageRepository.save(new Image(board,s3Uploader.upload(image,"boardImage"),board.getMember()));
            }
        }

        //coffee , dessert, price, mood, parking
        RatingRequestDto ratingDto = new RatingRequestDto(dto.getRatings()[0],dto.getRatings()[1],dto.getRatings()[2],dto.getRatings()[3],dto.getRatings()[4],dto.getRatings()[5]);
        Rating rating = new Rating(ratingDto,board,board.getMember());
        System.out.println(rating);
        ratingRepository.save(rating);


        System.out.println(Arrays.toString(dto.getRatings()));

        return new ResponseEntity<>("게시글 작성이 완료 되었습니다.", HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> deleteBoard(Long boardId, Member member) {

        Optional<Board> board = boardRepository.findById(boardId);
        if(board.isEmpty()){
            return new ResponseEntity<>("게시글이 존재 하지 않습니다",HttpStatus.BAD_REQUEST);
        }
        if(board.get().getMember()!=member){
            return new ResponseEntity<>("작성자가 일치 하지 않습니다", HttpStatus.BAD_REQUEST);
        }
        boardRepository.deleteById(boardId);
        return new ResponseEntity<>("삭제가 완료 되었습니다",HttpStatus.OK);


    }

//    @Transactional
//    public ResponseEntity<?> addImage(Long boardId, MultipartFile profileImage) throws IOException {
//
//        Optional<Board> board = boardRepository.findById(boardId);
//        if(board.isEmpty()){
//            return new ResponseEntity<>("보드가 존재하지 않습니다", HttpStatus.BAD_REQUEST);
//        }
//        Image image = new Image(board.get(),s3Uploader.upload(profileImage,"boardImage"));
//
//        imageRepository.save(image);
//
//        ImageResponseDto responseDto = new ImageResponseDto(image);
//
//        return new ResponseEntity<>(responseDto,HttpStatus.OK);
//    }


}
