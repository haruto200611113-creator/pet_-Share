package com.petshare;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/pet-knowledge-test")
public class PetKnowledgeTestServlet
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String QUESTIONS =
            "petKnowledgeQuestions";

    private static final String CURRENT =
            "petKnowledgeCurrentQuestion";

    private static final String SCORE =
            "petKnowledgeScore";

    /**
     * テスト開始
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        // ログイン確認
        if (session == null
                || session.getAttribute("loginUser") == null) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "ログインしてください。");

            return;
        }

        User loginUser =
                (User) session.getAttribute("loginUser");

        // すでに本会員ならテスト不要
        if (loginUser.getUserRank() == 2) {

            response.setContentType(
                    "application/json; charset=UTF-8");

            response.getWriter().print(
                    "{\"status\":\"already_member\"}");

            return;
        }

        // DBからランダム5問取得
        PetKnowledgeQuestionDAO dao =
                new PetKnowledgeQuestionDAO();

        List<PetKnowledgeQuestion> questions =
                dao.getRandomQuestions();

        if (questions.size() < 5) {

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "テスト問題を取得できませんでした。");

            return;
        }

        // セッションに保存
        session.setAttribute(
                QUESTIONS,
                questions);

        session.setAttribute(
                CURRENT, 0);

        session.setAttribute(
                SCORE, 0);

        // 正解を除いてJSONにする
        response.setContentType(
                "application/json; charset=UTF-8");

        StringBuilder json =
                new StringBuilder();

        json.append("{\"status\":\"start\",\"questions\":[");

        for (int i = 0;
                i < questions.size();
                i++) {

            PetKnowledgeQuestion q =
                    questions.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{");

            json.append("\"questionId\":")
                    .append(q.getQuestionId())
                    .append(",");

            json.append("\"questionText\":\"")
                    .append(escapeJson(
                            q.getQuestionText()))
                    .append("\",");

            json.append("\"choices\":[");

            json.append("\"")
                    .append(escapeJson(
                            q.getChoice1()))
                    .append("\",");

            json.append("\"")
                    .append(escapeJson(
                            q.getChoice2()))
                    .append("\",");

            json.append("\"")
                    .append(escapeJson(
                            q.getChoice3()))
                    .append("\",");

            json.append("\"")
                    .append(escapeJson(
                            q.getChoice4()))
                    .append("\"");

            json.append("]");

            json.append("}");
        }

        json.append("]}");

        response.getWriter().print(
                json.toString());
    }

    /**
     * 回答送信
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        // ログイン確認
        if (session == null
                || session.getAttribute("loginUser") == null) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "ログインしてください。");

            return;
        }

        User loginUser =
                (User) session.getAttribute("loginUser");

        // 本会員なら処理不要
        if (loginUser.getUserRank() == 2) {

            response.setContentType(
                    "application/json; charset=UTF-8");

            response.getWriter().print(
                    "{\"status\":\"already_member\"}");

            return;
        }

        @SuppressWarnings("unchecked")
        List<PetKnowledgeQuestion> questions =
                (List<PetKnowledgeQuestion>)
                        session.getAttribute(QUESTIONS);

        if (questions == null
                || questions.size() != 5) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "テストが開始されていません。");

            return;
        }

        String questionIndexText =
                request.getParameter(
                        "questionIndex");

        String answerText =
                request.getParameter("answer");

        if (questionIndexText == null
                || answerText == null) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "回答情報がありません。");

            return;
        }

        int questionIndex;
        int answer;

        try {

            questionIndex =
                    Integer.parseInt(
                            questionIndexText);

            answer =
                    Integer.parseInt(
                            answerText);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "回答形式が正しくありません。");

            return;
        }

        // 範囲確認
        if (questionIndex < 0
                || questionIndex >= questions.size()
                || answer < 1
                || answer > 4) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "不正な回答です。");

            return;
        }

        PetKnowledgeQuestion question =
                questions.get(questionIndex);

        int score =
                (int) session.getAttribute(
                        SCORE);

        // 正解判定
        if (answer ==
                question.getCorrectAnswer()) {

            score++;
        }

        session.setAttribute(
                SCORE,
                score);

        boolean lastQuestion =
                questionIndex ==
                        questions.size() - 1;

        response.setContentType(
                "application/json; charset=UTF-8");

        StringBuilder json =
                new StringBuilder();

        json.append("{");

        if (lastQuestion) {

            boolean passed =
                    score >= 4;

            json.append("\"status\":\"finished\",");
            json.append("\"score\":")
                    .append(score)
                    .append(",");
            json.append("\"passed\":")
                    .append(passed);

            // 合格した場合
            if (passed) {

                int accountId =
                        loginUser.getAccountId();

                UserDAO userDAO =
                        new UserDAO();

                boolean upgraded =
                        userDAO.updateUserRank(
                                accountId,
                                2);

                if (upgraded) {

                    // セッション上のユーザー情報も更新
                    loginUser.setUserRank(2);

                    session.setAttribute(
                            "loginUser",
                            loginUser);

                    json.append(",");
                    json.append(
                            "\"upgraded\":true");

                } else {

                    json.append(",");
                    json.append(
                            "\"upgraded\":false");
                }

            }

            // テスト情報を削除
            session.removeAttribute(
                    QUESTIONS);

            session.removeAttribute(
                    CURRENT);

            session.removeAttribute(
                    SCORE);

        } else {

            json.append(
                    "\"status\":\"next\",");

            json.append("\"score\":")
                    .append(score)
                    .append(",");

            json.append("\"nextQuestion\":")
                    .append(questionIndex + 1);
        }

        json.append("}");

        response.getWriter().print(
                json.toString());
    }

    /**
     * JSONエスケープ
     */
    private String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}