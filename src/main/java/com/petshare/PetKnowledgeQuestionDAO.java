package com.petshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PetKnowledgeQuestionDAO {

    private static final String URL =
            "jdbc:mysql://mysql-1589c60e-petshare-project.f.aivencloud.com:21440/defaultdb"
            + "?sslMode=VERIFY_CA"
            + "&trustCertificateKeyStoreUrl=file:/tmp/aiven-truststore-render.p12"
            + "&trustCertificateKeyStoreType=PKCS12"
            + "&trustCertificateKeyStorePassword="
            + System.getenv("AIVEN_TRUSTSTORE_PASSWORD");

    private static final String USER =
            System.getenv("AIVEN_DB_USER");

    private static final String PASSWORD =
            System.getenv("AIVEN_DB_PASSWORD");

    /**
     * DBからランダムに5問取得する
     */
    public List<PetKnowledgeQuestion> getRandomQuestions() {

        List<PetKnowledgeQuestion> questions =
                new ArrayList<>();

        String sql =
                "SELECT question_id, question_text, "
                + "choice_1, choice_2, choice_3, choice_4, "
                + "correct_answer, category "
                + "FROM pet_knowledge_questions "
                + "WHERE is_active = TRUE "
                + "ORDER BY RAND() "
                + "LIMIT 5";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection con =
                        DriverManager.getConnection(
                                URL, USER, PASSWORD);

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
            ) {

                while (rs.next()) {

                    PetKnowledgeQuestion question =
                            new PetKnowledgeQuestion();

                    question.setQuestionId(
                            rs.getInt("question_id"));

                    question.setQuestionText(
                            rs.getString("question_text"));

                    question.setChoice1(
                            rs.getString("choice_1"));

                    question.setChoice2(
                            rs.getString("choice_2"));

                    question.setChoice3(
                            rs.getString("choice_3"));

                    question.setChoice4(
                            rs.getString("choice_4"));

                    question.setCorrectAnswer(
                            rs.getInt("correct_answer"));

                    question.setCategory(
                            rs.getString("category"));

                    questions.add(question);
                }
            }

        } catch (ClassNotFoundException e) {

            System.err.println(
                    "MySQL JDBCドライバーが見つかりません。");

            e.printStackTrace();

        } catch (SQLException e) {

            System.err.println(
                    "ペット知識問題の取得に失敗しました。");

            e.printStackTrace();
        }

        return questions;
    }

    /**
     * 問題IDから1問取得する
     */
    public PetKnowledgeQuestion findById(
            int questionId) {

        String sql =
                "SELECT question_id, question_text, "
                + "choice_1, choice_2, choice_3, choice_4, "
                + "correct_answer, category "
                + "FROM pet_knowledge_questions "
                + "WHERE question_id = ? "
                + "AND is_active = TRUE";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection con =
                        DriverManager.getConnection(
                                URL, USER, PASSWORD);

                PreparedStatement ps =
                        con.prepareStatement(sql)
            ) {

                ps.setInt(1, questionId);

                try (ResultSet rs =
                        ps.executeQuery()) {

                    if (rs.next()) {

                        PetKnowledgeQuestion question =
                                new PetKnowledgeQuestion();

                        question.setQuestionId(
                                rs.getInt("question_id"));

                        question.setQuestionText(
                                rs.getString("question_text"));

                        question.setChoice1(
                                rs.getString("choice_1"));

                        question.setChoice2(
                                rs.getString("choice_2"));

                        question.setChoice3(
                                rs.getString("choice_3"));

                        question.setChoice4(
                                rs.getString("choice_4"));

                        question.setCorrectAnswer(
                                rs.getInt("correct_answer"));

                        question.setCategory(
                                rs.getString("category"));

                        return question;
                    }
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "ペット知識問題の取得に失敗しました。");

            e.printStackTrace();
        }

        return null;
    }
}