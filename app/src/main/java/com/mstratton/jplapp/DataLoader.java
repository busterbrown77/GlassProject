package com.mstratton.jplapp;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

public abstract class DataLoader<T> extends AsyncTaskLoader<T>{

    private T mData;

    public DataLoader(Context context){
        super(context);
    }

    @Override
    public void onStartLoading(){
        if(mData != null){
            deliverResult(mData);
        }else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(T data){
        mData = data;
        if(isStarted()){
            super.deliverResult(data);
        }
    }


}
