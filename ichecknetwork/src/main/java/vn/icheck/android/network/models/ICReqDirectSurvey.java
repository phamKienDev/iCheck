package vn.icheck.android.network.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class ICReqDirectSurvey {
    @Expose
    private long question_id;
    @Expose
    private List<Long> option_ids;

    public ICReqDirectSurvey() {
        this.question_id = 0;
        this.option_ids = new ArrayList<>();
    }

    public long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(long question_id) {
        this.question_id = question_id;
    }

    public List<Long> getOption_ids() {
        return option_ids;
    }

    public void setOption_ids(List<Long> options_ids) {
        this.option_ids = options_ids;
    }
}
