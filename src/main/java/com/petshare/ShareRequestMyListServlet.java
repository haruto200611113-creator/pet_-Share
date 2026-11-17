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
public class ShareRequestMyListServlet extends HttpServlet {

    private final ShareRequestDAO shareRequestDAO = new ShareRequestDAO();
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");

        HttpSession session = request.getSession(false);

        // ログイン確認
        if (!AccessControl.isLoggedIn(session)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // ログイン中のアカウントID取得
        Object accountIdObj = session.getAttribute("accountId");

        if (accountIdObj == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int borrowerId;

        try {
            borrowerId = Integer.parseInt(accountIdObj.toString());
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {

            // 自分が申し込んだシェアリクエストを取得
            List<ShareRequest> requests = shareRequestDAO.findRequestsForBorrower(borrowerId);

            StringBuilder json = new StringBuilder();

            json.append("[");

            for (int i = 0; i < requests.size(); i++) {

                ShareRequest requestData = requests.get(i);

                /*
                 * この申込みのペット情報を取得
                 */
                Pet pet = petDAO.findById(requestData.getPetId());

                String petName = "";
                String petImage = "";

                if (pet != null) {
                    petName = pet.getPetName();
                    petImage = pet.getPetImage();
                }

                if (i > 0) {
                    json.append(",");
                }

                json.append("{");

                // 申込みID
                json.append("\"requestId\":")
                        .append(requestData.getRequestId())
                        .append(",");

                // ペットID
                json.append("\"petId\":")
                        .append(requestData.getPetId())
                        .append(",");

                // ペット名
                json.append("\"petName\":\"")
                        .append(escapeJson(petName))
                        .append("\",");

                // ペット画像
                json.append("\"petImage\":\"")
                        .append(escapeJson(petImage))
                        .append("\",");

                // 借りる人のID
                json.append("\"borrowerId\":")
                        .append(requestData.getBorrowerId())
                        .append(",");

                // 利用開始日時
                json.append("\"startDatetime\":\"")
                        .append(escapeJson(
                                String.valueOf(
                                        requestData.getStartDatetime())))
                        .append("\",");

                // 利用終了日時
                json.append("\"endDatetime\":\"")
                        .append(escapeJson(
                                String.valueOf(
                                        requestData.getEndDatetime())))
                        .append("\",");

                // 利用時間
                json.append("\"usageHours\":")
                        .append(requestData.getUsageHours())
                        .append(",");

                // シェア料金
                json.append("\"sharePrice\":")
                        .append(requestData.getSharePrice())
                        .append(",");

                // ステータス
                json.append("\"status\":\"")
                        .append(escapeJson(requestData.getStatus()))
                        .append("\",");

                // 申込み日時
                json.append("\"createdAt\":\"")
                        .append(escapeJson(
                                String.valueOf(
                                        requestData.getCreatedAt())))
                        .append("\"");

                json.append("}");
            }

            json.append("]");

            PrintWriter out = response.getWriter();
            out.print(json.toString());
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

    /**
     * JSON用エスケープ処理
     */
    private String escapeJson(String value) {

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