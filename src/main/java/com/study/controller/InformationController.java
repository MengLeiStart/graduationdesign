package com.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.dao.InformationDao;
import com.study.dao.UserDao;
import com.study.domain.Information;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/informations")
public class InformationController {
    @Autowired
    InformationDao informationDao;

    @PostMapping("/upload")
    public Result upload(@RequestParam("photo") MultipartFile photo, @Param("description")String description,
                         @Param("type") String type,@Param("account")String account,@Param("username")String username){
        if (photo != null) {
            //获取文件名
            String filename = photo.getOriginalFilename();
            //获取文件后缀名
            String lastname = filename.substring(filename.lastIndexOf("."));
            if (".jpg".equals(lastname) || ".png".equals(lastname) || ".jpeg".equals(lastname)) {
                //随机生成名字，给上传文件重命名，防止重名
                String uuid = UUID.randomUUID().toString();
                filename = uuid + lastname;
                Information inf = new Information();
                inf.setPhoto(filename);
                inf.setDescription(description);
                inf.setType(type);
                inf.setName(username);
                inf.setUseraccount(account);
                informationDao.insert(inf);
                //将文件放到D盘的image目录下
                File file = new File("D:/image/" + filename);
                try {
                    photo.transferTo(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Result(Code.SAVE_OK, "上传成功");
            } else {
                return new Result(Code.SAVE_ERR, "请勿上传非图片文件");
            }
        }else{
            Information inf = new Information();
            inf.setDescription(description);
            inf.setType(type);
            inf.setUseraccount(account);
            inf.setName(username);
            informationDao.insert(inf);
            return new Result(Code.SAVE_OK,"上传成功");
        }
    }
    @GetMapping("/getAll")
    public Result getAll(){
        List<Information> informationList = informationDao.selectList(null);
        System.out.println(informationList);
        return new Result(Code.GET_OK,"查询成功",informationList);
    }
    @GetMapping("/getByType/{key}")
    public Result getByType(@PathVariable("key")String key){
        List<Information> information;
        if ("综合".equals(key)){
            information = informationDao.selectList(null);
        }else{
            LambdaQueryWrapper<Information> laq = new LambdaQueryWrapper<>();
            laq.eq(Information::getType,key);
            information = informationDao.selectList(laq);
        }
        if (information.size() == 0){
            return new Result(Code.GET_ERR,"没有相关内容");
        }else {
            return new Result(Code.GET_OK,"查询完成",information);
        }

    }
}
