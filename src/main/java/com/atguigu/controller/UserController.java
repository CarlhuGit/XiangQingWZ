package com.atguigu.controller;


import com.atguigu.demo.pojo.User;
import com.atguigu.service.UserService;
import org.apache.solr.client.solrj.SolrServerException;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/demo/user")
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping("/login")
    public String login(User userForm, HttpSession session,Map<String,Object>map){
        //1.在数据库中查询用户是否存在
        User userDB = userService.getRegistUser(userForm);

        //2.如果从数据库中查询到的用户不为空，将数据库中的user有效数据保存进session域对象
        if (userDB!=null){
            session.setAttribute("loginUser",userDB);
        }else{
            map.put("msg", "登录失败，请重新尝试！");
            return "user_login";
        }

        return "redirect:/index.jsp";

    }

    @RequestMapping("/regist")
    public String regist(User user, Map<String,Object>map) throws IOException, SolrServerException {
        //1.先判断用户名是否被占用
        String userName = user.getUserName();
        int count = userService.getUserNameCount(userName);
        if (count==0){
            //2.用户名未被注册，用户名可用，并保存
            userService.saveUser(user);
        }else{
            //3.用户名已经被占用，提示用户，并回到注册页面
            map.put("msg","用户名已存在，请重新注册！");
            return "user_regist";
        }
        return "user_login";
    }


    @RequestMapping("/logout")
    public String loginOut(HttpSession session){
        //删除session域中存储的数据，相当于退出登录
        session.invalidate();

        return "redirect:/index.jsp";
    }

    /*用户个人信息更新操作*/
    @RequestMapping("/update")
    public String update(User user, HttpSession session, @RequestParam("headPicture")MultipartFile headPicture) throws IOException, MyException, SolrServerException {

        //1.如果上传图像不为空
        if (!headPicture.isEmpty()){
            ////2.调用FastDFS远程服务执行文件上传
            //①获取tracker_server.conf配置文件的绝对物理路径
            String path = this.getClass().getResource("/tracker_server.conf").getPath();

            //②全局初始化
            ClientGlobal.init(path);

            //③创建4个对象
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();

            StorageServer storageServer = null;
            StorageClient storageClient = new StorageClient(trackerServer,storageServer);

            //④[1]执行文件上传，并获取执行文件的字节数组
            byte[] bytes = headPicture.getBytes();

            //[2]获取执行文件的扩展名(file_ext_name)
            String filename = headPicture.getOriginalFilename();
            int lastIndexOf = filename.lastIndexOf(".") + 1;
            String file_ext_name = filename.substring(lastIndexOf);

            //[3]文件的元数据（文件的描述信息）
            NameValuePair[] meta_list = null;

            //[4]执行文件上传，上传成功后文件的组名和文件名会在返回值中
            String[] uploadFile = storageClient.upload_file(bytes, file_ext_name, meta_list);

            //[5]获取文件的组名
            String userPicGroup = uploadFile[0];

            //[⑥]获取文件名
            String userPicFilename = uploadFile[1];

            //[7]为了避免缓存数据库空间溢出，在重新提交数据后要删除之前提交的文件
            String oldGrop = user.getUserPicGroup();
            String oldFileName = user.getUserPicFilename();
            //在执行删除操作时fastDFS不会关心文件是否存在，都会执行删除操作，而数据为空时，执行删除操作会报错，删除前先做判断
            if (oldGrop!=null && !"".equals(oldGrop)){
                //fastDFS删除文件的方法storageClient.delete_file
                storageClient.delete_file(oldGrop,oldFileName);
            }

            //[8]把组名和文件名存放进user对象中。
            user.setUserPicGroup(userPicGroup);
            user.setUserPicFilename(userPicFilename);
        }


        //3.更新用户的操作。
        try {
            userService.updateUser(user);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }

        //※修复bug
        //bug现象：更新用户信息后必须重新登录才能够生效
        //bug原因：页面上从Session域中读取数据，而不是每一次都从数据库中读取
        //bug解决办法：更新数据库之后，再更新Session域
        //使用从表单提交的user对象更新Session域的隐患：有可能会和数据库不一致
        user = userService.getUserById(user.getUserId());
        session.setAttribute("loginUser", user);

        return "redirect:/index.jsp";
    }

    @RequestMapping("/search")
    public String search(@RequestParam("keywords") String keywords, Model model) throws SolrServerException {
        List<Map<String,String>> searchResultList = userService.getDateFromSolrIndex(keywords);
        model.addAttribute("searchResultList",searchResultList);
        return "search_result";
    }


    @RequestMapping("/search_detail/{userId}")
    public String searchDetail(@PathVariable("userId") Integer userId, Map<String,Object>map){
        User user = userService.getUserById(userId);
        map.put("user",user);
        return "search_detail";
    }
}
