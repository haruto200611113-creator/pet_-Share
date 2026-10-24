package com.petshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {


// =====================================
// Aiven MySQL 接続設定
// =====================================

private static final String URL =
        "jdbc:mysql://mysql-1589c60e-petshare-project.f.aivencloud.com:21440/defaultdb?sslMode=VERIFY_CA";

// 環境変数から接続情報を取得
private static final String USER =
        System.getenv("AIVEN_DB_USER");

private static final String PASSWORD =
        System.getenv("AIVEN_DB_PASSWORD");

// =====================================
// ユーザー情報を登録
// =====================================

public boolean insert(User user) {

    if (user == null
            || isEmpty(user.getEmail())
            || isEmpty(user.getPassword())) {
        return false;
    }

    // 生年月日を年月日に分割
    LocalDate birthDate;

    try {
        birthDate = LocalDate.parse(user.getBirthDate());
    } catch (Exception e) {
        System.err.println("生年月日が正しくありません。");
        return false;
    }

    // 性別をDB用の値に変換
    String gender = convertGender(user.getGender());

    if (gender == null) {
        return false;
    }

    // パスワードをハッシュ化
    String passwordHash = BCrypt.hashpw(
            user.getPassword(),
            BCrypt.gensalt(12)
    );

    String accountSql = """
            INSERT INTO accounts (
                account_name,
                email,
                password_hash
            )
            VALUES (?, ?, ?)
            """;

    String personalSql = """
            INSERT INTO personal_information (
                account_id,
                last_name,
                first_name,
                birth_year,
                birth_month,
                birth_day,
                gender,
                phone,
                postal_code,
                prefecture,
                city,
                street_address,
                building
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    if (USER == null || USER.isBlank()
            || PASSWORD == null || PASSWORD.isBlank()) {
        System.err.println(
                "AIVEN_DB_USER または AIVEN_DB_PASSWORD が設定されていません。"
        );
        return false;
    }

    try (Connection con =
                 DriverManager.getConnection(URL, USER, PASSWORD)) {

        // 2つのテーブルへの登録を1つの処理として実行
        con.setAutoCommit(false);

        try {
            int accountId;

            // ① accountsへ登録
            try (PreparedStatement ps = con.prepareStatement(
                    accountSql,
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, user.getAccountName());
                ps.setString(2, user.getEmail());
                ps.setString(3, passwordHash);

                ps.executeUpdate();

                // 自動採番されたaccount_idを取得
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "account_idを取得できませんでした。"
                        );
                    }

                    accountId = rs.getInt(1);
                }
            }

            // ② personal_informationへ登録
            try (PreparedStatement ps =
                         con.prepareStatement(personalSql)) {

                ps.setInt(1, accountId);

                // 氏名を姓・名に分割
                String[] names = splitName(user.getName());

                ps.setString(2, names[0]);
                ps.setString(3, names[1]);

                ps.setInt(4, birthDate.getYear());
                ps.setInt(5, birthDate.getMonthValue());
                ps.setInt(6, birthDate.getDayOfMonth());

                ps.setString(7, gender);
                ps.setString(8, user.getPhone());
                ps.setString(9, user.getPostalCode());
                ps.setString(10, user.getPrefecture());
                ps.setString(11, user.getCity());
                ps.setString(12, user.getAddress());
                ps.setString(13, user.getBuilding());

                ps.executeUpdate();
            }

            // 両方成功した場合のみ確定
            con.commit();

            System.out.println("ユーザー登録に成功しました。");
            return true;

        } catch (Exception e) {

            // 片方でも失敗したら両方取り消す
            try {
                con.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }

            throw e;
        }

    } catch (Exception e) {

        // メール重複などの原因をターミナルに表示
        System.err.println("ユーザー登録に失敗しました。");
        e.printStackTrace();

        return false;
    }
}

// =====================================
// 氏名を姓・名に分割
// =====================================

private String[] splitName(String name) {

    if (name == null || name.isBlank()) {
        return new String[] {"", ""};
    }

    String normalized = name.trim();

    // 全角スペースを半角スペースに統一
    normalized = normalized.replace('　', ' ');

    String[] parts = normalized.split("\\s+", 2);

    if (parts.length == 2) {
        return parts;
    }

    // 姓・名の区切りがない場合は姓として保存
    return new String[] {normalized, ""};
}

// =====================================
// 性別をDB用の値に変換
// =====================================

private String convertGender(String gender) {

    if (gender == null || gender.isBlank()) {
        return "undisclosed";
    }

    switch (gender) {
        case "男性":
        case "male":
            return "male";

        case "女性":
        case "female":
            return "female";

        case "その他":
        case "other":
            return "other";

        case "回答しない":
        case "undisclosed":
            return "undisclosed";

        default:
            return null;
    }
}

// =====================================
// 空文字チェック
// =====================================

private boolean isEmpty(String value) {
    return value == null || value.trim().isEmpty();
}


}
