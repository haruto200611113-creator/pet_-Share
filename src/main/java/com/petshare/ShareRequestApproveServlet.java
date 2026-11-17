package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/share-request-approve")
public class ShareRequestApproveServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ShareRequestDAO shareRequestDAO = new ShareRequestDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(
                "application/json; charset=UTF-8");

        // ==========================================
        // ① セッション確認
        // ==========================================

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("accountId") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "ログインが必要です。"
                    }
                    """);

            return;
        }

        // ==========================================
        // ② ログイン中のユーザーID取得
        // ==========================================

        int ownerAccountId;

        try {

            ownerAccountId = Integer.parseInt(
                    session.getAttribute("accountId")
                            .toString());

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "ログイン情報が不正です。"
                    }
                    """);

            return;
        }

        // ==========================================
        // ③ 申込みID取得
        // ==========================================

        String requestIdParam = request.getParameter("requestId");

        System.out.println(
                "【承認確認】requestIdParam = "
                        + requestIdParam);

        if (requestIdParam == null
                || requestIdParam.isBlank()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "申込みIDが指定されていません。"
                    }
                    """);

            return;
        }

        // ==========================================
        // ④ 申込みIDを数値に変換
        // ==========================================

        int requestId;

        try {

            requestId = Integer.parseInt(requestIdParam);

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "申込みIDが不正です。"
                    }
                    """);

            return;
        }

        System.out.println(
                "【承認確認】requestId = "
                        + requestId
                        + ", ownerAccountId = "
                        + ownerAccountId);

        // ==========================================
        // ⑤ 申込みを承認
        // ==========================================

        boolean approved = shareRequestDAO.updateStatus(
                requestId,
                ownerAccountId,
                "approved");

        System.out.println(
                "【承認結果】approved = "
                        + approved);

        // ==========================================
        // ⑥ 承認結果
        // ==========================================

        if (!approved) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "承認できません。すでに処理されている可能性があります。"
                    }
                    """);

            return;
        }

        // ==========================================
        // ⑦ 承認成功
        // ==========================================

        response.getWriter().write("""
                {
                    "success": true,
                    "message": "シェア申込みを承認しました。"
                }
                """);
    }
}