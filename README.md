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

 

**通过跟踪源代码可以看到SqlSession通过mapper映射的id来查找数据的方法；**

org.apache.ibatis.session.defaults.DefaultSqlSession类

```java
public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds){
     try{
	 	MappedStatement ms = configuration.getMappedStatement(statement);
	 	List<E> result = executor.<E> query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
	 	return result;
     }catch (Exception e){
		throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
     }
     finally{
		ErrorContext.instance().reset();  
     }
}
```

org.apache.ibatis.session.Configuration类

```java
public MappedStatement getMappedStatement(String id){
	return this.getMappedStatement(id, true);
}
```

```java
protected final Map<String, MappedStatement> mappedStatements = 
new StrictMap<MappedStatement>("Mapped Statements collection");
```

```java
public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements)
{
	if (validateIncompleteStatements)
	{
		buildAllStatements();
	}
	return mappedStatements.get(id);
}
```

**其实就是根据一个map映射，key就是定义mapping时候的id来拿到的；**

至此，

 上述org.apache.ibatis.session.defaults.DefaultSqlSession类对象中的 selectList方法中的executor对象，

在默认情况下，即没有设置settings的cache和executor属性时，默认使用的

org.apache.ibatis.executor.CachingExecutor类

```java
作者：wuxinliulei
链接：https://www.zhihu.com/question/25007334/answer/266187562
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public Executor newExecutor(Transaction transaction, ExecutorType executorType, boolean autoCommit)
{
	        executorType = executorType == null ? defaultExecutorType : executorType;
	        executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
		Executor executor;
		if (ExecutorType.BATCH == executorType)
		{
			executor = new BatchExecutor(this, transaction);
		}
		else if (ExecutorType.REUSE == executorType)
		{
			executor = new ReuseExecutor(this, transaction);
		}
		else
		{
			executor = new SimpleExecutor(this, transaction);
		}
		if (cacheEnabled)
		{
			executor = new CachingExecutor(executor, autoCommit);
		}
		executor = (Executor) interceptorChain.pluginAll(executor);
		return executor;
}
```

 所以调用到了

```java
public <E> List<E> query(MappedStatement ms, Object parameterObject, 
                        RowBounds rowBounds, ResultHandler resultHandler)
			throws SQLException
{
      BoundSql boundSql = ms.getBoundSql(parameterObject);
      CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
      return query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}
```

在真正查询时先查询cache，可以看到这个cache层级在MappedStatement上，也就是在单个Sql上；若查到，则直接返回，无则通过jdbc查询，且返回结果

```java
public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler,
		CacheKey key, BoundSql boundSql) throws SQLException
{
	Cache cache = ms.getCache();
	if (cache != null)
	{
		flushCacheIfRequired(ms);
		if (ms.isUseCache() && resultHandler == null)
		{
			ensureNoOutParams(ms, key, parameterObject, boundSql);
			if (!dirty)
			{
				cache.getReadWriteLock().readLock().lock();
				try
				{
					@SuppressWarnings("unchecked")
					List<E> cachedList = (List<E>) cache.getObject(key);
					if (cachedList != null)
						return cachedList;
				}
				finally
				{
					cache.getReadWriteLock().readLock().unlock();
				}
			}
			List<E> list = delegate.<E> query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
			tcm.putObject(cache, key, list); // issue #578. Query must be
												// not synchronized to
												// prevent deadlocks
			return list;
		}
	}
	return delegate.<E> query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}
```

上述的使用方式是未使用代理的方式，这样需要我们自行openSession并且关闭Session； 

```java
作者：wuxinliulei
链接：https://www.zhihu.com/question/25007334/answer/266187562
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

SqlSession session = null;
try
{
	session = sessionFactory.openSession();
	/**
	 * 映射sql的标识字符串， com.test.mapping.userMapper是userMapper.
	 * xml文件中mapper标签的namespace属性的值，
	 * getUser是select标签的id属性值，通过select标签的id属性值就可以找到要执行的SQL
	 */
	String statement = "com.test.mapping.userMapper.getUser";// 映射sql的标识字符串
	// 执行查询返回一个唯一user对象的sql
	User user = session.selectOne(statement, 1);
	System.out.println(user);
}
catch (Exception e)
{
	// TODO: handle exception
}
finally
{
	if (session != null)
	{
		session.close();
	}
}
```

 事实上如果我们使用SqlSessionManager来管理，那么开启和关闭Session操作都不用我们来处理了。

```java
final SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(sessionFactory);
String statement = "com.test.mapping.userMapper.getUser";// 映射sql的标识字符串
User user = sqlSessionManager.selectOne(statement, 1);
System.out.println(user);
```

下面是Interceptor类实现，开启和关闭操作都交由了

```java
作者：wuxinliulei
链接：https://www.zhihu.com/question/25007334/answer/266187562
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

private class SqlSessionInterceptor implements InvocationHandler
{
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		final SqlSession sqlSession = SqlSessionManager.this.localSqlSession.get();
		if (sqlSession != null)
		{
			try
			{
				return method.invoke(sqlSession, args);
			}
			catch (Throwable t)
			{
				throw ExceptionUtil.unwrapThrowable(t);
			}
		}
		else
		{
			final SqlSession autoSqlSession = openSession();
			try
			{
				final Object result = method.invoke(autoSqlSession, args);
				autoSqlSession.commit();
				return result;
			}
			catch (Throwable t)
			{
				autoSqlSession.rollback();
				throw ExceptionUtil.unwrapThrowable(t);
			}
			finally
			{
				autoSqlSession.close();
			}
		}
	}
}

```

 如果使用Mapper方式来操作SQL，就是利用动态代理，可以避免我们手写mapper的id字符串，将查找sql过程和执行sql过程放到了代理处理中，更优雅些，不过大体流程就是这些，改变了查找sql的步骤，通过Mapper的方法名来查找对应的sql的，

 具体可以参看：

