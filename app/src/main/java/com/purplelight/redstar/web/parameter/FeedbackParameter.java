package com.purplelight.redstar.web.parameter;

import com.purplelight.redstar.web.entity.Feedback;

/**
 * 意见反馈参数
 * Created by wangyn on 16/5/13.
 */
public class FeedbackParameter extends Parameter {
    private Feedback feedback;

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}
