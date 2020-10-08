package com.wapazock.solveit.globalClasses;

import android.util.Log;

public class notifications {
    private String header , textPreview , userAccount , replyID , questionID;
    private Long time ;
    boolean toQuestion ;
    boolean toReply ;
    private static final String TAG = "notifications";


    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public boolean isToQuestion() {
        return toQuestion;
    }

    public void setToQuestion(boolean toQuestion) {
        this.toQuestion = toQuestion;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getTextPreview() {
        return textPreview;
    }

    public void setTextPreview(String textPreview) {
        this.textPreview = textPreview;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void parseReply(replies REPLY){
        this.header = "Replied to your question" ;
        this.textPreview = REPLY.getReplyText();
        this.time = REPLY.time ;
        this.userAccount = REPLY.userAccount ;
        this.toQuestion = true ;
        this.questionID = REPLY.replyTo ;
    }

    public void parseReplyToReply(replies REPLY){
        this.header = "Responded saying" ;
        this.textPreview = REPLY.getReplyText();
        this.time = REPLY.time ;
        this.userAccount = REPLY.userAccount ;
        this.toQuestion = false ;
        this.toReply = true ;
        this.replyID = REPLY.replyTo ;
    }


    public void parseReplyToReplyToReply(replies REPLY){
        this.header = "Replied again" ;
        this.textPreview = REPLY.getReplyText();
        this.time = REPLY.time ;
        this.userAccount = REPLY.userAccount ;
        this.toQuestion = false ;
        this.toReply = false ;
        this.replyID = REPLY.getReplyTo();
    }

    public boolean isToReply() {
        return toReply;
    }

    public void setToReply(boolean toReply) {
        this.toReply = toReply;
    }
}
