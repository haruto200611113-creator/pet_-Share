// ========================================
// ペット借用申込みページ
// ========================================


// ========================================
// URLからペットID取得
// ========================================

const params =
    new URLSearchParams(window.location.search);

const petId =
    params.get("petId");


// ========================================
// HTML要素取得
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

    if (!errorMessage) {
        return;
    }

    errorMessage.textContent =
        message;

    errorMessage.style.display =
        "block";
}


// ========================================
// エラー非表示
// ========================================

function hideError() {

    if (!errorMessage) {
        return;
    }

    errorMessage.textContent =
        "";

    errorMessage.style.display =
        "none";
}


// ========================================
// 確認モーダル
// ========================================

function createConfirmModal(hours, price) {

    return new Promise(function (resolve) {

        // --------------------------------
        // 背景
        // --------------------------------

        const overlay =
            document.createElement("div");

        overlay.id =
            "share-confirm-overlay";


        // --------------------------------
        // モーダル本体
        // --------------------------------

        const modal =
            document.createElement("div");

        modal.id =
            "share-confirm-modal";


        // --------------------------------
        // タイトル
        // --------------------------------

        const title =
            document.createElement("h2");

        title.textContent =
            "借用申込みの確認";


        // --------------------------------
        // メッセージ
        // --------------------------------

        const message =
            document.createElement("p");

        message.className =
            "share-confirm-message";

        message.textContent =
            "以下の内容で申込みますか？";


        // --------------------------------
        // 利用時間
        // --------------------------------

        const timeRow =
            document.createElement("div");

        timeRow.className =
            "share-confirm-row";

        timeRow.innerHTML = `
            <span>利用時間</span>
            <strong>${hours.toFixed(2)}時間</strong>
        `;


        // --------------------------------
        // 利用料金
        // --------------------------------

        const priceRow =
            document.createElement("div");

        priceRow.className =
            "share-confirm-row price-row";

        priceRow.innerHTML = `
            <span>利用料金</span>
            <strong>${price.toLocaleString()}円</strong>
        `;


        // --------------------------------
        // ボタンエリア
        // --------------------------------

        const buttonArea =
            document.createElement("div");

        buttonArea.className =
            "share-confirm-buttons";


        // --------------------------------
        // キャンセルボタン
        // --------------------------------

        const cancelButton =
            document.createElement("button");

        cancelButton.type =
            "button";

        cancelButton.textContent =
            "キャンセル";

        cancelButton.className =
            "share-cancel-button";


        // --------------------------------
        // 申込みボタン
        // --------------------------------

        const okButton =
            document.createElement("button");

        okButton.type =
            "button";

        okButton.textContent =
            "申し込む";

        okButton.className =
            "share-ok-button";


        // --------------------------------
        // ボタンを追加
        // --------------------------------

        buttonArea.appendChild(
            cancelButton
        );

        buttonArea.appendChild(
            okButton
        );


        // --------------------------------
        // モーダルを構築
        // --------------------------------

        modal.appendChild(title);

        modal.appendChild(message);

        modal.appendChild(timeRow);

        modal.appendChild(priceRow);

        modal.appendChild(buttonArea);

        overlay.appendChild(modal);

        document.body.appendChild(
            overlay
        );


        // --------------------------------
        // キャンセル
        // --------------------------------

        cancelButton.addEventListener(
            "click",
            function () {

                overlay.remove();

                resolve(false);
            }
        );


        // --------------------------------
        // 申込み
        // --------------------------------

        okButton.addEventListener(
            "click",
            function () {

                overlay.remove();

                resolve(true);
            }
        );


        // --------------------------------
        // 背景クリック
        // --------------------------------

        overlay.addEventListener(
            "click",
            function (event) {

                if (
                    event.target === overlay
                ) {

                    overlay.remove();

                    resolve(false);
                }
            }
        );
    });
}


