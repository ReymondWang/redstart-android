package com.purplelight.redstar.business.result;

import com.purplelight.redstar.business.entity.User;
import com.purplelight.redstar.web.result.Result;

/**
 * 绑定用户的返回值
 * Created by wangyn on 16/5/6.
 */
public class BindUserResult extends Result {

    private User obj = new User();

    public User getObj() {
        return obj;
    }

    public void setObj(User obj) {
        this.obj = obj;
    }
}
