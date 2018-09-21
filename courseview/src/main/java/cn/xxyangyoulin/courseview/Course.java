package cn.xxyangyoulin.courseview;

public class Course {
    /** 行号 */
    protected int row;
    /** 所占行数 */
    protected int rowNum = 1;
    /** 列号 */
    protected int col;

    /** 颜色 */
    protected int color;
    /** 显示的内容 */
    protected String text;

    /**活跃状态*/
    protected boolean activeStatus  =true;

    protected boolean showVisiable = true;

    public Course(int row, int rowNum, int col, int color) {
        this.row = row;
        this.rowNum = rowNum;
        this.col = col;
        this.color = color;
    }

    public Course() {
    }

    public int getRow() {
        return row;
    }

    public Course setRow(int row) {
        this.row = row;
        return this;
    }

    public int getRowNum() {
        return rowNum;
    }

    public Course setRowNum(int rowNum) {
        this.rowNum = rowNum;
        return this;
    }

    public int getCol() {
        return col;
    }

    public Course setCol(int col) {
        this.col = col;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Course setColor(int color) {
        this.color = color;
        return this;
    }

    public String getText() {
        return text;
    }

    public Course setText(String text) {
        this.text = text;
        return this;
    }

    public boolean getActiveStatus() {
        return activeStatus;
    }

    public Course setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
        return this;
    }

    public boolean isShowVisiable() {
        return showVisiable;
    }

    public Course setShowVisiable(boolean showVisiable) {
        this.showVisiable = showVisiable;
        return this;
    }

    @Override
    public String toString() {
        return "Course{" +
                "row=" + row +
                ", rowNum=" + rowNum +
                ", col=" + col +
                ", text=" + text +
                '}';
    }
}
