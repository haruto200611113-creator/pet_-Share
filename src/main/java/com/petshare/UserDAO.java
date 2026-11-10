package com.petshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    // =====================================
    // Aiven MySQL接続設定
    // =====================================

    // TrustStoreのパスワード
    private static final String TRUSTSTORE_PASSWORD = System.getenv("AIVEN_TRUSTSTORE_PASSWORD");

    // Renderの環境変数から取得するBase64化されたTrustStore
    private static final String TRUSTSTORE_BASE64 = System.getenv("AIVEN_TRUSTSTORE_BASE64");

    // Render上で復元するTrustStoreの保存先
    private static final String TRUSTSTORE_PATH = createTrustStoreFile();

    // Aiven MySQL接続URL
    private static final String URL = "jdbc:mysql://mysql-1589c60e-petshare-project.f.aivencloud.com:21440/defaultdb"
            + "?sslMode=VERIFY_CA"
            + "&trustCertificateKeyStoreUrl=file:" + TRUSTSTORE_PATH
            + "&trustCertificateKeyStoreType=PKCS12"
            + "&trustCertificateKeyStorePassword=" + TRUSTSTORE_PASSWORD;

    // データベースユーザー
    private static final String USER = System.getenv("AIVEN_DB_USER");

    // データベースパスワード
    private static final String PASSWORD = System.getenv("AIVEN_DB_PASSWORD");

    // =====================================
    // Base64 → TrustStore復元
    // =====================================

    private static String createTrustStoreFile() {

        if (TRUSTSTORE_BASE64 == null || TRUSTSTORE_BASE64.isBlank()) {
            throw new ExceptionInInitializerError(
                    "AIVEN_TRUSTSTORE_BASE64 が設定されていません");
        }

        try {

            byte[] trustStoreBytes = Base64.getMimeDecoder().decode(TRUSTSTORE_BASE64);

            Path path = Path.of("/tmp/aiven-truststore-render.p12");

            Files.write(path, trustStoreBytes);

            return path.toAbsolutePath().toString();

        } catch (Exception e) {

            throw new ExceptionInInitializerError(
                    "TrustStoreの復元に失敗しました: " + e.getMessage());
        }
    }

    // =====================================
    // DB接続設定確認
    // =====================================

    private boolean isDbConfigAvailable() {
        return USER != null && !USER.isBlank()
                && PASSWORD != null && !PASSWORD.isBlank();
    }

    // =====================================
    // データベース接続
    // =====================================

    private Connection getConnection() throws SQLException {
        try {
            // MySQL JDBCドライバーを明示的に読み込む
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBCドライバーの読み込みに失敗しました。", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // =====================================
    // ユーザー登録
    // =====================================

    public boolean insert(User user) {

        System.out.println("===== UserDAO登録開始 =====");

        if (user == null) {

            System.err.println("ユーザー情報がありません。");

            return false;
        }

        // 必須項目確認
        if (isEmpty(user.getAccountName())
                || isEmpty(user.getEmail())
                || isEmpty(user.getPassword())
                || isEmpty(user.getLastName())
                || isEmpty(user.getFirstName())
                || isEmpty(user.getBirthDate())) {
            System.err.println("必須情報が不足しています。");

            return false;
        }

        // DB接続設定確認
        if (!isDbConfigAvailable()) {

            System.err.println(
                    "Aivenの接続情報が設定されていません。");

            return false;
        }

        // JDBCドライバー読み込み
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("MySQLドライバー読み込み成功");

        } catch (ClassNotFoundException e) {

            System.err.println(
                    "MySQLドライバーが見つかりません。");

            e.printStackTrace();

            return false;
        }

        // 生年月日変換
        LocalDate birthDate;

        try {

            birthDate = LocalDate.parse(
                    user.getBirthDate());

        } catch (Exception e) {

            System.err.println(
                    "生年月日の形式が正しくありません。");

            e.printStackTrace();

            return false;
        }

        // 性別変換
        String gender = convertGender(
                user.getGender());

        if (gender == null) {

            System.err.println(
                    "性別の値が正しくありません。");

            return false;
        }

        // パスワードハッシュ化
        String passwordHash;

        try {

            passwordHash = BCrypt.hashpw(
                    user.getPassword(),
                    BCrypt.gensalt(12));

        } catch (Exception e) {

            System.err.println(
                    "パスワード処理に失敗しました。");

            e.printStackTrace();

            return false;
        }

        // accounts登録SQL
        String accountSql = "INSERT INTO accounts "
                + "(account_name, email, password_hash) "
                + "VALUES (?, ?, ?)";

        // personal_information登録SQL
        String personalSql = "INSERT INTO personal_information "
                + "(account_id, last_name, first_name, "
                + "birth_year, birth_month, birth_day, "
                + "gender, phone, postal_code, prefecture, "
                + "city, street_address, building) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // DB接続
        try (Connection con = getConnection()) {

            System.out.println("Aiven接続成功");

            con.setAutoCommit(false);

            try {

                int accountId;

                // ==============================
                // accounts登録
                // ==============================

                System.out.println("accounts登録開始");

                try (
                        PreparedStatement ps = con.prepareStatement(
                                accountSql,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(
                            1,
                            user.getAccountName());

                    ps.setString(
                            2,
                            user.getEmail());

                    ps.setString(
                            3,
                            passwordHash);

                    int result = ps.executeUpdate();

                    if (result != 1) {

                        throw new SQLException(
                                "accountsへの登録に失敗しました。");
                    }

                    // 登録されたaccount_id取得
                    try (
                            ResultSet rs = ps.getGeneratedKeys()) {

                        if (!rs.next()) {

                            throw new SQLException(
                                    "account_idを取得できません。");
                        }

                        accountId = rs.getInt(1);
                    }
                }

                System.out.println("accounts登録成功");

                // ==============================
                // personal_information登録
                // ==============================

                System.out.println(
                        "personal_information登録開始");

                try (
                        PreparedStatement ps = con.prepareStatement(personalSql)) {

                    ps.setInt(1, accountId);
                    ps.setString(2, user.getLastName());
                    ps.setString(3, user.getFirstName());

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
                                "personal_informationへの登録に失敗しました。");
                    }
                }

                System.out.println(
                        "personal_information登録成功");

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

                System.err.println(
                        "登録処理に失敗しました。");

                e.printStackTrace();

                return false;
            }

        } catch (SQLException e) {

            System.err.println(
                    "DB接続またはSQL処理に失敗しました。");

            e.printStackTrace();

            return false;

        } finally {

            System.out.println(
                    "===== UserDAO登録終了 =====");
        }
    }

    // =====================================
    // ログイン認証
    // =====================================

    public User login(String email, String password) {

        System.out.println("===== ログイン認証開始 =====");

        // 入力確認
        if (isEmpty(email) || isEmpty(password)) {

            System.err.println(
                    "メールアドレスまたはパスワードが空です。");

            return null;
        }

        // SQL
        String sql = "SELECT account_id, account_name, email, password_hash, user_rank "
                + "FROM accounts "
                + "WHERE email = ?";

        try (Connection con = getConnection();

                PreparedStatement ps = con.prepareStatement(sql)) {

            // メールアドレス設定
            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    // DBからハッシュ化されたパスワード取得
                    String passwordHash = rs.getString("password_hash");

                    // BCryptによる照合
                    boolean matched = false;

                    if (passwordHash != null) {

                        try {

                            matched = BCrypt.checkpw(
                                    password,
                                    passwordHash);

                        } catch (IllegalArgumentException e) {

                            // 不正な形式のハッシュ
                            matched = false;
                        }
                    }

                    if (matched) {

                        User user = new User();

                        user.setAccountId(
                                rs.getInt("account_id"));

                        user.setAccountName(
                                rs.getString("account_name"));

                        user.setEmail(
                                rs.getString("email"));

                        user.setUserRank(
                                rs.getInt("user_rank"));

                        System.out.println(
                                "ログイン認証成功");

                        return user;
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "ログイン認証に失敗しました。");

            e.printStackTrace();

        } finally {

            System.out.println(
                    "===== ログイン認証終了 =====");
        }

        return null;
    }

    // =====================================
    // マイページ用ユーザー情報取得
    // =====================================

    public User findById(int accountId) {

        System.out.println(
                "===== ユーザー情報取得開始 =====");

        if (accountId <= 0) {

            System.err.println(
                    "account_idが正しくありません。");

            return null;
        }

        // accountsとpersonal_informationを結合
        String sql = "SELECT "
                + "a.account_id, "
                + "a.account_name, "
                + "a.email, "
                + "a.user_rank, "
                + "p.last_name, "
                + "p.first_name, "
                + "p.birth_year, "
                + "p.birth_month, "
                + "p.birth_day, "
                + "p.gender, "
                + "p.phone, "
                + "p.postal_code, "
                + "p.prefecture, "
                + "p.city, "
                + "p.street_address, "
                + "p.building "
                + "FROM accounts a "
                + "LEFT JOIN personal_information p "
                + "ON a.account_id = p.account_id "
                + "WHERE a.account_id = ?";

        try (Connection con = getConnection();

                PreparedStatement ps = con.prepareStatement(sql)) {

            // アカウントID設定
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User user = new User();

                    // ==============================
                    // アカウント情報
                    // ==============================

                    user.setAccountId(
                            rs.getInt("account_id"));

                    user.setAccountName(
                            rs.getString("account_name"));

                    user.setEmail(
                            rs.getString("email"));
                    user.setUserRank(
                            rs.getInt("user_rank"));

                    // ==============================
                    // 氏名
                    // ==============================

                    String lastName = rs.getString("last_name");
                    String firstName = rs.getString("first_name");

                    user.setLastName(lastName);
                    user.setFirstName(firstName);

                    // ==============================
                    // 生年月日
                    // ==============================

                    Integer year = rs.getObject("birth_year", Integer.class);

                    Integer month = rs.getObject("birth_month", Integer.class);

                    Integer day = rs.getObject("birth_day", Integer.class);

                    if (year != null
                            && month != null
                            && day != null) {

                        user.setBirthDate(
                                String.format(
                                        "%04d-%02d-%02d",
                                        year,
                                        month,
                                        day));
                    }

                    // ==============================
                    // 性別
                    // ==============================

                    user.setGender(
                            rs.getString("gender"));

                    // ==============================
                    // 電話番号
                    // ==============================

                    user.setPhone(
                            rs.getString("phone"));

                    // ==============================
                    // 郵便番号
                    // ==============================

                    user.setPostalCode(
                            rs.getString("postal_code"));

                    // ==============================
                    // 都道府県
                    // ==============================

                    user.setPrefecture(
                            rs.getString("prefecture"));

                    // ==============================
                    // 市区町村
                    // ==============================

                    user.setCity(
                            rs.getString("city"));

                    // ==============================
                    // 丁目・番地
                    // ==============================

                    user.setAddress(
                            rs.getString("street_address"));

                    // ==============================
                    // 建物名
                    // ==============================

                    user.setBuilding(
                            rs.getString("building"));

                    System.out.println(
                            "ユーザー情報取得成功");

                    return user;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "ユーザー情報取得に失敗しました。");

            e.printStackTrace();

        } finally {

            System.out.println(
                    "===== ユーザー情報取得終了 =====");
        }

        return null;
    }

    // =====================================
    // 住所変更
    // =====================================

    public boolean updateAddress(
            int accountId,
            String postalCode,
            String prefecture,
            String city,
            String address,
            String building) {

        if (accountId <= 0
                || isEmpty(postalCode)
                || isEmpty(prefecture)
                || isEmpty(city)
                || isEmpty(address)) {

            return false;
        }

        String sql = "UPDATE personal_information "
                + "SET postal_code = ?, "
                + "prefecture = ?, "
                + "city = ?, "
                + "street_address = ?, "
                + "building = ? "
                + "WHERE account_id = ?";

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, postalCode);
            ps.setString(2, prefecture);
            ps.setString(3, city);
            ps.setString(4, address);
            ps.setString(5, building);
            ps.setInt(6, accountId);

            int result = ps.executeUpdate();

            System.out.println("===== 住所変更DB確認 =====");
            System.out.println("account_id：" + accountId);
            System.out.println("郵便番号：" + postalCode);
            System.out.println("都道府県：" + prefecture);
            System.out.println("市区町村：" + city);
            System.out.println("番地：" + address);
            System.out.println("建物名：" + building);
            System.out.println("更新件数：" + result);
            System.out.println("==========================");

            return result > 0;

        } catch (SQLException e) {

            System.err.println("住所変更に失敗しました。");
            e.printStackTrace();

            return false;
        }
    }

    // =====================================
    // 氏名変更
    // =====================================

    // =====================================
    // 氏名変更
    // =====================================

    public boolean updateName(
            int accountId,
            String lastName,
            String firstName) {

        // 入力チェック
        if (accountId <= 0
                || isEmpty(lastName)
                || isEmpty(firstName)) {

            return false;
        }

        // 氏名変更SQL
        String sql = "UPDATE personal_information "
                + "SET last_name = ?, first_name = ? "
                + "WHERE account_id = ?";

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            // 姓をそのまま登録
            ps.setString(1, lastName.trim());

            // 名をそのまま登録
            ps.setString(2, firstName.trim());

            // アカウントID
            ps.setInt(3, accountId);

            // DB更新
            int result = ps.executeUpdate();

            // 確認ログ
            System.out.println("===== 氏名変更DB確認 =====");
            System.out.println("account_id：" + accountId);
            System.out.println("変更後の姓：" + lastName);
            System.out.println("変更後の名：" + firstName);
            System.out.println("更新件数：" + result);
            System.out.println("==========================");

            return result > 0;

        } catch (SQLException e) {

            System.err.println("氏名変更に失敗しました。");
            e.printStackTrace();

            return false;
        }
    }

    // =====================================
    // メールアドレス変更
    // =====================================

    public boolean updateEmail(int accountId, String email) {

        if (accountId <= 0 || isEmpty(email)) {
            return false;
        }

        String sql = "UPDATE accounts "
                + "SET email = ? "
                + "WHERE account_id = ?";

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            ps.setInt(2, accountId);

            int result = ps.executeUpdate();

            System.out.println("===== メールアドレス変更DB確認 =====");
            System.out.println("account_id：" + accountId);
            System.out.println("変更後メールアドレス：" + email);
            System.out.println("更新件数：" + result);
            System.out.println("===============================");

            return result > 0;

        } catch (SQLException e) {

            System.err.println("メールアドレス変更に失敗しました。");
            e.printStackTrace();

            return false;
        }
    }

    // =====================================
    // 氏名を姓・名に分割
    // =====================================

    private String[] splitName(String name) {

        if (name == null || name.trim().isEmpty()) {

            return new String[] { "", "" };
        }

        String normalized = name.trim().replace('　', ' ');

        String[] parts = normalized.split("\\s+", 2);

        if (parts.length == 2) {

            return parts;
        }

        return new String[] { normalized, "" };
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
    // =====================================
    // 生年月日変更
    // =====================================

    public boolean updateBirthDate(
            int accountId,
            String birthDate) {

        if (accountId <= 0 || isEmpty(birthDate)) {
            System.out.println("生年月日変更：入力値エラー");
            return false;
        }

        // yyyy-MM-dd → 年・月・日に分解
        LocalDate date;

        try {
            date = LocalDate.parse(birthDate);
        } catch (Exception e) {
            System.err.println("生年月日の形式が正しくありません。");
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE personal_information "
                + "SET birth_year = ?, "
                + "birth_month = ?, "
                + "birth_day = ? "
                + "WHERE account_id = ?";

        try (
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            // 年
            ps.setInt(1, date.getYear());

            // 月
            ps.setInt(2, date.getMonthValue());

            // 日
            ps.setInt(3, date.getDayOfMonth());

            // アカウントID
            ps.setInt(4, accountId);

            int result = ps.executeUpdate();

            System.out.println("===== 生年月日変更DB確認 =====");
            System.out.println("account_id：" + accountId);
            System.out.println("変更後の生年月日：" + birthDate);
            System.out.println("更新件数：" + result);
            System.out.println("==============================");

            return result > 0;

        } catch (SQLException e) {

            System.err.println("生年月日の変更に失敗しました。");
            e.printStackTrace();

            return false;
        }
    }
    // =====================================
    // 性別変更
    // =====================================

    public boolean updateGender(
            int accountId,
            String gender) {

        if (accountId <= 0 || isEmpty(gender)) {

            System.out.println("性別変更：入力値エラー");

            return false;
        }

        // 画面の日本語をDB用の値に変換
        String dbGender;

        switch (gender) {

            case "男性":
                dbGender = "male";
                break;

            case "女性":
                dbGender = "female";
                break;

            case "その他":
                dbGender = "other";
                break;

            case "回答しない":
                dbGender = "undisclosed";
                break;

            default:
                System.out.println(
                        "性別変更：不正な値：" + gender);

                return false;
        }

        String sql = "UPDATE personal_information "
                + "SET gender = ? "
                + "WHERE account_id = ?";

        try (
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dbGender);
            ps.setInt(2, accountId);

            int result = ps.executeUpdate();

            System.out.println("===== 性別変更DB確認 =====");
            System.out.println("account_id：" + accountId);
            System.out.println("画面から受信：" + gender);
            System.out.println("DB保存値：" + dbGender);
            System.out.println("更新件数：" + result);
            System.out.println("==========================");

            return result > 0;

        } catch (SQLException e) {

            System.err.println(
                    "性別の変更に失敗しました。");

            e.printStackTrace();

            return false;
        }
    }
    // =====================================
    // パスワード変更
    // =====================================

    // =====================================
    // パスワード変更
    // =====================================

    public boolean updatePassword(
            int accountId,
            String currentPassword,
            String newPassword) {

        if (accountId <= 0
                || isEmpty(currentPassword)
                || isEmpty(newPassword)) {

            System.out.println("パスワード変更：入力値エラー");
            return false;
        }

        String selectSql = "SELECT password_hash "
                + "FROM accounts "
                + "WHERE account_id = ?";

        String updateSql = "UPDATE accounts "
                + "SET password_hash = ? "
                + "WHERE account_id = ?";

        try (Connection con = getConnection()) {

            // =====================================
            // 現在のパスワードハッシュを取得
            // =====================================

            String currentHash = null;

            try (PreparedStatement ps = con.prepareStatement(selectSql)) {

                ps.setInt(1, accountId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        currentHash = rs.getString("password_hash");
                    }
                }
            }

            // =====================================
            // DBにパスワードが存在するか確認
            // =====================================

            if (currentHash == null
                    || currentHash.isEmpty()) {

                System.err.println(
                        "パスワード変更：DBにパスワードハッシュがありません。");

                System.err.println(
                        "account_id：" + accountId);

                return false;
            }

            // =====================================
            // 現在のパスワード確認
            // =====================================

            boolean passwordCorrect = BCrypt.checkpw(
                    currentPassword,
                    currentHash);

            System.out.println(
                    "===== パスワード確認 =====");

            System.out.println(
                    "account_id：" + accountId);

            System.out.println(
                    "現在のパスワード：" +
                            (passwordCorrect ? "一致" : "不一致"));

            System.out.println(
                    "==========================");

            if (!passwordCorrect) {

                return false;
            }

            // =====================================
            // 新しいパスワードをハッシュ化
            // =====================================

            String newPasswordHash = BCrypt.hashpw(
                    newPassword,
                    BCrypt.gensalt());

            // =====================================
            // DB更新
            // =====================================

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {

                ps.setString(1, newPasswordHash);
                ps.setInt(2, accountId);

                int result = ps.executeUpdate();

                System.out.println(
                        "===== パスワード変更DB確認 =====");

                System.out.println(
                        "account_id：" +
                                accountId);

                System.out.println(
                        "パスワード変更：" +
                                (result > 0 ? "成功" : "失敗"));

                System.out.println(
                        "更新件数：" +
                                result);

                System.out.println(
                        "=================================");

                return result > 0;
            }

        } catch (SQLException e) {

            System.err.println(
                    "パスワード変更時にSQLエラーが発生しました。");

            e.printStackTrace();

            return false;
        }
    }
    // =====================================
    // プロフィール保存
    // =====================================

    public boolean updateProfile(
            int accountId,
            String accountName,
            String selfIntroduction,
            String profileImage) {

        // 入力チェック
        if (accountId <= 0 || isEmpty(accountName)) {
            System.err.println("プロフィール保存：入力値エラー");
            return false;
        }

        // =====================================
        // アカウント名更新
        // =====================================

        String accountSql = "UPDATE accounts "
                + "SET account_name = ? "
                + "WHERE account_id = ?";

        // =====================================
        // プロフィール取得・更新用
        // =====================================

        String profileCheckSql = "SELECT account_id "
                + "FROM user_profile "
                + "WHERE account_id = ?";

        String profileInsertSql = "INSERT INTO user_profile "
                + "(account_id, self_introduction, profile_image) "
                + "VALUES (?, ?, ?)";

        String profileUpdateSql = "UPDATE user_profile "
                + "SET self_introduction = ?, "
                + "profile_image = ? "
                + "WHERE account_id = ?";
        try (Connection con = getConnection()) {

            // トランザクション開始
            con.setAutoCommit(false);

            try {

                // =====================================
                // ① accountsのアカウント名を更新
                // =====================================

                try (PreparedStatement ps = con.prepareStatement(accountSql)) {

                    ps.setString(
                            1,
                            accountName.trim());

                    ps.setInt(
                            2,
                            accountId);

                    ps.executeUpdate();
                }

                // =====================================
                // ② user_profileが存在するか確認
                // =====================================

                boolean profileExists = false;

                try (PreparedStatement ps = con.prepareStatement(profileCheckSql)) {

                    ps.setInt(
                            1,
                            accountId);

                    try (ResultSet rs = ps.executeQuery()) {

                        profileExists = rs.next();
                    }
                }

                // =====================================
                // ③ プロフィール登録・更新
                // =====================================

                if (profileExists) {

                    /*
                     * 画像が選択されている場合
                     */
                    if (profileImage != null) {

                        try (PreparedStatement ps = con.prepareStatement(profileUpdateSql)) {

                            ps.setString(
                                    1,
                                    selfIntroduction);

                            ps.setString(
                                    2,
                                    profileImage);

                            ps.setInt(
                                    3,
                                    accountId);

                            ps.executeUpdate();
                        }

                    } else {

                        /*
                         * 画像を変更していない場合
                         * 既存画像をそのまま残す
                         */
                        String sql = "UPDATE user_profile "
                                + "SET self_introduction = ? "
                                + "WHERE account_id = ?";

                        try (PreparedStatement ps = con.prepareStatement(sql)) {

                            ps.setString(
                                    1,
                                    selfIntroduction);

                            ps.setInt(
                                    2,
                                    accountId);

                            ps.executeUpdate();
                        }
                    }

                } else {

                    /*
                     * 初めてプロフィールを作る場合
                     */
                    try (PreparedStatement ps = con.prepareStatement(profileInsertSql)) {

                        ps.setInt(
                                1,
                                accountId);

                        ps.setString(
                                2,
                                selfIntroduction);

                        ps.setString(
                                3,
                                profileImage);

                        ps.executeUpdate();
                    }
                }

                // =====================================
                // 登録確定
                // =====================================

                con.commit();

                System.out.println(
                        "===== プロフィール保存成功 =====");

                System.out.println(
                        "account_id：" + accountId);

                System.out.println(
                        "アカウント名：" + accountName);

                System.out.println(
                        "自己紹介：" + selfIntroduction);

                System.out.println(
                        "================================");

                return true;

            } catch (Exception e) {

                // エラー時はロールバック

                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                System.err.println(
                        "プロフィール保存に失敗しました。");

                e.printStackTrace();

                return false;

            } finally {

                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "プロフィール保存時のDB接続に失敗しました。");

            e.printStackTrace();

            return false;
        }
    }

    
       // =====================================
    // 会員ランク変更
    // =====================================

    public boolean updateUserRank(
            int accountId,
            int userRank) {

        if (accountId <= 0
                || (userRank != 1 && userRank != 2)) {
            return false;
        }

        String sql =
                "UPDATE accounts "
                + "SET user_rank = ? "
                + "WHERE account_id = ?";

        try (
                Connection con = getConnection();
                PreparedStatement ps =
                        con.prepareStatement(sql)) {

            ps.setInt(1, userRank);
            ps.setInt(2, accountId);

            int result = ps.executeUpdate();

            System.out.println("===== 会員ランク変更 =====");
            System.out.println("account_id：" + accountId);
            System.out.println("変更後user_rank：" + userRank);
            System.out.println("更新件数：" + result);
            System.out.println("==========================");

            return result > 0;

        } catch (SQLException e) {

            System.err.println(
                    "会員ランク変更に失敗しました。");

            e.printStackTrace();

            return false;
        }
    }


    // =====================================
    // プロフィール取得
    // =====================================

    public String[] getProfile(int accountId) {

        if (accountId <= 0) {
            return null;
        }

        String sql = "SELECT "
                + "a.account_name, "
                + "p.self_introduction, "
                + "p.profile_image "
                + "FROM accounts a "
                + "LEFT JOIN user_profile p "
                + "ON a.account_id = p.account_id "
                + "WHERE a.account_id = ?";

        try (
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    accountId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new String[] {

                            rs.getString(
                                    "account_name"),

                            rs.getString(
                                    "self_introduction"),

                            rs.getString(
                                    "profile_image")
                    };
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "プロフィール取得に失敗しました。");

            e.printStackTrace();
        }

        return null;
    }
}