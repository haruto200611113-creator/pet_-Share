// ========================================
// 設定ページ
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

            // アカウント設定からマイページへ戻る
            window.location.href =
                "mypage.html";

        }
    );

}


// ========================================
// 通知設定
// ========================================

const notificationToggle =
    document.getElementById(
        "notificationToggle"
    );


if (notificationToggle) {

    const savedNotification =
        localStorage.getItem(
            "notificationEnabled"
        );


    if (savedNotification !== null) {

        notificationToggle.checked =
            savedNotification === "true";

    }


    notificationToggle.addEventListener(
        "change",
        function () {

            localStorage.setItem(
                "notificationEnabled",
                String(
                    notificationToggle.checked
                )
            );

        }
    );

}


// ========================================
// チャット通知
// ========================================

const chatNotificationToggle =
    document.getElementById(
        "chatNotificationToggle"
    );


if (chatNotificationToggle) {

    const savedChatNotification =
        localStorage.getItem(
            "chatNotificationEnabled"
        );


    if (savedChatNotification !== null) {

        chatNotificationToggle.checked =
            savedChatNotification === "true";

    }


    chatNotificationToggle.addEventListener(
        "change",
        function () {

            localStorage.setItem(
                "chatNotificationEnabled",
                String(
                    chatNotificationToggle.checked
                )
            );

        }
    );

}


// ========================================
// 本人確認状態
// ========================================

const verificationStatus =
    document.getElementById(
        "verificationStatus"
    );


const savedVerification =
    localStorage.getItem(
        "identityVerified"
    );


if (
    verificationStatus &&
    savedVerification === "true"
) {

    verificationStatus.textContent =
        "本人確認済み";

}


// ========================================
// ログアウト
// ========================================

const logoutButton =
    document.getElementById(
        "logoutButton"
    );


if (logoutButton) {

    logoutButton.addEventListener(
        "click",
        function () {

            const result =
                confirm(
                    "ログアウトしますか？"
                );


            if (!result) {

                return;

            }


            // セッション情報を削除
            sessionStorage.clear();


            // ログイン画面へ移動
            window.location.href =
                "login.html";

        }
    );

}