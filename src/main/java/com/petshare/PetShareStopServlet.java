package com.petshare;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/pet-share-stop")
public class PetShareStopServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PetDAO petDAO = new PetDAO();

    private final PetShareDAO petShareDAO = new PetShareDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // ========================================
        // ログイン確認
        // ========================================

        HttpSession session = request.getSession(false);

        if (!AccessControl.requireFullMember(
                session,
                response)) {

            return;
        }

        try {

            // ========================================
            // ログイン中のアカウントID取得
            // ========================================

            int accountId = (Integer) session.getAttribute(
                    "accountId");

            // ========================================
            // petId取得
            // ========================================

            String petIdParam = request.getParameter(
                    "petId");

            if (petIdParam == null ||
                    petIdParam.isBlank()) {

                response.sendRedirect(
                        "mypage.jsp?shareError=ペットIDが指定されていません");

                return;
            }

            int petId = Integer.parseInt(
                    petIdParam);

            // ========================================
            // ペット取得
            // ========================================

            Pet pet = petDAO.findById(
                    petId);

            if (pet == null) {

                response.sendRedirect(
                        "mypage.jsp?shareError=ペットが見つかりません");

                return;
            }

            // ========================================
            // 自分のペットか確認
            // ========================================

            if (pet.getAccountId() != accountId) {

                response.sendRedirect(
                        "mypage.jsp?shareError=自分のペット以外は停止できません");

                return;
            }

            // ========================================
            // シェア停止
            // ========================================

            boolean stopped = petShareDAO.stopShare(
                    petId);

            if (!stopped) {

                response.sendRedirect(
                        "mypage.jsp?shareError=シェア停止に失敗しました");

                return;
            }

            // ========================================
            // pets.is_shared も false にする
            // ========================================
            boolean updated = petDAO.updateShareStatus(
                    petId,
                    false);

            System.out.println("==============================");
            System.out.println("シェア停止処理");
            System.out.println("petId = " + petId);
            System.out.println("pets.is_shared 更新結果 = " + updated);

            Pet checkPet = petDAO.findById(petId);

            if (checkPet != null) {
                System.out.println(
                        "更新後のisShared = "
                                + checkPet.isShared());
            }

            System.out.println("==============================");

            if (!updated) {

                response.sendRedirect(
                        "mypage.jsp?shareError=ペットのシェア状態更新に失敗しました");

                return;
            }

            // ========================================
            // 成功
            // ========================================

            response.sendRedirect(
                    "mypage.jsp?shareStopSuccess=true");

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    "mypage.jsp?shareError=ペットIDが不正です");

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    "mypage.jsp?shareError=シェア停止中にエラーが発生しました");

        }
    }
}