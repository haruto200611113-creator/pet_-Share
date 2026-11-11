package com.petshare;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccessControl {

    // ==============================
    // ログインしているか確認
    // ==============================
    public static boolean isLoggedIn(HttpSession session) {

        return session != null
                && session.getAttribute("loginUser") != null;
    }

    // ==============================
    // 仮会員以上か確認
    // ==============================
    public static boolean isMember(HttpSession session) {

        if (!isLoggedIn(session)) {
            return false;
        }

        Object rank = session.getAttribute("userRank");

        if (rank == null) {
            return false;
        }

        return Integer.parseInt(rank.toString()) >= 1;
    }

    // ==============================
    // 本会員か確認
    // ==============================
    public static boolean isFullMember(HttpSession session) {

        if (!isLoggedIn(session)) {
            return false;
        }

        Object rank = session.getAttribute("userRank");

        if (rank == null) {
            return false;
        }

        return Integer.parseInt(rank.toString()) == 2;
    }

    // ==============================
    // ログイン必須
    // ==============================
    public static boolean requireLogin(
            HttpSession session,
            HttpServletResponse response)
            throws IOException {

        if (!isLoggedIn(session)) {

            response.sendRedirect("login.html");

            return false;
        }

        return true;
    }

    // ==============================
    // 本会員必須
    // ==============================
    public static boolean requireFullMember(
            HttpSession session,
            HttpServletResponse response)
            throws IOException {

        // ログインしていない
        if (!isLoggedIn(session)) {

            response.sendRedirect("login.html");

            return false;
        }

        // 仮会員の場合
        if (!isFullMember(session)) {

            response.sendRedirect("membership-upgrade.html");

            return false;
        }

        return true;
    }
}