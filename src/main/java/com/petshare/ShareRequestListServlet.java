package com.petshare;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/share-request-list")
public class ShareRequestListServlet
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ShareRequestDAO shareRequestDAO = new ShareRequestDAO();

    private final PetDAO petDAO = new PetDAO();

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // ========================================
        // セッション確認
        // ========================================

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("loginUser") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType(
                    "application/json; charset=UTF-8");

            response.setCharacterEncoding(
                    "UTF-8");

            response.getWriter().write(
                    "{\"error\":\"ログインしてください\"}");

            return;
        }

        // ========================================
        // account_id取得
        // ========================================

        Object accountIdObject = session.getAttribute("accountId");

        if (accountIdObject == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType(
                    "application/json; charset=UTF-8");

            response.setCharacterEncoding(
                    "UTF-8");

            response.getWriter().write(
                    "{\"error\":\"アカウント情報を取得できません\"}");

            return;
        }

        int ownerAccountId = Integer.parseInt(
                accountIdObject.toString());

        // ========================================
        // JSON設定
        // ========================================

        response.setContentType(
                "application/json; charset=UTF-8");

        response.setCharacterEncoding(
                "UTF-8");

        PrintWriter out = response.getWriter();

        try {

            // ========================================
            // 自分のペットへの申込み取得
            // ========================================

            List<ShareRequest> requests = shareRequestDAO.findRequestsForOwner(
                    ownerAccountId);

            out.print("[");

            int outputCount = 0;

            // ========================================
            // リクエスト処理
            // ========================================

            for (ShareRequest shareRequest : requests) {

                // ====================================
                // ペット取得
                // ====================================

                Pet pet = petDAO.findById(
                        shareRequest.getPetId());

                if (pet == null) {
                    continue;
                }

                // ====================================
                // 申込者名取得
                // ====================================

                String borrowerName = "名前未設定";

                String[] profile = userDAO.getProfile(
                        shareRequest.getBorrowerId());

                if (profile != null
                        && profile.length > 0
                        && profile[0] != null
                        && !profile[0].isBlank()) {

                    borrowerName = profile[0];
                }

                // ====================================
                // カンマ
                // ====================================

                if (outputCount > 0) {
                    out.print(",");
                }

                outputCount++;

                // ====================================
                // JSON開始
                // ====================================

                out.print("{");

                // ====================================
                // requestId
                // ====================================

                out.print(
                        "\"requestId\":" +
                                shareRequest.getRequestId()
                                + ",");

                // ====================================
                // petId
                // ====================================

                out.print(
                        "\"petId\":" +
                                shareRequest.getPetId()
                                + ",");

                // ====================================
                // petName
                // ====================================

                out.print(
                        "\"petName\":\"" +
                                escapeJson(
                                        pet.getPetName())
                                + "\",");

                // ====================================
                // ★ petImage
                // ====================================

                String petImage = pet.getPetImage();

                if (petImage == null) {
                    petImage = "";
                }

                out.print(
                        "\"petImage\":\"" +
                                escapeJson(
                                        petImage)
                                + "\",");

                // ====================================
                // borrowerId
                // ====================================

                out.print(
                        "\"borrowerId\":" +
                                shareRequest
                                        .getBorrowerId()
                                + ",");

                // ====================================
                // borrowerName
                // ====================================

                out.print(
                        "\"borrowerName\":\"" +
                                escapeJson(
                                        borrowerName)
                                + "\",");

                // ====================================
                // startDatetime
                // ====================================

                out.print(
                        "\"startDatetime\":\"" +
                                formatTimestamp(
                                        shareRequest
                                                .getStartDatetime())
                                + "\",");

                // ====================================
                // endDatetime
                // ====================================

                out.print(
                        "\"endDatetime\":\"" +
                                formatTimestamp(
                                        shareRequest
                                                .getEndDatetime())
                                + "\",");

                // ====================================
                // usageHours
                // ====================================

                out.print(
                        "\"usageHours\":" +
                                shareRequest
                                        .getUsageHours()
                                + ",");

                // ====================================
                // status
                // ====================================

                out.print(
                        "\"status\":\"" +
                                escapeJson(
                                        shareRequest
                                                .getStatus())
                                + "\",");

                // ====================================
                // createdAt
                // ====================================

                out.print(
                        "\"createdAt\":\"" +
                                formatTimestamp(
                                        shareRequest
                                                .getCreatedAt())
                                + "\"");

                // ====================================
                // JSON終了
                // ====================================

                out.print("}");
            }

            out.print("]");

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {

            out.flush();
        }
    }

    // ========================================
    // Timestamp → 文字列
    // ========================================

    private String formatTimestamp(
            Timestamp timestamp) {

        if (timestamp == null) {
            return "";
        }

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm");

        return format.format(timestamp);
    }

    // ========================================
    // JSONエスケープ
    // ========================================

    private String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}