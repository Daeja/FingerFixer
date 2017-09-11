package eth.fingerfixer;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable levelDrawable ;
    private String titleStr;
    private String writerStr;

    public void setLevel(Drawable icon) {
        levelDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setWriter(String title) {
        writerStr = title ;
    }

    public Drawable getLevel() {
        return this.levelDrawable ;
    }
    public String getTitle() { return this.titleStr ; }
    public String getWriter() { return this.writerStr ; }
}