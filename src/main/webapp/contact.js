// ========================================
// お問い合わせページ
// ========================================


// ========================================
// 戻るボタン
// ========================================

const backButton =
    document.getElementById("backButton");


backButton.addEventListener(
    "click",
    function () {

        window.location.href =
            "mypage.html";

    }
);


// ========================================
// フォーム
// ========================================

const contactForm =
    document.getElementById("contactForm");


// ========================================
// 完了メッセージ
// ========================================

const completeMessage =
    document.getElementById(
        "completeMessage"
    );


// ========================================
// 送信
// ========================================

contactForm.addEventListener(
    "submit",
    function (event) {

        // ページをリロードしない
        event.preventDefault();


        // 入力内容を取得
        const name =
            document.getElementById("name").value;

        const email =
            document.getElementById("email").value;

        const category =
            document.getElementById("category").value;

        const subject =
            document.getElementById("subject").value;

        const message =
            document.getElementById("message").value;


        // ----------------------------------------
        // 現在は確認用
        // ----------------------------------------

        console.log("お問い合わせ内容");

        console.log("名前:", name);

        console.log("メール:", email);

        console.log("種類:", category);

        console.log("件名:", subject);

        console.log("内容:", message);


        // ----------------------------------------
        // フォームを非表示
        // ----------------------------------------

        contactForm.style.display =
            "none";


        // ----------------------------------------
        // 完了メッセージを表示
        // ----------------------------------------

        completeMessage.style.display =
            "block";

    }
);


// ========================================
// マイページに戻る
// ========================================

const homeButton =
    document.getElementById(
        "homeButton"
    );


homeButton.addEventListener(
    "click",
    function () {

        window.location.href =
            "mypage.html";

    }
);