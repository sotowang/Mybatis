package com.soto.test;

import com.soto.pojo.Category;
import com.soto.pojo.Order;
import com.soto.pojo.OrderItem;
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
//        List<Product> ps = session.selectList("listProduct");
//        for (Product p : ps) {
//            System.out.println(p+" 对应的分类是 \t "+ p.getCategory());
//        }

        //建立关系
//        addOrderItemItem(session);


        //删除关系
//        deleteOrderItem(session);

        //多对多
//        listOrder(session);


        //if语句
//        System.out.println("查询所有的");
//        List<Product> ps = session.selectList("listProduct");
//        for (Product p : ps) {
//            System.out.println(p);
//        }
//
//        System.out.println("模糊查询");
//        Map<String,Object> params = new HashMap<>();
//        params.put("name","a");
//        List<Product> ps2 = session.selectList("listProduct",params);
//        for (Product p : ps2) {
//            System.out.println(p);
//        }

        //where语句
        System.out.println("多条件查询");
        Map<String,Object> params = new HashMap<>();
//        params.put("name","a");
        params.put("price","10");
        List<Product> ps2 = session.selectList("listProduct",params);
        for (Product p : ps2) {
            System.out.println(p);
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


    private static void listOrder(SqlSession session) {
        List<Order> os = session.selectList("listOrder");
        for (Order o : os) {
            System.out.println(o.getCode());
            List<OrderItem> ois= o.getOrderItems();
            for (OrderItem oi : ois) {
                System.out.format("\t%s\t%f\t%d%n", oi.getProduct().getName(),oi.getProduct().getPrice(),oi.getNumber());
            }
        }
    }

    private static void addOrderItem(SqlSession session) {
        Order o1 = session.selectOne("getOrder", 1);
        Product p6 = session.selectOne("getProduct", 6);
        OrderItem oi = new OrderItem();
        oi.setProduct(p6);
        oi.setOrder(o1);
        oi.setNumber(200);

        session.insert("addOrderItem", oi);
    }

    private static void deleteOrderItem(SqlSession session) {
        Order o1 = session.selectOne("getOrder",1);
        Product p6 = session.selectOne("getProduct",6);
        OrderItem oi = new OrderItem();
        oi.setProduct(p6);
        oi.setOrder(o1);
        session.delete("deleteOrderItem", oi);
    }

}
