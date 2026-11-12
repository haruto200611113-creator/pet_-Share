package com.petshare;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/pet-share")
public class PetShareServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PetDAO petDAO = new PetDAO();
    private final PetShareDAO petShareDAO = new PetShareDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // =====================================
        // ① ログイン確認
        // =====================================

        HttpSession session = request.getSession(false);

        if (!AccessControl.requireFullMember(session, response)) {
            return;
        }

        try {

            // =====================================
            // ② ログインユーザー取得
            // =====================================

            Object accountIdObj = session.getAttribute("accountId");

            if (accountIdObj == null) {
                response.sendRedirect("login.html");
                return;
            }

            int loginAccountId = Integer.parseInt(accountIdObj.toString());

            // =====================================
            // ③ フォームから値を取得
            // =====================================

            String petIdStr = request.getParameter("petId");

            String startStr = request.getParameter("availableStart");

            String endStr = request.getParameter("availableEnd");

            if (petIdStr == null
                    || startStr == null
                    || endStr == null
                    || petIdStr.isBlank()
                    || startStr.isBlank()
                    || endStr.isBlank()) {

                response.sendRedirect(
                        "mypage.jsp?shareError=入力してください");

                return;
            }

            int petId = Integer.parseInt(petIdStr);

            // =====================================
            // ④ ペット取得
            // =====================================

            Pet pet = petDAO.findById(petId);

            if (pet == null) {

                response.sendRedirect(
                        "mypage.jsp?shareError=ペットが見つかりません");

                return;
            }

            // =====================================
            // ⑤ 自分のペットか確認
            // =====================================

            if (pet.getAccountId() != loginAccountId) {

                response.sendRedirect(
                        "mypage.jsp?shareError=自分のペットのみシェアできます");

                return;
            }

            // =====================================
            // ⑥ 日時を変換
            // =====================================

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd'T'HH:mm");

            LocalDateTime start = LocalDateTime.parse(
                    startStr,
                    formatter);

            LocalDateTime end = LocalDateTime.parse(
                    endStr,
                    formatter);

            // =====================================
            // ⑦ 開始日時 < 終了日時を確認
            // =====================================

            if (!start.isBefore(end)) {

                response.sendRedirect(
                        "mypage.jsp?shareError=終了日時は開始日時より後にしてください");

                return;
            }

            // =====================================
            // ⑧ 過去の日時を確認
            // =====================================

            if (start.isBefore(
                    LocalDateTime.now())) {

                response.sendRedirect(
                        "mypage.jsp?shareError=過去の日時は設定できません");

                return;
            }

            // =====================================
            // ⑨ すでにシェア中か確認
            // =====================================

            if (pet.isShared()) {

                response.sendRedirect(
                        "mypage.jsp?shareError=このペットはすでにシェア中です");

                return;
            }

            Timestamp startTimestamp = Timestamp.valueOf(start);

            Timestamp endTimestamp = Timestamp.valueOf(end);

            // =====================================
            // ⑩ シェア情報を作成
            // =====================================

            PetShare share = new PetShare();

            share.setPetId(petId);

            share.setAvailableStart(
                    startTimestamp);

            share.setAvailableEnd(
                    endTimestamp);

            share.setActive(true);

            // =====================================
            // ⑪ pet_shares に登録
            // =====================================

            boolean shareInserted = petShareDAO.insert(share);

            if (!shareInserted) {

                response.sendRedirect(
                        "mypage.jsp?shareError=シェア設定に失敗しました");

                return;
            }

            // =====================================
            // ⑫ pets.is_shared を更新
            // =====================================

            boolean statusUpdated = petDAO.updateShareStatus(
                    petId,
                    true);

            if (!statusUpdated) {

                response.sendRedirect(
                        "mypage.jsp?shareError=シェア状態の更新に失敗しました");

                return;
            }

            // =====================================
            // ⑬ 成功
            // =====================================

            response.sendRedirect(
                    "mypage.jsp?shareSuccess=true");

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    "mypage.jsp?shareError=ペットIDが正しくありません");

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    "mypage.jsp?shareError=シェア設定中にエラーが発生しました");
        }
    }
}
