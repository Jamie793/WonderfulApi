package com.myopicmobile.textwarrior.common;

/**
 * Created by nirenr on 2019/12/1.
 */

public class Quad {
    private int _first;
    private int _second;
    private int _third;
    private int _fourth;
    public int top;
    public int bottom;
    public int left;
    public int right;

    public Quad(int first, int second, int third, int fourth){
        _first = first;
        _second = second;
        _third = third;
        _fourth = fourth;
        left=first;
        top=second-1;
        right=third;
        bottom=fourth-1;
    }

    public final int getFirst(){
        return _first;
    }

    public final int getSecond(){
        return _second;
    }

    public final void setFirst(int value){
        _first = value;
    }

    public final void setSecond(int value){
        _second = value;
    }

    public int getThird() {
        return _third;
    }

    public void setThird(int value) {
        _third = value;
    }

    public int getFourth() {
        return _fourth;
    }

    public void setFourth(int value) {
        _fourth = value;
    }

    @Override
    public String toString() {
        return "Quad("+_first+":"+_second+":"+_third+":"+_fourth+")";
    }


}
