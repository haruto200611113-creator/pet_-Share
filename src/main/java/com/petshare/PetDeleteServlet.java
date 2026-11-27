package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/pet-delete")
public class PetDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // =====================================
        // ログイン確認
        // =====================================

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("loginUser") == null) {

            response.sendRedirect("login.html");
            return;
        }

        // =====================================
        // account_id取得
        // =====================================

        Object accountIdObj = session.getAttribute("accountId");

        if (accountIdObj == null) {

            response.sendRedirect("login.html");
            return;
        }

        int accountId;

        try {

            accountId = Integer.parseInt(
                    accountIdObj.toString());

        } catch (NumberFormatException e) {

            response.sendRedirect("login.html");
            return;
        }

        // =====================================
        // pet_id取得
        // =====================================

        String petIdParam = request.getParameter("petId");

        if (petIdParam == null
                || petIdParam.isBlank()) {

            response.sendRedirect(
                    "pet-share-select.jsp?error=delete");

            return;
        }

        int petId;

        try {

            petId = Integer.parseInt(
                    petIdParam);

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    "pet-share-select.jsp?error=delete");

            return;
        }

        // =====================================
        // ペット削除
        // =====================================

        PetDAO petDAO = new PetDAO();

        boolean deleted = petDAO.deletePet(
                petId,
                accountId);

        // =====================================
        // 結果に応じて戻る
        // =====================================

        if (deleted) {

            response.sendRedirect(
                    "pet-share-select.jsp?deleted=true");

        } else {

            response.sendRedirect(
                    "pet-share-select.jsp?error=delete");
        }
    }
}