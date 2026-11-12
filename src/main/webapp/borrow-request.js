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

        return;
    }


    const start =
        new Date(
            startDatetime.value
        );

    const end =
        new Date(
            endDatetime.value
        );


    // 終了が開始より前
    if (end <= start) {

        showError(
            "利用終了日時は利用開始日時より後にしてください。"
        );

        usageHours.textContent =
            "0時間";

        sharePrice.textContent =
            "0円";

        return;
    }


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

        return;
    }


    // ミリ秒 → 時間
    const milliseconds =
        end.getTime() -
        start.getTime();

    const hours =
        milliseconds /
        (1000 * 60 * 60);


    usageHours.textContent =
        `${hours.toFixed(2)}時間`;


    const price =
        calculatePrice(hours);

    sharePrice.textContent =
        `${price.toLocaleString()}円`;
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

    } else {

        return 9000;
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


        if (!petId) {

            showError(
                "ペットIDが指定されていません。"
            );

            return;
        }


        if (
            !startDatetime.value ||
            !endDatetime.value
        ) {

            showError(
                "利用開始日時と利用終了日時を入力してください。"
            );

            return;
        }


        const start =
            new Date(
                startDatetime.value
            );

        const end =
            new Date(
                endDatetime.value
            );


        if (end <= start) {

            showError(
                "利用終了日時は利用開始日時より後にしてください。"
            );

            return;
        }


        const milliseconds =
            end.getTime() -
            start.getTime();

        const hours =
            milliseconds /
            (1000 * 60 * 60);


        const price =
            calculatePrice(hours);


        console.log(
            "申込み内容:",
            {
                petId: petId,
                startDatetime:
                    startDatetime.value,
                endDatetime:
                    endDatetime.value,
                usageHours:
                    hours,
                sharePrice:
                    price
            }
        );


        // 次の段階でServletへ送信
        alert(
            "申込み内容を確認しました。\n\n" +
            `利用時間：${hours.toFixed(2)}時間\n` +
            `利用料金：${price.toLocaleString()}円`
        );
    }
);


// ========================================
// 初期処理
// ========================================

loadPet();