package cn.xxyangyoulin.courseview;

public class Course {
    /**
     * 行号
     */
    protected int row;
    /**
     * 所占行数
     */
    protected int rowNum;
    /**
     * 列号
     */
    protected int col;

    /**
     * 颜色
     */
    protected int color;

    public Course(int row, int rowNum, int col,int color) {
        this.row = row;
        this.rowNum = rowNum;
        this.col = col;
        this.color = color;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
