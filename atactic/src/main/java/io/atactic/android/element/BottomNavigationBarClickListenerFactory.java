package io.atactic.android.element;

import android.content.Context;

public abstract class BottomNavigationBarClickListenerFactory {

    private static BottomNavigationBarClickListener singleInstance;

    public static BottomNavigationBarClickListener getClickListener(Context c, Class current){
        if (singleInstance == null){
            singleInstance = new BottomNavigationBarClickListener(c, current);
        }else{
            singleInstance.setContext(c);
            singleInstance.setCurrentActivity(current);
        }
        return  singleInstance;
    }

}
