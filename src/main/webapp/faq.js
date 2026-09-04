// ========================================
// よくある質問
// ========================================


// ========================================
// 戻るボタン
// ========================================

const backButton =
    document.getElementById("backButton");


if (backButton) {

    backButton.addEventListener(
        "click",
        function () {

            window.location.href =
                "mypage.html";

        }
    );

}


// ========================================
// FAQの開閉
// ========================================

const questions =
    document.querySelectorAll(
        ".faq-question"
    );


questions.forEach(
    function (question) {

        question.addEventListener(
            "click",
            function () {

                const faqItem =
                    question.parentElement;


                // 開閉
                faqItem.classList.toggle(
                    "active"
                );

            }
        );

    }
);