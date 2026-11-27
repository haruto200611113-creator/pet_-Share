package com.petshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class PetShareDAO {

    // =====================================
    // Aiven MySQL接続設定
    // =====================================

    private static final String TRUSTSTORE_PASSWORD = System.getenv("AIVEN_TRUSTSTORE_PASSWORD");

    private static final String TRUSTSTORE_BASE64 = System.getenv("AIVEN_TRUSTSTORE_BASE64");

    private static final String TRUSTSTORE_PATH = createTrustStoreFile();

    private static final String URL = "jdbc:mysql://mysql-1589c60e-petshare-project.f.aivencloud.com:21440/defaultdb"
            + "?sslMode=VERIFY_CA"
            + "&trustCertificateKeyStoreUrl=file:"
            + TRUSTSTORE_PATH
            + "&trustCertificateKeyStoreType=PKCS12"
            + "&trustCertificateKeyStorePassword="
            + TRUSTSTORE_PASSWORD;

    private static final String USER = System.getenv("AIVEN_DB_USER");

    private static final String PASSWORD = System.getenv("AIVEN_DB_PASSWORD");

    // =====================================
    // TrustStore復元
    // =====================================

    private static String createTrustStoreFile() {

        if (TRUSTSTORE_BASE64 == null
                || TRUSTSTORE_BASE64.isBlank()) {

            throw new ExceptionInInitializerError(
                    "AIVEN_TRUSTSTORE_BASE64 が設定されていません");
        }

        try {

            byte[] trustStoreBytes = Base64.getMimeDecoder().decode(
                    TRUSTSTORE_BASE64);

            Path path = Path.of("/tmp/aiven-truststore-render.p12");

            Files.write(
                    path,
                    trustStoreBytes);

            return path.toAbsolutePath().toString();

        } catch (Exception e) {

            throw new ExceptionInInitializerError(
                    "TrustStoreの復元に失敗しました: "
                            + e.getMessage());
        }
    }

    // =====================================
    // DB接続
    // =====================================

    private Connection getConnection()
            throws SQLException {

        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                    "MySQL JDBCドライバーの読み込みに失敗しました。",
                    e);
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD);
    }

    // =====================================
    // シェア登録
    // =====================================

    public boolean insert(PetShare share) {

        String sql = """
                INSERT INTO pet_shares
                (
                    pet_id,
                    pet_name,
                    pet_image,
                    pet_type,
                    pet_gender,
                    owner_id,
                    share_price,
                    available_start,
                    available_end,
                    is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // -----------------------------
            // ペットID
            // -----------------------------

            ps.setInt(
                    1,
                    share.getPetId());

            // -----------------------------
            // ペット名
            // -----------------------------

            ps.setString(
                    2,
                    share.getPetName());

            // -----------------------------
            // ペット画像
            // -----------------------------

            ps.setString(
                    3,
                    share.getPetImage());

            // -----------------------------
            // ペット種類
            // -----------------------------

            ps.setString(
                    4,
                    share.getPetType());

            // -----------------------------
            // 性別
            // -----------------------------

            ps.setString(
                    5,
                    share.getPetGender());

            // -----------------------------
            // 飼い主ID
            // -----------------------------

            if (share.getOwnerId() == null) {

                ps.setNull(
                        6,
                        java.sql.Types.INTEGER);

            } else {

                ps.setInt(
                        6,
                        share.getOwnerId());
            }

            // -----------------------------
            // シェア料金
            // -----------------------------

            if (share.getSharePrice() == null) {

                ps.setNull(
                        7,
                        java.sql.Types.INTEGER);

            } else {

                ps.setInt(
                        7,
                        share.getSharePrice());
            }

            // -----------------------------
            // シェア開始日時
            // -----------------------------

            ps.setTimestamp(
                    8,
                    share.getAvailableStart());

            // -----------------------------
            // シェア終了日時
            // -----------------------------

            ps.setTimestamp(
                    9,
                    share.getAvailableEnd());

            // -----------------------------
            // 有効状態
            // -----------------------------

            ps.setBoolean(
                    10,
                    share.isActive());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }

    // =====================================
    // 有効なシェア情報取得
    // =====================================

    public PetShare findActiveByPetId(
            int petId) {

        String sql = """
                SELECT *
                FROM pet_shares
                WHERE pet_id = ?
                  AND is_active = TRUE
                ORDER BY share_id DESC
                LIMIT 1
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    petId);

            try (
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    PetShare share = new PetShare();

                    // -----------------------------
                    // シェアID
                    // -----------------------------

                    share.setShareId(
                            rs.getInt(
                                    "share_id"));

                    // -----------------------------
                    // ペットID
                    // -----------------------------

                    share.setPetId(
                            rs.getInt(
                                    "pet_id"));

                    // -----------------------------
                    // ペット情報
                    // -----------------------------

                    share.setPetName(
                            rs.getString(
                                    "pet_name"));

                    share.setPetImage(
                            rs.getString(
                                    "pet_image"));

                    share.setPetType(
                            rs.getString(
                                    "pet_type"));

                    share.setPetGender(
                            rs.getString(
                                    "pet_gender"));

                    // -----------------------------
                    // 飼い主ID
                    // -----------------------------

                    Integer ownerId = (Integer) rs.getObject(
                            "owner_id");

                    share.setOwnerId(
                            ownerId);

                    // -----------------------------
                    // シェア料金
                    // -----------------------------

                    Integer sharePrice = (Integer) rs.getObject(
                            "share_price");

                    share.setSharePrice(
                            sharePrice);

                    // -----------------------------
                    // シェア期間
                    // -----------------------------

                    share.setAvailableStart(
                            rs.getTimestamp(
                                    "available_start"));

                    share.setAvailableEnd(
                            rs.getTimestamp(
                                    "available_end"));

                    // -----------------------------
                    // 有効状態
                    // -----------------------------

                    share.setActive(
                            rs.getBoolean(
                                    "is_active"));

                    // -----------------------------
                    // 管理情報
                    // -----------------------------

                    share.setCreatedAt(
                            rs.getTimestamp(
                                    "created_at"));

                    share.setUpdatedAt(
                            rs.getTimestamp(
                                    "updated_at"));

                    return share;
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }

    // ========================================
    // シェアを停止する
    // ========================================

    public boolean stopShare(int petId) {

        String sql = """
                UPDATE pet_shares
                SET is_active = FALSE
                WHERE pet_id = ?
                  AND is_active = TRUE
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    petId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "シェア停止中にSQLエラーが発生しました。");

            e.printStackTrace();

            return false;
        }
    }

    // ========================================
    // 期限切れシェアを停止する
    // ========================================

    public int deactivateExpiredShares() {

        String sql = """
                UPDATE pet_shares
                SET is_active = FALSE
                WHERE is_active = TRUE
                  AND available_end <= NOW()
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            return ps.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "期限切れシェアの停止中にSQLエラーが発生しました。");

            e.printStackTrace();

            return 0;
        }
    }
}