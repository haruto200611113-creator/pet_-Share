// ========================================
// プライバシーポリシー
// ========================================

const backButton =
    document.getElementById(
        "backButton"
    );


if (backButton) {

    backButton.addEventListener(
        "click",
        function () {

            const privacyFrom =
                sessionStorage.getItem(
                    "privacyFrom"
                );


            // マイページから来た場合

            if (
                privacyFrom ===
                "mypage"
            ) {

                sessionStorage.removeItem(
                    "privacyFrom"
                );

                window.location.href =
                    "mypage.html";

                return;
            }


            // ホームから来た場合

            if (
                privacyFrom ===
                "home"
            ) {

                sessionStorage.removeItem(
                    "privacyFrom"
                );

                window.location.href =
                    "home.html";

                return;
            }


            // それ以外の場合

            window.history.back();

        }
    );

}