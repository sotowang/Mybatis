package com.soto.test;

import com.soto.study.model.Country;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class CountryMapperTest extends BaseMapperTest {


    @Test
    public void testSelectAll() {
        SqlSession sqlSession = getSqlSession();
        try {
            List<Country> countryList = sqlSession.selectList("selectAll");
            for (Country country :
                    countryList) {

                System.out.println(country);
            }
        }finally {
            sqlSession.close();
        }
    }
}
