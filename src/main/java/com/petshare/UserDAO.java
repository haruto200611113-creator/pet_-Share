package com.petshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    // データベース接続情報
    private static final String URL =
            "jdbc:mariadb://localhost:3306/pet_share";

    private static final String USER = "root";

    private static final String PASSWORD = "あなたのパスワード";

    // ユーザー登録
    public boolean insert(User user) {

        String sql = """
                INSERT INTO users
                (name, email, password, phone, address)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
            Connection con =
                    DriverManager.getConnection(
                            URL,
                            USER,
                            PASSWORD
                    );

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());

            int result = ps.executeUpdate();

            return result > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }
}