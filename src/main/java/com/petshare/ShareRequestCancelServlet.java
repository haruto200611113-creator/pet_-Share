package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/share-request-cancel")
public class ShareRequestCancelServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ShareRequestDAO shareRequestDAO =
            new ShareRequestDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");

        HttpSession session = request.getSession(false);

        // ログイン確認
        if (!AccessControl.isLoggedIn(session)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "ログインが必要です。"
                    }
                    """);

            return;
        }

        // リクエストID取得
        String requestIdParam =
                request.getParameter("requestId");

        if (requestIdParam == null
                || requestIdParam.isBlank()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "申込みIDが指定されていません。"
                    }
                    """);

            return;
        }

        int requestId;

        try {

            requestId =
                    Integer.parseInt(requestIdParam);

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "申込みIDが不正です。"
                    }
                    """);

            return;
        }

        // ログイン中のユーザーID
        int borrowerId =
                Integer.parseInt(
                        session.getAttribute("accountId")
                                .toString());

        // キャンセル処理
        boolean cancelled =
                shareRequestDAO.cancelRequest(
                        requestId,
                        borrowerId);

        if (!cancelled) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "キャンセルできません。すでに処理されている可能性があります。"
                    }
                    """);

            return;
        }

        response.getWriter().write("""
                {
                    "success": true,
                    "message": "申込みをキャンセルしました。"
                }
                """);
    }
}