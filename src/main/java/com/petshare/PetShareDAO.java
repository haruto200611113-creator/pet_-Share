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

            Files.write(path, trustStoreBytes);

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
                    available_start,
                    available_end,
                    is_active
                )
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    share.getPetId());

            ps.setTimestamp(
                    2,
                    share.getAvailableStart());

            ps.setTimestamp(
                    3,
                    share.getAvailableEnd());

            ps.setBoolean(
                    4,
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

            ps.setInt(1, petId);

            try (
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    PetShare share = new PetShare();

                    share.setShareId(
                            rs.getInt(
                                    "share_id"));

                    share.setPetId(
                            rs.getInt(
                                    "pet_id"));

                    share.setAvailableStart(
                            rs.getTimestamp(
                                    "available_start"));

                    share.setAvailableEnd(
                            rs.getTimestamp(
                                    "available_end"));

                    share.setActive(
                            rs.getBoolean(
                                    "is_active"));

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
}