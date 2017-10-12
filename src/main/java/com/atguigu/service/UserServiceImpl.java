package com.atguigu.service;

import com.atguigu.demo.mapper.UserMapper;
import com.atguigu.demo.pojo.User;
import com.github.abel533.entity.Example;
import com.github.abel533.entity.Example.Criteria;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SolrServer solrServer;

    @Override
    public int getUserNameCount(String userName) {
        Example example = new Example(User.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userName",userName);
        return userMapper.selectCountByExample(example);
    }

    @Override
    public void saveUser(User user) throws IOException, SolrServerException {
        //1.将用户注册信息保存进数据库中
        userMapper.insertSelective(user);

        //2.将用户信息保存进索引库中
        //[1]创建文档对象
        SolrInputDocument document = new SolrInputDocument();

        //[2]添加必要字段
        document.setField("id",user.getUserId());
        document.setField("user_nick",user.getUserNick());

        //[3]使用solrserver对象执行文档的添加操作
        solrServer.add(document);
        solrServer.commit();
    }

    @Override
    public User getRegistUser(User userForm) {
        String userName = userForm.getUserName();
        String userPwd = userForm.getUserPwd();

        Example example = new Example(User.class);
        Criteria criteria = example.createCriteria();
        //判断用户名与密码是否匹配
        criteria.andEqualTo("userName",userName).andEqualTo("userPwd",userPwd);
        List<User> users = userMapper.selectByExample(example);

        if(users.size()==0||users==null){
            return null;
        }
        //返回查询到的数据
        User user = users.get(0);
        return user;
    }

    @Override
    public void updateUser(User user) throws IOException, SolrServerException {

        userMapper.updateByPrimaryKeySelective(user);

        //获取当前用户的id
        Integer id = user.getUserId();

        //更新用户信息之前，先删除旧的信息根据user的id作为文档的id执行删除
        solrServer.deleteById(id + "");//由于solr中文档id有时为integer类型，有时为long类型，这里统一将其转换成String类型

        //更新数据时也要讲更新的数据放进索引库中
        //1.创建文档对象
        SolrInputDocument document = new SolrInputDocument();

        //2.添加必要字段
        document.setField("id",id);
        document.addField("user_nick", user.getUserNick());

        //适配性别的数字值
        int userGender = user.getUserGender();
        String userGenderString = (userGender==0)?"男" : "女";

        document.addField("user_gender", userGenderString);
        document.addField("user_job", user.getUserJob());
        document.addField("user_hometown", user.getUserHometown());
        document.addField("user_desc", user.getUserDesc());
        document.addField("user_pic_group", user.getUserPicGroup());
        document.addField("user_pic_filename", user.getUserPicFilename());

        //3.执行文档添加操作
        solrServer.add(document);
        solrServer.commit();
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public List<Map<String, String>> getDateFromSolrIndex(String keywords) throws SolrServerException {

        //1.创建solrQuery对象
        SolrQuery query = new SolrQuery();
        //2.设置查询关键字
        query.setQuery(keywords);

        //3.设置要查询的默认字段
        query.set("df","user_keywords");

        //4.设置高亮显示
        query.setHighlight(true);

        //5.添加使用高亮显示的字段
        query.addHighlightField("user_nick");
        query.addHighlightField("user_gender");
        query.addHighlightField("user_job");
        query.addHighlightField("user_hometown");

        //6.设置高亮显示的html标签
        query.setHighlightSimplePre("<span style='color:red'>");
        query.setHighlightSimplePost("</span>");

        //7.执行查询
        QueryResponse response = solrServer.query(query);

        //8.获取查询的文档结果 list集合
        SolrDocumentList results = response.getResults();


        //9.获取附加了高亮显示的标签的结果
        //Map<文档id, Map<高亮显示的字段名, [添加了高亮标签的查询结果]>>
        Map<String, Map<String, List<String>>> highlightingMap = response.getHighlighting();

        //10.申明一个集合，保存查询结果和高亮结果解析得到的数据
        //List<Map<字段名,字段的原始值/添加了高亮标签的字段值>>
        List<Map<String,String>> finalResult = new ArrayList<>();


        //11.解析查询结果和高亮结果
        for(SolrDocument solrDocument : results){
            //每一个Document对应一条查询结果记录，每一条记录封装到一个finalMap中
            Map<String,String> finalMap = new HashMap<>();

            //获取当前对象的id值
            String id = solrDocument.getFieldValue("id") + "";

            //遍历solrDocument中的所有字段
            Collection<String> fieldNames = solrDocument.getFieldNames();
            for (String fielName :fieldNames){
                //排除默认的—_version_字段
                if("_version_".equals(fielName)){
                    continue;//结束这个循环，执行这个循环之后的代码
                }

                //获取某个字段值
                String fieldValue = solrDocument.getFieldValue(fielName) + "";
                //当前字段的名和值存入finalMap中
                finalMap.put(fielName, fieldValue);

                //根据当前文档的id尝试从高亮的Map中获取对应的数据
                //Map<高亮显示的字段名, [添加了高亮标签的查询结果]>
                Map<String, List<String>> highlightRecordMap = highlightingMap.get(id);

                List<String> highlightRecordList = highlightRecordMap.get(fielName);

                //判断highlightRecordList是否有效
                if (highlightRecordList!=null && highlightRecordList.size()>0){
                    //如果有效就获取添加了高亮标签的字段值
                    String highlightRecord = highlightRecordList.get(0);

                    //以字段名为键以“添加了高亮标签的字段值”为值存入finalMap，覆盖原始值
                    finalMap.put(fielName,highlightRecord);
                }
            }

            //把finalMap存入最终结果的List
            finalResult.add(finalMap);

        }
        return finalResult;

    }


}
