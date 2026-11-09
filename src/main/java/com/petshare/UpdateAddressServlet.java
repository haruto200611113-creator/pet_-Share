package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateAddressServlet")
public class UpdateAddressServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        // ログイン情報を取得
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "ログインしてください。");
            return;
        }

        User loginUser = (User) session.getAttribute("loginUser");

        // 入力された住所情報を取得
        String postalCode = request.getParameter("postalCode");

        String prefecture = request.getParameter("prefecture");

        String city = request.getParameter("city");

        String address = request.getParameter("address");

        String building = request.getParameter("building");

        // 必須項目チェック
        if (postalCode == null || postalCode.trim().isEmpty()
                || prefecture == null || prefecture.trim().isEmpty()
                || city == null || city.trim().isEmpty()
                || address == null || address.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "必須項目を入力してください。");
            return;
        }

        // 前後の空白を削除
        postalCode = postalCode.trim();
        prefecture = prefecture.trim();
        city = city.trim();
        address = address.trim();

        if (building != null) {
            building = building.trim();
        }

        // データベースを更新
        UserDAO userDAO = new UserDAO();

        boolean result = userDAO.updateAddress(
                loginUser.getAccountId(),
                postalCode,
                prefecture,
                city,
                address,
                building);

        if (result) {

            // セッション内の住所情報も更新
            loginUser.setPostalCode(postalCode);
            loginUser.setPrefecture(prefecture);
            loginUser.setCity(city);
            loginUser.setAddress(address);
            loginUser.setBuilding(building);

            response.setStatus(
                    HttpServletResponse.SC_OK);

            response.getWriter().write("success");

        } else {

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "住所の更新に失敗しました。");
        }
    }
}