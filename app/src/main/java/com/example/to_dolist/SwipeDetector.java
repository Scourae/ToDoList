package com.example.to_dolist;

import android.view.MotionEvent;
import android.view.View;

public class SwipeDetector implements View.OnTouchListener {

    public enum Action {
        LtR,
        RtL, 
        TtB, 
        BtT, 
        None 
    }
    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) 
        {
        	case MotionEvent.ACTION_DOWN: 
        	{
	            downX = event.getX();
	            downY = event.getY();
	            mSwipeDetected = Action.None;
	            return false;
        	}
	        case MotionEvent.ACTION_MOVE: 
	        {
	            upX = event.getX();
	            upY = event.getY();
	
	            float deltaX = downX - upX;
	            float deltaY = downY - upY;
	
	            // horizontal swipe
	            if (Math.abs(deltaX) > MIN_DISTANCE) 
	            {
	                // Left to right
	                if (deltaX < 0) 
	                {
	                    mSwipeDetected = Action.LtR;
	                    return true;
	                }
	                // Right to left
	                if (deltaX > 0) 
	                {
	                    mSwipeDetected = Action.RtL;
	                    return true;
	                }
	            } 
	            else 
	            {
	                // vertical swipe
	                if (Math.abs(deltaY) > MIN_DISTANCE) 
	                {
	                    // Top to bottom
	                    if (deltaY < 0) 
	                    {
	                        mSwipeDetected = Action.TtB;
	                        return false;
	                    }
	                    // Bottom to top
	                    if (deltaY > 0) 
	                    {
	                        mSwipeDetected = Action.BtT;
	                        return false;
	                    }
	                } 
	            }
	            return true;
	        }
        }
        return false;
    }
}