document.addEventListener("DOMContentLoaded", function () {

    console.log("Pet Knowledge Test JS 読み込みOK");

    // ==============================
    // HTML要素
    // ==============================

    const startCard =
        document.getElementById("startCard");

    const startTestButton =
        document.getElementById("startTestButton");

    const testCard =
        document.getElementById("testCard");

    const resultCard =
        document.getElementById("resultCard");

    const questionNumber =
        document.getElementById("questionNumber");

    const questionCount =
        document.getElementById("questionCount");

    const timer =
        document.getElementById("timer");

    const questionText =
        document.getElementById("questionText");

    const choices =
        document.getElementById("choices");

    const resultIcon =
        document.getElementById("resultIcon");

    const resultTitle =
        document.getElementById("resultTitle");

    const resultMessage =
        document.getElementById("resultMessage");

    const scoreElement =
        document.getElementById("score");

    const resultDetail =
        document.getElementById("resultDetail");

    const upgradeButton =
        document.getElementById("upgradeButton");

    const retryButton =
        document.getElementById("retryButton");


    // ==============================
    // テスト用変数
    // ==============================

    let questions = [];
    let currentQuestionIndex = 0;
    let timerInterval = null;
    let timeLeft = 10;
    let answerLocked = false;


    // ==============================
    // テスト開始ボタン
    // ==============================

    startTestButton.addEventListener(
        "click",
        startTest
    );


    // ==============================
    // テスト開始
    // ==============================

    async function startTest() {

        console.log("開始ボタンが押されました");

        try {

            startTestButton.disabled = true;

            startTestButton.textContent =
                "問題を読み込んでいます...";


            const response =
                await fetch(
                    "pet-knowledge-test",
                    {
                        method: "GET",
                        credentials: "same-origin"
                    }
                );


            console.log(
                "GETステータス:",
                response.status
            );


            if (!response.ok) {

                throw new Error(
                    "テスト開始に失敗しました。"
                );

            }


            const data =
                await response.json();


            console.log(
                "取得したデータ:",
                data
            );


            // ==============================
            // すでに本会員
            // ==============================

            if (
                data.status ===
                "already_member"
            ) {

                alert(
                    "すでに本会員です。"
                );

                window.location.href =
                    "mypage.jsp";

                return;
            }


            // ==============================
            // 問題取得
            // ==============================

            questions =
                data.questions || [];


            console.log(
                "取得した問題:",
                questions
            );


            if (
                questions.length !== 30
            ) {

                throw new Error(
                    "テスト問題を正しく取得できませんでした。"
                );

            }


            // ==============================
            // 画面切り替え
            // ==============================

            startCard.style.display =
                "none";

            testCard.style.display =
                "block";

            resultCard.style.display =
                "none";


            currentQuestionIndex = 0;

            showQuestion();

        }

        catch (error) {

            console.error(
                "テスト開始エラー:",
                error
            );


            alert(
                "テストを開始できませんでした。\n"
                + error.message
            );


            startTestButton.disabled =
                false;

            startTestButton.textContent =
                "テストを開始する";

        }

    }


    // ==============================
    // 問題表示
    // ==============================

    function showQuestion() {

        stopTimer();

        answerLocked = false;


        const question =
            questions[
                currentQuestionIndex
            ];


        if (!question) {

            console.error(
                "問題データがありません:",
                currentQuestionIndex
            );

            return;

        }


        // ==============================
        // 問題番号
        // ==============================

        questionNumber.textContent =
            "第" +
            (currentQuestionIndex + 1) +
            "問";


        questionCount.textContent =
            (currentQuestionIndex + 1) +
            " / " +
            questions.length;


        // ==============================
        // 問題文
        // ==============================

        questionText.textContent =
            question.questionText;


        // ==============================
        // 選択肢
        // ==============================

        choices.innerHTML = "";


        question.choices.forEach(
            function (choice, index) {

                const button =
                    document.createElement(
                        "button"
                    );


                button.type =
                    "button";

                button.className =
                    "choice-button";

                button.textContent =
                    choice;

                button.dataset.answer =
                    index + 1;


                button.addEventListener(
                    "click",
                    function () {

                        submitAnswer(
                            index + 1
                        );

                    }
                );


                choices.appendChild(
                    button
                );

            }
        );


        // ==============================
        // タイマー開始
        // ==============================

        startTimer();

    }


    // ==============================
    // タイマー開始
    // ==============================

    function startTimer() {

        stopTimer();

        timeLeft = 10;

        timer.textContent =
            timeLeft;


        timerInterval =
            setInterval(
                function () {

                    timeLeft--;

                    timer.textContent =
                        timeLeft;


                    if (
                        timeLeft <= 0
                    ) {

                        stopTimer();

                        // 時間切れは不正解
                        submitAnswer(null);

                    }

                },
                1000
            );

    }


    // ==============================
    // タイマー停止
    // ==============================

    function stopTimer() {

        if (
            timerInterval !== null
        ) {

            clearInterval(
                timerInterval
            );

            timerInterval = null;

        }

    }


    // ==============================
    // 回答送信
    // ==============================

    async function submitAnswer(
        answer
    ) {

        if (
            answerLocked
        ) {

            return;

        }


        answerLocked = true;

        stopTimer();


        // ==============================
        // 選択肢を無効化
        // ==============================

        const buttons =
            choices.querySelectorAll(
                "button"
            );


        buttons.forEach(
            function (button) {

                button.disabled =
                    true;

            }
        );


        try {

            const formData =
                new URLSearchParams();


            formData.append(
                "questionIndex",
                currentQuestionIndex
            );


            formData.append(
                "answer",
                answer === null
                    ? "0"
                    : answer
            );


            console.log(
                "回答送信:",
                currentQuestionIndex,
                answer
            );


            const response =
                await fetch(
                    "pet-knowledge-test",
                    {
                        method: "POST",

                        credentials:
                            "same-origin",

                        headers: {
                            "Content-Type":
                                "application/x-www-form-urlencoded"
                        },

                        body:
                            formData.toString()
                    }
                );


            if (
                !response.ok
            ) {

                throw new Error(
                    "回答送信に失敗しました。"
                );

            }


            const data =
                await response.json();


            console.log(
                "回答結果:",
                data
            );


            // ==============================
            // テスト終了
            // ==============================

            if (
                data.status ===
                "finished"
            ) {

                showResult(
                    data
                );

                return;

            }


            // ==============================
            // 次の問題
            // ==============================

            if (
                data.status ===
                "next"
            ) {

                currentQuestionIndex =
                    data.nextQuestion;

                showQuestion();

                return;

            }


            throw new Error(
                "不正な回答結果です。"
            );

        }

        catch (error) {

            console.error(
                "回答送信エラー:",
                error
            );


            alert(
                "回答の送信中にエラーが発生しました。\n"
                + error.message
            );


            answerLocked =
                false;


            buttons.forEach(
                function (button) {

                    button.disabled =
                        false;

                }
            );


            startTimer();

        }

    }


    // ==============================
    // 結果表示
    // ==============================

    function showResult(data) {

        stopTimer();


        testCard.style.display =
            "none";

        resultCard.style.display =
            "block";


        const score =
            data.score;


        scoreElement.textContent =
            score + " / 30";


        // ==============================
        // 合格
        // ==============================

        if (
            data.passed &&
            data.upgraded
        ) {

            resultIcon.textContent =
                "🎉";

            resultTitle.textContent =
                "本会員への昇格成功！";

            resultMessage.textContent =
                "ペット知識テストに合格しました。";

            resultDetail.textContent =
                "あなたは本会員になりました。";


            upgradeButton.style.display =
                "block";

            retryButton.style.display =
                "none";

            return;

        }


        // ==============================
        // 合格したがDB更新失敗
        // ==============================

        if (
            data.passed &&
            !data.upgraded
        ) {

            resultIcon.textContent =
                "⚠️";

            resultTitle.textContent =
                "テストには合格しました";

            resultMessage.textContent =
                "しかし、本会員への変更に失敗しました。";

            resultDetail.textContent =
                "もう一度お試しください。";


            upgradeButton.style.display =
                "none";

            retryButton.style.display =
                "block";

            return;

        }


        // ==============================
        // 不合格
        // ==============================

        resultIcon.textContent =
            "😢";

        resultTitle.textContent =
            "不合格";

        resultMessage.textContent =
            "本会員になるには90%以上正解する必要があります。";

        resultDetail.textContent =
            "今回は" +
            score +
            "問正解でした。";


        upgradeButton.style.display =
            "none";

        retryButton.style.display =
            "block";

    }


    // ==============================
    // 本会員になった後
    // ==============================

    if (
        upgradeButton
    ) {

        upgradeButton.addEventListener(
            "click",
            function () {

                window.location.href =
                    "mypage.jsp";

            }
        );

    }


    // ==============================
    // 再受験
    // ==============================

    if (
        retryButton
    ) {

        retryButton.addEventListener(
            "click",
            function () {

                resultCard.style.display =
                    "none";

                startCard.style.display =
                    "block";


                startTestButton.disabled =
                    false;

                startTestButton.textContent =
                    "もう一度テストを受ける";


                questions = [];

                currentQuestionIndex =
                    0;

            }
        );

    }

});