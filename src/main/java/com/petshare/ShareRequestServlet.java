package com.petshare;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // ==========================================
        // ① ログイン確認
        // ==========================================
        if (session == null
                || session.getAttribute("loginUser") == null) {

            System.out.println("=== share-request ===");
            System.out.println("ログインしていません");

            response.sendRedirect(
                    request.getContextPath() + "/login.html");

            return;
        }

        // ==========================================
        // ② 本会員確認
        // ==========================================
        if (!AccessControl.isFullMember(session)) {

            System.out.println("=== share-request ===");
            System.out.println("本会員ではありません");

            response.sendRedirect(
                    request.getContextPath()
                            + "/membership-upgrade.html");

            return;
        }

        try {

            // ==========================================
            // ③ ログインユーザーID取得
            // ==========================================
            Object accountIdObj = session.getAttribute("accountId");

            if (accountIdObj == null) {

                System.out.println("=== share-request ===");
                System.out.println("accountId がセッションにありません");

                response.sendRedirect(
                        request.getContextPath() + "/login.html");

                return;
            }

            int borrowerId = Integer.parseInt(
                    accountIdObj.toString());

            System.out.println("==============================");
            System.out.println("借りる申込み開始");
            System.out.println("borrowerId = " + borrowerId);

            // ==========================================
            // ④ パラメータ取得
            // ==========================================
            String petIdParam = request.getParameter("petId");

            String startDatetimeParam = request.getParameter("startDatetime");

            String endDatetimeParam = request.getParameter("endDatetime");

            System.out.println("petId = " + petIdParam);
            System.out.println(
                    "startDatetime = " + startDatetimeParam);
            System.out.println(
                    "endDatetime = " + endDatetimeParam);

            // ==========================================
            // ⑤ パラメータチェック
            // ==========================================
            if (petIdParam == null
                    || petIdParam.isBlank()
                    || startDatetimeParam == null
                    || startDatetimeParam.isBlank()
                    || endDatetimeParam == null
                    || endDatetimeParam.isBlank()) {

                System.out.println("パラメータ不足");

                response.sendRedirect(
                        request.getContextPath()
                                + "/pet-detail.html?petId="
                                + petIdParam);

                return;
            }

            int petId = Integer.parseInt(petIdParam);

            // ==========================================
            // ⑥ 日時をLocalDateTimeへ変換
            // ==========================================
            LocalDateTime startDatetime = LocalDateTime.parse(
                    startDatetimeParam,
                    FORMATTER);

            LocalDateTime endDatetime = LocalDateTime.parse(
                    endDatetimeParam,
                    FORMATTER);

            System.out.println(
                    "start = " + startDatetime);

            System.out.println(
                    "end = " + endDatetime);

            // ==========================================
            // ⑦ 開始日時 < 終了日時チェック
            // ==========================================
            if (!startDatetime.isBefore(endDatetime)) {

                System.out.println(
                        "開始日時が終了日時より後です");

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=invalid_datetime");

                return;
            }

            // ==========================================
            // ⑧ 過去日時チェック
            // ==========================================
            if (startDatetime.isBefore(LocalDateTime.now())) {

                System.out.println(
                        "利用開始日時が過去です");

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=past_datetime");

                return;
            }

            // ==========================================
            // ⑨ ペット取得
            // ==========================================
            Pet pet = petDAO.findById(petId);

            if (pet == null) {

                System.out.println(
                        "ペットが見つかりません");

                response.sendRedirect(
                        request.getContextPath()
                                + "/home.html");

                return;
            }

            System.out.println("==============================");
            System.out.println("ペット情報取得成功");
            System.out.println("petId = " + pet.getPetId());
            System.out.println("petName = " + pet.getPetName());
            System.out.println("owner accountId = "
                    + pet.getAccountId());
            System.out.println("login accountId = "
                    + borrowerId);

            System.out.println(
                    "pet.isShared = "
                            + pet.isShared());

            // ==========================================
            // ⑩ 自分のペットを借りることを禁止
            // ==========================================
            if (pet.getAccountId() == borrowerId) {

                System.out.println(
                        "自分のペットなので申込み不可");

                response.sendRedirect(
                        request.getContextPath()
                                + "/pet-detail.html?petId="
                                + petId);

                return;
            }

            // ==========================================
            // ⑪ シェア設定確認
            // ==========================================
            if (!pet.isShared()) {

                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(
                        "NG：pets.is_shared が false");
                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                response.sendRedirect(
                        request.getContextPath()
                                + "/pet-detail.html?petId="
                                + petId);

                return;
            }

            System.out.println(
                    "OK：pets.is_shared = true");

            // ==========================================
            // ⑫ pet_shares 取得
            // ==========================================
            PetShare petShare = petShareDAO.findActiveByPetId(petId);

            if (petShare == null) {

                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(
                        "NG：pet_shares が見つかりません");
                System.out.println(
                        "petId = " + petId);
                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                response.sendRedirect(
                        request.getContextPath()
                                + "/pet-detail.html?petId="
                                + petId);

                return;
            }

            System.out.println("==============================");
            System.out.println(
                    "pet_shares 取得成功");

            System.out.println(
                    "shareId = "
                            + petShare.getShareId());

            System.out.println(
                    "availableStart = "
                            + petShare.getAvailableStart());

            System.out.println(
                    "availableEnd = "
                            + petShare.getAvailableEnd());

            System.out.println(
                    "isActive = "
                            + petShare.isActive());

            // ==========================================
            // ⑬ シェア可能期間取得
            // ==========================================
            LocalDateTime availableStart = petShare.getAvailableStart()
                    .toLocalDateTime();

            LocalDateTime availableEnd = petShare.getAvailableEnd()
                    .toLocalDateTime();

            // ==========================================
            // ⑭ シェア期間内か確認
            // ==========================================
            if (startDatetime.isBefore(availableStart)
                    || endDatetime.isAfter(availableEnd)) {

                System.out.println(
                        "シェア可能期間外です");

                System.out.println(
                        "availableStart = "
                                + availableStart);

                System.out.println(
                        "availableEnd = "
                                + availableEnd);

                System.out.println(
                        "requestStart = "
                                + startDatetime);

                System.out.println(
                        "requestEnd = "
                                + endDatetime);

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=outside_period");

                return;
            }

            // ==========================================
            // ⑮ 利用時間計算
            // ==========================================
            Duration duration = Duration.between(
                    startDatetime,
                    endDatetime);

            double usageHours = duration.toMinutes() / 60.0;

            System.out.println(
                    "利用時間 = "
                            + usageHours
                            + "時間");

            // ==========================================
            // ⑯ 料金計算
            // ==========================================
            int sharePrice = calculatePrice(usageHours);

            System.out.println(
                    "シェア料金 = "
                            + sharePrice
                            + "円");

            // ==========================================
            // ⑰ 重複申込みチェック
            // ==========================================
            boolean overlap = shareRequestDAO.existsOverlappingRequest(
                    petId,
                    startDatetime,
                    endDatetime);

            if (overlap) {

                System.out.println(
                        "同じ時間帯に申込みがあります");

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=overlap");

                return;
            }

            System.out.println(
                    "重複申込みなし");

            // ==========================================
            // ⑱ ShareRequest作成
            // ==========================================
            ShareRequest shareRequest = new ShareRequest();

            shareRequest.setPetId(petId);
            shareRequest.setBorrowerId(borrowerId);

            shareRequest.setStartDatetime(
                    java.sql.Timestamp.valueOf(
                            startDatetime));

            shareRequest.setEndDatetime(
                    java.sql.Timestamp.valueOf(
                            endDatetime));

            shareRequest.setUsageHours(
                    usageHours);

            shareRequest.setSharePrice(
                    sharePrice);

            shareRequest.setStatus("pending");

            System.out.println("==============================");
            System.out.println(
                    "share_requests INSERT開始");

            System.out.println(
                    "petId = " + petId);

            System.out.println(
                    "borrowerId = " + borrowerId);

            System.out.println(
                    "start = " + startDatetime);

            System.out.println(
                    "end = " + endDatetime);

            System.out.println(
                    "usageHours = " + usageHours);

            System.out.println(
                    "sharePrice = " + sharePrice);

            System.out.println(
                    "status = pending");

            // ==========================================
            // ⑲ DB登録
            // ==========================================
            boolean result = shareRequestDAO.insert(
                    shareRequest);

            // ==========================================
            // ⑳ 登録結果
            // ==========================================
            if (result) {

                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(
                        "申込み登録成功");
                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&success=true");

            } else {

                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(
                        "申込み登録失敗");
                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                response.sendRedirect(
                        request.getContextPath()
                                + "/borrow-request.html?petId="
                                + petId
                                + "&error=insert_failed");
            }

        } catch (Exception e) {

            System.out.println(
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            System.out.println(
                    "ShareRequestServlet エラー");

            System.out.println(
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath()
                            + "/borrow-request.html?petId="
                            + request.getParameter("petId")
                            + "&error=system_error");
        }
    }

    /**
     * 利用時間から料金を計算
     *
     * 1時間 = 500円
     * 2時間 = 900円
     * 3時間 = 1200円
     * 4時間 = 1500円
     * 5時間 = 1800円
     * 6時間 = 2000円
     * 8時間 = 2500円
     * 12時間 = 3000円
     * 24時間 = 4000円
     * 48時間 = 7000円
     * 72時間 = 9000円
     *
     * 72時間を超える場合
     * 24時間ごとに3000円追加
     */
    private int calculatePrice(double hours) {

        if (hours <= 1) {
            return 500;
        }

        if (hours <= 2) {
            return 900;
        }

        if (hours <= 3) {
            return 1200;
        }

        if (hours <= 4) {
            return 1500;
        }

        if (hours <= 5) {
            return 1800;
        }

        if (hours <= 6) {
            return 2000;
        }

        if (hours <= 8) {
            return 2500;
        }

        if (hours <= 12) {
            return 3000;
        }

        if (hours <= 24) {
            return 4000;
        }

        if (hours <= 48) {
            return 7000;
        }

        if (hours <= 72) {
            return 9000;
        }

        double additionalHours = hours - 72;

        int additionalBlocks = (int) Math.ceil(
                additionalHours / 24.0);

        return 9000
                + (additionalBlocks * 3000);
    }
}