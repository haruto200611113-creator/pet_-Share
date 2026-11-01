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

    // Aiven MySQL接続設定
    // TrustStoreの保存場所
// TrustStoreの保存場所
private static final String TRUSTSTORE_PATH =
    "C:/Users/kaker/OneDrive/Desktop/pet_share/certs/aiven-truststore.p12";

// TrustStoreのパスワード
private static final String TRUSTSTORE_PASSWORD =
    System.getenv("AIVEN_TRUSTSTORE_PASSWORD");

// データベース接続URL
private static final String URL =
    "jdbc:mysql://mysql-1589c60e-petshare-project.f.aivencloud.com:21440/defaultdb"
    + "?sslMode=VERIFY_CA"
    + "&trustCertificateKeyStoreUrl=file:" + TRUSTSTORE_PATH
    + "&trustCertificateKeyStoreType=PKCS12"
    + "&trustCertificateKeyStorePassword=" + TRUSTSTORE_PASSWORD;
    private static final String USER =
            System.getenv("AIVEN_DB_USER");

    private static final String PASSWORD =
            System.getenv("AIVEN_DB_PASSWORD");

    // =====================================
    // ユーザー登録
    // =====================================

    public boolean insert(User user) {

        System.out.println("===== UserDAO登録開始 =====");

        if (user == null) {
            System.err.println("ユーザー情報がありません。");
            return false;
        }

        if (isEmpty(user.getAccountName())
                || isEmpty(user.getEmail())
                || isEmpty(user.getPassword())
                || isEmpty(user.getName())
                || isEmpty(user.getBirthDate())) {

            System.err.println("必須情報が不足しています。");
            return false;
        }

        // DB接続情報確認
        if (USER == null || USER.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {

            System.err.println("Aivenの接続情報が設定されていません。");
            return false;
        }

        // JDBCドライバー読み込み
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQLドライバー読み込み成功");

        } catch (ClassNotFoundException e) {

            System.err.println("MySQLドライバーが見つかりません。");
            e.printStackTrace();
            return false;
        }

        // 生年月日変換
        LocalDate birthDate;

        try {
            birthDate = LocalDate.parse(user.getBirthDate());

        } catch (Exception e) {

            System.err.println("生年月日の形式が正しくありません。");
            e.printStackTrace();
            return false;
        }

        // 性別変換
        String gender = convertGender(user.getGender());

        if (gender == null) {
            System.err.println("性別の値が正しくありません。");
            return false;
        }

        // パスワードハッシュ化
        String passwordHash;

        try {
            passwordHash = BCrypt.hashpw(
                    user.getPassword(),
                    BCrypt.gensalt(12)
            );

        } catch (Exception e) {

            System.err.println("パスワード処理に失敗しました。");
            e.printStackTrace();
            return false;
        }

        // accounts登録SQL
        String accountSql =
                "INSERT INTO accounts "
                + "(account_name, email, password_hash) "
                + "VALUES (?, ?, ?)";

        // personal_information登録SQL
        String personalSql =
                "INSERT INTO personal_information "
                + "(account_id, last_name, first_name, "
                + "birth_year, birth_month, birth_day, "
                + "gender, phone, postal_code, prefecture, "
                + "city, street_address, building) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // データベース接続
        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.println("Aiven接続成功");

            con.setAutoCommit(false);

            try {

                int accountId;

                // accounts登録
                System.out.println("accounts登録開始");

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     accountSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, user.getAccountName());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, passwordHash);

                    int result = ps.executeUpdate();

                    if (result != 1) {
                        throw new SQLException(
                                "accountsへの登録に失敗しました。"
                        );
                    }

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "account_idを取得できません。"
                            );
                        }

                        accountId = rs.getInt(1);
                    }
                }

                System.out.println("accounts登録成功");

                // personal_information登録
                System.out.println("personal_information登録開始");

                try (PreparedStatement ps =
                             con.prepareStatement(personalSql)) {

                    String[] names = splitName(user.getName());

                    ps.setInt(1, accountId);
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

                    int result = ps.executeUpdate();

                    if (result != 1) {
                        throw new SQLException(
                                "personal_informationへの登録に失敗しました。"
                        );
                    }
                }

                System.out.println("personal_information登録成功");

                // 登録確定
                con.commit();

                System.out.println("ユーザー登録成功");

                return true;

            } catch (Exception e) {

                // エラー時は登録を取り消す
                try {
                    con.rollback();
                    System.err.println("ロールバック成功");

                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                System.err.println("登録処理に失敗しました。");
                e.printStackTrace();

                return false;
            }

        } catch (SQLException e) {

            System.err.println("DB接続またはSQL処理に失敗しました。");
            e.printStackTrace();

            return false;

        } finally {

            System.out.println("===== UserDAO登録終了 =====");
        }
    }

    // =====================================
    // 氏名を姓・名に分割
    // =====================================

    private String[] splitName(String name) {

        if (name == null || name.trim().isEmpty()) {
            return new String[]{"", ""};
        }

        String normalized = name.trim().replace('　', ' ');

        String[] parts = normalized.split("\\s+", 2);

        if (parts.length == 2) {
            return parts;
        }

        return new String[]{normalized, ""};
    }

    // =====================================
    // 性別変換
    // =====================================

    private String convertGender(String gender) {

        if (gender == null || gender.trim().isEmpty()) {
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
    // 空文字確認
    // =====================================

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}