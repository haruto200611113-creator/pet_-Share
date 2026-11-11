package com.petshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class PetDAO {

    // =====================================
    // Aiven MySQL接続設定
    // =====================================

    // TrustStoreのパスワード
    private static final String TRUSTSTORE_PASSWORD = System.getenv("AIVEN_TRUSTSTORE_PASSWORD");

    // Renderの環境変数から取得するBase64化されたTrustStore
    private static final String TRUSTSTORE_BASE64 = System.getenv("AIVEN_TRUSTSTORE_BASE64");

    // Render上で復元するTrustStoreの保存先
    private static final String TRUSTSTORE_PATH = createTrustStoreFile();

    // Aiven MySQL接続URL
    private static final String URL = "jdbc:mysql://mysql-1589c60e-petshare-project.f.aivencloud.com:21440/defaultdb"
            + "?sslMode=VERIFY_CA"
            + "&trustCertificateKeyStoreUrl=file:" + TRUSTSTORE_PATH
            + "&trustCertificateKeyStoreType=PKCS12"
            + "&trustCertificateKeyStorePassword=" + TRUSTSTORE_PASSWORD;

    // データベースユーザー
    private static final String USER = System.getenv("AIVEN_DB_USER");

    // データベースパスワード
    private static final String PASSWORD = System.getenv("AIVEN_DB_PASSWORD");

    // =====================================
    // Base64 → TrustStore復元
    // =====================================

    private static String createTrustStoreFile() {

        if (TRUSTSTORE_BASE64 == null
                || TRUSTSTORE_BASE64.isBlank()) {

            throw new ExceptionInInitializerError(
                    "AIVEN_TRUSTSTORE_BASE64 が設定されていません");
        }

        try {

            byte[] trustStoreBytes = Base64.getMimeDecoder().decode(
                    TRUSTSTORE_BASE64);

            Path path = Path.of("/tmp/aiven-truststore-render.p12");

            Files.write(path, trustStoreBytes);

            return path.toAbsolutePath().toString();

        } catch (Exception e) {

            throw new ExceptionInInitializerError(
                    "TrustStoreの復元に失敗しました: "
                            + e.getMessage());
        }
    }

    // =====================================
    // DB接続設定確認
    // =====================================

    private boolean isDbConfigAvailable() {

        return USER != null
                && !USER.isBlank()
                && PASSWORD != null
                && !PASSWORD.isBlank();
    }

    // =====================================
    // データベース接続
    // =====================================

    private Connection getConnection()
            throws SQLException {

        if (!isDbConfigAvailable()) {

            throw new SQLException(
                    "Aivenの接続情報が設定されていません。");
        }

        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                    "MySQL JDBCドライバーの読み込みに失敗しました。",
                    e);
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD);
    }

    // =====================================
    // ペット登録
    // =====================================

    public boolean insertPet(Pet pet) {

        if (pet == null) {

            System.err.println(
                    "ペット情報がありません。");

            return false;
        }

        String sql = """
                INSERT INTO pets (
                    account_id,
                    pet_name,
                    pet_image,
                    pet_type,
                    breed,
                    gender,
                    birth_date,
                    weight,
                    personality,
                    features,
                    likes,
                    dislikes,
                    other_animals,
                    children_compatibility,
                    food_info,
                    walk_info,
                    toilet_info,
                    alone_time,
                    care_info,
                    vaccination,
                    neutering,
                    medical_history,
                    allergies,
                    medication,
                    precautions,
                    share_price,
                    share_start_date,
                    share_end_date,
                    share_conditions
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?
                )
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;

            ps.setInt(i++, pet.getAccountId());

            ps.setString(i++, pet.getPetName());
            ps.setString(i++, pet.getPetImage());
            ps.setString(i++, pet.getPetType());
            ps.setString(i++, pet.getBreed());
            ps.setString(i++, pet.getGender());

            // 生年月日
            if (pet.getBirthDate() != null) {

                ps.setDate(
                        i++,
                        pet.getBirthDate());

            } else {

                ps.setNull(
                        i++,
                        java.sql.Types.DATE);
            }

            // 体重
            if (pet.getWeight() > 0) {

                ps.setDouble(
                        i++,
                        pet.getWeight());

            } else {

                ps.setNull(
                        i++,
                        java.sql.Types.DECIMAL);
            }

            // 性格・特徴
            ps.setString(
                    i++,
                    pet.getPersonality());

            ps.setString(
                    i++,
                    pet.getFeatures());

            ps.setString(
                    i++,
                    pet.getLikes());

            ps.setString(
                    i++,
                    pet.getDislikes());

            // 相性
            ps.setString(
                    i++,
                    pet.getOtherAnimals());

            ps.setString(
                    i++,
                    pet.getChildrenCompatibility());

            // 飼育情報
            ps.setString(
                    i++,
                    pet.getFoodInfo());

            ps.setString(
                    i++,
                    pet.getWalkInfo());

            ps.setString(
                    i++,
                    pet.getToiletInfo());

            ps.setString(
                    i++,
                    pet.getAloneTime());

            ps.setString(
                    i++,
                    pet.getCareInfo());

            // 健康・安全情報
            ps.setString(
                    i++,
                    pet.getVaccination());

            ps.setString(
                    i++,
                    pet.getNeutering());

            ps.setString(
                    i++,
                    pet.getMedicalHistory());

            ps.setString(
                    i++,
                    pet.getAllergies());

            ps.setString(
                    i++,
                    pet.getMedication());

            ps.setString(
                    i++,
                    pet.getPrecautions());

            // シェア情報
            ps.setInt(
                    i++,
                    pet.getSharePrice());

            if (pet.getShareStartDate() != null) {

                ps.setDate(
                        i++,
                        pet.getShareStartDate());

            } else {

                ps.setNull(
                        i++,
                        java.sql.Types.DATE);
            }

            if (pet.getShareEndDate() != null) {

                ps.setDate(
                        i++,
                        pet.getShareEndDate());

            } else {

                ps.setNull(
                        i++,
                        java.sql.Types.DATE);
            }

            ps.setString(
                    i++,
                    pet.getShareConditions());

            int result = ps.executeUpdate();

            return result > 0;

        } catch (SQLException e) {

            System.err.println(
                    "ペット登録中にSQLエラーが発生しました。");

            e.printStackTrace();

            return false;
        }
    }

    // =====================================
    // ペットIDから取得
    // =====================================

    public Pet findById(int petId) {

        String sql = """
                SELECT *
                FROM pets
                WHERE pet_id = ?
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, petId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Pet pet = new Pet();

                    pet.setPetId(
                            rs.getInt("pet_id"));

                    pet.setAccountId(
                            rs.getInt("account_id"));

                    pet.setPetName(
                            rs.getString("pet_name"));

                    pet.setPetImage(
                            rs.getString("pet_image"));

                    pet.setPetType(
                            rs.getString("pet_type"));

                    pet.setBreed(
                            rs.getString("breed"));

                    pet.setGender(
                            rs.getString("gender"));

                    pet.setBirthDate(
                            rs.getDate("birth_date"));

                    pet.setWeight(
                            rs.getDouble("weight"));

                    pet.setPersonality(
                            rs.getString("personality"));

                    pet.setFeatures(
                            rs.getString("features"));

                    pet.setLikes(
                            rs.getString("likes"));

                    pet.setDislikes(
                            rs.getString("dislikes"));

                    pet.setOtherAnimals(
                            rs.getString("other_animals"));

                    pet.setChildrenCompatibility(
                            rs.getString(
                                    "children_compatibility"));

                    pet.setFoodInfo(
                            rs.getString("food_info"));

                    pet.setWalkInfo(
                            rs.getString("walk_info"));

                    pet.setToiletInfo(
                            rs.getString("toilet_info"));

                    pet.setAloneTime(
                            rs.getString("alone_time"));

                    pet.setCareInfo(
                            rs.getString("care_info"));

                    pet.setVaccination(
                            rs.getString("vaccination"));

                    pet.setNeutering(
                            rs.getString("neutering"));

                    pet.setMedicalHistory(
                            rs.getString("medical_history"));

                    pet.setAllergies(
                            rs.getString("allergies"));

                    pet.setMedication(
                            rs.getString("medication"));

                    pet.setPrecautions(
                            rs.getString("precautions"));

                    pet.setSharePrice(
                            rs.getInt("share_price"));

                    pet.setShareStartDate(
                            rs.getDate(
                                    "share_start_date"));

                    pet.setShareEndDate(
                            rs.getDate(
                                    "share_end_date"));

                    pet.setShareConditions(
                            rs.getString(
                                    "share_conditions"));

                    pet.setCreatedAt(
                            rs.getTimestamp(
                                    "created_at"));

                    pet.setUpdatedAt(
                            rs.getTimestamp(
                                    "updated_at"));

                    return pet;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "ペット取得中にSQLエラーが発生しました。");

            e.printStackTrace();
        }

        return null;
    }
}