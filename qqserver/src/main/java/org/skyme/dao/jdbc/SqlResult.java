package org.skyme.dao.jdbc;

import java.util.List;

/**
 * @author:Skyme
 * @create: 2023-08-15 22:22
 * @Description:
 */
public class SqlResult {
    private String sql;
    private List<Object> params;

    private List<List> batchParams;

    public SqlResult() {
    }

    public SqlResult(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public List<List> getBatchParams() {
        return batchParams;
    }

    public void setBatchParams(List<List> batchParams) {
        this.batchParams = batchParams;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
