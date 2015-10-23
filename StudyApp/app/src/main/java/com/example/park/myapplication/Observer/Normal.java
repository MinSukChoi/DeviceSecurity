package com.example.park.myapplication.Observer;

import android.widget.EditText;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by prena on 15. 10. 4..
 */
public class Normal implements Observer {
    private Observable observable;
    private EditText editText;

    public Normal(Observable observable, EditText editText){
        this.observable = observable;
        this.editText = editText;
        observable.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof Monitoring){
            Monitoring monitoring = (Monitoring) observable;
            display(monitoring);
        }
    }

    public void display(Monitoring monitoring){
        editText.setText(monitoring.getState());
    }
}
