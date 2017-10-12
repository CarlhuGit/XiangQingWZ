package com.atguigu.service;

import com.atguigu.demo.pojo.User;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
    int getUserNameCount(String userName);

    void saveUser(User user) throws IOException, SolrServerException;

    User getRegistUser(User userForm);

    void updateUser(User user) throws IOException, SolrServerException;

    User getUserById(Integer userId);

    List<Map<String,String>> getDateFromSolrIndex(String keywords) throws SolrServerException;
}
