package com.petshare;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/pet-list")
public class PetListServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        private final PetDAO petDAO = new PetDAO();

        @Override
        protected void doGet(
                        HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {

                HttpSession session = request.getSession(false);

                // ログイン確認
                if (session == null
                                || session.getAttribute("loginUser") == null) {

                        response.setStatus(
                                        HttpServletResponse.SC_UNAUTHORIZED);

                        response.setContentType(
                                        "application/json; charset=UTF-8");

                        response.getWriter().write(
                                        "{\"error\":\"ログインしてください\"}");

                        return;
                }

                try {

                        // ログイン中のアカウントIDを取得
                        int loginAccountId = (Integer) session.getAttribute("accountId");

                        // DBからペット一覧を取得
                        List<Pet> pets = petDAO.findAll();

                        // シェア中 ＋ 自分以外のペットだけに絞り込む
                        pets = pets.stream()
                                        .filter(Pet::isShared)
                                        .filter(pet -> pet.getAccountId() != loginAccountId)
                                        .toList();
                        response.setContentType(
                                        "application/json; charset=UTF-8");

                        response.setCharacterEncoding("UTF-8");

                        PrintWriter out = response.getWriter();

                        out.print("[");

                        for (int i = 0; i < pets.size(); i++) {

                                Pet pet = pets.get(i);

                                if (i > 0) {
                                        out.print(",");
                                }

                                out.print("{");

                                out.print(
                                                "\"petId\":"
                                                                + pet.getPetId()
                                                                + ",");

                                out.print(
                                                "\"name\":\""
                                                                + escapeJson(
                                                                                pet.getPetName())
                                                                + "\",");

                                out.print(
                                                "\"type\":\""
                                                                + escapeJson(
                                                                                pet.getPetType())
                                                                + "\",");

                                out.print(
                                                "\"imagePath\":\""
                                                                + escapeJson(
                                                                                pet.getPetImage())
                                                                + "\",");

                                out.print(
                                                "\"age\":"
                                                                + calculateAge(
                                                                                pet.getBirthDate())
                                                                + ",");

                                out.print(
                                                "\"area\":\"\" ,");

                                out.print(
                                                "\"createdAt\":\""
                                                                + formatDate(
                                                                                pet.getCreatedAt())
                                                                + "\",");

                                out.print(
                                                "\"liked\":false");

                                out.print("}");
                        }

                        out.print("]");

                } catch (Exception e) {

                        e.printStackTrace();

                        response.setStatus(
                                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                        response.setContentType(
                                        "application/json; charset=UTF-8");

                        response.getWriter().write(
                                        "{\"error\":\"ペット一覧の取得に失敗しました\"}");
                }
        }

        // =====================================
        // JSONエスケープ
        // =====================================

        private String escapeJson(String value) {

                if (value == null) {
                        return "";
                }

                return value
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n")
                                .replace("\r", "\\r")
                                .replace("\t", "\\t");
        }

        // =====================================
        // 年齢計算
        // =====================================

        private int calculateAge(
                        java.sql.Date birthDate) {

                if (birthDate == null) {
                        return 0;
                }

                java.time.LocalDate birth = birthDate.toLocalDate();

                java.time.LocalDate today = java.time.LocalDate.now();

                int age = today.getYear()
                                - birth.getYear();

                if (today.getMonthValue() < birth.getMonthValue()
                                || (today.getMonthValue() == birth.getMonthValue()
                                                && today.getDayOfMonth() < birth.getDayOfMonth())) {

                        age--;
                }

                return Math.max(age, 0);
        }

        // =====================================
        // 日付フォーマット
        // =====================================

        private String formatDate(
                        java.sql.Timestamp timestamp) {

                if (timestamp == null) {
                        return "";
                }

                SimpleDateFormat format = new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss");

                return format.format(timestamp);
        }
}