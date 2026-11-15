// ========================================
// ペット借用申込みページ
// ========================================


// ========================================
// ペットID取得
// ========================================

const params =
    new URLSearchParams(window.location.search);

const petId =
    params.get("petId");


// ========================================
// 要素取得
// ========================================

const petName =
    document.getElementById("petName");

const petImage =
    document.getElementById("petImage");

const petType =
    document.getElementById("petType");

const petAge =
    document.getElementById("petAge");

const sharePeriod =
    document.getElementById("sharePeriod");

const shareConditions =
    document.getElementById("shareConditions");

const startDatetime =
    document.getElementById("startDatetime");

const endDatetime =
    document.getElementById("endDatetime");

const usageHours =
    document.getElementById("usageHours");

const sharePrice =
    document.getElementById("sharePrice");

const errorMessage =
    document.getElementById("errorMessage");

const submitButton =
    document.getElementById("submitButton");


// ========================================
// エラー表示
// ========================================

function showError(message) {

    errorMessage.textContent =
        message;

    errorMessage.style.display =
        "block";
}


// ========================================
// エラー非表示
// ========================================

function hideError() {

    errorMessage.textContent =
        "";

    errorMessage.style.display =
        "none";
}


// ========================================
// ペット詳細取得
// ========================================

async function loadPet() {

    if (!petId) {

        showError(
            "ペットIDが指定されていません。"
        );

        return;
    }

    try {

        const response =
            await fetch(
                `pet-detail?petId=${encodeURIComponent(petId)}`
            );

        if (!response.ok) {

            throw new Error(
                "ペット情報を取得できませんでした。"
            );
        }

        const pet =
            await response.json();


        // ------------------------------
        // ペット名
        // ------------------------------

        petName.textContent =
            pet.name || "名前未設定";


        // ------------------------------
        // 画像
        // ------------------------------

        if (pet.imagePath) {

            petImage.src =
                pet.imagePath;

        } else {

            petImage.src =
                "https://placehold.co/300x300?text=Pet";
        }


        // ------------------------------
        // 種類
        // ------------------------------

        let typeText =
            pet.type || "未設定";

        if (pet.breed) {

            typeText +=
                `（${pet.breed}）`;
        }

        petType.textContent =
            typeText;


        // ------------------------------
        // 年齢
        // ------------------------------

        if (
            pet.age !== undefined &&
            pet.age !== null
        ) {

            petAge.textContent =
                `${pet.age}歳`;

        } else {

            petAge.textContent =
                "不明";
        }


        // ------------------------------
        // シェア期間
        // ------------------------------

        const start =
            formatDateTime(
                pet.shareStartDate
            );

        const end =
            formatDateTime(
                pet.shareEndDate
            );

        if (start && end) {

            sharePeriod.textContent =
                `${start} ～ ${end}`;

        } else if (start) {

            sharePeriod.textContent =
                `${start} ～`;

        } else if (end) {

            sharePeriod.textContent =
                `～ ${end}`;

        } else {

            sharePeriod.textContent =
                "設定なし";
        }


        // ------------------------------
        // シェア条件
        // ------------------------------

        if (pet.shareConditions) {

            shareConditions.textContent =
                `シェア条件：${pet.shareConditions}`;

        } else {

            shareConditions.textContent =
                "シェア条件は登録されていません。";
        }

    } catch (error) {

        console.error(
            "ペット情報取得エラー:",
            error
        );

        showError(
            "ペット情報を取得できませんでした。"
        );
    }
}


// ========================================
// 日付表示
// ========================================

function formatDateTime(dateString) {

    if (
        !dateString ||
        dateString === "null" ||
        dateString === "undefined"
    ) {

        return "";
    }

    const date =
        new Date(dateString);

    if (
        Number.isNaN(
            date.getTime()
        )
    ) {

        return "";
    }

    const year =
        date.getFullYear();

    const month =
        date.getMonth() + 1;

    const day =
        date.getDate();

    const hour =
        String(
            date.getHours()
        ).padStart(2, "0");

    const minute =
        String(
            date.getMinutes()
        ).padStart(2, "0");

    return `${year}年${month}月${day}日 ${hour}:${minute}`;
}


// ========================================
// 利用時間計算
// ========================================

function calculateUsageHours() {

    hideError();

    if (
        !startDatetime.value ||
        !endDatetime.value
    ) {

        usageHours.textContent =
            "0時間";

        sharePrice.textContent =
            "0円";

        return null;
    }


    const start =
        new Date(
            startDatetime.value
        );

    const end =
        new Date(
            endDatetime.value
        );


    // ------------------------------------
    // 終了が開始より前
    // ------------------------------------

    if (end <= start) {

        showError(
            "利用終了日時は利用開始日時より後にしてください。"
        );

        usageHours.textContent =
            "0時間";

        sharePrice.textContent =
            "0円";

        return null;
    }


    // ------------------------------------
    // 利用開始日時が現在より前
    // ------------------------------------

    const now =
        new Date();

    if (start < now) {

        showError(
            "利用開始日時は現在時刻以降にしてください。"
        );

        usageHours.textContent =
            "0時間";

        sharePrice.textContent =
            "0円";

        return null;
    }


    // ------------------------------------
    // ミリ秒 → 時間
    // ------------------------------------

    const milliseconds =
        end.getTime() -
        start.getTime();

    const hours =
        milliseconds /
        (1000 * 60 * 60);


    usageHours.textContent =
        `${hours.toFixed(2)}時間`;


    // ------------------------------------
    // 料金計算
    // ------------------------------------

    const price =
        calculatePrice(hours);

    sharePrice.textContent =
        `${price.toLocaleString()}円`;

    return hours;
}


