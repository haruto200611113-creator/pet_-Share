package com.petshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    // ==============================
    // データベース接続設定
    // ==============================

    // データベースのURL
    private static final String URL =
            "jdbc:mariadb://localhost:3306/petshare";

    // MariaDBのユーザー名
    private static final String USER = "root";

    // MariaDBのパスワード
    private static final String PASSWORD = "root";


    // ==============================
    // ユーザー情報を登録する
    // ==============================

    public boolean insert(User user) {

        String sql = """
                INSERT INTO users (
                    account_name,
                    email,
                    password,
                    name,
                    birth_date,
                    gender,
                    phone,
                    postal_code,
                    prefecture,
                    city,
                    address,
                    building
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
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

            // アカウント名
            ps.setString(
                    1,
                    user.getAccountName()
            );

            // メールアドレス
            ps.setString(
                    2,
                    user.getEmail()
            );

            // パスワード
            ps.setString(
                    3,
                    user.getPassword()
            );

            // 氏名
            ps.setString(
                    4,
                    user.getName()
            );

            // 生年月日
            ps.setString(
                    5,
                    user.getBirthDate()
            );

            // 性別
            ps.setString(
                    6,
                    user.getGender()
            );

            // 電話番号
            ps.setString(
                    7,
                    user.getPhone()
            );

            // 郵便番号
            ps.setString(
                    8,
                    user.getPostalCode()
            );

            // 都道府県
            ps.setString(
                    9,
                    user.getPrefecture()
            );

            // 市区町村
            ps.setString(
                    10,
                    user.getCity()
            );

            // 丁目・番地
            ps.setString(
                    11,
                    user.getAddress()
            );

            // 建物名
            ps.setString(
                    12,
                    user.getBuilding()
            );

            // SQLを実行
            int result = ps.executeUpdate();

            // 登録成功ならtrue
            return result > 0;

        } catch (SQLException e) {

            // エラー内容をVS Codeのターミナルに表示
            e.printStackTrace();

            // 登録失敗
            return false;
        }
    }
}