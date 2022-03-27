package com.util;

import java.util.ArrayList;

public class FisherYatesShuffle {
	
	public static ArrayList<Integer> shuffle(ArrayList<Integer> questions) {
        int n = questions.size();
        for(int i=n-1; i>0; i--) {
            int r = (int) (Math.random()*1000)%n;
            int temp = questions.get(i);
            questions.set(i, questions.get(r));
            questions.set(r, temp);
        }
        return questions;
    }
	
}
