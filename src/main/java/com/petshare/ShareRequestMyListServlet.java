package com.petshare;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/share-request-my-list")
public class ShareRequestMyListServlet extends HttpServlet {

    private final ShareRequestDAO shareRequestDAO = new ShareRequestDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // ログイン確認
        if (session == null
                || session.getAttribute("loginUser") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"not_logged_in\"}");

            return;
        }

        try {

            // ログイン中のアカウントID取得
            int accountId = Integer.parseInt(
                    session.getAttribute("accountId").toString());

            // 自分が申し込んだリクエストを取得
            List<ShareRequest> requests = shareRequestDAO.findRequestsForBorrower(
                    accountId);

            StringBuilder json = new StringBuilder();

            json.append("[");

            for (int i = 0; i < requests.size(); i++) {

                ShareRequest r = requests.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{");

                json.append("\"requestId\":")
                        .append(r.getRequestId())
                        .append(",");

                json.append("\"petId\":")
                        .append(r.getPetId())
                        .append(",");

                json.append("\"borrowerId\":")
                        .append(r.getBorrowerId())
                        .append(",");

                json.append("\"startDatetime\":\"")
                        .append(r.getStartDatetime())
                        .append("\",");

                json.append("\"endDatetime\":\"")
                        .append(r.getEndDatetime())
                        .append("\",");

                json.append("\"usageHours\":")
                        .append(r.getUsageHours())
                        .append(",");

                json.append("\"sharePrice\":")
                        .append(r.getSharePrice())
                        .append(",");

                json.append("\"status\":\"")
                        .append(escapeJson(r.getStatus()))
                        .append("\",");

                json.append("\"createdAt\":\"")
                        .append(r.getCreatedAt())
                        .append("\"");

                json.append("}");
            }

            json.append("]");

            response.getWriter().write(
                    json.toString());

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            response.getWriter().write(
                    "{\"error\":\"system_error\"}");
        }
    }

    // JSON文字列用のエスケープ
    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}