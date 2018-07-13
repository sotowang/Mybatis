package com.soto.mapper;

import com.soto.pojo.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CategoryMapper {
    @Insert(" insert into category_ ( name ) values (#{name}) ")
    public int add(Category category);

    @Delete(" delete from category_ where id= #{id} ")
    public void delete(int id);

    @Select("select * from category_ where id= #{id} ")
    public Category get(int id);

    @Update("update category_ set name=#{name} where id=#{id} ")
    public int update(Category category);


    @Select(" select * from category_ ")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "products", javaType = List.class, column = "id", many = @Many(select = "com.soto.mapper.ProductMapper.listByCategory") )
    })
    public List<Category> list();
}
