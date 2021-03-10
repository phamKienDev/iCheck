package vn.icheck.android.component.tag_view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import vn.icheck.android.R;

public class TagView extends RelativeLayout {
    public static final String TAG = "TagView";
    private int mWidth;
    private int lineMargin;
    private int tagMargin;
    private int textPaddingLeft;
    private int textPaddingRight;
    private int textPaddingTop;
    private int texPaddingBottom;
    private List<Tag> mTags = new ArrayList();
    private LayoutInflater mInflater;
    private OnTagClickListener mClickListener;
    private OnTagDeleteListener mDeleteListener;

    public TagView(Context context) {
        super(context, (AttributeSet) null);
        this.init(context, (AttributeSet) null, 0, 0);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs, 0, 0);
    }

    public TagView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        this.init(ctx, attrs, defStyle, defStyle);
    }

    @TargetApi(21)
    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        ConstantTagView.DEBUG = (context.getApplicationContext().getApplicationInfo().flags & 2) != 0;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, defStyleRes);
        this.lineMargin = (int) typeArray.getDimension(R.styleable.TagView_lineMargin, (float) ResolutionUtil.dpToPx(this.getContext(), 5.0F));
        this.tagMargin = (int) typeArray.getDimension(R.styleable.TagView_tagMargin, (float) ResolutionUtil.dpToPx(this.getContext(), 5.0F));
        this.textPaddingLeft = (int) typeArray.getDimension(R.styleable.TagView_textPaddingLeft, (float) ResolutionUtil.dpToPx(this.getContext(), 8.0F));
        this.textPaddingRight = (int) typeArray.getDimension(R.styleable.TagView_textPaddingRight, (float) ResolutionUtil.dpToPx(this.getContext(), 8.0F));
        this.textPaddingTop = (int) typeArray.getDimension(R.styleable.TagView_textPaddingTop, (float) ResolutionUtil.dpToPx(this.getContext(), 5.0F));
        this.texPaddingBottom = (int) typeArray.getDimension(R.styleable.TagView_textPaddingBottom, (float) ResolutionUtil.dpToPx(this.getContext(), 5.0F));
        typeArray.recycle();
        this.mWidth = ResolutionUtil.getScreenWidth(context);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    private void drawTags() {
        if (this.getVisibility() == View.VISIBLE) {
            this.removeAllViews();
            float total = (float) (this.getPaddingLeft() + this.getPaddingRight());
            int listIndex = 1;
            int index_bottom = 1;
            int index_header = 1;
            Tag tag_pre = null;

            for (Iterator var6 = this.mTags.iterator(); var6.hasNext(); ++listIndex) {
                // inflate tag layout
                final Tag item = (Tag) var6.next();
                final int position = listIndex - 1;
                View tagLayout = this.mInflater.inflate(R.layout.tagview_item, (ViewGroup) null);
                tagLayout.setId(listIndex);
                tagLayout.setBackgroundDrawable(this.getSelector(item));
                TextView tagView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_contain);
                tagView.setText(item.text);
                LayoutParams params = (RelativeLayout.LayoutParams) tagView.getLayoutParams();
                params.setMargins(this.textPaddingLeft, this.textPaddingTop, this.textPaddingRight, this.texPaddingBottom);
                tagView.setLayoutParams(params);
                tagView.setTextColor(item.tagTextColor);
                tagView.setTextSize(2, item.tagTextSize);
                Typeface face = ResourcesCompat.getFont(getContext(), item.fontFamily);
                tagView.setTypeface(face);
                tagLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (TagView.this.mClickListener != null) {
                            TagView.this.mClickListener.onTagClick(position, item);
                        }

                    }
                });
                // calculate　of tag layout width
                float tagWidth = tagView.getPaint().measureText(item.text) + (float) this.textPaddingLeft + (float) this.textPaddingRight;
                TextView deletableView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_delete);
                // deletable text
                if (item.isDeletable) {
                    deletableView.setVisibility(View.VISIBLE);
                    deletableView.setText(item.deleteIcon);
                    int offset = ResolutionUtil.dpToPx(this.getContext(), 2.0F);
                    deletableView.setPadding(offset, this.textPaddingTop, this.textPaddingRight + offset, this.texPaddingBottom);
                    deletableView.setTextColor(item.deleteIndicatorColor);
                    deletableView.setTextSize(2, item.deleteIndicatorSize);
                    deletableView.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            TagView.this.remove(position);
                            if (TagView.this.mDeleteListener != null) {
                                TagView.this.mDeleteListener.onTagDeleted(position, item);
                            }

                        }
                    });
                    tagWidth += deletableView.getPaint().measureText(item.deleteIcon) + (float) deletableView.getPaddingLeft() + (float) deletableView.getPaddingRight();
                } else {
                    deletableView.setVisibility(View.GONE);
                }

                RelativeLayout.LayoutParams tagParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                //add margin of each line
                tagParams.bottomMargin = this.lineMargin;


                float s1 = (float) this.mWidth;
                float s2 = total + (float) this.tagMargin + tagWidth + (float) ResolutionUtil.dpToPx(this.getContext(), 2.0F);
                Log.d(TAG, "drawTags: " + item.text + " - " + s1 + " - " + s2);
                if ((float) s1 <= s2) {
                    if (position != 0) {
                        //need to add in new line
                        tagParams.addRule(RelativeLayout.BELOW, index_bottom);
                        // initialize total param (layout padding left & layout padding right)
                        total = (float) (this.getPaddingLeft() + this.getPaddingRight());
                        index_bottom = listIndex;
                        index_header = listIndex;
                    } else {
                        //no need to new line
                        tagParams.addRule(RelativeLayout.ALIGN_TOP, index_header);
                        if (listIndex != index_header) {
                            //not header of the line
                            tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                            tagParams.leftMargin = this.tagMargin;
                            total += (float) this.tagMargin;
                            if (tag_pre.tagTextSize < item.tagTextSize) {
                                index_bottom = listIndex;
                            }
                        }
                    }
                } else {
                    //no need to new line
                    tagParams.addRule(RelativeLayout.ALIGN_TOP, index_header);
                    if (listIndex != index_header) {
                        //not header of the line
                        tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                        tagParams.leftMargin = this.tagMargin;
                        total += (float) this.tagMargin;
                        if (tag_pre.tagTextSize < item.tagTextSize) {
                            index_bottom = listIndex;
                        }
                    }
                }

                total += (tagWidth + this.tagMargin);
                this.addView(tagLayout, tagParams);
                tag_pre = item;
            }

        }
    }

    private Drawable getSelector(Tag tag) {
        if (tag.background != null) {
            return tag.background;
        } else {
            StateListDrawable states = new StateListDrawable();
            GradientDrawable gd_normal = new GradientDrawable();
            gd_normal.setColor(tag.layoutColor);
            gd_normal.setCornerRadius(tag.radius);
            if (tag.layoutBorderSize > 0.0F) {
                gd_normal.setStroke(ResolutionUtil.dpToPx(this.getContext(), tag.layoutBorderSize), tag.layoutBorderColor);
            }

            GradientDrawable gd_press = new GradientDrawable();
            gd_press.setColor(tag.layoutColorPress);
            gd_press.setCornerRadius(tag.radius);
            states.addState(new int[]{16842919}, gd_press);
            states.addState(new int[0], gd_normal);
            return states;
        }
    }

    public void addTag(Tag tag) {
        this.mTags.add(tag);
        this.drawTags();
    }

    public void addTags(String[] tags) {
        if (tags != null && tags.length > 0) {
            String[] var2 = tags;
            int var3 = tags.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String item = var2[var4];
                Tag tag = new Tag(item);
                this.mTags.add(tag);
            }

            this.drawTags();
        }
    }

    public void addTags(List<Tag> tagList) {
        if (tagList != null && tagList.size() > 0) {
            this.mTags.addAll(tagList);
            this.drawTags();
        }
    }

    public List<Tag> getTags() {
        return this.mTags;
    }

    public void remove(int position) {
        this.mTags.remove(position);
        this.drawTags();
    }

    public void removeAllTags() {
        this.mTags.clear();
        this.drawTags();
    }

    public int getLineMargin() {
        return this.lineMargin;
    }

    public void setLineMargin(float lineMargin) {
        this.lineMargin = ResolutionUtil.dpToPx(this.getContext(), lineMargin);
    }

    public int getTagMargin() {
        return this.tagMargin;
    }

    public void setTagMargin(float tagMargin) {
        this.tagMargin = ResolutionUtil.dpToPx(this.getContext(), tagMargin);
    }

    public int getTextPaddingLeft() {
        return this.textPaddingLeft;
    }

    public void setTextPaddingLeft(float textPaddingLeft) {
        this.textPaddingLeft = ResolutionUtil.dpToPx(this.getContext(), textPaddingLeft);
    }

    public int getTextPaddingRight() {
        return this.textPaddingRight;
    }

    public void setTextPaddingRight(float textPaddingRight) {
        this.textPaddingRight = ResolutionUtil.dpToPx(this.getContext(), textPaddingRight);
    }

    public int getTextPaddingTop() {
        return this.textPaddingTop;
    }

    public void setTextPaddingTop(float textPaddingTop) {
        this.textPaddingTop = ResolutionUtil.dpToPx(this.getContext(), textPaddingTop);
    }

    public int getTexPaddingBottom() {
        return this.texPaddingBottom;
    }

    public void setTexPaddingBottom(float texPaddingBottom) {
        this.texPaddingBottom = ResolutionUtil.dpToPx(this.getContext(), texPaddingBottom);
    }

    public void setOnTagClickListener(OnTagClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setOnTagDeleteListener(OnTagDeleteListener deleteListener) {
        this.mDeleteListener = deleteListener;
    }
}

