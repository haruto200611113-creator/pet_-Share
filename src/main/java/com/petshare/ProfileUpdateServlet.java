package com.petshare;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * プロフィール更新Servlet
 *
 * profile-edit.html
 * ↓
 * ProfileUpdateServlet
 * ↓
 * UserDAO
 * ↓
 * Aiven MySQL
 */
@WebServlet("/profile-update")
public class ProfileUpdateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // UTF-8で受け取る
        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name());

        // UTF-8で返す
        response.setContentType(
                "text/html; charset=UTF-8");

        /*
         * =========================================
         * ログインセッションを取得
         * =========================================
         */

        HttpSession session = request.getSession(false);

        // ログインしていない場合
        if (session == null) {

            response.sendRedirect(
                    "login.html");

            return;
        }

        /*
         * =========================================
         * セッションからaccount_idを取得
         * =========================================
         */

        Object accountIdObject = session.getAttribute("accountId");

        // accountIdが存在しない場合
        if (accountIdObject == null) {

            response.sendRedirect(
                    "login.html");

            return;
        }

        int accountId;

        try {

            accountId = Integer.parseInt(
                    accountIdObject.toString());

        } catch (NumberFormatException e) {

            System.err.println(
                    "accountIdの取得に失敗しました。");

            response.sendRedirect(
                    "login.html");

            return;
        }

        /*
         * =========================================
         * フォームから入力値を取得
         * =========================================
         */

        String accountName = request.getParameter(
                "accountName");

        String selfIntroduction = request.getParameter(
                "selfIntroduction");

        /*
         * =========================================
         * 入力値チェック
         * =========================================
         */

        if (accountName == null
                || accountName.trim().isEmpty()) {

            response.sendRedirect(
                    "profile-edit.html?error=accountName");

            return;
        }

        // 自己紹介がnullの場合は空文字にする
        if (selfIntroduction == null) {
            selfIntroduction = "";
        }

        /*
         * 自己紹介文は300文字以内
         */
        if (selfIntroduction.length() > 300) {

            response.sendRedirect(
                    "profile-edit.html?error=introduction");

            return;
        }

        /*
         * =========================================
         * UserDAOを呼び出す
         * =========================================
         */

        UserDAO dao = new UserDAO();

        boolean result = dao.updateProfile(
                accountId,
                accountName,
                selfIntroduction);

        /*
         * =========================================
         * 保存結果を確認
         * =========================================
         */

        if (result) {

            // 保存成功
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("success");

        } else {

            // 保存失敗
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("error");
        }
    }
}