// ========================================
// ログアウト確認画面
// ========================================


// ========================================
// 要素取得
// ========================================

const logoutModal =
    document.getElementById(
        "logoutModal"
    );


const logoutYesButton =
    document.getElementById(
        "logoutYesButton"
    );


const logoutNoButton =
    document.getElementById(
        "logoutNoButton"
    );


// ========================================
// ログアウト確認画面を表示
// ========================================

window.showLogoutConfirm =
    function () {

        if (!logoutModal) {

            console.error(
                "ログアウト確認画面が見つかりません。"
            );

            return;

        }


        logoutModal.style.display =
            "flex";

    };


// ========================================
// 「はい」ボタン
// ========================================

if (logoutYesButton) {

    logoutYesButton.addEventListener(
        "click",
        function () {

            // 確認画面を閉じる
            logoutModal.style.display =
                "none";


            // セッション情報を削除
            sessionStorage.clear();


            // ログイン画面へ移動
            window.location.href =
                "login.html";

        }
    );

}


// ========================================
// 「いいえ」ボタン
// ========================================

if (logoutNoButton) {

    logoutNoButton.addEventListener(
        "click",
        function () {

            // 確認画面を閉じる
            logoutModal.style.display =
                "none";

        }
    );

}


// ========================================
// 確認画面の外側をクリック
// ========================================

if (logoutModal) {

    logoutModal.addEventListener(
        "click",
        function (event) {

            // 白い確認画面部分ではなく、
            // 背景部分をクリックした場合
            if (
                event.target ===
                logoutModal
            ) {

                logoutModal.style.display =
                    "none";

            }

        }
    );

}


// ========================================
// ESCキーで閉じる
// ========================================

document.addEventListener(
    "keydown",
    function (event) {

        if (
            event.key === "Escape" &&
            logoutModal &&
            logoutModal.style.display === "flex"
        ) {

            logoutModal.style.display =
                "none";

        }

    }
);
