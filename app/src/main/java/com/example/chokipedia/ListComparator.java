package com.example.chokipedia;

import java.util.Comparator;

public class ListComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        float rate1 = ((WrongAnswerListItem)o1).rate;
        float rate2 = ((WrongAnswerListItem)o2).rate;

        if(rate1<rate2){
            return 1;
        }
        else if(rate1>rate2){
            return -1;
        }
        else{
            return 0;
        }

    }



}
