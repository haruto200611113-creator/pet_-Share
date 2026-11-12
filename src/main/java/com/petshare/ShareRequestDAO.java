package com.petshare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ShareRequestDAO {

    /**
     * シェア申請を登録する
     */
    public boolean insert(ShareRequest request) {

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

            ps.setInt(1, request.getPetId());
            ps.setInt(2, request.getBorrowerId());
            ps.setTimestamp(3, request.getStartDatetime());
            ps.setTimestamp(4, request.getEndDatetime());
            ps.setDouble(5, request.getUsageHours());
            ps.setInt(6, request.getSharePrice());
            ps.setString(7, request.getStatus());

            int result = ps.executeUpdate();

            return result == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * シェア申請IDから取得する
     */
    public ShareRequest findById(int requestId) {

        String sql = """
                    SELECT *
                    FROM share_requests
                    WHERE request_id = ?
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    ShareRequest request = new ShareRequest();

                    request.setRequestId(
                            rs.getInt("request_id"));

                    request.setPetId(
                            rs.getInt("pet_id"));

                    request.setBorrowerId(
                            rs.getInt("borrower_id"));

                    request.setStartDatetime(
                            rs.getTimestamp("start_datetime"));

                    request.setEndDatetime(
                            rs.getTimestamp("end_datetime"));

                    request.setUsageHours(
                            rs.getDouble("usage_hours"));

                    request.setSharePrice(
                            rs.getInt("share_price"));

                    request.setStatus(
                            rs.getString("status"));

                    request.setCreatedAt(
                            rs.getTimestamp("created_at"));

                    return request;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 指定したペットの予約時間が重複しているか確認する
     *
     * 既存予約：
     * 10:00 ～ 15:00
     *
     * 新しい申請：
     * 13:00 ～ 18:00
     *
     * → 重複あり
     */
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

            ps.setInt(1, petId);
            ps.setTimestamp(2, endDatetime);
            ps.setTimestamp(3, startDatetime);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * DB接続
     *
     * UserDAOなどで使用している
     * DB接続処理に合わせてください。
     */
    private Connection getConnection() throws SQLException {

        return java.sql.DriverManager.getConnection(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD"));
    }
}