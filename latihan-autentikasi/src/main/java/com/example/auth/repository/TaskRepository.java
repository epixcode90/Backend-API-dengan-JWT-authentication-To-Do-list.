package com.example.auth.repository;

import com.example.auth.config.Database;
import com.example.auth.model.Task;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    public List<Task> findAllByUserId(Long userId) {
        String sql = "select * from tasks where user_id = ? order by created_at desc";

        List<Task> tasks = new ArrayList<>();

        try (Connection con = Database.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tasks.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error findAllByUserId", e);
        }
        return tasks;
    }

    public Task findByIdAndUserId(Long id, Long userId) {
        String sql = "select * from tasks where id = ? and user_id = ?";

        try (Connection con = Database.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setLong(1, id);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);

            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error findByIdAndUserId", e);
        }
        return null;
    }


    public Task save(Task task) {
        String sql = """
                insert into tasks
                (user_id,title,description,completed)
                values
                (?,?,?,?) 
                """;

        try (Connection con = Database.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setLong(1, task.getUser_id());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setBoolean(4, task.isCompleted());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("insert failed");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setId(keys.getLong(1));
                }
            }

            return task;
        } catch (SQLException e) {
            throw new RuntimeException("DB error save", e);
        }
    }


    public boolean update(Task task) {

        String sql = """
                        update tasks
                        set title = ?,description = ?,completed =?
                        where id = ?
                        and user_id = ?
                """;

        try (Connection con = Database.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setBoolean(3, task.isCompleted());
            ps.setLong(4, task.getId());
            ps.setLong(5, task.getUser_id());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error update task", e);
        }
    }

    public boolean deleteByIddanUserId(Long id ,Long userId) {
        String sql = """
                                delete from tasks 
                                where id = ?
                                and user_id = ?
                """;

        try(Connection con = Database.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setLong(1,id);
            ps.setLong(2,userId);

            return ps.executeUpdate()>0;

        }catch (SQLException e){
            throw new RuntimeException("DB error delete task", e);
        }
    }

    private Task map(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setUser_id(rs.getLong("user_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCompleted(rs.getBoolean("completed"));

        Timestamp created_at = rs.getTimestamp("created_at");
        if(created_at != null){
            task.setCreatedAt(created_at.toLocalDateTime());
        }

        Timestamp updated_at = rs.getTimestamp("updated_at");
        if(updated_at != null){
            task.setUpdatedAt(updated_at.toLocalDateTime());
        }
        return task;
    }
}
