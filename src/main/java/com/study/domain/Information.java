package com.study.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("informations")
public class Information {
    private int id;
    @TableField("information_photo")
    private String photo;
    @TableField("information_description")
    private String description;
    @TableField("information_type")
    private String type;
    @TableField("information_useraccount")
    private String useraccount;
    @TableField("information_username")
    private String name;
}
