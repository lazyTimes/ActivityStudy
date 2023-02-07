package com.zsp.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 审理流转状态
 * @author Xander
 * @version v1.0.0
 * @Package : com.zsp.pojo
 * @Description : 审理流转状态
 * @Create on : 2/7/2023 19:38
 **/

public class ApprovalTemp implements Serializable {

    private String agree;

    private String toNext;

    public String getAgree() {
        return agree;
    }

    public void setAgree(String agree) {
        this.agree = agree;
    }

    public String getToNext() {
        return toNext;
    }

    public void setToNext(String toNext) {
        this.toNext = toNext;
    }
}
