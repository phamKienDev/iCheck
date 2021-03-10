package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {
    private ProgressBar progressBar;
    private float from;
    private float to;
    private TextView progressTv;

    public ProgressBarAnimation(ProgressBar progressBar, TextView progressTv, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
        this.progressTv = progressTv;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
        try {
            if ((int) value < 86) {
                progressTv.setText(String.format("%d%%", (int) value));
            } else {
                progressTv.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }
}
