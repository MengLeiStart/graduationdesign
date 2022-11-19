package com.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.dao.CommentDao;
import com.study.dao.InformationDao;
import com.study.dao.UserDao;
import com.study.domain.Comment;
import com.study.domain.Information;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/informations")
public class InformationController {
    @Autowired
    InformationDao informationDao;

    @Autowired
    CommentDao commentDao;

    @PostMapping("/upload")
    public Result upload(@RequestParam("photo") MultipartFile photo, @Param("description")String description,
                         @Param("type") String type,@Param("account")String account,@Param("username")String username){
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
                File file = new File("D:/informationimage/" + filename);
                try {
                    photo.transferTo(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Result(Code.SAVE_OK, "上传成功");
            } else {
                return new Result(Code.SAVE_ERR, "请勿上传非图片文件");
            }

    }
    @PostMapping("/uploads")
    public Result uploads(@Param("description")String description,
                          @Param("type") String type,@Param("account")String account,@Param("username")String username){
        Information inf = new Information();
        inf.setDescription(description);
        inf.setType(type);
        inf.setUseraccount(account);
        inf.setName(username);
        informationDao.insert(inf);
        return new Result(Code.SAVE_OK,"上传成功");
    }
    //查询所有的信息
    @GetMapping("/getAll")
    public Result getAll(){
        List<Information> informationList = informationDao.selectList(null);
        return new Result(Code.GET_OK,"查询成功",informationList);
    }
    //根据类别来查询对应的信息
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
    //添加评论的功能
    @PostMapping("/addComment")
    public Result addComment(@Param("comment") String comment,@Param("id")int id){
        Comment comment1 = new Comment();
        comment1.setComment(comment);
        comment1.setOtherId(id);
        commentDao.insert(comment1);
        return new Result(Code.SAVE_OK,"评论成功");
    }
    //查询评论功能
    @GetMapping("/getComment/{id}")
    public Result getComment(@PathVariable("id")Integer id){
        LambdaQueryWrapper<Comment> laq = new LambdaQueryWrapper<>();
        laq.eq(Comment::getOtherId,id);
        List<Comment> comments = commentDao.selectList(laq);
        return new Result(Code.GET_OK,"查询完成",comments);
    }
    //根据用户输入，查询对应信息
    @GetMapping("/getInformationByLimit/{select}/{input3}")
    public Result getInformationByLimit(@PathVariable("select") String select,@PathVariable("input3") String input3){
        LambdaQueryWrapper<Information> laq = new LambdaQueryWrapper<>();
        if ("2".equals(select)){
            laq.like(Information::getDescription,input3);
            List<Information> information = informationDao.selectList(laq);
            if (information.size() > 0){
                return new Result(Code.GET_OK,"查询到数据",information);
            }else {
                return new Result(Code.GET_ERR,"未查询到数据");
            }
        }else if ("1".equals(select)){
            laq.eq(Information::getName,input3);
            List<Information> information = informationDao.selectList(laq);
            if (information.size() > 0){
                return new Result(Code.GET_OK,"查询到数据",information);
            }else {
                return new Result(Code.GET_ERR,"未查询到数据");
            }
        }else {
            return null;
        }
    }

}
