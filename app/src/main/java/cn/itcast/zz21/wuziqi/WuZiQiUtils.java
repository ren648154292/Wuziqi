package cn.itcast.zz21.wuziqi;

import android.graphics.Point;

import java.util.List;

public class WuZiQiUtils {
    private static final int MAX_COUNT_IN_LINE=5;

    public static boolean checkFiveInLine(List<Point> piecesArray) {
        checkIsFull(piecesArray.size());

        for(Point p:piecesArray) {
            int x=p.x;
            int y=p.y;

            if(checkHorizontal(x,y,piecesArray)) return true;
            else if(checkVertical(x,y,piecesArray)) return true;
            else if(checkLeftDiagonal(x,y,piecesArray)) return true;
            else if(checkRightDiagonal(x,y,piecesArray)) return true;
        }
        return false;
    }

    /**
     * 判断棋盘是否已满
     */
    public static boolean checkIsFull(int number) {
        if(number==WuZiQiPanel.MAX_PIECES_NUMBER) {
            return true;
        }
        return false;
    }

    /**
     * 判断(x,y)点的棋子是否在横向上有5个相连的棋子
     */
    private static boolean checkHorizontal(int x, int y, List<Point> piecesArray) {
        int count=1;

        //往同一个方向最多只要判断四次

        //先判断左边的
        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x-i,y))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //判断右边的
        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x+i,y))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        return false;
    }

    /**
     * 判断(x,y)点的棋子是否在纵向上有5个相连的棋子
     */
    private static boolean checkVertical(int x, int y, List<Point> piecesArray) {
        int count=1;

        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x,y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //判断右边的
        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x,y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        return false;
    }

    /**
     * 判断(x,y)点的棋子是否在左斜(/)向上有5个相连的棋子
     */
    private static boolean checkLeftDiagonal(int x, int y, List<Point> piecesArray) {
        int count=1;

        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x-i,y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //判断右边的
        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x+i,y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        return false;
    }

    /**
     * 判断(x,y)点的棋子是否在右斜(\)向上有5个相连的棋子
     */
    private static boolean checkRightDiagonal(int x, int y, List<Point> piecesArray) {
        int count=1;

        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x-i,y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //判断右边的
        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x+i,y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        return false;
    }
}