// ========================================
// 料金計算
// ========================================

function calculatePrice(hours) {

    if (hours <= 1) {

        return 500;

    } else if (hours <= 2) {

        return 900;

    } else if (hours <= 3) {

        return 1200;

    } else if (hours <= 4) {

        return 1500;

    } else if (hours <= 5) {

        return 1800;

    } else if (hours <= 6) {

        return 2000;

    } else if (hours <= 8) {

        return 2500;

    } else if (hours <= 12) {

        return 3000;

    } else if (hours <= 24) {

        return 4000;

    } else if (hours <= 48) {

        return 7000;

    } else if (hours <= 72) {

        return 9000;

    } else {

        // --------------------------------
        // 72時間を超えた場合
        // 24時間増えるごとに3,000円追加
        // --------------------------------

        const extraHours =
            hours - 72;

        const extraDays =
            Math.ceil(
                extraHours / 24
            );

        return 9000 +
            (extraDays * 3000);
    }
}


// ========================================
// 日時変更時
// ========================================

startDatetime.addEventListener(
    "change",
    calculateUsageHours
);

endDatetime.addEventListener(
    "change",
    calculateUsageHours
);


// ========================================
// 申込み
// ========================================

submitButton.addEventListener(
    "click",
    function () {

        hideError();


        // --------------------------------
        // ペットID確認
        // --------------------------------

        if (!petId) {

            showError(
                "ペットIDが指定されていません。"
            );

            return;
        }


        // --------------------------------
        // 日時入力確認
        // --------------------------------

        if (
            !startDatetime.value ||
            !endDatetime.value
        ) {

            showError(
                "利用開始日時と利用終了日時を入力してください。"
            );

            return;
        }


        // --------------------------------
        // 利用時間・料金を再計算
        // --------------------------------

        const hours =
            calculateUsageHours();

        if (hours === null) {

            return;
        }


        const price =
            calculatePrice(hours);


        // --------------------------------
        // 確認
        // --------------------------------

        const confirmed =
            confirm(
                "以下の内容で申込みますか？\n\n" +
                `利用時間：${hours.toFixed(2)}時間\n` +
                `利用料金：${price.toLocaleString()}円`
            );

        if (!confirmed) {

            return;
        }


        // --------------------------------
        // ServletへPOST送信
        // --------------------------------

        const form =
            document.createElement("form");

        form.method =
            "POST";

        form.action =
            "share-request";


        // --------------------------------
        // ペットID
        // --------------------------------

        const petIdInput =
            document.createElement("input");

        petIdInput.type =
            "hidden";

        petIdInput.name =
            "petId";

        petIdInput.value =
            petId;

        form.appendChild(
            petIdInput
        );


        // --------------------------------
        // 利用開始日時
        // --------------------------------

        const startInput =
            document.createElement("input");

        startInput.type =
            "hidden";

        startInput.name =
            "startDatetime";

        startInput.value =
            startDatetime.value;

        form.appendChild(
            startInput
        );


        // --------------------------------
        // 利用終了日時
        // --------------------------------

        const endInput =
            document.createElement("input");

        endInput.type =
            "hidden";

        endInput.name =
            "endDatetime";

        endInput.value =
            endDatetime.value;

        form.appendChild(
            endInput
        );


        // --------------------------------
        // フォームを画面に追加
        // --------------------------------

        document.body.appendChild(
            form
        );


        // --------------------------------
        // 送信
        // --------------------------------

        form.submit();
    }
);


// ========================================
// 申込み結果表示
// ========================================

const resultParams =
    new URLSearchParams(
        window.location.search
    );


// ----------------------------------------
// 申込み成功
// ----------------------------------------

if (
    resultParams.get("success") === "true"
) {

    alert(
        "ペットの借用申込みが完了しました。"
    );

    window.history.replaceState(
        {},
        document.title,
        `borrow-request.html?petId=${encodeURIComponent(petId)}`
    );
}


// ----------------------------------------
// 申込みエラー
// ----------------------------------------

const requestError =
    resultParams.get("error");

if (requestError) {

    showError(
        requestError
    );

    window.history.replaceState(
        {},
        document.title,
        `borrow-request.html?petId=${encodeURIComponent(petId)}`
    );
}


// ========================================
// 初期処理
// ========================================

loadPet();