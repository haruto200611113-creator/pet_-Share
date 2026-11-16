package com.petshare;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class ShareRequestDAO {

    // ========================================
    // Aiven MySQL 接続
    // ========================================

    private Connection getConnection() throws SQLException {

        try {

            // ----------------------------------------
            // 環境変数取得
            // ----------------------------------------

            String host = System.getenv("AIVEN_DB_HOST");

            String port = System.getenv("AIVEN_DB_PORT");

            String database = System.getenv("AIVEN_DB_NAME");

            String user = System.getenv("AIVEN_DB_USER");

            String password = System.getenv("AIVEN_DB_PASSWORD");

            String truststoreBase64 = System.getenv("AIVEN_TRUSTSTORE_BASE64");

            String truststorePassword = System.getenv("AIVEN_TRUSTSTORE_PASSWORD");

            // ----------------------------------------
            // 環境変数確認
            // ----------------------------------------

            System.out.println("==============================");
            System.out.println("ShareRequestDAO Aiven接続開始");
            System.out.println("host     = " + host);
            System.out.println("port     = " + port);
            System.out.println("database = " + database);
            System.out.println("user     = " + user);

            System.out.println(
                    "password = "
                            + (password != null
                                    ? "設定済み"
                                    : "null"));

            System.out.println(
                    "truststoreBase64 = "
                            + (truststoreBase64 != null
                                    ? "設定済み"
                                    : "null"));

            System.out.println(
                    "truststorePassword = "
                            + (truststorePassword != null
                                    ? "設定済み"
                                    : "null"));

            System.out.println("==============================");

            // ----------------------------------------
            // 必須環境変数チェック
            // ----------------------------------------

            if (host == null || host.isBlank()) {

                throw new SQLException(
                        "AIVEN_DB_HOST が設定されていません。");
            }

            if (port == null || port.isBlank()) {

                throw new SQLException(
                        "AIVEN_DB_PORT が設定されていません。");
            }

            if (database == null || database.isBlank()) {

                throw new SQLException(
                        "AIVEN_DB_NAME が設定されていません。");
            }

            if (user == null || user.isBlank()) {

                throw new SQLException(
                        "AIVEN_DB_USER が設定されていません。");
            }

            if (password == null || password.isBlank()) {

                throw new SQLException(
                        "AIVEN_DB_PASSWORD が設定されていません。");
            }

            if (truststoreBase64 == null
                    || truststoreBase64.isBlank()) {

                throw new SQLException(
                        "AIVEN_TRUSTSTORE_BASE64 が設定されていません。");
            }

            if (truststorePassword == null
                    || truststorePassword.isBlank()) {

                throw new SQLException(
                        "AIVEN_TRUSTSTORE_PASSWORD が設定されていません。");
            }

            // ----------------------------------------
            // Truststoreファイル作成
            // ----------------------------------------

            String truststorePath = "/tmp/aiven-truststore-share-request.p12";

            File truststoreFile = new File(truststorePath);

            if (!truststoreFile.exists()) {

                byte[] decoded = Base64.getDecoder()
                        .decode(truststoreBase64);

                try (
                        FileOutputStream fos = new FileOutputStream(
                                truststoreFile)) {

                    fos.write(decoded);
                }
            }

            // ----------------------------------------
            // JDBC URL作成
            // ----------------------------------------

            String url = "jdbc:mysql://"
                    + host
                    + ":"
                    + port
                    + "/"
                    + database
                    + "?sslMode=VERIFY_CA"
                    + "&trustCertificateKeyStoreUrl=file:"
                    + truststorePath
                    + "&trustCertificateKeyStoreType=PKCS12"
                    + "&trustCertificateKeyStorePassword="
                    + truststorePassword;

            System.out.println(
                    "Aiven JDBC URL作成完了");

            // パスワードそのものはログに出さない
            System.out.println(
                    "Aiven DBへ接続します");

            // ----------------------------------------
            // DB接続
            // ----------------------------------------

            Connection connection = DriverManager.getConnection(
                    url,
                    user,
                    password);

            System.out.println(
                    "Aiven DB接続成功");

            return connection;

        } catch (Exception e) {

            System.err.println(
                    "================================");

            System.err.println(
                    "Aiven MySQLへの接続に失敗しました。");

            System.err.println(
                    "================================");

            e.printStackTrace();

            throw new SQLException(
                    "Aiven MySQLへの接続に失敗しました。",
                    e);
        }
    }

    // ========================================
    // 申込み登録
    // ========================================

    public boolean insert(
            ShareRequest request) {

        String sql = """
                INSERT INTO share_requests
                (
                    pet_id,
                    borrower_id,
                    start_datetime,
                    end_datetime,
                    usage_hours,
                    share_price,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = getConnection();

                PreparedStatement ps = conn.prepareStatement(sql)) {

            // ----------------------------------------
            // パラメータ設定
            // ----------------------------------------

            ps.setInt(
                    1,
                    request.getPetId());

            ps.setInt(
                    2,
                    request.getBorrowerId());

            ps.setTimestamp(
                    3,
                    request.getStartDatetime());

            ps.setTimestamp(
                    4,
                    request.getEndDatetime());

            ps.setDouble(
                    5,
                    request.getUsageHours());

            ps.setInt(
                    6,
                    request.getSharePrice());

            ps.setString(
                    7,
                    request.getStatus());

            // ----------------------------------------
            // INSERT実行
            // ----------------------------------------

            int result = ps.executeUpdate();

            System.out.println(
                    "share_requests INSERT結果 = "
                            + result);

            return result == 1;

        } catch (SQLException e) {

            System.err.println(
                    "================================");

            System.err.println(
                    "シェア申込み登録中に"
                            + "SQLエラーが発生しました。");

            System.err.println(
                    "================================");

            e.printStackTrace();

            return false;
        }
    }

    // ========================================
    // ID検索
    // ========================================

    public ShareRequest findById(
            int requestId) {

        String sql = """
                SELECT
                    request_id,
                    pet_id,
                    borrower_id,
                    start_datetime,
                    end_datetime,
                    usage_hours,
                    share_price,
                    status,
                    created_at
                FROM share_requests
                WHERE request_id = ?
                """;

        try (
                Connection conn = getConnection();

                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    requestId);

            try (
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    ShareRequest request = new ShareRequest();

                    request.setRequestId(
                            rs.getInt(
                                    "request_id"));

                    request.setPetId(
                            rs.getInt(
                                    "pet_id"));

                    request.setBorrowerId(
                            rs.getInt(
                                    "borrower_id"));

                    request.setStartDatetime(
                            rs.getTimestamp(
                                    "start_datetime"));

                    request.setEndDatetime(
                            rs.getTimestamp(
                                    "end_datetime"));

                    request.setUsageHours(
                            rs.getDouble(
                                    "usage_hours"));

                    request.setSharePrice(
                            rs.getInt(
                                    "share_price"));

                    request.setStatus(
                            rs.getString(
                                    "status"));

                    request.setCreatedAt(
                            rs.getTimestamp(
                                    "created_at"));

                    return request;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "シェア申込み取得中に"
                            + "SQLエラーが発生しました。");

            e.printStackTrace();
        }

        return null;
    }

    // ========================================
    // 重複申込みチェック
    // ========================================

    public boolean existsOverlappingRequest(
            int petId,
            java.time.LocalDateTime startDatetime,
            java.time.LocalDateTime endDatetime) {

        String sql = """
                SELECT COUNT(*)
                FROM share_requests
                WHERE pet_id = ?
                  AND status IN ('pending', 'approved')
                  AND start_datetime < ?
                  AND end_datetime > ?
                """;

        try (
                Connection conn = getConnection();

                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    petId);

            /*
             * 重複判定
             *
             * 既存申込みの開始 < 新しい申込みの終了
             * かつ
             * 既存申込みの終了 > 新しい申込みの開始
             *
             * の場合、時間が重複している。
             */

            ps.setTimestamp(
                    2,
                    java.sql.Timestamp.valueOf(
                            endDatetime));

            ps.setTimestamp(
                    3,
                    java.sql.Timestamp.valueOf(
                            startDatetime));

            try (
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int count = rs.getInt(1);

                    System.out.println(
                            "重複申込み件数 = "
                                    + count);

                    return count > 0;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "================================");

            System.err.println(
                    "重複申込みチェックで"
                            + "SQLエラーが発生しました。");

            System.err.println(
                    "================================");

            e.printStackTrace();
        }

        return false;
    }
}