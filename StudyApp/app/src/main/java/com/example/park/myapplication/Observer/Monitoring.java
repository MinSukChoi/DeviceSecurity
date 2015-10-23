package com.example.park.myapplication.Observer;

import java.util.Observable;

/**
 * Created by prena on 15. 10. 4..
 */
public class Monitoring extends Observable {
    String state = "Unlocked"; // 옌 없어져야함

    public void notifyToObservers(){
        setChanged();
        notifyObservers();
    }

    public String getState(){
        return this.state;
    } // 이 부분에 레퍼런스 모니터에서 받아서 출력

    public void setStudyMode(String s){ // 너도 없어져야해
        this.state = s;
        notifyToObservers();
    }
}
