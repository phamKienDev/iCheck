package vn.icheck.android.component.tag_view;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import vn.icheck.android.R;

public class Tag {
    public int id;
    public String text;
    public int tagTextColor;
    public float tagTextSize;
    public int layoutColor;
    public int layoutColorPress;
    public boolean isDeletable;
    public int deleteIndicatorColor;
    public float deleteIndicatorSize;
    public float radius;
    public String deleteIcon;
    public float layoutBorderSize;
    public int layoutBorderColor;
    public int fontFamily;
    public Drawable background;

    public Tag(String text) {
        this.init(0, text, ConstantTagView.DEFAULT_TAG_TEXT_COLOR, 14.0F, ConstantTagView.DEFAULT_TAG_LAYOUT_COLOR, ConstantTagView.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, ConstantTagView.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 14.0F, 100.0F, "×", 0.0F, ConstantTagView.DEFAULT_TAG_LAYOUT_BORDER_COLOR,R.font.barlow_medium);
    }

    public Tag(String text, int color) {
        this.init(0, text, ConstantTagView.DEFAULT_TAG_TEXT_COLOR, 14.0F, color, ConstantTagView.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, ConstantTagView.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 14.0F, 100.0F, "×", 0.0F, ConstantTagView.DEFAULT_TAG_LAYOUT_BORDER_COLOR,R.font.barlow_medium);
    }

    public Tag(String text, String color) {
        this.init(0, text, ConstantTagView.DEFAULT_TAG_TEXT_COLOR, 14.0F, Color.parseColor(color), ConstantTagView.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, ConstantTagView.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 14.0F, 100.0F, "×", 0.0F, ConstantTagView.DEFAULT_TAG_LAYOUT_BORDER_COLOR,R.font.barlow_medium);
    }

    public Tag(int id, String text) {
        this.init(id, text, ConstantTagView.DEFAULT_TAG_TEXT_COLOR, 14.0F, ConstantTagView.DEFAULT_TAG_LAYOUT_COLOR, ConstantTagView.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, ConstantTagView.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 14.0F, 100.0F, "×", 0.0F, ConstantTagView.DEFAULT_TAG_LAYOUT_BORDER_COLOR,R.font.barlow_medium);
    }

    public Tag(int id, String text, int color) {
        this.init(id, text, ConstantTagView.DEFAULT_TAG_TEXT_COLOR, 14.0F, color, ConstantTagView.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, ConstantTagView.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 14.0F, 100.0F, "×", 0.0F, ConstantTagView.DEFAULT_TAG_LAYOUT_BORDER_COLOR,R.font.barlow_medium);
    }

    public Tag(int id, String text, String color) {
        this.init(id, text, ConstantTagView.DEFAULT_TAG_TEXT_COLOR, 14.0F, Color.parseColor(color), ConstantTagView.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, ConstantTagView.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 14.0F, 100.0F, "×", 0.0F, ConstantTagView.DEFAULT_TAG_LAYOUT_BORDER_COLOR,R.font.barlow_medium);
    }

    private void init(int id, String text, int tagTextColor, float tagTextSize, int layout_color, int layout_color_press, boolean isDeletable, int deleteIndicatorColor, float deleteIndicatorSize, float radius, String deleteIcon, float layoutBorderSize, int layoutBorderColor,int fontFamily) {
        this.id = id;
        this.text = text;
        this.tagTextColor = tagTextColor;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layout_color;
        this.layoutColorPress = layout_color_press;
        this.isDeletable = isDeletable;
        this.deleteIndicatorColor = deleteIndicatorColor;
        this.deleteIndicatorSize = deleteIndicatorSize;
        this.radius = radius;
        this.deleteIcon = deleteIcon;
        this.layoutBorderSize = layoutBorderSize;
        this.layoutBorderColor = layoutBorderColor;
        this.fontFamily=fontFamily;
    }
}