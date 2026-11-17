package com.petshare;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ShareRequestDAO {

    // ========================================
    // Aiven MySQL 接続
    // ========================================

    private Connection getConnection() throws SQLException {

        try {

            String truststoreBase64 = System.getenv("AIVEN_TRUSTSTORE_BASE64");

            if (truststoreBase64 == null
                    || truststoreBase64.isBlank()) {

                throw new SQLException(
                        "AIVEN_TRUSTSTORE_BASE64 が設定されていません。");
            }

            String truststorePath = "/tmp/aiven-truststore-share-request.p12";

            File truststoreFile = new File(truststorePath);

            if (!truststoreFile.exists()) {

                byte[] decoded = Base64.getDecoder().decode(
                        truststoreBase64);

                try (FileOutputStream fos = new FileOutputStream(truststoreFile)) {

                    fos.write(decoded);
                }
            }

            String host = System.getenv("AIVEN_DB_HOST");

            String port = System.getenv("AIVEN_DB_PORT");

            String database = System.getenv("AIVEN_DB_NAME");

            String user = System.getenv("AIVEN_DB_USER");

            String password = System.getenv("AIVEN_DB_PASSWORD");

            String truststorePassword = System.getenv(
                    "AIVEN_TRUSTSTORE_PASSWORD");

            String url = "jdbc:mysql://" +
                    host + ":" +
                    port + "/" +
                    database +
                    "?sslMode=VERIFY_CA" +
                    "&trustCertificateKeyStoreUrl=file:" +
                    truststorePath +
                    "&trustCertificateKeyStoreType=PKCS12" +
                    "&trustCertificateKeyStorePassword=" +
                    truststorePassword;

            return DriverManager.getConnection(
                    url,
                    user,
                    password);

        } catch (Exception e) {

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

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            System.err.println(
                    "シェア申込み登録中にSQLエラーが発生しました。");

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

                    return createShareRequest(rs);
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "シェア申込み取得中にSQLエラーが発生しました。");

            e.printStackTrace();
        }

        return null;
    }

    // ========================================
    // 重複申込みチェック
    // ========================================

    public boolean existsOverlappingRequest(
            int petId,
            Timestamp startDatetime,
            Timestamp endDatetime) {

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

            ps.setTimestamp(
                    2,
                    endDatetime);

            ps.setTimestamp(
                    3,
                    startDatetime);

            try (
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "重複申込み確認中にSQLエラーが発生しました。");

            e.printStackTrace();
        }

        return false;
    }

    // ========================================
    // 所有者のリクエスト一覧取得
    // ========================================

    public List<ShareRequest> findRequestsForOwner(
            int ownerAccountId) {

        List<ShareRequest> list = new ArrayList<>();

        String sql = """
                SELECT
                    sr.request_id,
                    sr.pet_id,
                    sr.borrower_id,
                    sr.start_datetime,
                    sr.end_datetime,
                    sr.usage_hours,
                    sr.share_price,
                    sr.status,
                    sr.created_at
                FROM share_requests sr
                INNER JOIN pets p
                    ON sr.pet_id = p.pet_id
                WHERE p.account_id = ?
                AND sr.status <> 'rejected'
                ORDER BY sr.created_at DESC
                """;

        try (
                Connection conn = getConnection();

                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    ownerAccountId);

            try (
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(
                            createShareRequest(rs));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "所有者のシェアリクエスト取得中にSQLエラーが発生しました。");

            e.printStackTrace();
        }

        return list;
    }
    // ========================================
    // 借りる側のリクエスト一覧取得
    // ========================================

    public List<ShareRequest> findRequestsForBorrower(
            int borrowerAccountId) {

        List<ShareRequest> list = new ArrayList<>();

        String sql = """
                SELECT
                    sr.request_id,
                    sr.pet_id,
                    sr.borrower_id,
                    sr.start_datetime,
                    sr.end_datetime,
                    sr.usage_hours,
                    sr.share_price,
                    sr.status,
                    sr.created_at
                FROM share_requests sr
                INNER JOIN pets p
                    ON sr.pet_id = p.pet_id
                WHERE sr.borrower_id = ?
                ORDER BY sr.created_at DESC
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    borrowerAccountId);

            try (
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(
                            createShareRequest(rs));
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "借りる側のシェアリクエスト取得中にSQLエラーが発生しました。");

            e.printStackTrace();
        }

        return list;
    }

    // ========================================
    // リクエストの所有者ID取得
    // ========================================

    public int findOwnerAccountId(
            int requestId) {

        String sql = """
                SELECT
                    p.account_id
                FROM share_requests sr
                INNER JOIN pets p
                    ON sr.pet_id = p.pet_id
                WHERE sr.request_id = ?
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

                    return rs.getInt(
                            "account_id");
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "シェアリクエスト所有者取得中にSQLエラーが発生しました。");

            e.printStackTrace();
        }

        return -1;
    }

    // ========================================
    // リクエストのステータス更新
    // ========================================

    public boolean updateStatus(
            int requestId,
            int ownerAccountId,
            String status) {

        // 承認・拒否以外は更新不可
        if (!"approved".equals(status)
                && !"rejected".equals(status)) {

            return false;
        }

        String sql = """
                UPDATE share_requests sr
                INNER JOIN pets p
                    ON sr.pet_id = p.pet_id
                SET sr.status = ?
                WHERE sr.request_id = ?
                  AND p.account_id = ?
                  AND sr.status = 'pending'
                """;

        try (
                Connection conn = getConnection();

                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(
                    1,
                    status);

            ps.setInt(
                    2,
                    requestId);

            ps.setInt(
                    3,
                    ownerAccountId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            System.err.println(
                    "シェアリクエストのステータス更新中にSQLエラーが発生しました。");

            e.printStackTrace();

            return false;
        }
    }

    // ========================================
    // ResultSet → ShareRequest
    // ========================================

    private ShareRequest createShareRequest(
            ResultSet rs)
            throws SQLException {

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