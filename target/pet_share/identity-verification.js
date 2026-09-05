const backButton =
    document.getElementById("backButton");

const startButton =
    document.getElementById("startButton");

const completedCard =
    document.getElementById(
        "completedCard"
    );


/* =========================
   戻る
========================= */

backButton.addEventListener(
    "click",
    function () {

        window.location.href =
            "settings.html";

    }
);


/* =========================
   本人確認開始
========================= */

startButton.addEventListener(
    "click",
    function () {

        const result =
            confirm(
                "本人確認を開始しますか？"
            );


        if (!result) {

            return;

        }


        alert(
            "本人確認が完了しました。"
        );


        completedCard.classList.remove(
            "hidden"
        );


        startButton.textContent =
            "本人確認済み";

        startButton.disabled =
            true;

        startButton.style.opacity =
            "0.6";


        const verificationStatus =
            window.localStorage;


        verificationStatus.setItem(
            "identityVerified",
            "true"
        );

    }
);