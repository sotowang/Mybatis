package com.soto.test;

import com.soto.pojo.Category;
import com.soto.pojo.Product;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMybatis {
    public static void main(String[] args) throws IOException {
//        根据配置文件mybatis-config.xml得到sqlSessionFactory 。
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

//        然后再根据sqlSessionFactory 得到session
        SqlSession session=sqlSessionFactory.openSession();
        //增
//        Category c = new Category();
//        c.setName("新增加的Category");
//        session.insert("addCategory",c);

        //删
//        Category c = new Category();
//        c.setId(2);
//        session.delete("deleteCategory",c);

        //查
//        Category c= session.selectOne("getCategory",3);
//        System.out.println(c.getName());

        //改
//        Category c= session.selectOne("getCategory",3);
//        c.setName("修改了的Category名称");
//        session.update("updateCategory",c);

        //查询所有
//        listAll(session);
        //模糊查询
//        List<Category> cs = session.selectList("listCategoryByName","cat");

        //多条件查询
//        Map<String,Object> params = new HashMap<>();
//        params.put("id", 3);
//        params.put("name", "cat");
//        List<Category> cs = session.selectList("listCategoryByIdAndName",params);
//        for (Category c : cs) {
//            System.out.println(c.getName());
//        }

        //一对多
//        List<Category> cs = session.selectList("listCategory");
//        for (Category c : cs) {
//            System.out.println(c);
//            List<Product> ps = c.getProducts();
//            for (Product p : ps) {
//                System.out.println("\t"+p);
//            }
//        }

        //多对一
        List<Product> ps = session.selectList("listProduct");
        for (Product p : ps) {
            System.out.println(p+" 对应的分类是 \t "+ p.getCategory());
        }





        session.commit();
        session.close();


    }
    private static void listAll(SqlSession session) {
        List<Category> cs = session.selectList("listCategory");
        for (Category c : cs) {
            System.out.println(c.getName());
        }
    }
}
