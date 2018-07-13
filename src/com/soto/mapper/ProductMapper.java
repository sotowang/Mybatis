package com.soto.mapper;

import com.soto.pojo.Product;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMapper {
    @Select(" select * from product_ ")
    @Results({
            @Result(property="category",column="cid",one=@One(select="com.soto.mapper.CategoryMapper.get"))
    })
    public List<Product> list();

    @Select("select * from product_ where id = #{id}")
    public Product get(int id);
}
