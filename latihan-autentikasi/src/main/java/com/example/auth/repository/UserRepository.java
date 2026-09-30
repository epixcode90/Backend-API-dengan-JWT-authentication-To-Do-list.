package com.example.auth.repository;

import com.example.auth.config.Database;
import com.example.auth.model.User;

import javax.management.RuntimeMBeanException;
import java.sql.*;

public class UserRepository {

    public User findByUsername(String username) {
        String sql = "select * from users where username=?";

        try(Connection connection = Database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,username);

            try(ResultSet rs = ps.executeQuery()){
                if (rs.next())return map(rs);
            }
        }catch (SQLException e) {
            throw new RuntimeException("DB ERROR find by username" ,e);
        }
        return null;
    }


    public User save(User user) {
        String sql = "INSERT INTO users (username,password,role) VALUES (?,?,?)";

        try(Connection connection = Database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            int affacted = ps.executeUpdate();
            if (affacted == 0) {
                throw new RuntimeException("insert gagal");
            }

            try(ResultSet keys = ps.getGeneratedKeys()){
                if(keys.next()){
                    user.setId(keys.getLong(1));
                }
            }
            return user;
        } catch (SQLIntegrityConstraintViolationException e){
            throw new IllegalArgumentException("User already exists");
        } catch (SQLException e){
            throw new RuntimeException("DB ERROR save user" ,e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
        );
    }

    public User findById(Long userId) {

        String sql = "select * from users where id=?";

        try(Connection connection = Database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1,userId);

            try(ResultSet rs = ps.executeQuery()){
                if (rs.next())return map(rs);
            }
        }catch (SQLException e) {
            throw new RuntimeException("DB ERROR find by user id" ,e);
        }
        return null;
    }

}
