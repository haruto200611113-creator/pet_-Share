package com.petshare;

import java.io.IOException;
import java.sql.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/pet-register")
public class PetRegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        /*
         * 本会員のみ利用可能
         */
        if (!AccessControl.requireFullMember(session, response)) {
            return;
        }

        /*
         * ログイン中のアカウントIDを取得
         *
         * HTMLからaccountIdを受け取らない。
         * ログイン中のユーザーとペットを紐付ける。
         */
        Object accountIdObject = session.getAttribute("accountId");

        if (accountIdObject == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        int accountId = Integer.parseInt(accountIdObject.toString());

        /*
         * Petオブジェクトを作成
         */
        Pet pet = new Pet();

        pet.setAccountId(accountId);

        // 基本情報
        pet.setPetName(request.getParameter("petName"));
        pet.setPetImage(request.getParameter("petImage"));
        pet.setPetType(request.getParameter("petType"));
        pet.setBreed(request.getParameter("breed"));
        pet.setGender(request.getParameter("gender"));

        pet.setBirthDate(
                parseDate(request.getParameter("birthDate")));

        pet.setWeight(
                parseDouble(request.getParameter("weight")));

        // 性格・特徴
        pet.setPersonality(
                request.getParameter("personality"));

        pet.setFeatures(
                request.getParameter("features"));

        pet.setLikes(
                request.getParameter("likes"));

        pet.setDislikes(
                request.getParameter("dislikes"));

        // 相性
        pet.setOtherAnimals(
                request.getParameter("otherAnimals"));

        pet.setChildrenCompatibility(
                request.getParameter("childrenCompatibility"));

        // お世話情報
        pet.setFoodInfo(
                request.getParameter("foodInfo"));

        pet.setWalkInfo(
                request.getParameter("walkInfo"));

        pet.setToiletInfo(
                request.getParameter("toiletInfo"));

        pet.setAloneTime(
                request.getParameter("aloneTime"));

        pet.setCareInfo(
                request.getParameter("careInfo"));

        // 健康・安全
        pet.setVaccination(
                request.getParameter("vaccination"));

        pet.setNeutering(
                request.getParameter("neutering"));

        pet.setMedicalHistory(
                request.getParameter("medicalHistory"));

        pet.setAllergies(
                request.getParameter("allergies"));

        pet.setMedication(
                request.getParameter("medication"));

        pet.setPrecautions(
                request.getParameter("precautions"));

        // シェア情報
        pet.setSharePrice(
                parseInteger(request.getParameter("sharePrice")));

        pet.setShareStartDate(
                parseDate(request.getParameter("shareStartDate")));

        pet.setShareEndDate(
                parseDate(request.getParameter("shareEndDate")));

        pet.setShareConditions(
                request.getParameter("shareConditions"));

        /*
         * DBへ登録
         */
        PetDAO dao = new PetDAO();

        System.out.println("===== ペット登録開始 =====");
        System.out.println("accountId = " + accountId);
        System.out.println("petName = " + pet.getPetName());
        System.out.println("petType = " + pet.getPetType());
        boolean result = dao.insertPet(pet);

        if (result) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/pet-register.html?success=true");

        } else {

            response.sendRedirect(
                    request.getContextPath()
                            + "/pet-register.html?error=true");
        }
    }

    /**
     * 日付を変換
     */
    private Date parseDate(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Date.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 小数を変換
     */
    private Double parseDouble(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 整数を変換
     */
    private Integer parseInteger(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}