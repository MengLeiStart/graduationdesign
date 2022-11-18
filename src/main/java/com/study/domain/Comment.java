package com.study.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("comments")
public class Comment {
    private int id;
    private String comment;
    @TableField("information_id")
    private int otherId;
}
