package com.study.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("userinformations")
public class User {
private int id;

@TableField("user_name")
private String username;

@TableField("user_password")
private String password;

@TableField("user_account")
private String account;

@TableField("user_sex")
private String sex;

@TableField("user_birthday")
private String birthday;

@TableField("user_status")
private boolean status;

@TableField("user_photo")
private String photo;
}
