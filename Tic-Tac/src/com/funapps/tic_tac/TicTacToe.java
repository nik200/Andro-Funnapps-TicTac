package com.funapps.tic_tac;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;


public class TicTacToe extends Activity {

	public GameView gameView;	
	public boolean bp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	gameView = new GameView(this);
        setContentView(gameView);
        bp = false;
    }
    
    @Override
    public void onBackPressed(){
    	bp = !bp;
    	if(bp)
    		gameView.resetGame();
    	else
    		System.exit(0);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event){
    	if (event.getAction() == MotionEvent.ACTION_DOWN){
    		bp = false;
    		gameView.setClickPosition(event.getX(),event.getY());
    		return true;
    	}
    	return super.onTouchEvent(event);
    }
    
    
}
