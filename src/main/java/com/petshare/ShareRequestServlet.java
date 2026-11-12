package com.petshare;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/share-request")
public class ShareRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PetDAO petDAO = new PetDAO();

    private final PetShareDAO petShareDAO = new PetShareDAO();

    private final ShareRequestDAO shareRequestDAO = new ShareRequestDAO();

    // ========================================
    // POST
    // ========================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // ====================================
        // ログインチェック
        // ====================================

        if (!AccessControl.isFullMember(
                session)) {

            response.sendRedirect(
                    "login.html");

            return;
        }

        try {

            // =================================
            // ログインユーザー
            // =================================

            int borrowerId = (Integer) session.getAttribute(
                    "accountId");

            // =================================
            // パラメータ
            // =================================

            int petId = Integer.parseInt(
                    request.getParameter(
                            "petId"));

            String startString = request.getParameter(
                    "startDatetime");

            String endString = request.getParameter(
                    "endDatetime");

            // =================================
            // 入力チェック
            // =================================

            if (startString == null ||
                    endString == null ||
                    startString.isBlank() ||
                    endString.isBlank()) {

                response.sendRedirect(
                        "borrow-request.html?petId="
                                + petId
                                + "&error=日時を入力してください");

                return;
            }

            LocalDateTime start = LocalDateTime.parse(
                    startString);

            LocalDateTime end = LocalDateTime.parse(
                    endString);

            // =================================
            // 開始 < 終了
            // =================================

            if (!start.isBefore(end)) {

                response.sendRedirect(
                        "borrow-request.html?petId="
                                + petId
                                + "&error=利用終了日時は開始日時より後にしてください");

                return;
            }

            // =================================
            // 過去日時チェック
            // =================================

            if (start.isBefore(
                    LocalDateTime.now())) {

                response.sendRedirect(
                        "borrow-request.html?petId="
                                + petId
                                + "&error=利用開始日時は現在時刻以降にしてください");

                return;
            }

            // =================================
            // ペット取得
            // =================================

            Pet pet = petDAO.findById(
                    petId);

            if (pet == null) {

                response.sendRedirect(
                        "home.html");

                return;
            }

            // =================================
            // 自分のペットを借りるのを禁止
            // =================================

            if (pet.getAccountId() == borrowerId) {

                response.sendRedirect(
                        "home.html");

                return;
            }

            // =================================
            // シェア設定確認
            // =================================

            if (!pet.isShared()) {

                response.sendRedirect(
                        "pet-detail.html?petId="
                                + petId);

                return;
            }

            // =================================
            // シェア可能期間確認
            // =================================

            PetShare petShare = petShareDAO.findActiveByPetId(
                    petId);

            if (petShare == null) {

                response.sendRedirect(
                        "pet-detail.html?petId="
                                + petId);

                return;
            }

            LocalDateTime availableStart = petShare
                    .getAvailableStart()
                    .toLocalDateTime();

            LocalDateTime availableEnd = petShare
                    .getAvailableEnd()
                    .toLocalDateTime();

            if (start.isBefore(
                    availableStart)
                    ||
                    end.isAfter(
                            availableEnd)) {

                response.sendRedirect(
                        "borrow-request.html?petId="
                                + petId
                                + "&error=シェア可能期間外です");

                return;
            }

            // =================================
            // 利用時間
            // =================================

            Duration duration = Duration.between(
                    start,
                    end);

            double hours = duration.toMinutes()
                    / 60.0;

            // =================================
            // 料金計算
            // =================================

            int price = calculatePrice(
                    hours);

            // =================================
            // 重複チェック
            // =================================

            if (shareRequestDAO
                    .existsOverlappingRequest(
                            petId,
                            Timestamp.valueOf(
                                    start),
                            Timestamp.valueOf(
                                    end))) {

                response.sendRedirect(
                        "borrow-request.html?petId="
                                + petId
                                + "&error=この時間帯にはすでに申込みがあります");

                return;
            }

            // =================================
            // ShareRequest作成
            // =================================

            ShareRequest shareRequest = new ShareRequest();

            shareRequest.setPetId(
                    petId);

            shareRequest.setBorrowerId(
                    borrowerId);

            shareRequest.setStartDatetime(
                    Timestamp.valueOf(
                            start));

            shareRequest.setEndDatetime(
                    Timestamp.valueOf(
                            end));

            shareRequest.setUsageHours(
                    hours);

            shareRequest.setSharePrice(
                    price);

            shareRequest.setStatus(
                    "pending");

            // =================================
            // DB登録
            // =================================

            boolean result = shareRequestDAO.insert(
                    shareRequest);

            if (result) {

                response.sendRedirect(
                        "borrow-request.html?petId="
                                + petId
                                + "&success=true");

            } else {

                response.sendRedirect(
                        "borrow-request.html?petId="
                                + petId
                                + "&error=申込みの登録に失敗しました");
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    "borrow-request.html?error=システムエラーが発生しました");
        }
    }

    // ========================================
    // 料金計算
    // ========================================

    private int calculatePrice(
            double hours) {

        if (hours <= 1) {

            return 500;

        } else if (hours <= 2) {

            return 900;

        } else if (hours <= 3) {

            return 1200;

        } else if (hours <= 4) {

            return 1500;

        } else if (hours <= 5) {

            return 1800;

        } else if (hours <= 6) {

            return 2000;

        } else if (hours <= 8) {

            return 2500;

        } else if (hours <= 12) {

            return 3000;

        } else if (hours <= 24) {

            return 4000;

        } else if (hours <= 48) {

            return 7000;

        } else {

            return 9000;
        }
    }
}