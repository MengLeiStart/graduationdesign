package com.study.controller;
import com.study.util.CheckCodeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session) throws IOException {
        ServletOutputStream os = response.getOutputStream();
        String code = CheckCodeUtil.outputVerifyImage(100,50,os,4);
    }
}
