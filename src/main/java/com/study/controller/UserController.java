package com.study.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.dao.UserDao;
import com.study.domain.User;
import com.study.util.CheckCodeUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDao userDao;

    //生成验证码
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取流，来输出验证码
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
    //登录功能
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
    //查询用户昵称和头像
    @GetMapping("/selectByAccount/{account}")
    public Result selectByAccount(@PathVariable("account")String account){
        LambdaQueryWrapper<User> laq = new LambdaQueryWrapper<>();
        //根据接收到的account查询数据库里面的用户
        laq.eq(User::getAccount,account);
        User user = userDao.selectOne(laq);
        String username = user.getUsername();
        String photo = user.getPhoto();
        //将用户的头像和昵称返回给前端
        return new Result(Code.GET_OK,photo,username);
    }
    //查询用户所有信息
    @GetMapping("/selectAllByAccount/{account}")
    public Result selectAllByAccount(@PathVariable("account")String account){
        LambdaQueryWrapper<User> laq = new LambdaQueryWrapper<>();
        laq.eq(User::getAccount,account);
        User user = userDao.selectOne(laq);
        return new Result(Code.GET_OK,"查询成功",user);
    }
    //上传头像功能
    @PostMapping("/files")
    public Result upload(@RequestParam("userphoto")MultipartFile userphoto,@Param("account")String account){
        //获得上传的文件名
        String names = userphoto.getOriginalFilename();
        //获得上传文件的后缀
        String lastname = names.substring(names.lastIndexOf("."));
        if(".jpg".equals(lastname) || ".jpeg".equals(lastname) || ".png".equals(lastname)){
            //随机生成一个名字
            String uuid = UUID.randomUUID().toString();
            //给上传的文件重新命名，防止重名，也解决中文名称图片上传乱码问题
            names = uuid + lastname;
            User user = new User();
            user.setPhoto(names);
            LambdaQueryWrapper<User> laq = new LambdaQueryWrapper<>();
            laq.eq(User::getAccount,account);
            User user1 = userDao.selectOne(laq);
            int id = user1.getId();
            user.setId(id);
            userDao.updateById(user);
            //将文件放到D盘的image目录下
            File file = new File("D:/image/" + names);
            try {
                userphoto.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //img为映射路径，浏览器为了安全无法直接访问本地文件
            return new Result(Code.SAVE_OK,"图片上传成功",names);
        }else {
            return new Result(Code.SAVE_ERR,"请上传图片格式文件");
        }

    }

    @PutMapping("/update")
    public Result update(@RequestBody User user){
        String account = user.getAccount();
        String username = user.getUsername();
        String password = user.getPassword();
        String sex = user.getSex();
        String birthday = user.getBirthday();
        String status = user.getStatus();
        LambdaQueryWrapper<User> laq = new LambdaQueryWrapper<>();
        laq.eq(User::getAccount,account);
        User user1 = userDao.selectOne(laq);
        int id = user1.getId();
        User user2 = new User();
        user2.setId(id);
        if (user1.getUsername().equals(username)){
        }else {
            user2.setUsername(username);
        }
        if (password == null || password == ""){
        }else {
            user2.setPassword(password);
        }
        if (sex == null){
        }else {
            user2.setSex(sex);
        }
        if (birthday == null){
        }else {
            user2.setBirthday(birthday);
        }
        if ("true".equals(status)){
            status = "1";
        }else {
            status = "0";
        }
        user2.setStatus(status);
        userDao.updateById(user2);
        return new Result(Code.UPDATE_OK,"修改完成");
    }

}

