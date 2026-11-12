package com.petshare;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/pet-detail")
public class PetDetailServlet extends HttpServlet {

    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String petIdParam = request.getParameter("petId");

        // ==============================
        // petId確認
        // ==============================

        if (petIdParam == null || petIdParam.isBlank()) {

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
            // JSON出力
            // ==============================

            PrintWriter out = response.getWriter();

            out.print("{");

            out.print("\"petId\":");
            out.print(pet.getPetId());
            out.print(",");

            out.print("\"name\":\"");
            out.print(escapeJson(pet.getPetName()));
            out.print("\",");

            out.print("\"imagePath\":\"");
            out.print(escapeJson(pet.getPetImage()));
            out.print("\",");

            out.print("\"type\":\"");
            out.print(escapeJson(pet.getPetType()));
            out.print("\",");

            out.print("\"breed\":\"");
            out.print(escapeJson(pet.getBreed()));
            out.print("\",");

            out.print("\"gender\":\"");
            out.print(escapeJson(pet.getGender()));
            out.print("\",");

            out.print("\"age\":");
            out.print(calculateAge(pet.getBirthDate()));
            out.print(",");

            out.print("\"weight\":");
            out.print(
                    pet.getWeight() != null
                            ? pet.getWeight()
                            : 0);
            out.print(",");

            out.print("\"personality\":\"");
            out.print(escapeJson(pet.getPersonality()));
            out.print("\",");

            out.print("\"features\":\"");
            out.print(escapeJson(pet.getFeatures()));
            out.print("\",");

            out.print("\"likes\":\"");
            out.print(escapeJson(pet.getLikes()));
            out.print("\",");

            out.print("\"dislikes\":\"");
            out.print(escapeJson(pet.getDislikes()));
            out.print("\",");

            out.print("\"otherAnimals\":\"");
            out.print(escapeJson(pet.getOtherAnimals()));
            out.print("\",");

            out.print("\"childrenCompatibility\":\"");
            out.print(
                    escapeJson(
                            pet.getChildrenCompatibility()));
            out.print("\",");

            out.print("\"foodInfo\":\"");
            out.print(escapeJson(pet.getFoodInfo()));
            out.print("\",");

            out.print("\"walkInfo\":\"");
            out.print(escapeJson(pet.getWalkInfo()));
            out.print("\",");

            out.print("\"toiletInfo\":\"");
            out.print(escapeJson(pet.getToiletInfo()));
            out.print("\",");

            out.print("\"aloneTime\":\"");
            out.print(escapeJson(pet.getAloneTime()));
            out.print("\",");

            out.print("\"careInfo\":\"");
            out.print(escapeJson(pet.getCareInfo()));
            out.print("\",");

            out.print("\"vaccination\":\"");
            out.print(escapeJson(pet.getVaccination()));
            out.print("\",");

            out.print("\"neutering\":\"");
            out.print(escapeJson(pet.getNeutering()));
            out.print("\",");

            out.print("\"medicalHistory\":\"");
            out.print(
                    escapeJson(
                            pet.getMedicalHistory()));
            out.print("\",");

            out.print("\"allergies\":\"");
            out.print(escapeJson(pet.getAllergies()));
            out.print("\",");

            out.print("\"medication\":\"");
            out.print(escapeJson(pet.getMedication()));
            out.print("\",");

            out.print("\"precautions\":\"");
            out.print(escapeJson(pet.getPrecautions()));
            out.print("\",");

            out.print("\"sharePrice\":");
            out.print(
                    pet.getSharePrice() != null
                            ? pet.getSharePrice()
                            : 0);
            out.print(",");

            out.print("\"shareStartDate\":\"");
            out.print(
                    escapeJson(
                            String.valueOf(
                                    pet.getShareStartDate())));
            out.print("\",");

            out.print("\"shareEndDate\":\"");
            out.print(
                    escapeJson(
                            String.valueOf(
                                    pet.getShareEndDate())));
            out.print("\",");

            out.print("\"shareConditions\":\"");
            out.print(
                    escapeJson(
                            pet.getShareConditions()));
            out.print("\"");

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