package com.micropos.products.model;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ImageTypeHandler implements TypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        String imageUrls = rs.getString(columnName);
        return imageUrls.split(",")[0];
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        String imageUrls = rs.getString(columnIndex);
        return imageUrls.split(",")[0];
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String imageUrls = cs.getString(columnIndex);
        return imageUrls.split(",")[0];
    }
}
