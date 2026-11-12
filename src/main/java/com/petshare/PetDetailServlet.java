package com.petshare;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/pet-detail")
public class PetDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PetDAO petDAO = new PetDAO();

    // 新しい時間単位のシェア情報を取得するDAO
    private final PetShareDAO petShareDAO = new PetShareDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(
                "application/json; charset=UTF-8");

        response.setCharacterEncoding("UTF-8");

        String petIdParam = request.getParameter("petId");

        // ==============================
        // petId確認
        // ==============================

        if (petIdParam == null ||
                petIdParam.isBlank()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\"petIdが指定されていません\"}");

            return;
        }

        try {

            int petId = Integer.parseInt(petIdParam);

            // ==============================
            // DBからペット取得
            // ==============================

            Pet pet = petDAO.findById(petId);

            if (pet == null) {

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND);

                response.getWriter().write(
                        "{\"error\":\"ペットが見つかりません\"}");

                return;
            }

            // ==============================
            // 新しいシェア情報取得
            // ==============================

            PetShare petShare = petShareDAO.findActiveByPetId(
                    petId);

            // ==============================
            // JSON出力
            // ==============================

            PrintWriter out = response.getWriter();

            out.print("{");

            // ==============================
            // 基本情報
            // ==============================

            out.print("\"petId\":");
            out.print(pet.getPetId());
            out.print(",");

            out.print("\"name\":\"");
            out.print(
                    escapeJson(
                            pet.getPetName()));
            out.print("\",");

            out.print("\"imagePath\":\"");
            out.print(
                    escapeJson(
                            pet.getPetImage()));
            out.print("\",");

            out.print("\"type\":\"");
            out.print(
                    escapeJson(
                            pet.getPetType()));
            out.print("\",");

            out.print("\"breed\":\"");
            out.print(
                    escapeJson(
                            pet.getBreed()));
            out.print("\",");

            out.print("\"gender\":\"");
            out.print(
                    escapeJson(
                            pet.getGender()));
            out.print("\",");

            // ==============================
            // 年齢
            // ==============================

            out.print("\"age\":");
            out.print(
                    calculateAge(
                            pet.getBirthDate()));
            out.print(",");

            // ==============================
            // 体重
            // ==============================

            out.print("\"weight\":");

            if (pet.getWeight() != null) {
                out.print(
                        pet.getWeight());
            } else {
                out.print("0");
            }

            out.print(",");

            // ==============================
            // 性格
            // ==============================

            out.print("\"personality\":\"");
            out.print(
                    escapeJson(
                            pet.getPersonality()));
            out.print("\",");

            // ==============================
            // 特徴
            // ==============================

            out.print("\"features\":\"");
            out.print(
                    escapeJson(
                            pet.getFeatures()));
            out.print("\",");

            // ==============================
            // 好きなもの
            // ==============================

            out.print("\"likes\":\"");
            out.print(
                    escapeJson(
                            pet.getLikes()));
            out.print("\",");

            // ==============================
            // 苦手なもの
            // ==============================

            out.print("\"dislikes\":\"");
            out.print(
                    escapeJson(
                            pet.getDislikes()));
            out.print("\",");

            // ==============================
            // 他の動物
            // ==============================

            out.print("\"otherAnimals\":\"");
            out.print(
                    escapeJson(
                            pet.getOtherAnimals()));
            out.print("\",");

            // ==============================
            // 子どもとの相性
            // ==============================

            out.print("\"childrenCompatibility\":\"");
            out.print(
                    escapeJson(
                            pet.getChildrenCompatibility()));
            out.print("\",");

            // ==============================
            // 食事
            // ==============================

            out.print("\"foodInfo\":\"");
            out.print(
                    escapeJson(
                            pet.getFoodInfo()));
            out.print("\",");

            // ==============================
            // 散歩
            // ==============================

            out.print("\"walkInfo\":\"");
            out.print(
                    escapeJson(
                            pet.getWalkInfo()));
            out.print("\",");

            // ==============================
            // トイレ
            // ==============================

            out.print("\"toiletInfo\":\"");
            out.print(
                    escapeJson(
                            pet.getToiletInfo()));
            out.print("\",");

            // ==============================
            // 留守番
            // ==============================

            out.print("\"aloneTime\":\"");
            out.print(
                    escapeJson(
                            pet.getAloneTime()));
            out.print("\",");

            // ==============================
            // お世話
            // ==============================

            out.print("\"careInfo\":\"");
            out.print(
                    escapeJson(
                            pet.getCareInfo()));
            out.print("\",");

            // ==============================
            // ワクチン
            // ==============================

            out.print("\"vaccination\":\"");
            out.print(
                    escapeJson(
                            pet.getVaccination()));
            out.print("\",");

            // ==============================
            // 去勢・避妊
            // ==============================

            out.print("\"neutering\":\"");
            out.print(
                    escapeJson(
                            pet.getNeutering()));
            out.print("\",");

            // ==============================
            // 既往歴
            // ==============================

            out.print("\"medicalHistory\":\"");
            out.print(
                    escapeJson(
                            pet.getMedicalHistory()));
            out.print("\",");

            // ==============================
            // アレルギー
            // ==============================

            out.print("\"allergies\":\"");
            out.print(
                    escapeJson(
                            pet.getAllergies()));
            out.print("\",");

            // ==============================
            // 薬
            // ==============================

            out.print("\"medication\":\"");
            out.print(
                    escapeJson(
                            pet.getMedication()));
            out.print("\",");

            // ==============================
            // 注意事項
            // ==============================

            out.print("\"precautions\":\"");
            out.print(
                    escapeJson(
                            pet.getPrecautions()));
            out.print("\",");

            // ==============================
            // 旧シェア料金
            // ==============================

            out.print("\"sharePrice\":");

            if (pet.getSharePrice() != null) {

                out.print(
                        pet.getSharePrice());

            } else {

                out.print("0");
            }

            out.print(",");

            // ==============================
            // 旧シェア開始日
            // ※互換性維持のため残す
            // ==============================

            out.print("\"shareStartDate\":\"");

            out.print(
                    escapeJson(
                            String.valueOf(
                                    pet.getShareStartDate())));

            out.print("\",");

            // ==============================
            // 旧シェア終了日
            // ※互換性維持のため残す
            // ==============================

            out.print("\"shareEndDate\":\"");

            out.print(
                    escapeJson(
                            String.valueOf(
                                    pet.getShareEndDate())));

            out.print(",");

            // ==============================
            // 新しい時間単位シェア情報
            // ==============================

            if (petShare != null) {

                out.print("\"shareAvailable\":true,");

                out.print("\"availableStart\":\"");
                out.print(
                        escapeJson(
                                petShare
                                        .getAvailableStart()
                                        .toString()));
                out.print("\",");

                out.print("\"availableEnd\":\"");
                out.print(
                        escapeJson(
                                petShare
                                        .getAvailableEnd()
                                        .toString()));
                out.print("\"");

            } else {

                out.print("\"shareAvailable\":false,");
                out.print("\"availableStart\":\"\",");
                out.print("\"availableEnd\":\"\"");
            }

            out.print(",");

            // ==============================
            // シェア条件
            // ==============================

            out.print("\"shareConditions\":\"");

            out.print(
                    escapeJson(
                            pet.getShareConditions()));

            out.print("\"");

            // ==============================
            // JSON終了
            // ==============================

            out.print("}");

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\"petIdが不正です\"}");

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            response.getWriter().write(
                    "{\"error\":\"ペット情報の取得に失敗しました\"}");
        }
    }

    // ==============================
    // JSONエスケープ
    // ==============================

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    // ==============================
    // 年齢計算
    // ==============================

    private int calculateAge(Date birthDate) {

        if (birthDate == null) {
            return 0;
        }

        LocalDate birth = birthDate.toLocalDate();

        LocalDate today = LocalDate.now();

        return Period.between(
                birth,
                today).getYears();
    }
}