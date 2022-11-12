package com.study.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.dao.UserDao;
import com.study.domain.User;
import com.study.util.CheckCodeUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDao userDao;

    //生成验证码
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        ServletOutputStream os = response.getOutputStream();
        String code = CheckCodeUtil.outputVerifyImage(100,50,os,4);
        session.setAttribute("checkCodegen",code);
    }
    //注册功能
    @PostMapping("/insert")
    //将前端传过来的数据与后端的变量绑定
    public Result insert(HttpServletResponse response, HttpServletRequest request, @Param("username")String username,
                         @Param("account")String account, @Param("password")String password
                        , @Param("checkCode")String checkCode) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object checkCodegen = session.getAttribute("checkCodegen");
        System.out.println(checkCodegen);
        if (username == null){
            return new Result(Code.SAVE_ERR,"昵称不能为空");
        }else if (account == null){
            return new Result(Code.SAVE_ERR,"账号不能为空");
        }else if (password == null){
            return new Result(Code.SAVE_ERR,"密码不能为空");
        }
        //比较用户输入的验证码和生成的验证码
        if (checkCodegen.equals(checkCode)){
            LambdaQueryWrapper<User> laq = new LambdaQueryWrapper();
            //查询条件,此处类似于 account.equals(user.account)
            laq.eq(User::getAccount,account);
            User user = userDao.selectOne(laq);
            if (user != null){
                return new Result(Code.SAVE_ERR,"账号已被注册");
            }else {
                //将属性值放入user对象中
                User user1 = new User();
                user1.setAccount(account);
                user1.setPassword(password);
                user1.setUsername(username);
                //向数据库中添加user对象
                userDao.insert(user1);
                if (userDao.selectOne(laq) != null){
                    return new Result(Code.SAVE_OK,"注册成功");
                }else {
                    return new Result(Code.SAVE_ERR,"注册失败");
                }
            }
        }else {
            return new Result(Code.CHECK_ERR,"验证码错误");
        }

    }
    @PostMapping("/login")
    //接收前端传来的账号和密码，并将其与对应变量一一绑定
    public Result login(HttpServletResponse response, HttpServletRequest request,@Param("account")String account,
                        @Param("password")String password,@Param("remember")String remember){
        LambdaQueryWrapper<User> laq = new LambdaQueryWrapper();
        //查询条件,此处类似于 account.equals(user.account)
        laq.eq(User::getAccount,account).eq(User::getPassword,password);
        //从数据库查询匹配的信息
        User user = userDao.selectOne(laq);
        if (user != null){
            return new Result(Code.GET_OK,"查询到对应用户",account);
        }else {
            return new Result(Code.GET_ERR,"查无此人");
        }
    }
    @GetMapping("/selectByAccount/{account}")
    public Result selectByAccount(@PathVariable("account")String account){
        LambdaQueryWrapper<User> laq = new LambdaQueryWrapper<>();
        laq.eq(User::getAccount,account);
        User user = userDao.selectOne(laq);
        String username = user.getUsername();
        String photo = user.getPhoto();
        return new Result(Code.GET_OK,photo,username);
    }
}

