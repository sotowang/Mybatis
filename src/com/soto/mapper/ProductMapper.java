package com.soto.mapper;

import com.soto.pojo.Product;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMapper {
    @Select(" select * from product_ where cid = #{cid}")
    public List<Product> listByCategory(int cid);
}
