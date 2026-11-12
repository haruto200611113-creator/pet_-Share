document.addEventListener("DOMContentLoaded", () => {

    // URLからpetIdを取得
    const params = new URLSearchParams(window.location.search);
    const petId = params.get("petId");

    const petIdInput =
        document.getElementById("petId");

    const startInput =
        document.getElementById("availableStart");

    const endInput =
        document.getElementById("availableEnd");

    const usageHours =
        document.getElementById("usageHours");

    const sharePrice =
        document.getElementById("sharePrice");

    const errorMessage =
        document.getElementById("errorMessage");


    // =====================================
    // petId確認
    // =====================================

    if (!petId) {

        showError(
            "ペットIDが指定されていません。"
        );

        return;
    }

    petIdInput.value = petId;


    // =====================================
    // 日時変更時
    // =====================================

    startInput.addEventListener(
        "change",
        calculatePrice
    );

    endInput.addEventListener(
        "change",
        calculatePrice
    );


    // =====================================
    // 料金計算
    // =====================================

    function calculatePrice() {

        clearError();

        if (!startInput.value ||
            !endInput.value) {

            usageHours.textContent = "0時間";
            sharePrice.textContent = "0円";

            return;
        }

        const start =
            new Date(startInput.value);

        const end =
            new Date(endInput.value);


        // 終了日時が開始日時以前
        if (end <= start) {

            showError(
                "終了日時は開始日時より後にしてください。"
            );

            usageHours.textContent = "0時間";
            sharePrice.textContent = "0円";

            return;
        }


        // 現在日時より前
        const now = new Date();

        if (start < now) {

            showError(
                "開始日時は現在より後にしてください。"
            );

            usageHours.textContent = "0時間";
            sharePrice.textContent = "0円";

            return;
        }


        // ミリ秒 → 時間
        const milliseconds =
            end.getTime() - start.getTime();

        const hours =
            milliseconds /
            (1000 * 60 * 60);


        // 小数第2位まで
        const displayHours =
            Math.round(hours * 100) / 100;

        usageHours.textContent =
            displayHours + "時間";


        // =====================================
        // 料金計算
        // =====================================

        const price =
            calculateSharePrice(hours);

        sharePrice.textContent =
            price.toLocaleString() + "円";
    }


    // =====================================
    // 時間 → 料金
    // =====================================

    function calculateSharePrice(hours) {

        if (hours <= 1) {
            return 500;
        }

        if (hours <= 2) {
            return 900;
        }

        if (hours <= 3) {
            return 1200;
        }

        if (hours <= 4) {
            return 1500;
        }

        if (hours <= 5) {
            return 1800;
        }

        if (hours <= 6) {
            return 2000;
        }

        if (hours <= 8) {
            return 2500;
        }

        if (hours <= 12) {
            return 3000;
        }

        if (hours <= 24) {
            return 4000;
        }

        if (hours <= 48) {
            return 7000;
        }

        if (hours <= 72) {
            return 9000;
        }

        return 9000;
    }


    // =====================================
    // エラー表示
    // =====================================

    function showError(message) {

        errorMessage.textContent =
            message;

        errorMessage.style.display =
            "block";
    }


    // =====================================
    // エラー解除
    // =====================================

    function clearError() {

        errorMessage.textContent = "";

        errorMessage.style.display =
            "none";
    }

});
