package com.example.bullbearwar.Firebase;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

import com.example.bullbearwar.Firebase.TradingMasterActivity;

public class AnimationOnTrading implements TabHost.OnTabChangeListener {
private Context context;
private static final int ANIMATION_TIME = 240;
private TabHost tabHost;
private View preivousView;
private View currentView;
private int currentTab;

public AnimationOnTrading(Context context , TabHost tabHost){

    this.context = context;
    this.tabHost = tabHost;
    this.preivousView = tabHost.getCurrentView();
}


    @Override
    public void onTabChanged(String s) {
    currentView = tabHost.getCurrentView();
        ((TradingMasterActivity) this.context).updatePortfolioAndViews();

    if (tabHost.getCurrentTab() > currentTab)
    {
        preivousView.setAnimation(outToLeftAnimation());
        currentView.setAnimation(inFromRightAnimation());

    }
    else
        {
            preivousView.setAnimation(outToRightAnimation());

            currentView.setAnimation(inFromLeftAnimation());
        }
        preivousView = currentView;
        currentTab = tabHost.getCurrentTab();



    }
    private Animation inFromLeftAnimation() {
    Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
            -1.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f);
    return setProperties(inFromLeft);

    }

    private Animation setProperties(Animation animation) {
    animation.setDuration(ANIMATION_TIME);
    animation.setInterpolator(new AccelerateInterpolator());
    return animation;
    }

    private Animation outToRightAnimation() {
        Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                1.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f);
        return setProperties(outToRight);
    }

    private Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                1.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f);
        return setProperties(inFromRight);
    }

    private Animation outToLeftAnimation() {
        Animation outToLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                -1.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f);
        return setProperties(outToLeft);
    }
}
