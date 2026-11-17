package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/share-request-status")
public class ShareRequestStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ShareRequestDAO shareRequestDAO = new ShareRequestDAO();

    @Override
    protected void doPost(
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

            response.getWriter().write(
                    "{\"success\":false,\"message\":\"ログインしてください\"}");

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

            response.getWriter().write(
                    "{\"success\":false,\"message\":\"アカウント情報を取得できません\"}");

            return;
        }

        int ownerAccountId = Integer.parseInt(
                accountIdObject.toString());

        // ========================================
        // リクエストパラメータ取得
        // ========================================

        String requestIdString = request.getParameter("requestId");

        String status = request.getParameter("status");

        // ========================================
        // 入力チェック
        // ========================================

        if (requestIdString == null
                || requestIdString.isBlank()
                || status == null
                || status.isBlank()) {

            sendError(
                    response,
                    "必要な情報が不足しています。");

            return;
        }

        int requestId;

        try {

            requestId = Integer.parseInt(
                    requestIdString);

        } catch (NumberFormatException e) {

            sendError(
                    response,
                    "リクエストIDが正しくありません。");

            return;
        }

        // ========================================
        // ステータスチェック
        // ========================================

        if (!"approved".equals(status)
                && !"rejected".equals(status)) {

            sendError(
                    response,
                    "不正なステータスです。");

            return;
        }

        // ========================================
        // リクエストの所有者確認
        // ========================================

        int requestOwnerAccountId = shareRequestDAO
                .findOwnerAccountId(
                        requestId);

        if (requestOwnerAccountId == -1) {

            sendError(
                    response,
                    "リクエストが見つかりません。");

            return;
        }

        // ========================================
        // 自分のペットへの申込みか確認
        // ========================================

        if (requestOwnerAccountId != ownerAccountId) {

            sendError(
                    response,
                    "このリクエストを操作する権限がありません。");

            return;
        }

        // ========================================
        // ステータス更新
        // ========================================

        boolean result = shareRequestDAO.updateStatus(
                requestId,
                ownerAccountId,
                status);

        // ========================================
        // 結果
        // ========================================

        response.setContentType(
                "application/json; charset=UTF-8");

        response.setCharacterEncoding(
                "UTF-8");

        if (result) {

            String message;

            if ("approved".equals(status)) {

                message = "シェア申込みを承認しました。";

            } else {

                message = "シェア申込みを拒否しました。";
            }

            response.getWriter().write(
                    "{\"success\":true,\"message\":\""
                            + escapeJson(message)
                            + "\"}");

        } else {

            sendError(
                    response,
                    "このリクエストはすでに処理されているか、更新に失敗しました。");
        }
    }

    // ========================================
    // エラーJSON
    // ========================================

    private void sendError(
            HttpServletResponse response,
            String message)
            throws IOException {

        response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST);

        response.setContentType(
                "application/json; charset=UTF-8");

        response.setCharacterEncoding(
                "UTF-8");

        response.getWriter().write(
                "{\"success\":false,\"message\":\""
                        + escapeJson(message)
                        + "\"}");
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