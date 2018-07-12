1. 应用程序找Mybatis要数据
2. mybatis从数据库中找来数据

    2.1 通过mybatis-config.xml 定位哪个数据库

    2.2 通过Category.xml执行对应的select语句

    2.3 基于Category.xml把返回的数据库记录封装在Category对象中

    2.4 把多个Category对象装在一个Category集合中

3. 返回一个Category集合