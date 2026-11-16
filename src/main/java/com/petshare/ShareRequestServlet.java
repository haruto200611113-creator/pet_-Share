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

        // リクエストの文字コード
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // ========================================
        // ログインチェック
        // ========================================

        if (!AccessControl.isFullMember(session)) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/login.html");

            return;
        }

        try {

            // ========================================
            // ログインユーザー取得
            // ========================================

            Object accountId = session.getAttribute("accountId");

            if (accountId == null) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/login.html");

                return;
            }

            int borrowerId = (Integer) accountId;

            // ========================================
            // パラメータ取得
            // ========================================

            String petIdString = request.getParameter("petId");

            String startString = request.getParameter("startDatetime");

            String endString = request.getParameter("endDatetime");

            // ========================================
            // ペットID確認
            // ========================================

            if (petIdString == null
                    || petIdString.isBlank()) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/home.html");

                return;
            }

            int petId = Integer.parseInt(petIdString);

            // ========================================
            // 日時入力チェック
            // ========================================

            if (startString == null
                    || endString == null
                    || startString.isBlank()
                    || endString.isBlank()) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=invalid_datetime");

                return;
            }

            // ========================================
            // 日時変換
            // ========================================

            LocalDateTime start = LocalDateTime.parse(
                    startString);

            LocalDateTime end = LocalDateTime.parse(
                    endString);

            // ========================================
            // 開始日時 < 終了日時
            // ========================================

            if (!start.isBefore(end)) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=invalid_datetime");

                return;
            }

            // ========================================
            // 過去日時チェック
            // ========================================

            if (start.isBefore(
                    LocalDateTime.now())) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=past_datetime");

                return;
            }

            // ========================================
            // ペット取得
            // ========================================

            Pet pet = petDAO.findById(petId);

            if (pet == null) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/home.html");

                return;
            }

            // ========================================
            // 自分のペットは借りられない
            // ========================================

            if (pet.getAccountId() == borrowerId) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/home.html");

                return;
            }

            // ========================================
            // シェア設定確認
            // ========================================

            if (!pet.isShared()) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/pet-detail.html?petId="
                                + petId);

                return;
            }

            // ========================================
            // シェア可能期間取得
            // ========================================

            PetShare petShare = petShareDAO.findActiveByPetId(
                    petId);

            if (petShare == null) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/pet-detail.html?petId="
                                + petId);

                return;
            }

            LocalDateTime availableStart = petShare
                    .getAvailableStart()
                    .toLocalDateTime();

            LocalDateTime availableEnd = petShare
                    .getAvailableEnd()
                    .toLocalDateTime();

            // ========================================
            // シェア可能期間チェック
            // ========================================

            if (start.isBefore(
                    availableStart)
                    ||
                    end.isAfter(
                            availableEnd)) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=outside_period");

                return;
            }

            // ========================================
            // 利用時間計算
            // ========================================

            Duration duration = Duration.between(
                    start,
                    end);

            double hours = duration.toMinutes()
                    / 60.0;

            // ========================================
            // 料金計算
            // ========================================

            int price = calculatePrice(hours);

            // ========================================
            // 重複チェック
            // ========================================

            if (shareRequestDAO
                    .existsOverlappingRequest(
                            petId,
                            Timestamp.valueOf(start),
                            Timestamp.valueOf(end))) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=overlap");

                return;
            }

            // ========================================
            // ShareRequest作成
            // ========================================

            ShareRequest shareRequest = new ShareRequest();

            shareRequest.setPetId(
                    petId);

            shareRequest.setBorrowerId(
                    borrowerId);

            shareRequest.setStartDatetime(
                    Timestamp.valueOf(start));

            shareRequest.setEndDatetime(
                    Timestamp.valueOf(end));

            shareRequest.setUsageHours(
                    hours);

            shareRequest.setSharePrice(
                    price);

            shareRequest.setStatus(
                    "pending");

            // ========================================
            // DB登録
            // ========================================

            boolean result = shareRequestDAO.insert(
                    shareRequest);

            // ========================================
            // 登録成功
            // ========================================

            if (result) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&success=true");

                return;
            }

            // ========================================
            // 登録失敗
            // ========================================

            response.sendRedirect(
                    request.getContextPath()
                            + "/borrow-request.html?petId="
                            + petId
                            + "&error=insert_failed");

        } catch (Exception e) {

            // ========================================
            // システムエラー
            // ========================================

            e.printStackTrace();

            String petId = request.getParameter("petId");

            response.sendRedirect(
                    request.getContextPath()
                            + "/borrow-request.html?petId="
                            + petId
                            + "&error=system_error");
        }
    }

    // ========================================
    // 料金計算
    // ========================================

    private int calculatePrice(
            double hours) {

        // 1時間まで
        if (hours <= 1) {

            return 500;

            // 2時間まで
        } else if (hours <= 2) {

            return 900;

            // 3時間まで
        } else if (hours <= 3) {

            return 1200;

            // 4時間まで
        } else if (hours <= 4) {

            return 1500;

            // 5時間まで
        } else if (hours <= 5) {

            return 1800;

            // 6時間まで
        } else if (hours <= 6) {

            return 2000;

            // 8時間まで
        } else if (hours <= 8) {

            return 2500;

            // 12時間まで
        } else if (hours <= 12) {

            return 3000;

            // 24時間まで
        } else if (hours <= 24) {

            return 4000;

            // 48時間まで
        } else if (hours <= 48) {

            return 7000;

            // 72時間まで
        } else if (hours <= 72) {

            return 9000;

            // 72時間を超える場合
        } else {

            double extraHours = hours - 72;

            int extraDays = (int) Math.ceil(
                    extraHours / 24.0);

            return 9000
                    + (extraDays * 3000);
        }
    }
}