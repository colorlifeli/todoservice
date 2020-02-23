package org.me.todoservice.utils.mybatis;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

@Intercepts({
    @Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class})})
public class PagePlugin implements Interceptor {

    private String dialect = "gauss"; // 数据库方言

    private String pageSqlId = ".*ByPage"; // mapper.xml中需要拦截的ID(正则匹配)

    public Object intercept(Invocation invocation) throws Exception{
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) getValueByFieldName(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) getValueByFieldName(delegate, "mappedStatement");

            if (null != mappedStatement && mappedStatement.getId().matches(pageSqlId)) { // 拦截需要分页的SQL
                BoundSql boundSql = statementHandler.getBoundSql();
                Object parameterObject = boundSql
                    .getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
                if (parameterObject == null) {
                    throw new NullPointerException("parameterObject尚未实例化！");
                } else {
                    Page page = null;
                    if (parameterObject instanceof Page) { // 参数就是Page实体
                        page = (Page) parameterObject;
                    } else if (parameterObject instanceof Map<?, ?>) {// 使用map传参
                        page = (Page) ((Map<String, Object>) parameterObject).get("page");
                    } else {
                        throw new NullPointerException("参数中没有找到page对象！");
                    }
                    String sql = boundSql.getSql();
                    if (!page.isNotCount()) {
                        Connection connection = (Connection) invocation.getArgs()[0];
                        String countSql = "select count(0) from (" + sql + ") tmp_count"; // 记录统计
                        // 如果是oracle数据库去除关键字as
                        if ("oracle".equals(dialect)) {
                            countSql = "select count(0) from (" + sql + ") tmp_count";
                        }
                        PreparedStatement countStmt = connection.prepareStatement(countSql);
                        BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
                            boundSql.getParameterMappings(), parameterObject);
                        //解决 foreach 问题
                        if (getValueByFieldName(boundSql, "metaParameters") != null) {
                            MetaObject mo = (MetaObject) getValueByFieldName(boundSql, "metaParameters");
                            setValueByFieldName(countBS, "metaParameters", mo);
                        }
                        if (getValueByFieldName(boundSql, "additionalParameters") != null) {
                            Map ap = (Map) getValueByFieldName(boundSql, "additionalParameters");
                            setValueByFieldName(countBS, "additionalParameters", ap);
                        }
                        //解决 foreach问题 end
                        setParameters(countStmt, mappedStatement, countBS, parameterObject);
                        int count = 0;
                        try (ResultSet rs = countStmt.executeQuery()) {
                            if (rs.next()) {
                                count = rs.getInt(1);
                            }
                        }
                        countStmt.close();
                        page.setTotalCount((long) count);
                    }
                    String pageSql = generatePageSql(sql, page);
                    setValueByFieldName(boundSql, "sql", pageSql); // 将分页sql语句反射回BoundSql.
                }
            }
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties arg0) {
        String value = arg0.getProperty("dialect");
        if (value != null && !"".equals(value)) {
            this.dialect = value;
        }
    }

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     *
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
        Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
                        && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value)
                                .getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
                            + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    private static void setValueByFieldName(Object obj, String fieldName, Object value)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        if (field.isAccessible()) {
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        }
    }

    /**
     * 根据数据库方言，生成特定的分页sql
     *
     * @param sql
     * @param page
     * @return
     */
    private String generatePageSql(String sql, Page page) {
        if (page != null && dialect != null && !"".equals(dialect)) {
            StringBuilder pageSql = new StringBuilder();
            if ("gauss".equals(dialect)) {
                pageSql.append(sql);
                pageSql.append(" limit " + page.getStart() + "," + page.getPageSize());
            } else if ("gauss300".equals(dialect) || "tbase".equals(dialect) || "h2".equals(dialect)) {
                pageSql.append(sql);
                pageSql.append(" limit " + page.getPageSize() + " offset " + page.getStart());
            } else if ("oracle".equals(dialect)) {
                pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
                pageSql.append(sql);
                pageSql.append(") tmp_tb where ROWNUM<=");
                pageSql.append(page.getStart() + page.getPageSize());
                pageSql.append(") where row_id>");
                pageSql.append(page.getStart());
            }
            return pageSql.toString();
        } else {
            return sql;
        }
    }

    private static Object getValueByFieldName(Object obj, String fieldName)
        throws SecurityException, NoSuchFieldException,
        IllegalArgumentException, IllegalAccessException {
        Field field = getFieldByFieldName(obj, fieldName);
        Object value = null;
        if (field != null) {
            if (field.isAccessible()) {
                value = field.get(obj);
            } else {
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        }
        return value;
    }


    private static Field getFieldByFieldName(Object obj, String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
            .getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                //Do nothing
            }
        }
        return null;
    }
}
