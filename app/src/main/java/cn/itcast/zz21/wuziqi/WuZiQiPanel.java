package cn.itcast.zz21.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

//网格棋盘（面板）
public class WuZiQiPanel extends View {

    private int mPanelWidth;
    private float mLinearHeight;//每一个格子高度,注意为float类型
    //上面两个成员变量在初始化的时候，有人也许会考虑在setMeasuredDimension(length,length)之后初始化
    //不过还有一个更好的选择，可以在onSizeChanged（当宽和高确定发生改变以后会回调）方法中初始化

    private static final int MAX_LINE = 10;

    public static final int MAX_PIECES_NUMBER = MAX_LINE * MAX_LINE;//用于判断是否没有点可下，如果没有了即为和棋

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private static final float RATIO_PIECE = 3 * 1.0f / 4;//设置棋子的大小为棋盘格子的3/4

    private boolean mIsWhite = true;//判断是否白子执手,初始化为true表明白子先手
    private List<Point> mWhiteArray = new ArrayList<>();//存放白子的坐标
    private List<Point> mBlackArray = new ArrayList<>();

    private boolean mIsGameOver;
    private int mResult;//0-和棋，1-白子赢，2-黑子赢
    public static final int DRAW = 0;//平局
    public static final int WHITE_WON = 1;
    public static final int BLACK_WON = 2;

    private ResultListener mListener;

    public void setListener(ResultListener listener) {
        mListener = listener;
    }

    public WuZiQiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        mPaint.setColor(0x99ffffff);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //想把网格棋盘绘制成正方形
        //如果传入的是一个精确的值，就直接取值
        //同时也考虑到获得的widthSize与heightSize是设置的同样的值(如固定的100dp)，但也有可能是match_parent，所以在这里取最小值
        int length = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            length = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {//注意这里为else if，限定heightMode==MeasureSpec.UNSPECIFIED时才进入，使得逻辑更加严谨
            length = widthSize;
        }

        //将宽和高设置为同样的值
        //在重写onMeasure方法时，必需要调用该方法存储测量好的宽高值
        setMeasuredDimension(length, length);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLinearHeight = mPanelWidth * 1.0f / MAX_LINE;

        //根据实际的棋盘格子的宽度按照一定的比例缩小棋子
        int pieceWidth = (int) (mLinearHeight * RATIO_PIECE);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean isWhiteWon = WuZiQiUtils.checkFiveInLine(mWhiteArray);
        boolean isBlackWon = WuZiQiUtils.checkFiveInLine(mBlackArray);
        if (isWhiteWon || isBlackWon) {
            mIsGameOver = true;
            mResult = isWhiteWon ? WHITE_WON : BLACK_WON;
            mListener.showResult(mResult);
            return;
        }

        boolean isFull = WuZiQiUtils.checkIsFull(mWhiteArray.size() + mBlackArray.size());
        if (isFull) {
            mResult = DRAW;
            mListener.showResult(mResult);
        }
    }

    private void drawPieces(Canvas canvas) {
        for (Point whitePoint : mWhiteArray) {
            canvas.drawBitmap(mWhitePiece, (whitePoint.x + (1 - RATIO_PIECE) / 2) * mLinearHeight, (whitePoint.y + (1 - RATIO_PIECE) / 2) * mLinearHeight, null);
        }
        for (Point blackPoint : mBlackArray) {
            canvas.drawBitmap(mBlackPiece, (blackPoint.x + (1 - RATIO_PIECE) / 2) * mLinearHeight, (blackPoint.y + (1 - RATIO_PIECE) / 2) * mLinearHeight, null);
        }
    }

    /**
     * 绘制棋盘
     * 因为棋子可以下在边界的点上，所以边界的线与View的边界还是有一定距离的(左右上下的情况是一样的)
     * 所以这里设定边界线距离View的边界有1/2mLinearHeight
     */
    private void drawBoard(Canvas canvas) {
        for (int i = 0; i < MAX_LINE; i++) {

            int startX = (int) mLinearHeight / 2;
            int endX = (int) (mPanelWidth - mLinearHeight / 2);

            int y = (int) ((0.5 + i) * mLinearHeight);

            //首先画横线
            canvas.drawLine(startX, y, endX, y, mPaint);

            //然后再画纵线(与横线的坐标是相反的)
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) return false;

        //首先需要设置该View是对MotionEvent.ACTION_UP事件感兴趣的
        //return true就表示告诉父View自己的态度，表明可以响应MotionEvent.ACTION_UP事件（而至于父View是否将该事件交给你处理还是拦截下来，那是父View的事了），这里只是表明自己的态度
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point point = getValidPoint(x, y);

            //这里还需要考虑contains方法是间接的通过equals方法比较的
            //而Point中的equals方法是通过比较x和y的值来实现的，而不是比较两个引用变量是否指向同一地址（因为在getValidPoint方法中每次都new了一个Point实例）
            //所以这里用contains方法是符合要求的
            if (mWhiteArray.contains(point) || mBlackArray.contains(point)) {
                return false;//（1）
            }

            if (mIsWhite) {
                mWhiteArray.add(point);
            } else {
                mBlackArray.add(point);
            }
            invalidate();//请求重绘
            mIsWhite = !mIsWhite;
            return true;//（2）
            //因为这里只需要处理到ACTION_UP事件即可，接下来要发生的事件不用管（如果有的话），
            //所以在（1）（2）处返回true还是false都所谓（因为WuZiQiPanel是最终层次的view了，其下不会再有子view了），程序都能够正常执行，但是逻辑上返回true比较好
        }
        //上面这点一定要注意

        //改成ACTION_UP的话，需要在末尾直接return true;如果return super.onTouchEvent(event)的话是不会绘制棋子的
        //return super.onTouchEvent(event);
        return true;

        //如果返回true，表示该方法消费了此事，
        //如果为false，那么表明该方法并未处理完全，该事件任然需要以某种方法传递下去继续等待处理
    }

    /**
     * 通过传入的坐标得到一个合法的落子位置
     */
    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLinearHeight), (int) (y / mLinearHeight));//逻辑的实现很巧妙
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());//注意，系统本来需要保存的数据不能忽视
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, (ArrayList) mWhiteArray);//Point已经实现了Parcelable接口
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, (ArrayList) mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //需要判断state是否为我们设置的state类型，如果是则表明有需要自己去恢复的数据
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));//恢复保存起来的系统数据，不能忽略
            return;
        }
        super.onRestoreInstanceState(state);
    }

    protected void restart() {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        invalidate();
    }
}
