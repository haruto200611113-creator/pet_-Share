package com.petshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import java.util.ArrayList;
import java.util.List;

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

                String sql = "INSERT INTO pets (" +
                                "account_id, " +
                                "pet_name, " +
                                "pet_image, " +
                                "pet_type, " +
                                "breed, " +
                                "gender, " +
                                "birth_date, " +
                                "weight, " +
                                "personality, " +
                                "features, " +
                                "likes, " +
                                "dislikes, " +
                                "other_animals, " +
                                "children_compatibility, " +
                                "food_info, " +
                                "walk_info, " +
                                "toilet_info, " +
                                "alone_time, " +
                                "care_info, " +
                                "vaccination, " +
                                "neutering, " +
                                "medical_history, " +
                                "allergies, " +
                                "medication, " +
                                "precautions, " +
                                "share_price, " +
                                "share_start_date, " +
                                "share_end_date, " +
                                "share_conditions" +
                                ") VALUES (" +
                                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                "?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (
                                Connection conn = getConnection();
                                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                        int i = 1;

                        pstmt.setInt(i++, pet.getAccountId());

                        pstmt.setString(i++, pet.getPetName());
                        pstmt.setString(i++, pet.getPetImage());
                        pstmt.setString(i++, pet.getPetType());
                        pstmt.setString(i++, pet.getBreed());
                        pstmt.setString(i++, pet.getGender());

                        pstmt.setDate(i++, pet.getBirthDate());

                        if (pet.getWeight() != null) {
                                pstmt.setDouble(i++, pet.getWeight());
                        } else {
                                pstmt.setNull(i++, java.sql.Types.DECIMAL);
                        }

                        pstmt.setString(i++, pet.getPersonality());
                        pstmt.setString(i++, pet.getFeatures());
                        pstmt.setString(i++, pet.getLikes());
                        pstmt.setString(i++, pet.getDislikes());

                        pstmt.setString(i++, pet.getOtherAnimals());
                        pstmt.setString(i++, pet.getChildrenCompatibility());

                        pstmt.setString(i++, pet.getFoodInfo());
                        pstmt.setString(i++, pet.getWalkInfo());
                        pstmt.setString(i++, pet.getToiletInfo());
                        pstmt.setString(i++, pet.getAloneTime());
                        pstmt.setString(i++, pet.getCareInfo());

                        pstmt.setString(i++, pet.getVaccination());
                        pstmt.setString(i++, pet.getNeutering());
                        pstmt.setString(i++, pet.getMedicalHistory());
                        pstmt.setString(i++, pet.getAllergies());
                        pstmt.setString(i++, pet.getMedication());
                        pstmt.setString(i++, pet.getPrecautions());

                        if (pet.getSharePrice() != null) {
                                pstmt.setInt(i++, pet.getSharePrice());
                        } else {
                                pstmt.setNull(i++, java.sql.Types.INTEGER);
                        }

                        pstmt.setDate(i++, pet.getShareStartDate());
                        pstmt.setDate(i++, pet.getShareEndDate());

                        pstmt.setString(i++, pet.getShareConditions());

                        int result = pstmt.executeUpdate();

                        System.out.println("=================================");
                        System.out.println("ペット登録結果: " + result);
                        System.out.println("account_id: " + pet.getAccountId());
                        System.out.println("pet_name: " + pet.getPetName());
                        System.out.println("pet_type: " + pet.getPetType());
                        System.out.println("=================================");

                        return result > 0;

                } catch (SQLException e) {

                        System.out.println("=================================");
                        System.out.println("ペット登録でSQLエラーが発生しました");
                        System.out.println("エラー内容: " + e.getMessage());
                        System.out.println("SQLState: " + e.getSQLState());
                        System.out.println("ErrorCode: " + e.getErrorCode());
                        System.out.println("=================================");

                        e.printStackTrace();

                        return false;
                }
        }

        // =====================================
        // ペット一覧取得
        // =====================================

        public List<Pet> findAll() {

                List<Pet> pets = new ArrayList<>();

                String sql = """
                                SELECT *
                                FROM pets
                                ORDER BY created_at DESC
                                """;

                try (
                                Connection conn = getConnection();
                                PreparedStatement ps = conn.prepareStatement(sql);
                                ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {

                                Pet pet = new Pet();

                                pet.setPetId(rs.getInt("pet_id"));
                                pet.setAccountId(rs.getInt("account_id"));
                                pet.setPetName(rs.getString("pet_name"));
                                pet.setPetImage(rs.getString("pet_image"));
                                pet.setPetType(rs.getString("pet_type"));
                                pet.setBreed(rs.getString("breed"));
                                pet.setGender(rs.getString("gender"));
                                pet.setBirthDate(rs.getDate("birth_date"));
                                pet.setWeight(rs.getDouble("weight"));
                                pet.setPersonality(rs.getString("personality"));
                                pet.setFeatures(rs.getString("features"));
                                pet.setLikes(rs.getString("likes"));
                                pet.setDislikes(rs.getString("dislikes"));
                                pet.setOtherAnimals(rs.getString("other_animals"));
                                pet.setChildrenCompatibility(
                                                rs.getString("children_compatibility"));
                                pet.setFoodInfo(rs.getString("food_info"));
                                pet.setWalkInfo(rs.getString("walk_info"));
                                pet.setToiletInfo(rs.getString("toilet_info"));
                                pet.setAloneTime(rs.getString("alone_time"));
                                pet.setCareInfo(rs.getString("care_info"));
                                pet.setVaccination(rs.getString("vaccination"));
                                pet.setNeutering(rs.getString("neutering"));
                                pet.setMedicalHistory(
                                                rs.getString("medical_history"));
                                pet.setAllergies(rs.getString("allergies"));
                                pet.setMedication(rs.getString("medication"));
                                pet.setPrecautions(
                                                rs.getString("precautions"));

                                pet.setIsShared(
                                                rs.getBoolean("is_shared"));

                                pet.setSharePrice(
                                                rs.getInt("share_price"));

                                pet.setShareStartDate(
                                                rs.getDate(
                                                                "share_start_date"));
                                pet.setShareEndDate(
                                                rs.getDate("share_end_date"));
                                pet.setShareConditions(
                                                rs.getString("share_conditions"));
                                pet.setCreatedAt(
                                                rs.getTimestamp("created_at"));
                                pet.setUpdatedAt(
                                                rs.getTimestamp("updated_at"));

                                pets.add(pet);
                        }

                } catch (SQLException e) {

                        System.err.println(
                                        "ペット一覧取得中にSQLエラーが発生しました。");

                        e.printStackTrace();
                }

                return pets;
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

                                        pet.setPrecautions(rs.getString("precautions"));

                                        pet.setIsShared(rs.getBoolean("is_shared"));

                                        pet.setSharePrice(rs.getInt("share_price"));
                                        pet.setShareStartDate(
                                                        rs.getDate("share_start_date"));

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
        // =====================================
        // シェア状態更新
        // =====================================

        public boolean updateShareStatus(
                        int petId,
                        boolean isShared) {

                String sql = """
                                UPDATE pets
                                SET is_shared = ?
                                WHERE pet_id = ?
                                """;

                try (
                                Connection conn = getConnection();
                                PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setBoolean(1, isShared);
                        ps.setInt(2, petId);

                        return ps.executeUpdate() == 1;

                } catch (SQLException e) {

                        System.err.println(
                                        "ペットのシェア状態更新中にSQLエラーが発生しました。");

                        e.printStackTrace();

                        return false;
                }
        }

        // =====================================
        // アカウントIDからペット一覧を取得
        // =====================================

        public List<Pet> findByAccountId(int accountId) {

                String sql = """
                                SELECT *
                                FROM pets
                                WHERE account_id = ?
                                ORDER BY pet_id DESC
                                """;

                List<Pet> pets = new ArrayList<>();

                try (
                                Connection conn = getConnection();
                                PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setInt(1, accountId);

                        try (ResultSet rs = ps.executeQuery()) {

                                while (rs.next()) {

                                        Pet pet = new Pet();

                                        pet.setPetId(rs.getInt("pet_id"));
                                        pet.setAccountId(rs.getInt("account_id"));
                                        pet.setPetName(rs.getString("pet_name"));
                                        pet.setPetImage(rs.getString("pet_image"));
                                        pet.setPetType(rs.getString("pet_type"));
                                        pet.setBreed(rs.getString("breed"));
                                        pet.setGender(rs.getString("gender"));

                                        pet.setBirthDate(rs.getDate("birth_date"));
                                        pet.setWeight(rs.getDouble("weight"));

                                        pet.setPersonality(rs.getString("personality"));
                                        pet.setFeatures(rs.getString("features"));
                                        pet.setLikes(rs.getString("likes"));
                                        pet.setDislikes(rs.getString("dislikes"));

                                        pet.setOtherAnimals(
                                                        rs.getString("other_animals"));

                                        pet.setChildrenCompatibility(
                                                        rs.getString("children_compatibility"));

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

                                        pet.setIsShared(
                                                        rs.getBoolean("is_shared"));

                                        pet.setSharePrice(
                                                        rs.getInt("share_price"));

                                        pet.setShareStartDate(
                                                        rs.getDate("share_start_date"));

                                        pet.setShareEndDate(
                                                        rs.getDate("share_end_date"));

                                        pet.setShareConditions(
                                                        rs.getString("share_conditions"));

                                        pet.setCreatedAt(
                                                        rs.getTimestamp("created_at"));

                                        pet.setUpdatedAt(
                                                        rs.getTimestamp("updated_at"));

                                        pets.add(pet);
                                }
                        }

                } catch (SQLException e) {

                        System.err.println(
                                        "アカウントIDからペット一覧取得中にSQLエラーが発生しました。");

                        e.printStackTrace();
                }

                return pets;
        }

}