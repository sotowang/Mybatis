package com.soto.generator.dao;

import com.soto.generator.model.SysPrivilege;
import java.util.List;

public interface SysPrivilegeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_privilege
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_privilege
     *
     * @mbg.generated
     */
    int insert(SysPrivilege record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_privilege
     *
     * @mbg.generated
     */
    SysPrivilege selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_privilege
     *
     * @mbg.generated
     */
    List<SysPrivilege> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_privilege
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(SysPrivilege record);
}