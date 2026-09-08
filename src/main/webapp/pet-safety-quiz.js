const questions = [
    ["犬がふらつき、呼吸が荒く、歯ぐきが赤紫色です。休ませても改善しません。", ["水を飲ませて観察", "冷水をかけて帰宅", "運動を中止し記録して飼い主と病院へ連絡"], 2],
    ["猫が何度もトイレに入り、尿がほとんど出ません。", ["腹部を押す", "水を制限する", "記録して緊急相談先と病院へ連絡"], 2],
    ["投薬を2時間遅れ、薬や次回時刻が不明です。", ["次回分と倍量にする", "報告せず与える", "薬名・量・時刻を記録し与える前に確認する"], 2],
    ["預かり犬が固まり唸り、相手が『挨拶だけ』と近づけようとしています。", ["任せて近づける", "強く引く", "距離を取り接触を断る"], 2],
    ["チョコレートを食べた疑いがありますが、量は不明で無症状です。", ["様子を見る", "牛乳を飲ませる", "種類・時刻・量・体重を確認し吐かせず相談する"], 2],
    ["預かり犬が車道方向へ脱走しました。", ["車道へ走って追う", "安全な位置を確保し飼い主等へ連絡する", "見つけてから報告する"], 1],
    ["高湿度の日、短い散歩でパンティングが続きます。", ["予定を完了する", "水を制限する", "散歩を切り上げ観察し回復しなければ相談する"], 2],
    ["猫が耳を伏せ、瞳孔が開き、体を低くしています。", ["正面から抱く", "隠れ場所を塞ぐ", "逃げ道と高い場所を確保し接触を避ける"], 2],
    ["嘔吐物に赤いものが混じりましたが、その後は元気です。", ["普段通り食べさせる", "内容・回数・時刻を記録して連絡する", "人用の薬を与える"], 1],
    ["首輪が緩み、犬も興奮しています。", ["車道側で捕まえる", "リードを放す", "安全な囲いへ誘導し二重に確保する"], 2],
    ["給餌量の指示と袋の表示が違い、犬には持病があります。", ["多い方を与える", "少ない方を与える", "飼い主へ確認し決めるまで与えない"], 2],
    ["皮膚の発疹があり、同居ペットにも接触しました。", ["全員に市販薬", "接触を続ける", "接触を分け、共有を止めて手洗い後に連絡する"], 2],
    ["地震後、玄関が開いています。", ["扉を全開にする", "ペットを確保し脱走と落下物を確認する", "捕まえず見守る"], 1],
    ["呼吸困難、歯ぐきの色の変化、意識低下があります。", ["水を与える", "仰向けに固定する", "刺激を避け気道を確認し緊急連絡する"], 2],
    ["不明な物を拾い食いし、口内に残っているようです。", ["指を奥まで入れる", "吐かせず物・時刻・量を記録して相談する", "水を大量に飲ませる"], 1],
    ["許可を得て写真を投稿する場合でも避けるべき情報は？", ["ペットの名前", "指定ニックネーム", "自宅外観・現在地・散歩時間"], 2],
    ["同居犬がいることを伝え忘れていました。", ["当日会わせる", "短時間なら隔離不要", "健康・隔離設備・接触条件を共有し同意を得る"], 2],
    ["飲水量・尿量・食欲が変わり、連絡可能時間外です。", ["時間まで待つ", "水や食事を制限する", "記録し緊急性を判断して必要なら連絡する"], 2],
    ["飼い主の説明と症状が食い違います。", ["病名を決める", "説明だけ採用する", "事実と推測を分けて記録し確認する"], 2],
    ["安全な環境を維持できず、預かり継続が困難です。", ["無断で第三者へ預ける", "ペットを残して退出する", "危険を減らして監督し引き渡しを合意する"], 2]
];

const answers = Array(questions.length).fill(null);
const questionContainer = document.getElementById("quizQuestions");
const progress = document.getElementById("quizProgress");
const status = document.getElementById("quizStatus");
const returnLink = document.getElementById("quizReturn");
const scoreButton = document.getElementById("scoreButton");

function clearReviewStyles() {
    document.querySelectorAll('.quiz-option').forEach((label) => {
        label.classList.remove('wrong-choice', 'correct-choice');
    });
}

function showReviewResults() {
    clearReviewStyles();
    questions.forEach((question, index) => {
        const selectedAnswer = answers[index];
        if (selectedAnswer === null) {
            return;
        }

        const optionInputs = document.querySelectorAll(`input[name="quiz-${index}"]`);
        optionInputs.forEach((input) => {
            const optionIndex = Number(input.value);
            const optionLabel = input.closest('.quiz-option');
            if (!optionLabel) {
                return;
            }

            if (optionIndex === question[2]) {
                optionLabel.classList.add('correct-choice');
            }

            if (optionIndex === selectedAnswer && optionIndex !== question[2]) {
                optionLabel.classList.add('wrong-choice');
            }
        });
    });
}