// ========================================
// ペット情報取得
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


        console.log(
            "取得したペット情報:",
            pet
        );


        // =================================
        // ペット名
        // =================================

        petName.textContent =
            pet.name || "名前未設定";


        // =================================
        // ペット画像
        // =================================

        if (pet.imagePath) {

            petImage.src =
                pet.imagePath;

        } else {

            petImage.src =
                "https://placehold.co/300x300?text=Pet";
        }


        // =================================
        // ペット種類
        // =================================

        let typeText =
            pet.type || "未設定";


        if (pet.breed) {

            typeText +=
                `（${pet.breed}）`;
        }


        petType.textContent =
            typeText;


        // =================================
        // 年齢
        // =================================

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


        // =================================
        // シェア可能期間
        // =================================

        const start =
            formatDateTime(
                pet.availableStart
            );

        const end =
            formatDateTime(
                pet.availableEnd
            );


        if (start && end) {

            sharePeriod.innerHTML = `
                <strong>
                    貸し出し可能な期間
                </strong><br>

                ${start}<br>

                ～<br>

                ${end}
            `;

        } else if (start) {

            sharePeriod.innerHTML = `
                <strong>
                    貸し出し可能な期間
                </strong><br>

                ${start}～
            `;

        } else if (end) {

            sharePeriod.innerHTML = `
                <strong>
                    貸し出し可能な期間
                </strong><br>

                ～${end}
            `;

        } else {

            sharePeriod.textContent =
                "貸し出し可能な期間が設定されていません。";
        }


        // =================================
        // シェア条件
        // =================================

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
// 日付・時刻表示
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


    return (
        `${year}年` +
        `${month}月` +
        `${day}日 ` +
        `${hour}:${minute}`
    );
}


// ========================================
// 利用時間計算
// ========================================

function calculateUsageHours() {

    hideError();


    // 日時未入力
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


    // --------------------------------
    // 終了日時チェック
    // --------------------------------

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


    // --------------------------------
    // 開始日時チェック
    // --------------------------------

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


    // --------------------------------
    // 利用時間計算
    // --------------------------------

    const milliseconds =
        end.getTime() -
        start.getTime();


    const hours =
        milliseconds /
        (1000 * 60 * 60);


    usageHours.textContent =
        `${hours.toFixed(2)}時間`;


    // --------------------------------
    // 料金計算
    // --------------------------------

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

        const extraHours =
            hours - 72;

        const extraDays =
            Math.ceil(
                extraHours / 24
            );

        return (
            9000 +
            (extraDays * 3000)
        );
    }
}


// ========================================
// 日時変更イベント
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
// 申込みボタン
// ========================================

submitButton.addEventListener(
    "click",
    async function () {

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
        // 利用時間計算
        // --------------------------------

        const hours =
            calculateUsageHours();


        if (hours === null) {

            return;
        }


        // --------------------------------
        // 料金取得
        // --------------------------------

        const price =
            calculatePrice(hours);


        // --------------------------------
        // 確認モーダル表示
        // --------------------------------

        const confirmed =
            await createConfirmModal(
                hours,
                price
            );


        // キャンセル
        if (!confirmed) {

            return;
        }


        // --------------------------------
        // ServletへPOST
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
        // フォーム送信
        // --------------------------------

        document.body.appendChild(
            form
        );


        submitButton.disabled =
            true;


        submitButton.textContent =
            "申込み処理中...";


        form.submit();
    }
);


// ========================================
// 申込み結果
// ========================================

const resultParams =
    new URLSearchParams(
        window.location.search
    );


// ========================================
// 申込み成功
// ========================================

if (
    resultParams.get("success") === "true"
) {

    const message =
        document.getElementById(
            "requestCompleteMessage"
        );


    if (message) {

        message.style.display =
            "block";


        setTimeout(
            function () {

                message.style.display =
                    "none";

            },
            1500
        );
    }


    // URLからsuccess=trueを削除

    window.history.replaceState(
        {},
        document.title,
        `borrow-request.html?petId=${encodeURIComponent(petId)}`
    );
}


// ========================================
// 申込みエラー
// ========================================

const requestError =
    resultParams.get("error");


if (requestError) {

    const errorMessages = {

        invalid_datetime:
            "利用開始日時と利用終了日時を正しく入力してください。",

        past_datetime:
            "利用開始日時は現在時刻以降にしてください。",

        outside_period:
            "シェア可能期間外です。",

        overlap:
            "この時間帯にはすでに申込みがあります。",

        insert_failed:
            "申込みの登録に失敗しました。",

        system_error:
            "システムエラーが発生しました。"
    };


    const message =
        document.getElementById(
            "requestErrorMessage"
        );


    if (message) {

        message.textContent =
            errorMessages[requestError]
            ||
            "申込み処理でエラーが発生しました。";


        message.style.display =
            "block";


        setTimeout(
            function () {

                message.style.display =
                    "none";

            },
            3000
        );
    }


    // URLからerrorを削除

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