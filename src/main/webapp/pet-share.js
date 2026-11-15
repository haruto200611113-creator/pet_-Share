document.addEventListener("DOMContentLoaded", () => {

    // =====================================
    // URLからpetIdを取得
    // =====================================

    const params =
        new URLSearchParams(window.location.search);

    const petId =
        params.get("petId");


    // =====================================
    // HTML要素取得
    // =====================================

    const petIdInput =
        document.getElementById("petId");

    const startInput =
        document.getElementById("availableStart");

    const endInput =
        document.getElementById("availableEnd");

    const usageHours =
        document.getElementById("usageHours");

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


    // =====================================
    // petIdをフォームに設定
    // =====================================

    petIdInput.value = petId;


    // =====================================
    // 日時変更時
    // =====================================

    startInput.addEventListener(
        "change",
        calculateUsageHours
    );

    endInput.addEventListener(
        "change",
        calculateUsageHours
    );


    // =====================================
    // シェア可能時間計算
    // =====================================

    function calculateUsageHours() {

        clearError();


        // 開始日時または終了日時が未入力
        if (!startInput.value ||
            !endInput.value) {

            usageHours.textContent =
                "0時間";

            return;
        }


        // =====================================
        // 日時取得
        // =====================================

        const start =
            new Date(startInput.value);

        const end =
            new Date(endInput.value);


        // =====================================
        // 終了日時チェック
        // =====================================

        if (end <= start) {

            showError(
                "終了日時は開始日時より後にしてください。"
            );

            usageHours.textContent =
                "0時間";

            return;
        }


        // =====================================
        // 現在日時チェック
        // =====================================

        const now =
            new Date();

        if (start < now) {

            showError(
                "開始日時は現在より後にしてください。"
            );

            usageHours.textContent =
                "0時間";

            return;
        }


        // =====================================
        // 利用時間計算
        // =====================================

        const milliseconds =
            end.getTime() -
            start.getTime();

        const hours =
            milliseconds /
            (1000 * 60 * 60);


        // =====================================
        // 小数第2位まで表示
        // =====================================

        const displayHours =
            Math.round(
                hours * 100
            ) / 100;


        usageHours.textContent =
            displayHours + "時間";
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

        errorMessage.textContent =
            "";

        errorMessage.style.display =
            "none";
    }

});