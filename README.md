# [Mybatis工作原理?](https://www.zhihu.com/question/25007334/answer/266187562)

Mybatis原名Ibatis，在2011年从Ibatis2.x升级到Mybatis 3.X，并将项目地址从Apache迁移到了Google code，事实上我们看MyBatis的类全路径名，还是保留了Apache和Ibatis的的包前缀

```java
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
```

不过MyBatis的配置文件以及操作类和实现方式都有了很大变化，这里我们重点讲述的是Mybatis，不是Ibatis；

* Mybatis的配置文件一共由两类：

一类用于指定数据源、事务属性以及其他一些参数配置信息（通常是一个独立的文件，可以称之为**全局配置文件**）；

另一类则用于 指定**数据库表和程序之间的映射信息（可能不止一个文件，我们称之为映射文件**）

这些文件的名字并没有确定的要求；只是要遵从特定的dtd的xml文件约束，即xml标签需要符合要求；

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC" />
            <!-- 配置数据库连接信息 -->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver" />
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis" />
                <property name="username" value="root" />
                <property name="password" value="root" />
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <!-- 注册userMapper.xml文件， 
        userMapper.xml位于com.test.mapping这个包下，所以resource写成com/test/mapping/userMapper.xml-->
        <mapper resource="com/test/mapping/userMapper.xml"/>
    </mappers>

</configuration>
```

**上述就是MyBatis的数据源，事务属性，以及映射文件的索引；**  

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 为这个mapper指定一个唯一的namespace，namespace的值习惯上设置成包名+sql映射文件名，这样就能够保证namespace的值是唯一的
例如namespace="com.test.mapping.userMapper"就是com.test.mapping(包名)+userMapper(userMapper.xml文件去除后缀)
 -->
<mapper namespace="com.test.mapping.userMapper">
    <!-- 
        根据id查询得到一个user对象
     -->
    <select id="getUser" parameterType="int" 
        resultType="com.test.domain.User">
        select * from users where id=#{id}
    </select>
</mapper>
```

**上面是数据库表与程序之间的映射文件，定义了一个根据id来获取User对象的sql**

 ```java
作者：wuxinliulei
链接：https://www.zhihu.com/question/25007334/answer/266187562
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

package com.test.domain;

/**
 * users表所对应的实体类
 */
public class User {

    //实体类的属性和表的字段名称一一对应
    private int id;
    private String name;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", age=" + age + "]";
    }
}
 ```

问题：

## mybatis怎么在程序中找到sqlmapper

```java
// mybatis的配置文件
String resource = "conf.xml";
// 使用类加载器加载mybatis的配置文件（它也加载关联的映射文件）
InputStream is = Test1.class.getClassLoader().getResourceAsStream(resource);
// 构建sqlSession的工厂
SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(is);
```

**题主问的sqlmapper可以理解为两种组件，一种是mapping映射文件，通过id名来获取相应的sql语句，操作数据库；一种是sql的返回对象，**

```xml
resultType="com.test.domain.User"
```

这个就是返回的sql结果映射成为具体的POJO(Plain Ordinary Java Object)对象;

两个重要的类即：

org.apache.ibatis.session.SqlSessionFactory;

org.apache.ibatis.session.SqlSession;

```java
作者：wuxinliulei
链接：https://www.zhihu.com/question/25007334/answer/266187562
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

package org.apache.ibatis.session;

import java.sql.Connection;

public interface SqlSessionFactory {

  SqlSession openSession();

  SqlSession openSession(boolean autoCommit);
  SqlSession openSession(Connection connection);
  SqlSession openSession(TransactionIsolationLevel level);

  SqlSession openSession(ExecutorType execType);
  SqlSession openSession(ExecutorType execType, boolean autoCommit);
  SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);
  SqlSession openSession(ExecutorType execType, Connection connection);

  Configuration getConfiguration();

}
```

 在构建SqlSessionFactory类的时候，将会对数据源及事务配置进行解析，具体在

org.apache.ibatis.builder.xml.XMLConfigBuilder类

org.apache.ibatis.builder.BaseBuilder类

XMLConfigBuilder类是解析产生org.apache.ibatis.Session.Configuration类的的具体类，Configuration类中将保存中所有的配置；

[mybatis的源代码解析(1)--xml文件解析 - 王久勇 - 博客园](https://link.zhihu.com/?target=https%3A//www.cnblogs.com/wangjiuyong/articles/6720501.html)

这篇博客介绍了一些xml文件解析的基本；

具体mybatis的xml解析使用到了XPath方式，具体解析过程参看

<https://zhuanlan.zhihu.com/p/31418285>

其实一般各种轮子都会有一个解析XML后信息的专用存储类，比如Config.Java,xxxConf.java,都是在启动组件时解析XML配置以用作程序中使用的。

引用网络上的一段源代码

 ```java
作者：wuxinliulei
链接：https://www.zhihu.com/question/25007334/answer/266187562
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public class Test1 {

    public static void main(String[] args) throws IOException {
        //mybatis的配置文件
        String resource = "conf.xml";
        //使用类加载器加载mybatis的配置文件（它也加载关联的映射文件）
        InputStream is = Test1.class.getClassLoader().getResourceAsStream(resource);
        //构建sqlSession的工厂
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(is);
        //使用MyBatis提供的Resources类加载mybatis的配置文件（它也加载关联的映射文件）
        //Reader reader = Resources.getResourceAsReader(resource); 
        //构建sqlSession的工厂
        //SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        //创建能执行映射文件中sql的sqlSession
        SqlSession session = sessionFactory.openSession();
        /**
         * 映射sql的标识字符串，
         * me.gacl.mapping.userMapper是userMapper.xml文件中mapper标签的namespace属性的值，
         * getUser是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
         */
        String statement = "me.gacl.mapping.userMapper.getUser";//映射sql的标识字符串
        //执行查询返回一个唯一user对象的sql
        User user = session.selectOne(statement, 1);
        System.out.println(user);
    }
}
 ```



 

 

 

 

 

 

 

  

 

 

 

 

 

 















