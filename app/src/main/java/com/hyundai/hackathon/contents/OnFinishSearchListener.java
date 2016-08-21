package com.hyundai.hackathon.contents;

/**
 * Created by Cho on 2016-08-20.
 */
import java.util.List;

public interface OnFinishSearchListener {
    public void onSuccess(List<Item> itemList);
    public void onFail();
}