function resetQuiz(message) {
    answers.fill(null);
    sessionStorage.removeItem("quizAnswersV2");
    localStorage.removeItem("petCareQuizPassed");

    document.querySelectorAll('input[type="radio"]').forEach((input) => {
        input.checked = false;
    });
    clearReviewStyles();

    if (returnLink) {
        returnLink.textContent = "登録画面へ戻る";
    }

    status.classList.remove("complete");
    status.textContent = message || "すべての問題に回答してから採点してください。";
    progress.textContent = `0 / ${questions.length} 回答`;
    if (scoreButton) {
        scoreButton.disabled = true;
        scoreButton.textContent = "すべて回答して採点する";
        scoreButton.dataset.mode = "score";
    }

    const firstQuestion = document.querySelector('.quiz-question');
    if (firstQuestion) {
        firstQuestion.scrollIntoView({ behavior: "smooth", block: "start" });
    }
}

function updateProgress() {
    const answered = answers.filter((answer) => answer !== null).length;
    progress.textContent = `${answered} / ${questions.length} 回答`;
    if (scoreButton) {
        scoreButton.disabled = answered !== questions.length;
        if (answered === questions.length && scoreButton.dataset.mode !== "retry") {
            scoreButton.textContent = "採点する";
        }
    }
}

function submitQuiz() {
    if (scoreButton && scoreButton.dataset.mode === "retry") {
        resetQuiz("すべての問題に回答してください。");
        return;
    }

    const correct = answers.reduce((count, answer, index) =>
        count + (answer === questions[index][2] ? 1 : 0), 0);

    if (correct === questions.length) {
        localStorage.setItem("petCareQuizPassed", "true");
        status.textContent = "合格です。登録画面へ戻って登録を続けてください。";
        status.classList.add("complete");
        if (returnLink) {
            returnLink.textContent = "登録画面へ戻る（合格済み）";
        }
        if (scoreButton) {
            scoreButton.textContent = "合格済み";
            scoreButton.disabled = true;
        }
        return;
    }

    showReviewResults();
    status.textContent = `採点結果: ${correct} / ${questions.length} です。赤色の選択が間違い、緑色の選択が正解です。全問正解でないため、もう一度最初からやり直してください。`;
    status.classList.remove("complete");
    if (scoreButton) {
        scoreButton.textContent = "もう一度最初からやり直す";
        scoreButton.disabled = false;
        scoreButton.dataset.mode = "retry";
    }
    if (returnLink) {
        returnLink.textContent = "登録画面へ戻る";
    }
}

questions.forEach((question, index) => {
    const fieldset = document.createElement("fieldset");
    fieldset.className = "quiz-question";
    const legend = document.createElement("legend");
    legend.textContent = `${index + 1}. ${question[0]}`;
    fieldset.appendChild(legend);

    question[1].forEach((option, optionIndex) => {
        const label = document.createElement("label");
        label.className = "quiz-option";

        const input = document.createElement("input");
        input.type = "radio";
        input.name = `quiz-${index}`;
        input.value = String(optionIndex);

        input.addEventListener("change", () => {
            answers[index] = optionIndex;
            sessionStorage.setItem("quizAnswersV2", JSON.stringify(answers));
            label.classList.remove('wrong-choice');
            if (scoreButton) {
                scoreButton.dataset.mode = "score";
                if (answers.every((answer) => answer !== null)) {
                    scoreButton.textContent = "採点する";
                }
            }
            updateProgress();

            if (window.matchMedia("(max-width: 600px)").matches) {
                const nextFieldset = fieldset.nextElementSibling;
                if (nextFieldset) {
                    nextFieldset.scrollIntoView({ behavior: "smooth", block: "start" });
                }
            }
        });

        label.appendChild(input);
        label.appendChild(document.createTextNode(option));
        fieldset.appendChild(label);
    });

    questionContainer.appendChild(fieldset);
});

const saved = JSON.parse(sessionStorage.getItem("quizAnswersV2") || "[]");
saved.forEach((answer, index) => {
    if (Number.isInteger(answer) && answer >= 0 && answer < questions[index][1].length) {
        answers[index] = answer;
        const input = document.querySelector(`input[name="quiz-${index}"][value="${answer}"]`);
        if (input) input.checked = true;
    }
});

if (scoreButton) {
    scoreButton.addEventListener("click", submitQuiz);
}

if (localStorage.getItem("petCareQuizPassed") === "true") {
    status.textContent = "合格済みです。登録画面へ戻って登録を続けてください。";
    status.classList.add("complete");
}

updateProgress();
