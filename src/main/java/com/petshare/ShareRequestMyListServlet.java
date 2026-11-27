package com.petshare;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/share-request-my-list")
public class ShareRequestMyListServlet
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ShareRequestDAO shareRequestDAO = new ShareRequestDAO();

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(
                "application/json; charset=UTF-8");

        response.setCharacterEncoding(
                "UTF-8");

        HttpSession session = request.getSession(false);

        // ==========================================
        // ログイン確認
        // ==========================================

        if (!AccessControl.isLoggedIn(
                session)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        // ==========================================
        // ログイン中のアカウントID取得
        // ==========================================

        Object accountIdObj = session.getAttribute(
                "accountId");

        if (accountIdObj == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        int borrowerId;

        try {

            borrowerId = Integer.parseInt(
                    accountIdObj.toString());

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        try {

            // ==========================================
            // 自分が申し込んだシェアリクエストを取得
            // ==========================================

            List<ShareRequest> requests = shareRequestDAO
                    .findRequestsForBorrower(
                            borrowerId);

            StringBuilder json = new StringBuilder();

            json.append("[");

            for (int i = 0; i < requests.size(); i++) {

                ShareRequest requestData = requests.get(i);

                // ==========================================
                // 飼い主名取得
                // ==========================================

                String ownerName = "";

                Integer ownerAccountId = requestData.getOwnerId();

                if (ownerAccountId != null
                        && ownerAccountId > 0) {

                    User owner = userDAO.findById(
                            ownerAccountId);

                    if (owner != null) {

                        // ----------------------------------
                        // account_nameを優先
                        // ----------------------------------

                        if (owner.getAccountName() != null
                                && !owner
                                        .getAccountName()
                                        .trim()
                                        .isEmpty()) {

                            ownerName = owner.getAccountName();

                        } else {

                            // ----------------------------------
                            // 姓＋名
                            // ----------------------------------

                            String lastName = owner.getLastName();

                            String firstName = owner.getFirstName();

                            StringBuilder name = new StringBuilder();

                            if (lastName != null
                                    && !lastName
                                            .trim()
                                            .isEmpty()) {

                                name.append(
                                        lastName.trim());
                            }

                            if (firstName != null
                                    && !firstName
                                            .trim()
                                            .isEmpty()) {

                                if (name.length() > 0) {

                                    name.append(
                                            " ");
                                }

                                name.append(
                                        firstName
                                                .trim());
                            }

                            ownerName = name.toString();
                        }
                    }
                }

                // ==========================================
                // 飼い主名が取得できなかった場合
                // ==========================================

                if (ownerName == null
                        || ownerName
                                .trim()
                                .isEmpty()) {

                    ownerName = "飼い主情報なし";
                }

                // ==========================================
                // JSON区切り
                // ==========================================

                if (i > 0) {

                    json.append(",");
                }

                json.append("{");

                // ==========================================
                // 申込みID
                // ==========================================

                json.append(
                        "\"requestId\":")
                        .append(
                                requestData
                                        .getRequestId())
                        .append(",");

                // ==========================================
                // ペットID
                // ==========================================

                json.append(
                        "\"petId\":")
                        .append(
                                requestData
                                        .getPetId())
                        .append(",");

                // ==========================================
                // ★ シェア申込み時点のペット名
                // ==========================================

                json.append(
                        "\"petName\":\"")
                        .append(
                                escapeJson(
                                        requestData
                                                .getPetName()))
                        .append("\",");

                // ==========================================
                // ★ シェア申込み時点のペット画像
                // ==========================================

                json.append(
                        "\"petImage\":\"")
                        .append(
                                escapeJson(
                                        requestData
                                                .getPetImage()))
                        .append("\",");

                // ==========================================
                // ★ ペット種類
                // ==========================================

                json.append(
                        "\"petType\":\"")
                        .append(
                                escapeJson(
                                        requestData
                                                .getPetType()))
                        .append("\",");

                // ==========================================
                // ★ ペット性別
                // ==========================================

                json.append(
                        "\"petGender\":\"")
                        .append(
                                escapeJson(
                                        requestData
                                                .getPetGender()))
                        .append("\",");

                // ==========================================
                // 借りる人のID
                // ==========================================

                json.append(
                        "\"borrowerId\":")
                        .append(
                                requestData
                                        .getBorrowerId())
                        .append(",");

                // ==========================================
                // 飼い主名
                // ==========================================

                json.append(
                        "\"ownerName\":\"")
                        .append(
                                escapeJson(
                                        ownerName))
                        .append("\",");

                // ==========================================
                // 利用開始日時
                // ==========================================

                json.append(
                        "\"startDatetime\":\"")
                        .append(
                                escapeJson(
                                        String.valueOf(
                                                requestData
                                                        .getStartDatetime())))
                        .append("\",");

                // ==========================================
                // 利用終了日時
                // ==========================================

                json.append(
                        "\"endDatetime\":\"")
                        .append(
                                escapeJson(
                                        String.valueOf(
                                                requestData
                                                        .getEndDatetime())))
                        .append("\",");

                // ==========================================
                // 利用時間
                // ==========================================

                json.append(
                        "\"usageHours\":")
                        .append(
                                requestData
                                        .getUsageHours())
                        .append(",");

                // ==========================================
                // シェア料金
                // ==========================================

                json.append(
                        "\"sharePrice\":")
                        .append(
                                requestData
                                        .getSharePrice())
                        .append(",");

                // ==========================================
                // ステータス
                // ==========================================

                json.append(
                        "\"status\":\"")
                        .append(
                                escapeJson(
                                        requestData
                                                .getStatus()))
                        .append("\",");

                // ==========================================
                // 申込み日時
                // ==========================================

                json.append(
                        "\"createdAt\":\"")
                        .append(
                                escapeJson(
                                        String.valueOf(
                                                requestData
                                                        .getCreatedAt())))
                        .append("\"");

                json.append("}");
            }

            json.append("]");

            // ==========================================
            // JSONを返す
            // ==========================================

            PrintWriter out = response.getWriter();

            out.print(
                    json.toString());

            out.flush();

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            PrintWriter out = response.getWriter();

            out.print(
                    "{\"error\":\"system_error\"}");

            out.flush();
        }
    }

    // ==========================================
    // JSON用エスケープ処理
    // ==========================================

    private String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }

        return value
                .replace(
                        "\\",
                        "\\\\")
                .replace(
                        "\"",
                        "\\\"")
                .replace(
                        "\r",
                        "\\r")
                .replace(
                        "\n",
                        "\\n")
                .replace(
                        "\t",
                        "\\t");
    }
}