package org.skyme.dao.jdbc;

import org.skyme.annotation.Column;
import org.skyme.annotation.Id;
import org.skyme.annotation.ManyToOne;
import org.skyme.annotation.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * @author:Skyme
 * @create: 2023-08-15 20:05
 * @Description:
 */
public class SqlUtil {
    private static final String DRIVER ="com.mysql.cj.jdbc.Driver" ;
    private static final String URL ="jdbc:mysql://127.0.0.1:3306/qq?useSSL=false&rewriteBatchedStatements=true" ;
    private static final String USER = "root";
    private static final String PASSWORD ="w5103265";
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       return con;
    }

    //查询 select * from user where
    public static <T> List<T> select(Class<T> clazz,String sql,Object...params){
        if(clazz.isAnnotationPresent(Table.class)){
            Connection connection = getConnection();
            PreparedStatement ps=null;
            ResultSet resultSet=null;
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(params!=null&&params.length>0){
                for(int i=0;i< params.length;i++){
                    try {
                        if(sql.contains("like")){
                            ps.setObject(i+1,"%"+params[i]+ "%");
                        }else{
                            ps.setObject(i+1,params[i]);
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            try {

                resultSet = ps.executeQuery();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            List<T> objects = resloveResultSet(clazz, resultSet);

            close(resultSet,ps,connection);
            return  objects;
            //将查询结果返回
        }
        throw new RuntimeException(clazz.getName()+"不支出数据库查询");

    }
    private static <T> List<T> resloveResultSet(Class<T> aClass,ResultSet rs){
       //遍历结果集,拿到元素

       List<T> list=new ArrayList<>();
       try {
           while (rs.next()){
               T o =  aClass.newInstance();
               ResultSetMetaData metaData = rs.getMetaData();
               for(int i=1;i<=metaData.getColumnCount();i++){
                   String columnName = metaData.getColumnName(i);
                   setValue(o,columnName,rs);
               }
               list.add(o);
           }
           return list;
       } catch (SQLException e) {
           throw new RuntimeException(e);
       } catch (InstantiationException e) {
           throw new RuntimeException(e);
       } catch (IllegalAccessException e) {
           throw new RuntimeException(e);
       }
   }
   //将
    private static <T> void setValue(T o, String columnName, ResultSet rs) {
        Class<T> aClass = (Class<T>) o.getClass();
        //查找是否有当前字段
            Field field1 = getField(aClass, columnName);
            if(field1!=null){
                field1.setAccessible(true);
                Class<?> type = field1.getType();
                try{
                if(type == Integer.class || type == int.class){
                        field1.set(o,rs.getInt(columnName));

                } else if(type == Double.class || type == double.class){
                    field1.set(o,rs.getDouble(columnName));
                } else if(type == Long.class || type == long.class){
                    field1.set(o,rs.getLong(columnName));
                } else if(type == String.class){
                    field1.set(o,rs.getString(columnName));
                }
                else if(type==Date.class){
                    field1.set(o,rs.getDate(columnName));
                }} catch (IllegalAccessException e) {
            throw new RuntimeException(e);
           } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }else {
                //处理ManyToOne字段
                Field manyToOneField = getManyToField(aClass, columnName);
                //如果manyToOneField不是null,则我们进去修改他的子类的属性即可
                if (manyToOneField != null) {
                    manyToOneField.setAccessible(true);
                    Object obj = null;
                    try {
                        obj = manyToOneField.get(o);//拿到类对象
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if (obj == null) {//如果类对象是null
                        try {
                            obj = manyToOneField.getType().newInstance();//创建对象
                            manyToOneField.set(o, obj);//将o的这个属性设置obj
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    ManyToOne annotation = manyToOneField.getAnnotation(ManyToOne.class);
                    //如果主类的名称与字段名称相同,那么就进入这里面去处理
                    if (annotation.value().equals(columnName)) {
                        //获取ID的Field
                        Field primaryKey = getIdName(obj.getClass());//获取子类里面主键的值
                        primaryKey.setAccessible(true);
                        Class<?> type1 = primaryKey.getType();
                        //将Field赋值
                        if (type1 == Integer.class || type1 == int.class) {
                            try {
                                primaryKey.set(obj, rs.getInt(columnName));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (type1 == Double.class || type1 == double.class) {
                            try {
                                primaryKey.set(obj, rs.getDouble(columnName));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (type1 == Long.class || type1 == long.class) {
                            try {
                                primaryKey.set(obj, rs.getLong(columnName));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (type1 == String.class) {
                            try {
                                primaryKey.set(obj, rs.getString(columnName));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        setValue(obj, columnName, rs);
                    }


                }
            }

//        Field[] declaredFields = aClass.getDeclaredFields();
//
//        for (Field field : declaredFields) {
//            field.setAccessible(true);
//            String name = null;
//            if(field.isAnnotationPresent(Id.class)){
//                name = getIdName(field);
//
//            }else if (field.isAnnotationPresent(Column.class)){
//               name= getColumnName(field);
//            }
//            if(name.equals(columnName)){
//                Class<?> type = field.getType();
//                if(type==int.class||type==Integer.class){
//                    try {
//                        field.set(o,rs.getInt(columnName));
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//               else if(type==long.class||type==Long.class){
//                    try {
//                        field.set(o,rs.getLong(columnName));
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//               else if(type==String.class){
//                    try {
//                        field.set(o,rs.getString(columnName));
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//               else if(type==double.class||type==Double.class){
//                    try {
//                        field.set(o,rs.getDouble(columnName));
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//               else if(type== Date.class){
//                    try {
//                        field.set(o,rs.getDate(columnName));
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }
    }
    private static Field getIdName(Class clazz){
        if(clazz.isAnnotationPresent(Table.class)){
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(Id.class)){
                    return field;
                }
            }
        }
        return null;
    }

    private static <T> Field getManyToField(Class<T> aClass, String columnName) {
        Field nField = null;
        for (Field field : aClass.getDeclaredFields()) {
            if(field.isAnnotationPresent(ManyToOne.class)){
                ManyToOne annotation = field.getAnnotation(ManyToOne.class);
                if(annotation.value().equals(columnName)){
                    nField = field;
                }else if(field.getName().equals(columnName)){
                    nField=field;
                }
                Field field1 = getField(field.getType(), columnName);
                if (field1 != null){
                    return field;
                }
            }
        }
        return nField;


    }

    public static int insert(Object obj){
        SqlResult result = generateInsertSql(obj);
        int update1 = update(result.getSql(), result.getParams().toArray());
        return update1;

    }
    public static int delete(Object obj){
        SqlResult result=generateDeleteSql(obj);
        int update1 = update(result.getSql(), result.getParams().toArray());
        return update1;
    }

    public static int update(Object obj){
        SqlResult result = generateUpdateSql(obj);

        int update1 = update(result.getSql(), result.getParams().toArray());
        return update1;


    }
    //根据主键生成update-> update 表 set 字段=?,字段=？。。。where 主键=?
    private static SqlResult generateUpdateSql(Object object){
        Class<?> aClass = object.getClass();

        if(aClass.isAnnotationPresent(Table.class)){
            StringBuilder sql = new StringBuilder();
            StringBuilder key = new StringBuilder();
            List<Object> parameters = new ArrayList<Object>();
            String tableName = getTableName(object);
            sql.append("update ");
            key.append(" where ");
            sql.append(tableName);
            sql.append(" set ");
            Field[] field = aClass.getDeclaredFields();
            for (Field field1 : field) {
                field1.setAccessible(true);
                Object value=null;
                try {
                    value = field1.get(object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if(value==null){
                    continue;
                }
                if(field1.isAnnotationPresent(Column.class)){
                    String columnName = getColumnName(field1);
                    sql.append(columnName+"=?,");
                    parameters.add(value);
                }else if(field1.isAnnotationPresent(ManyToOne.class)){
                    ManyToOne manyToOne = field1.getAnnotation(ManyToOne.class);
                    String manyToOneName = getManyToOneName(field1);
                    Field idName = getIdName(field1.getType());//主键字段
                    Object v=null;
                    try {
                        idName.setAccessible(true);
                        //这里的value是属性子类
                        v  = idName.get(value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    sql.append(manyToOneName+"=?");
                    sql.append(",");
                    parameters.add(v);
                }
            }
            Field field1 = FindPrimaryKey(object);
            String idName = getIdName(field1);
            key.append(idName+"=?");
            field1.setAccessible(true);
            Object value=null;
            try {
                value = field1.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if(value==null){
                throw new RuntimeException(field1.getName()+"主键为null!");
            }
            parameters.add(value);
            sql.deleteCharAt((sql.length() - 1));
            sql.append(key);

            SqlResult result = new SqlResult(sql.toString(), parameters);
            return result;

        }
        throw new RuntimeException(aClass.getName()+"不支持数据库操作");



    }
    public static List<Map<String,Object>> select(String sql, Object...params){
        Connection connection = getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String,Object>> list = new ArrayList<>();
        try {
            pst = connection.prepareStatement(sql);
            if(params != null && params.length > 0){
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i + 1,params[i]);
                }
            }
            rs = pst.executeQuery();
            //处理结果集
            while(rs.next()){
                Map<String,Object> map = new HashMap<>();
                list.add(map);
                //给对象的属性赋值
                //先找到查询结果中的所有字段名
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);//第几列的名字
                    map.put(columnName,rs.getObject(columnName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }  finally {
            SqlUtil.close(rs,pst,connection);
        }
        return  list;
    }
    public static <T> T selectOne(Class<T> clazz,String sql,Object...params){

        if(clazz.isAnnotationPresent(Table.class)){
            Connection connection = getConnection();
            PreparedStatement ps=null;
            ResultSet resultSet=null;
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(params!=null&&params.length>0){
                for(int i=0;i< params.length;i++){
                    try {
                        ps.setObject(i+1,params[i]);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            try {
                resultSet = ps.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            List<T> objects = resloveResultSet(clazz, resultSet);
            close(resultSet,ps,connection);
            return objects.get(0);
            //将查询结果返回
        }
        throw new RuntimeException(clazz.getName()+"不支出数据库查询");
    }
    private static String getTableName(Object obj){
        if(obj.getClass().isAnnotationPresent(Table.class)){
        Table table = obj.getClass().getAnnotation(Table.class);
        String tableName=table.value();
        if("".equals(table.value())){
            tableName=obj.getClass().getSimpleName();
            return  tableName;
        }
        return tableName;}
        throw new RuntimeException(obj.getClass().getName()+"不是表");
    }
    private static String getIdName(Field field){
        if(field.isAnnotationPresent(Id.class)){
            Id id = field.getAnnotation(Id.class);
            String idName=id.value();
            if("".equals(id.value())){
                idName=field.getName();
                return  idName;
            }
            return idName;}
        throw new RuntimeException(field.getName()+"不是主键");
    }

    private static String getColumnName(Field field){
        if(field.isAnnotationPresent(Column.class)){
            Column column= field.getAnnotation(Column.class);
            String columnName=column.value();
            if("".equals(columnName)){
                columnName=field.getName();
                return  columnName;
            }
            return columnName;}
        throw new RuntimeException(field.getName()+"不是字段");
    }

    /*
    根据主键删除数据1.找到表,2.找到主键,3删除
     */
    private static SqlResult generateDeleteSql(Object object) {
        Class<?> aClass = object.getClass();
        Table table = object.getClass().getAnnotation(Table.class);//拿到表的注解
        if (aClass.isAnnotationPresent(Table.class)) {
            StringBuilder sql = new StringBuilder();
            //构造语句
            sql.append("delete from ");
            List<Object> parameters = new ArrayList<>();
            String tableName = getTableName(object);

//            if ("".equals(table.value())) {
//                String tableName = object.getClass().getSimpleName();
//                sql.append(tableName);
//            } else {
//                sql.append(table.value());
//            }
            sql.append(tableName);
            sql.append(" where ");
            Field field = FindPrimaryKey(object);
            field.setAccessible(true);
            Id annotation = field.getAnnotation(Id.class);
            String idName = getIdName(field);
//            String idName = annotation.value();
//            if ("".equals(idName)) {
//                    idName = field.getName();
//            }
            sql.append(idName);
            sql.append("=?");
//                    valueSql.append("?,");
            Object value = null;
                try {
                    value = field.get(object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                parameters.add(value);

                SqlResult result = new SqlResult(sql.toString(), parameters);
                return result;



        } else {
            throw new RuntimeException(aClass.getSimpleName() + "不支持插入数据库");
        }
    }

    public static int insertRetrunID(Object obj){
        SqlResult result = generateInsertSql(obj);
        List<Object> params = result.getParams();

        Connection connection = getConnection();
        PreparedStatement pS=null;
        int res=0;
        try {
            pS = connection.prepareStatement(result.getSql(),Statement.RETURN_GENERATED_KEYS);
            if(params!=null&&params.size()>0){
                for(int i=0;i<params.size();i++){
                    pS.setObject(i+1,params.get(i));
                }
            }
            res= pS.executeUpdate();
            ResultSet generatedKeys = pS.getGeneratedKeys();
           if(generatedKeys.next()){
               Object value = generatedKeys.getLong(1);

               Field field = FindPrimaryKey(obj);
               field.setAccessible(true);
               field.set(obj,value);
           }
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        close(null,pS,connection);
        return res;
    }
    private static Field FindPrimaryKey(Object obj){
        Class<?> aClass = obj.getClass();
        if(aClass.isAnnotationPresent(Table.class)){
            Field[] fields= aClass.getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(Id.class)){
                    return field;
                }
            }
        }
            throw new RuntimeException("没有主键");



    }
    private static <T>Field getField(Class<T> clazz,String columnName){
        //查找有没有匹配columnName的属性
        Field sField=null;
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if(field.isAnnotationPresent(Id.class)){
                Id id = field.getAnnotation(Id.class);
                if(id.value().equals(columnName)){
                    sField=field;

                }else if(field.getName().equals(columnName)){
                    sField=field;
                }
            }else if (field.isAnnotationPresent(Column.class)){
                Column column = field.getAnnotation(Column.class);
                if(column.value().equals(columnName)){
                    sField=field;

                }else if(field.getName().equals(columnName)){
                    sField=field;
                }
            }
        }
        return sField;
    }
    private static  int update(String sql, Object... params) {
        //更新
        Connection connection = getConnection();
        PreparedStatement pS=null;
        int res=0;
        try {
           pS = connection.prepareStatement(sql);
            if(params!=null&&params.length>0){
            for(int i=0;i<params.length;i++){
            pS.setObject(i+1,params[i]);
            }
            }
            res= pS.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       close(null,pS,connection);
        return res;
    }
    //insert into 表名 (参数1,参数2,参数....) values (?,?,?,?,?)
    public static SqlResult generateInsertSql(Object object){

        Class<?> aClass = object.getClass();
        Table table = object.getClass().getAnnotation(Table.class);//拿到表的注解
        if(aClass.isAnnotationPresent(Table.class)){
        StringBuilder sql = new StringBuilder();
        //构造语句
        sql.append("insert into ");
        StringBuilder valueSql=new StringBuilder();
        valueSql.append(") values (");
        List<Object> parameters = new ArrayList<>();
            String tableName = getTableName(object);
//        if("".equals(table.value())){
//            String tableName = object.getClass().getSimpleName();
//            sql.append(tableName);
//        }else{
//            sql.append(table.value());
//        }
            sql.append(tableName);
        sql.append(" (");
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object value = null;
            try {
                //获取值
                value = declaredField.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (value == null) {
                continue;
            }

            if (declaredField.isAnnotationPresent(Id.class)) {
                Id annotation = declaredField.getAnnotation(Id.class);
                String idName = getIdName(declaredField);
//                String idName = annotation.value();
//                if ("".equals(idName)) {
//                    idName = declaredField.getName();
//
//                }
                sql.append(idName);
                sql.append(",");
                valueSql.append("?,");
                parameters.add(value);
            } else if (declaredField.isAnnotationPresent(Column.class)) {
                Column annotation = declaredField.getAnnotation(Column.class);
                String columnName = getColumnName(declaredField);
//                String column = annotation.value();
//                if ("".equals(column)) {
//                    column = declaredField.getName();
//
//                }
                sql.append(columnName);
                sql.append(",");
                valueSql.append("?,");
                parameters.add(value);
            }else if(declaredField.isAnnotationPresent(ManyToOne.class)){
                ManyToOne manyToOne = declaredField.getAnnotation(ManyToOne.class);
                String manyToOneName = getManyToOneName(declaredField);
                Field idName = getIdName(declaredField.getType());//主键字段
                Object v=null;
                try {
                    idName.setAccessible(true);
                    //这里的value是属性子类
                  v  = idName.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                sql.append(manyToOneName);
                sql.append(",");
                valueSql.append("?,");
                parameters.add(v);
            }

        }
            sql.deleteCharAt(sql.length() - 1);
            valueSql.deleteCharAt(valueSql.length() - 1);
            valueSql.append(")");
            sql.append(valueSql);

            SqlResult result=new SqlResult(sql.toString(),parameters);
            return result;


        }else{
        throw new RuntimeException(aClass.getSimpleName() + "不支持插入数据库");
    }

    }
    private static String getManyToOneName(Field field){
        if (field.isAnnotationPresent(ManyToOne.class)){
            ManyToOne annotation = field.getAnnotation(ManyToOne.class);
            String columnName = annotation.value();
            if ("".equals(columnName)){
                Field primaryKey = getIdName(field.getType());
                columnName = getIdName(primaryKey);
            }
            return columnName;
        }
        throw new RuntimeException(field.getName() + "不是字段");
    }
    public static void close(ResultSet rs, PreparedStatement ps, Connection conn){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(ps!=null){
            try {
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //生成批量查询的sql和参数

    public static SqlResult generateBatchInsertSql(List objects){
        if(objects==null||objects.isEmpty()){
            throw new RuntimeException("插入的数据为空");
        }
        Class<?> aClass = objects.get(0).getClass();
        Table table = aClass.getAnnotation(Table.class);//拿到表的注解
        if(aClass.isAnnotationPresent(Table.class)){
            StringBuilder sql = new StringBuilder();
            //构造语句
            sql.append("insert into ");
            StringBuilder valueSql=new StringBuilder();
            valueSql.append(") values (");
            List<List> parameters = new ArrayList<>();
            List<Field> fieldList = new ArrayList<>();//存储所有的属性
            String tableName = getTableName(objects.get(0));
//        if("".equals(table.value())){
//            String tableName = object.getClass().getSimpleName();
//            sql.append(tableName);
//        }else{
//            sql.append(table.value());
//        }
            sql.append(tableName);
            sql.append(" (");
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
//                Object value = null;
//                try {
//                    //获取值
//                    value = declaredField.get(object);
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//                if (value == null) {
//                    continue;
//                }

                if (declaredField.isAnnotationPresent(Id.class)) {
                    Id annotation = declaredField.getAnnotation(Id.class);
                    String idName = getIdName(declaredField);
//                String idName = annotation.value();
//                if ("".equals(idName)) {
//                    idName = declaredField.getName();
//
//                }
                    sql.append(idName);
                    sql.append(",");
                    valueSql.append("?,");

                } else if (declaredField.isAnnotationPresent(Column.class)) {
                    Column annotation = declaredField.getAnnotation(Column.class);
                    String columnName = getColumnName(declaredField);
//                String column = annotation.value();
//                if ("".equals(column)) {
//                    column = declaredField.getName();
//
//                }
                    sql.append(columnName);
                    sql.append(",");
                    valueSql.append("?,");

                }else if(declaredField.isAnnotationPresent(ManyToOne.class)){
                    ManyToOne manyToOne = declaredField.getAnnotation(ManyToOne.class);
                    String manyToOneName = getManyToOneName(declaredField);
//                    Field idName = getIdName(declaredField.getType());//主键字段
                    Object v=null;
//                    try {
//                        idName.setAccessible(true);
//                        //这里的value是属性子类
//                        v  = idName.get(value);
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    }
                    sql.append(manyToOneName);
                    sql.append(",");
                    valueSql.append("?,");
//                    parameters.add(v);
                }
                fieldList.add(declaredField);

            }
            sql.deleteCharAt(sql.length() - 1);
            valueSql.deleteCharAt(valueSql.length() - 1);
            valueSql.append(")");
            sql.append(valueSql);
            for (Object object : objects) {
                List parm=new ArrayList();

                for (Field field : declaredFields) {
                    Object o=null;
                    try {
                        o= field.get(object);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if(o!=null&&field.isAnnotationPresent(ManyToOne.class)){
                        Field idName = getIdName(field.getType());
                        idName.setAccessible(true);
                        try {
                            parm.add(idName.get(o));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        continue;
                    }

                    parm.add(o);
                }
                parameters.add(parm);
            }



            SqlResult result=new SqlResult(sql.toString(),null);
            result.setBatchParams(parameters);
            return result;


        }else{
            throw new RuntimeException(aClass.getSimpleName() + "不支持插入数据库");
        }

    }
    public static int batchInsert(List objects){
        Connection connection = getConnection();
        PreparedStatement preparedStatement=null;
        SqlResult result = generateBatchInsertSql(objects);


        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(result.getSql());
            List<List> batchParams = result.getBatchParams();
            for (int i = 0; i < batchParams.size(); i++) {
                List list = batchParams.get(i);
                for (int j = 0; j < list.size(); j++) {
                        preparedStatement.setObject(j+1,list.get(j));
                }
                preparedStatement.addBatch();
                if(i%1000==0){
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
        close(null,preparedStatement,connection);
        return objects.size();
    }


}
