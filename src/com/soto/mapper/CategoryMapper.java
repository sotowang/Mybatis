package com.soto.mapper;

import com.soto.CategoryDynaSqlProvider;
import com.soto.pojo.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CategoryMapper {
    @InsertProvider(type=CategoryDynaSqlProvider.class,method="add")
    public int add(Category category);

    @DeleteProvider(type=CategoryDynaSqlProvider.class,method="delete")
    public void delete(int id);

    @SelectProvider(type=CategoryDynaSqlProvider.class,method="get")
    public Category get(int id);

    @UpdateProvider(type=CategoryDynaSqlProvider.class,method="update")
    public int update(Category category);

//    @SelectProvider(type=CategoryDynaSqlProvider.class,method="list")
//    public List<Category> list();
    //分页
    @Select(" select * from category_ ")
    @Results({@Result(property = "products", javaType = List.class, column = "id",
            many = @Many(select = "com.how2java.mapper.ProductMapper.listByCategory"))})
    public List<Category> list();

    @Select(" select * from category_ limit #{start},#{count}")
    public List<Category> listByPage(@Param("start") int start, @Param("count")int count);
}
