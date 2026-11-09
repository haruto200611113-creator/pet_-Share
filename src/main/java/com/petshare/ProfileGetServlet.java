package com.petshare;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * プロフィール取得Servlet
 *
 * profile-edit.js
 * ↓
 * ProfileGetServlet
 * ↓
 * UserDAO
 * ↓
 * Aiven MySQL
 */
@WebServlet("/profile-get")
public class ProfileGetServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(
                "application/json; charset=UTF-8");

        response.setCharacterEncoding("UTF-8");

        /*
         * =========================================
         * ログインセッション取得
         * =========================================
         */

        HttpSession session = request.getSession(false);

        if (session == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"login\"}");

            return;
        }

        /*
         * =========================================
         * accountId取得
         * =========================================
         */

        Object accountIdObject = session.getAttribute("accountId");

        if (accountIdObject == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"login\"}");

            return;
        }

        int accountId;

        try {

            accountId = Integer.parseInt(
                    accountIdObject.toString());

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"login\"}");

            return;
        }

        /*
         * =========================================
         * DBからプロフィール取得
         * =========================================
         */

        UserDAO dao = new UserDAO();

        String[] profile = dao.getProfile(accountId);

        if (profile == null) {

            response.setStatus(
                    HttpServletResponse.SC_NOT_FOUND);

            response.getWriter().write(
                    "{\"error\":\"not_found\"}");

            return;
        }

        /*
         * =========================================
         * JSONとして返す
         *
         * profile[0] = アカウント名
         * profile[1] = 自己紹介
         * profile[2] = プロフィール画像
         * =========================================
         */

        String accountName = escapeJson(profile[0]);

        String selfIntroduction = escapeJson(profile[1]);

        String profileImage = escapeJson(profile[2]);

        String json = "{"
                + "\"accountName\":\""
                + accountName
                + "\","
                + "\"selfIntroduction\":\""
                + selfIntroduction
                + "\","
                + "\"profileImage\":\""
                + profileImage
                + "\""
                + "}";

        PrintWriter out = response.getWriter();

        out.write(json);

        out.flush();
    }

    /*
     * =========================================
     * JSON用エスケープ
     * =========================================
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