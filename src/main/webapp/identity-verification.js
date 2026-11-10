// ========================================
// 本人確認画面
// identity-verification.js
// ========================================


// ========================================
// HTML要素を取得
// ========================================

const backButton =
    document.getElementById("backButton");

const startButton =
    document.getElementById("startButton");

const completedCard =
    document.getElementById("completedCard");

const testButton =
    document.getElementById("testButton");



// ========================================
// ページ読み込み時の処理
// ========================================

document.addEventListener("DOMContentLoaded", function () {

    /*
     * localStorageに本人確認済みの情報がある場合、
     * すでに本人確認が完了している状態にする
     */
    const identityVerified =
        localStorage.getItem("identityVerified");

    if (identityVerified === "true") {

        showCompletedState();

    }

});



// ========================================
// 戻るボタン
// ========================================

if (backButton) {

    backButton.addEventListener(
        "click",
        function () {

            // 本会員への昇格画面へ戻る
            window.location.href =
                "membership-upgrade.html";

        }
    );

}



// ========================================
// 本人確認開始ボタン
// ========================================

if (startButton) {

    startButton.addEventListener(
        "click",
        function () {

            /*
             * 現在は画面確認用のため、
             * confirmで本人確認を開始する。
             */
            const result =
                confirm("本人確認を開始しますか？");


            // キャンセルされた場合
            if (!result) {

                return;

            }



            /*
             * 現在は実際の本人確認システムではなく、
             * 画面確認用として本人確認完了とする。
             */
            alert("本人確認が完了しました。");



            // 本人確認済みの状態を保存
            localStorage.setItem(
                "identityVerified",
                "true"
            );



            // 本人確認完了画面を表示
            showCompletedState();

        }
    );

}



// ========================================
// 本人確認完了後の状態を表示
// ========================================

function showCompletedState() {

    // 完了カードを表示
    if (completedCard) {

        completedCard.classList.remove("hidden");

    }



    // 開始ボタンを「本人確認済み」に変更
    if (startButton) {

        startButton.textContent =
            "本人確認済み";

        startButton.disabled =
            true;

        startButton.style.opacity =
            "0.6";

        startButton.style.cursor =
            "default";

    }

}



// ========================================
// ペット知識テストへ進む
// ========================================

if (testButton) {

    testButton.addEventListener(
        "click",
        function () {

            /*
             * 本人確認が完了しているか確認
             */
            const identityVerified =
                localStorage.getItem("identityVerified");



            // 本人確認済みの場合
            if (identityVerified === "true") {

                window.location.href =
                    "pet-knowledge-test.html";

            }

            // 本人確認が完了していない場合
            else {

                alert(
                    "先に本人確認を完了してください。"
                );

            }

        }
    );

}
