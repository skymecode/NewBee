package org.skyme.jdbc;

import java.sql.*;

/**
 * @author:Skyme
 * @create: 2023-08-15 18:44
 * @Description:
 */
public class Test {
    public static void main(String[] args) throws SQLException {
        //加载驱动
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        Connection connection=null;
//        try {
//            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/db_skyme", "root", "w5103265");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        String sql=" select * from kl_user";
//        Statement statement = null;
//        try {
//            statement = connection.createStatement();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        ResultSet resultSet=null;
//        try {
//           resultSet=  statement.executeQuery(sql);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        while(resultSet.next()){
//            int anInt = resultSet.getInt("user_id");
//            String string = resultSet.getString("nickname");
//
//            System.out.println(anInt+" "+string);
//        }
//
//        connection.close();
        update();
    }
    public static void update(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection=null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/db_skyme", "root", "w5103265");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql="update kl_user set status=status+? where user_id=?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1,1);
            preparedStatement.setInt(2,52);
            preparedStatement.executeUpdate();
            System.out.println(1/0);
            preparedStatement.setInt(1,-2);
            preparedStatement.setInt(2,52);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(preparedStatement!=null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
