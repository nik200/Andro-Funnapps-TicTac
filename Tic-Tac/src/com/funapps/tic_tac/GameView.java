package com.funapps.tic_tac;

import com.funapps.tic_tac.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private GameThread gameThread;
	private SurfaceHolder holder;
	
	public GameView(Context context) {
		super(context);
		holder = this.getHolder();
		gameThread = new GameThread(holder);
		holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		if(gameThread.getState() == Thread.State.NEW)
		gameThread.start();
		else
			gameThread.threadControl = true;
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		gameThread.threadControl = false;
	}
	
	public void setClickPosition(float x,float y){
		gameThread.clickX = x;
		gameThread.clickY = y;
		gameThread.clicked = true;
	}
	
	public class GameThread extends Thread{
		private static final int BLANK_GRID = 1;
		private static final int FILLED_GRID = 2;
		private static final int WON_GRID = 3;
		private static final int DRAW_GRID = 4;
		private float[][] gridPositions;//size = 9 positions of tics and toes in grid
		private float[][] horLinePositions;//size = 3 game wine line horizontal positions
		private float[][] verLinePositions;//size = 3 game win line vertical positions
		private float[][] inclinedLinePositions;//size 2 incline win lines positions
		
		private int DimX,DimY;
		private Canvas canvas;
		public float clickX, clickY;
		
		private SurfaceHolder mSurfaceHolder;
		public boolean turn,firstTurn, clicked,recieved,connected,threadControl,send,rstGame;
		public int rcvdPos,sendPos;
		private int gamesPlayed,win,lose,position,winSequence;
		private int[] positionArray;//size = 9 initially 0
		private Bitmap tempBitmap;
		
		private Bitmap[] bitmaps;//size=25
		private float[][] dimensions;//digitSizeForGameNo  15, digitSizeForScore  16  size=17X2
		private int[] drawables;//size = 25
		/*
		private Bitmap background;		//10
		private Bitmap grid;			//11
		private Bitmap tic;			//12
		private Bitmap toe;			//13
		private Bitmap gameNumber;		//14
		private Bitmap gameNumberLSB;	//15
		private Bitmap gameNumberMSB;	//16
		private Bitmap turn;			//17
		private Bitmap turn_toe;			//18
		private Bitmap turn_tic;			//19
		private Bitmap score;			//20
		private Bitmap horLine;			//21
		private Bitmap verLine;			//22
		private Bitmap frontInclinedLine;	//23
		private Bitmap backInclinedLine;	//24  ----
		private Bitmap zero;			//0
		private Bitmap one;			//1
		private Bitmap two;			//2
		private Bitmap three;			//3
		private Bitmap four;			//4
		private Bitmap five;			//5
		private Bitmap six;			//6
		private Bitmap seven;			//7
		private Bitmap eight;			//8
		private Bitmap nine;			//9
		*/
		
		public GameThread(SurfaceHolder holder){
			mSurfaceHolder  = holder;
			gridPositions = new float[9][2];
			horLinePositions = new float[3][2];
			verLinePositions = new float[3][2];
			inclinedLinePositions = new float[2][2];
			positionArray = new int[9];
			bitmaps = new Bitmap[25];
			dimensions = new float[17][2];
			drawables = new int[25];
			firstTurn = true;
			clicked = false;
			recieved = false;
			connected = false;
			threadControl = true;
		}
		
		//sets winSequence returns true if player with last updated position wins
		private boolean checkForWin(){  
			int x,y,i,n;
			x = (position-1)/3;
			y = (position-1)%3;
			n = positionArray[position-1];
			for(i=0;i<3;i++)
				if(positionArray[x*3+i]!=n)
					break;
			if(i==3){
				winSequence = x;
				return true;
			}
			for(i=0;i<3;i++)
				if(positionArray[i*3+y]!=n)
					break;
			if(i==3){
				winSequence = y+3;
				return true;
			}
			if(positionArray[2] == n && positionArray[4] == n && positionArray[6] == n)
			{	winSequence = 6;
				return true;
			}
			if((positionArray[0] == n) && (positionArray[4] == n) && (positionArray[8] == n))
			{	winSequence = 7;
				return true;
			}
			return false;
		}
		
		//returns true if all places are filled
		private boolean checkForDraw(){
			for(int i=0;i<=8;i++)
				if(positionArray[i]==0)
					return false;
			return true;
		}
		
		//checks for validity of current move and sets 'position' based on clickX and clickY
		//does not effects 'clicked' flag	
		private boolean getClickedPosition(){
			for(int i=0;i<=8;i++){
				if(clickX > DimX*(gridPositions[i][0]-0.05) && clickX < DimX*(gridPositions[i][0]+0.15933) && clickY > DimY*(gridPositions[i][1]+0.06) && clickY < DimY*(gridPositions[i][1]+0.19350))
						if(positionArray[i]==0)	{
							position = i+1;
							return true;
						}
			}
			position = 0;
			return false;
		}
		
		private void initBitmaps(){
			try{
				synchronized(canvas = mSurfaceHolder.lockCanvas()){
					DimX = canvas.getWidth();
					DimY = canvas.getHeight();
				}
			}
			catch(NullPointerException e){
			}
			finally{
				if(canvas!=null){ mSurfaceHolder.unlockCanvasAndPost(canvas); canvas= null;}
			}
			int i;
			drawables[0] = R.drawable.zero;
			drawables[1] = R.drawable.one;
			drawables[2] = R.drawable.two;
			drawables[3] = R.drawable.three;
			drawables[4] = R.drawable.four;
			drawables[5] = R.drawable.five;
			drawables[6] = R.drawable.six;
			drawables[7] = R.drawable.seven;
			drawables[8] = R.drawable.eight;
			drawables[9] = R.drawable.nine;
			drawables[10] = R.drawable.background;
			drawables[11] = R.drawable.grid;
			drawables[12] = R.drawable.tic;
			drawables[13] = R.drawable.toe;
			drawables[14] = R.drawable.game_no;
			drawables[15] = R.drawable.zero;
			drawables[16] = R.drawable.zero;
			drawables[17] = R.drawable.turn;
			drawables[18] = R.drawable.toe;
			drawables[19] = R.drawable.tic;
			drawables[20] = R.drawable.score;
			drawables[21] = R.drawable.hor_line;
			drawables[22] = R.drawable.ver_line;
			drawables[23] = R.drawable.front_incline_line;
			drawables[24] = R.drawable.back_incline_line;
			
			dimensions[0][0] = (float) 1.00000 ; dimensions[0][1] = (float) 1.00000 ;  
			dimensions[1][0] = (float) 0.82000 ; dimensions[1][1] = (float) 0.53650 ;  
			dimensions[2][0] = (float) 0.10933 ; dimensions[2][1] = (float) 0.10350 ;  
			dimensions[3][0] = (float) 0.10933 ; dimensions[3][1] = (float) 0.10350 ;  
			dimensions[4][0] = (float) 0.54133 ; dimensions[4][1] = (float) 0.07300 ;  
			dimensions[5][0] = (float) 1.00000 ; dimensions[5][1] = (float) 1.00000 ;  
			dimensions[6][0] = (float) 1.00000 ; dimensions[6][1] = (float) 1.00000 ;  
			dimensions[7][0] = (float) 0.30933 ; dimensions[7][1] = (float) 0.06100 ;  
			dimensions[8][0] = (float) 0.08333 ; dimensions[8][1] = (float) 0.07950 ;  
			dimensions[9][0] = (float) 0.08333 ; dimensions[9][1] = (float) 0.07950 ;  
			dimensions[10][0] = (float) 0.74200 ; dimensions[10][1] = (float) 0.06000 ;  
			dimensions[11][0] = (float) 0.83600 ; dimensions[11][1] = (float) 0.04250 ;  
			dimensions[12][0] = (float) 0.05667 ; dimensions[12][1] = (float) 0.54800 ;  
			dimensions[13][0] = (float) 0.74000 ; dimensions[13][1] = (float) 0.50900 ;  
			dimensions[14][0] = (float) 0.73533 ; dimensions[14][1] = (float) 0.47950 ;  
			dimensions[15][0] = (float) 0.09867 ; dimensions[15][1] = (float) 0.06300 ;  
			dimensions[16][0] = (float) 0.08000 ; dimensions[16][1] = (float) 0.05050 ;  
			
			gridPositions[0][0] = (float) 0.15600; gridPositions[0][1] = (float) 0.24650;
			gridPositions[1][0] = (float) 0.42267; gridPositions[1][1] = (float) 0.24650;
			gridPositions[2][0] = (float) 0.70100; gridPositions[2][1] = (float) 0.24650;
			gridPositions[3][0] = (float) 0.15600; gridPositions[3][1] = (float) 0.40700;
			gridPositions[4][0] = (float) 0.42267; gridPositions[4][1] = (float) 0.40700;
			gridPositions[5][0] = (float) 0.70100; gridPositions[5][1] = (float) 0.40700;
			gridPositions[6][0] = (float) 0.15600; gridPositions[6][1] = (float) 0.58000;
			gridPositions[7][0] = (float) 0.42267; gridPositions[7][1] = (float) 0.58000;
			gridPositions[8][0] = (float) 0.70100; gridPositions[8][1] = (float) 0.58000;
			
			horLinePositions[0][0] = (float) 0.06267; horLinePositions[0][1] = (float) 0.27100;
			horLinePositions[1][0] = (float) 0.06267; horLinePositions[1][1] = (float) 0.44350;
			horLinePositions[2][0] = (float) 0.06267; horLinePositions[2][1] = (float) 0.61450;
		
			verLinePositions[0][0] = (float) 0.19067; verLinePositions[0][1] = (float) 0.18750;
			verLinePositions[1][0] = (float) 0.45733; verLinePositions[1][1] = (float) 0.18750;
			verLinePositions[2][0] = (float) 0.72400; verLinePositions[2][1] = (float) 0.18750;
			
			inclinedLinePositions[0][0] = (float) 0.13933; inclinedLinePositions[0][1] = (float) 0.21850;//front inclined'/'
			inclinedLinePositions[1][0] = (float) 0.09067; inclinedLinePositions[1][1] = (float) 0.21200;//back inclined '\'
			
			for(i=0;i<=24;i++)
			{	
				bitmaps[i] = BitmapFactory.decodeResource(getResources(),drawables[i]);
				if(i>9)
					bitmaps[i]= Bitmap.createScaledBitmap(bitmaps[i], (int)(DimX*dimensions[i-10][0]), (int)(DimY*dimensions[i-10][1]), true);
			}
			
			
		}
		
		
		private void drawGrid(int mode){
			int i;
			try{
				synchronized(canvas = mSurfaceHolder.lockCanvas()){
					canvas.drawBitmap(bitmaps[10],0,0,null);//for background
					canvas.drawBitmap(bitmaps[11],(float)(DimX*0.07467),(float)(DimY*0.18750),null);//for grid
					canvas.drawBitmap(bitmaps[14],(float)(DimX*0.09333),(float)(DimY*0.05450),null);//for game_no
					i=gamesPlayed/10;
					tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[15][0]),(int)(DimY*dimensions[15][1]),true);//scale digit bitmap
					canvas.drawBitmap(tempBitmap,(float)(DimX*0.64667),(float)(DimY*0.06250),null);//for game_no_msb
					i=gamesPlayed%10;
					tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[15][0]),(int)(DimY*dimensions[15][1]),true);//scale digit bitmap
					canvas.drawBitmap(tempBitmap,(float)(DimX*0.75000),(float)(DimY*0.06250),null);//for game_no_lsb
					
					
					switch(mode){
						case BLANK_GRID:canvas.drawBitmap(bitmaps[17],(float)(DimX*0.25333),(float)(DimY*0.77650),null);//for turn
										if(turn) canvas.drawBitmap(bitmaps[18],(float)(DimX*0.58067),(float)(DimY*0.77100),null);//draw toe for turn;
										else canvas.drawBitmap(bitmaps[19],(float)(DimX*0.58067),(float)(DimY*0.77100),null);//draw tic for turn;
										
										canvas.drawBitmap(bitmaps[20],(float)(DimX*0.05800),(float)(DimY*0.89350),null);//for score
										//draw games_won in front of toe
										//draw games_lost in front of tic
										i=win/10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.50200),(float)(DimY*0.90200),null);//for game_won_msb
										i=win%10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.58000),(float)(DimY*0.90200),null);//for game_won_lsb
										
										i=lose/10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.79667),(float)(DimY*0.90200),null);//for game_lost_msb
										i=lose%10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.87900),(float)(DimY*0.90200),null);//for game_lost_lsb
		
										break;
						case FILLED_GRID:canvas.drawBitmap(bitmaps[17],(float)(DimX*0.25333),(float)(DimY*0.77650),null);//for turn
										if(turn) canvas.drawBitmap(bitmaps[18],(float)(DimX*0.58067),(float)(DimY*0.77100),null);//draw toe for turn;
										else canvas.drawBitmap(bitmaps[19],(float)(DimX*0.58067),(float)(DimY*0.77100),null);//draw tic for turn;
										
										canvas.drawBitmap(bitmaps[20],(float)(DimX*0.05800),(float)(DimY*0.89350),null);//for score
										//draw games_won in front of toe
										//draw games_lost in front of tic
										i=win/10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.50200),(float)(DimY*0.90200),null);//for game_won_msb
										i=win%10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.58000),(float)(DimY*0.90200),null);//for game_won_lsb
										
										i=lose/10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.79667),(float)(DimY*0.90200),null);//for game_lost_msb
										i=lose%10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.87900),(float)(DimY*0.90200),null);//for game_lost_lsb
		
										for(i=0;i<=8;i++){
											if(positionArray[i]==1)
												canvas.drawBitmap(bitmaps[13],DimX*gridPositions[i][0],DimY*gridPositions[i][1],null);//draw toe @ gridPositions[i]
												
											if(positionArray[i]==2)
												canvas.drawBitmap(bitmaps[12],DimX*gridPositions[i][0],DimY*gridPositions[i][1],null);//draw tic @ gridPositions[i]
										}			
										break;
						case WON_GRID:canvas.drawBitmap(bitmaps[20],(float)(DimX*0.05800),(float)(DimY*0.81850),null);//for score
										//draw games_won in front of toe
										//draw games_lost in front of tic
										i=win/10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.50200),(float)(DimY*0.82700),null);//for game_won_msb
										i=win%10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.58000),(float)(DimY*0.82700),null);//for game_won_lsb
										
										i=lose/10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.79667),(float)(DimY*0.82700),null);//for game_lost_msb
										i=lose%10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.87900),(float)(DimY*0.82700),null);//for game_lost_lsb
										for(i=0;i<=8;i++){
												if(positionArray[i]==1)
													canvas.drawBitmap(bitmaps[13],DimX*gridPositions[i][0],DimY*gridPositions[i][1],null);//draw toe @ gridPositions[i]
													
												if(positionArray[i]==2)
													canvas.drawBitmap(bitmaps[12],DimX*gridPositions[i][0],DimY*gridPositions[i][1],null);//draw tic @ gridPositions[i]
										}			
										switch(winSequence){
											case 0:
											case 1:
											case 2://draw horizontal line @horLinePositions[Winsequence]
													canvas.drawBitmap(bitmaps[21],DimX*horLinePositions[winSequence][0],DimY*horLinePositions[winSequence][1],null);
													break;
											case 3:
											case 4:
											case 5://draw vertical line @verLinePositions[Winsequence-3]
													canvas.drawBitmap(bitmaps[22],DimX*verLinePositions[winSequence-3][0],DimY*verLinePositions[winSequence-3][1],null);
													break;
											case 6://draw front inclined line @inclinedLinePositions[Winsequence-6]
													canvas.drawBitmap(bitmaps[23],DimX*inclinedLinePositions[winSequence-6][0],DimY*inclinedLinePositions[winSequence-6][1],null);
													break;
											case 7://draw back inclined line @inclinedLinePositions[Winsequence-6]
													canvas.drawBitmap(bitmaps[24],DimX*inclinedLinePositions[winSequence-6][0],DimY*inclinedLinePositions[winSequence-6][1],null);
													break;
										}
										break;
						case DRAW_GRID:canvas.drawBitmap(bitmaps[20],(float)(DimX*0.05800),(float)(DimY*0.81850),null);//for score
										//draw games_won in front of toe
										//draw games_lost in front of tic
										i=win/10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.50200),(float)(DimY*0.82700),null);//for game_won_msb
										i=win%10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.58000),(float)(DimY*0.82700),null);//for game_won_lsb
										
										i=lose/10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.79667),(float)(DimY*0.82700),null);//for game_lost_msb
										i=lose%10;
										tempBitmap = Bitmap.createScaledBitmap(bitmaps[i],(int)(DimX*dimensions[16][0]),(int)(DimY*dimensions[16][1]),true);//scale digit bitmap
										canvas.drawBitmap(tempBitmap,(float)(DimX*0.87900),(float)(DimY*0.82700),null);//for game_lost_lsb
										
										for(i=0;i<=8;i++){
												if(positionArray[i]==1)
													canvas.drawBitmap(bitmaps[13],DimX*gridPositions[i][0],DimY*gridPositions[i][1],null);//draw toe @ gridPositions[i]
													
												if(positionArray[i]==2)
													canvas.drawBitmap(bitmaps[12],DimX*gridPositions[i][0],DimY*gridPositions[i][1],null);//draw tic @ gridPositions[i]
										}	
										break;
					}
				}	
			}
			catch(NullPointerException e){
			}
			finally{
				if(canvas!=null) mSurfaceHolder.unlockCanvasAndPost(canvas);
				canvas = null;
			}
		}
			
		private void initVariables(){
			
			turn = firstTurn=true;
			clicked = false;
			recieved = false;
			
			gamesPlayed = 0;
			win = 0;
			lose = 0;
			position = 0;
			for(int i=0;i<=8;i++)
				positionArray[i]=0;
		}
		
		private void newGameConnectionless(){
		
			clicked = false;
			position = 0;
			turn = firstTurn;
			for(int i=0;i<=8;i++)
				positionArray[i] = 0;
			gamesPlayed++;
			drawGrid(BLANK_GRID);
			
			while(threadControl){
				if(rstGame){
					
					//rstGame=false;
					break;
				}
				if(clicked){
					if(getClickedPosition()){   //returns true if clicked position is valid and sets clicked position
						if(turn) positionArray[position-1]=1;
						else positionArray[position-1]=2;
						turn = !turn;
						drawGrid(FILLED_GRID);
						if(checkForWin()){
							if(!turn)
								win++;
							else
								lose++;
							firstTurn = !turn;
							drawGrid(WON_GRID);
							break;
						}
						if(checkForDraw()){
							firstTurn = !firstTurn;
							drawGrid(DRAW_GRID);
							break;
						}
						position = 0;
					}
					clicked = false;
				}
			}
		}
		
		
		
		public void run(){
			initBitmaps();
			initVariables();
			//drawGrid(BLANK_GRID);
			while(threadControl){
				newGameConnectionless();
				if(rstGame){
						rstGame = false;
						initVariables();
						continue;
				}
				clicked = false;
				while(!clicked)
					if(rstGame){
						rstGame = false;
						initVariables();
						break;
				}
				clicked = false;
			}
		}

	}
	
	public void resetGame(){
		gameThread.rstGame = true;
	}

}
