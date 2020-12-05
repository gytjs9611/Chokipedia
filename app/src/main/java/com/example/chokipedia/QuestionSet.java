package com.example.chokipedia;

class QuestionSet {
    private String question;
    private String answer;
    private Boolean result = false;

    void setQuestion(String str){
        question = str;
    }
    void setAnswer(String str){
        answer = str;
    }
    void setResult(Boolean value){
        result= value;
    }


    String getQuestion(){
        return question;
    }
    String getAnswer(){
        return answer;
    }
    Boolean getResult(){
        return result;
    }

}
