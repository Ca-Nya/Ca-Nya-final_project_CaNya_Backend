package com.example.canya.timestamp;

import com.example.canya.board.entity.Board;
import com.example.canya.comment.entity.Comment;
import com.example.canya.communityComment.entity.CommunityComment;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;

@Component
public class Time {
    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String calculateTime(Board board) {

        long curTime = System.currentTimeMillis();
        long regTime = Timestamp.valueOf(board.getCreatedAt()).getTime();
        long diffTime = Math.abs((curTime - regTime) / 1000);
        String msg = null;

        if (diffTime < TIME_MAXIMUM.SEC) {
// sec
            msg = diffTime + "초 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
// min
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
// hour
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
// day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
// day
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }

    public static String calculateTime(CommunityComment communityCommentList) {

        long curTime = System.currentTimeMillis();
        long regTime = Timestamp.valueOf(communityCommentList.getCreatedAt()).getTime();
        long diffTime = Math.abs((curTime - regTime) / 1000);
        String msg = null;

        if (diffTime < TIME_MAXIMUM.SEC) {
// sec
            msg = diffTime + "초 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
// min
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
// hour
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
// day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
// day
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }


    public static String calculateTime(Comment comment) {

        long curTime = System.currentTimeMillis();
        long regTime = Timestamp.valueOf(comment.getCreatedAt()).getTime();
        long diffTime = Math.abs((curTime - regTime) / 1000);
        String msg = null;

        if (diffTime < TIME_MAXIMUM.SEC) {
// sec
            msg = diffTime + "초 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
// min
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
// hour
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
// day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
// day
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}

