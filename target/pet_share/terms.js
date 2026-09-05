// ========================================
// 利用規約ページ
// ========================================

document.addEventListener("DOMContentLoaded", function () {


    // ========================================
    // どこから利用規約を開いたか取得
    // ========================================

    const termsFrom =
        sessionStorage.getItem("termsFrom");


    // ========================================
    // 要素取得
    // ========================================

    const confirmArea =
        document.querySelector(".confirm-area");

    const confirmTermsButton =
        document.getElementById(
            "confirmTermsButton"
        );

    const backButton =
        document.getElementById("backButton");


    // ========================================
    // 確認エリア
    // ========================================

    if (confirmArea) {

        // ------------------------------------
        // 新規登録から来た場合
        // ------------------------------------

        if (termsFrom === "signup") {

            // 「利用規約を確認しました」
            // ボタンを表示

            confirmArea.style.display =
                "block";

        }


        // ------------------------------------
        // マイページ・ホームから来た場合
        // ------------------------------------

        else {

            // 確認ボタンを非表示

            confirmArea.style.display =
                "none";

        }

    }


    // ========================================
    // 戻るボタン
    // ========================================

    if (backButton) {

        backButton.addEventListener(
            "click",
            function () {


                // --------------------------------
                // 新規登録から来た場合
                // --------------------------------

                if (termsFrom === "signup") {

                    window.location.href =
                        "sign.html";

                }


                // --------------------------------
                // マイページから来た場合
                // --------------------------------

                else if (
                    termsFrom === "mypage"
                ) {

                    window.location.href =
                        "mypage.html";

                }


                // --------------------------------
                // ホームから来た場合
                // --------------------------------

                else if (
                    termsFrom === "home"
                ) {

                    window.location.href =
                        "home.html";

                }


                // --------------------------------
                // どこから来たか分からない場合
                // --------------------------------

                else {

                    window.location.href =
                        "home.html";

                }

            }
        );

    }


    // ========================================
    // 「利用規約を確認しました」
    // ========================================

    if (confirmTermsButton) {

        confirmTermsButton.addEventListener(
            "click",
            function () {


                // 利用規約を確認済みにする

                sessionStorage.setItem(
                    "termsConfirmed",
                    "true"
                );


                // 新規登録画面へ戻る

                window.location.href =
                    "sign.html";

            }
        );

    }

});