[Java 动态代理作用是什么?](https://zhstatic.zhihu.com/assets/zhihu/editor/zhihu-card-default.svg)

 

---

# Mybatis从入门到精通

使用 XML 形式进行配置，首先在 src/main/resources 下面创建 mybatis-config.xml 配置文件，
然后输入如下内容。

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <settings>
        <setting name="logImpl" value="LOG4J"/>
    </settings>
    <typeAliases>
        <package name="com.soto.study.model"/>
    </typeAliases>

    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="UNPOOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>


    <mappers>
        <mapper resource="com/soto/study/mapper/CountryMapper.xml"/>
    </mappers>


</configuration>
```

简单讲解一下这个配置。

* <settings ＞ 中的 l ogimp l 属性配置指定使用 LOG4J 输出日志 。
* <typeAliases ＞元素下面配置了 一个包的别名，通常确定一个类的时候需要使用类的全限定名称 ，例如 tk .mybatis .simple.model.Country。在 MyBatis 中需要频繁用到类的全限定名称，为了方便使用，我们配置了 tk .mybatis. simple .model 包，这样配置后，在使用类的时候不需要写包名的部分，只使用 Couηtry 即可。
* <environments ＞环境 配置中 主要 配置了数据库连接，数据库的 url 为jdbc:mysql://localhost:3306/mybatis ，使用的是本机 MySQL 中的 mybatis数据库，后面的 username 和 password 分别是数据库的用户名和密码（如果你的数据库用户名及密码和这里的不一样，请修改为自己数据库可用的用户名和密码〉 。
* <mappers ＞中配置了 一个包含完整类路径的 CountryMapper.xml ，这是一个 MyBatis 的SQL 语句和映射配置文件，这个 XML 文件会在后面的章节中介绍 。



在 src/main/resources 下面创 建 tk/mybatis/simple/mapper 目录，再在该目录下面创建
CountryMapper.xml 文件，添加如下内容 。

 ```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.soto.study.mapper.CountryMapper">
    <select id="selectAll" resultType="Country">
		select id,countryname,countrycode from country
	</select>
</mapper>
 ```

SQL 定义在 CountryMapper.xml 文件中，里面的配置作用如下。


* <mapper> : XML 的根元素 ， 属性 ηamespace 定义了当前 XML 的命名 空间。
*  <select ＞元素：我们所定义的一个 SELECT 查询。
* id 属性：定义了当前 SELECT 查询的唯一一个 id 。
* resultType：定义了当前查询的返回值类型，此处就是指实体类 Country，前面配置中提到的别名主要用于这里，如果没有设置别名 ，此处就需要写成 resu ltType=”tk.mybatis . simple . model . Country ” 。
* select id , ...： 查询 SQL 语句。

创建好实体和 Mapper.xml 后 ， 接下来要有针对性地配置 Log4j ，让 MyBatis 在执行数据库
操作的时候可以将执行的 SQL 和其他信息输出到控制台。

 

需要在 1.3.2 节中创建的
mybati s-config.xml 配置文件中的 mappers 元素中配置所有的 mapper ，部分配置代码如下 。

```xml
<mappers>
<mapper resource=” tk/mybatis/simple/mapper/CountryMapper.xml ” / >
<mapper resource=” tk/mybatis/simple/mapper/UserMapper . xml ” / >
<mapper resource=” tk/mybatis/simple/mapper/RoleMapper.xml ” />
<mapper resource=” tk/mybatis/simple/mapper/PrivilegeMapper . xml ” />
<mapper resource=” tk/mybatis/simple/mapper/UserRoleMapper . xml ” />
<mapper resource=” tk/mybatis/simple/mapper / RolePrivilegeMapper.xml ” />
</mappers>
```

 这种配置方式需要将所有映射文件一一列举出来，如果增加了新的映射文件，还需要注意
在此处进行配置，操作起来比较麻烦 。 因为此处所有的 XML 映射文件都有对应的 Mapper 接口，
所以还有一种更简单的配置方式，代码如下 。

```xml
<mappers>
<package name= ” tk.mybatis . simple . mapper ” />
</mappers>

```


这种配置方式会先查找 tk.mybatis.simple . mapper 包下所有的接口，循环对接口进行
如下操作。
    I.  判断接口对应的命名 空 间是否己经存在，如果存在就抛出异常，不存在就继续进行接下
来的操作。

2. 加载接口对应的却也映射文件 ， 将接口全限定名转换为路径 ， 例如 ， 将接口
  tk.mybat 工 S .s 工mple.mapper.UserMapper 转换为 tk/mybati s/simple/mapper/UserMapper.xml,
  12J,.xm l 为后缀搜索 XML 资源，如果找到就解析 XML 。
3. 处理接口中的注解方法。
  因为这里的接口和 XML 映射文件完全符合上面操作的第 2 点，因此直接配置包名就能自
  动扫描包下 的接口和 XML 映射文件，省去了很多麻烦 。 准备好这一切后就可 以开始学习具体
  的用法了 。

 

 

 

 

  

 

 

 

 

 

 

  

 

 

 

 

 

 















