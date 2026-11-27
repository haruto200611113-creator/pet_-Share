"use strict";


/* ========================================
   ページ読み込み
======================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadShareRequests();

    }
);



/* ========================================
   申し込み一覧取得
======================================== */

async function loadShareRequests() {

    const loadingMessage =
        document.getElementById(
            "loadingMessage"
        );

    const requestList =
        document.getElementById(
            "requestList"
        );

    const emptyMessage =
        document.getElementById(
            "emptyMessage"
        );


    try {

        const response =
            await fetch(
                "share-request-my-list",
                {
                    method: "GET",

                    headers: {
                        "Accept":
                            "application/json"
                    }
                }
            );


        /* ==================================
           ログイン切れ
        ================================== */

        if (response.status === 401) {

            window.location.href =
                "login.html";

            return;

        }


        /* ==================================
           HTTPエラー
        ================================== */

        if (!response.ok) {

            throw new Error(
                "申し込み情報の取得に失敗しました。"
            );

        }


        /* ==================================
           JSON取得
        ================================== */

        const requests =
            await response.json();


        loadingMessage.style.display =
            "none";


        requestList.innerHTML =
            "";


        /* ==================================
           申し込みなし
        ================================== */

        if (
            !Array.isArray(requests)
            ||
            requests.length === 0
        ) {

            emptyMessage.style.display =
                "block";

            return;

        }


        emptyMessage.style.display =
            "none";


        /* ==================================
           カード生成
        ================================== */

        requests.forEach(
            function (request) {

                const card =
                    createRequestCard(
                        request
                    );

                requestList.appendChild(
                    card
                );

            }
        );


    } catch (error) {

        console.error(
            "申し込み一覧取得エラー:",
            error
        );


        loadingMessage.style.display =
            "none";


        showMessage(
            "申し込み情報を取得できませんでした。",
            "error"
        );

    }

}



/* ========================================
   申し込みカード作成
======================================== */

function createRequestCard(request) {

    const card =
        document.createElement(
            "div"
        );

    card.className =
        "request-card";


    /* ==================================
       ステータス
    ================================== */

    const status =
        request.status || "";


    let statusText =
        "不明";


    let statusClass =
        "status-unknown";


    if (status === "pending") {

        statusText =
            "申込み中";

        statusClass =
            "status-pending";

    } else if (status === "approved") {

        statusText =
            "承認済み";

        statusClass =
            "status-approved";

    } else if (status === "rejected") {

        statusText =
            "拒否";

        statusClass =
            "status-rejected";

    } else if (status === "cancelled") {

        statusText =
            "キャンセル済み";

        statusClass =
            "status-cancelled";

    }


    /* ==================================
       ペット画像
    ================================== */

    let petImageHtml =
        "";


    if (
        request.petImage
        &&
        request.petImage.trim() !== ""
    ) {

        petImageHtml = `

            <img
                src="${escapeHtml(request.petImage)}"
                alt="${escapeHtml(request.petName || "ペット")}"
                class="pet-image">

        `;

    } else {

        petImageHtml = `

            <div class="pet-image-placeholder">
                🐾
            </div>

        `;

    }


    /* ==================================
       削除済みペット判定
    ================================== */

    let deletedPetHtml =
        "";


    /*
     * petId が null の場合
     * ペット本体が削除されたと判断する。
     */

    if (
        request.petId === null
        ||
        request.petId === undefined
        ||
        Number(request.petId) === 0
    ) {

        deletedPetHtml = `

            <span class="deleted-pet-label">
                ※ このペットは削除されています
            </span>

        `;

    }


    /* ==================================
       カードHTML
    ================================== */

    card.innerHTML = `

        <div class="request-header">


            <div class="pet-info">

                ${petImageHtml}


                <div>

                    <h2 class="pet-name">
                        ${escapeHtml(
                            request.petName
                            || "名前なし"
                        )}
                    </h2>

                    ${deletedPetHtml}

                </div>

            </div>


            <span
                class="request-status ${statusClass}">

                ${statusText}

            </span>


        </div>



        <div class="request-details">


            <div class="request-row">

                <span class="request-label">
                    ペットの種類
                </span>

                <span class="request-value">
                    ${escapeHtml(
                        request.petType
                        || "情報なし"
                    )}
                </span>

            </div>



            <div class="request-row">

                <span class="request-label">
                    性別
                </span>

                <span class="request-value">
                    ${escapeHtml(
                        request.petGender
                        || "情報なし"
                    )}
                </span>

            </div>



            <div class="request-row">

                <span class="request-label">
                    飼い主
                </span>

                <span class="request-value">
                    ${escapeHtml(
                        request.ownerName
                        || "飼い主情報なし"
                    )}
                </span>

            </div>



            <div class="request-row">

                <span class="request-label">
                    利用開始
                </span>

                <span class="request-value">
                    ${formatDateTime(
                        request.startDatetime
                    )}
                </span>

            </div>



            <div class="request-row">

                <span class="request-label">
                    利用終了
                </span>

                <span class="request-value">
                    ${formatDateTime(
                        request.endDatetime
                    )}
                </span>

            </div>



            <div class="request-row">

                <span class="request-label">
                    利用時間
                </span>

                <span class="request-value">
                    ${formatUsageHours(
                        request.usageHours
                    )}
                </span>

            </div>



            <div class="request-row">

                <span class="request-label">
                    利用料金
                </span>

                <span class="request-value">
                    ${formatPrice(
                        request.sharePrice
                    )}
                </span>

            </div>



            <div class="request-row">

                <span class="request-label">
                    申込み日時
                </span>

                <span class="request-value">
                    ${formatDateTime(
                        request.createdAt
                    )}
                </span>

            </div>


        </div>


        ${
            status === "pending"

            ? createCancelButton(
                request.requestId
            )

            : createProcessedMessage(
                statusText
            )
        }

    `;


    return card;

}



/* ========================================
   キャンセルボタン
======================================== */

function createCancelButton(
    requestId
) {

    return `

        <div class="request-actions">

            <button
                type="button"
                class="cancel-button"
                onclick="cancelRequest(${requestId})">

                申し込みをキャンセル

            </button>

        </div>

    `;

}



/* ========================================
   処理済みメッセージ
======================================== */

function createProcessedMessage(
    statusText
) {

    return `

        <p class="processed-message">

            この申し込みは
            「${escapeHtml(statusText)}」
            です。

        </p>

    `;

}



/* ========================================
   申し込みキャンセル
======================================== */

async function cancelRequest(
    requestId
) {


    /* ==================================
       requestId確認
    ================================== */

    if (
        requestId === null
        ||
        requestId === undefined
        ||
        requestId === ""
    ) {

        showMessage(
            "申込みIDが取得できませんでした。",
            "error"
        );

        return;

    }


    /* ==================================
       確認
    ================================== */

    const confirmed =
        window.confirm(
            "この申し込みをキャンセルしますか？"
        );


    if (!confirmed) {

        return;

    }


    try {

        /* ==============================
           POSTデータ
        ============================== */

        const formData =
            new URLSearchParams();


        formData.append(
            "requestId",
            String(requestId)
        );


        /* ==============================
           Servletへ送信
        ============================== */

        const response =
            await fetch(
                "share-request-cancel",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded; charset=UTF-8",

                        "Accept":
                            "application/json"
                    },

                    body:
                        formData.toString()
                }
            );


        /* ==================================
           ログイン切れ
        ================================== */

        if (
            response.status === 401
        ) {

            window.location.href =
                "login.html";

            return;

        }


        /* ==================================
           JSON取得
        ================================== */

        let result;


        try {

            result =
                await response.json();

        } catch (jsonError) {

            throw new Error(
                "サーバーから正しい応答がありませんでした。"
            );

        }


        /* ==================================
           キャンセル成功
        ================================== */

        if (
            response.ok
            &&
            result.success
        ) {

            showMessage(
                result.message
                ||
                "申し込みをキャンセルしました。",
                "success"
            );


            /*
             * DBの状態を再取得
             */
            await loadShareRequests();


            return;

        }


        /* ==================================
           キャンセル失敗
        ================================== */

        showMessage(
            result.message
            ||
            "申し込みをキャンセルできませんでした。",
            "error"
        );


    } catch (error) {

        console.error(
            "申し込みキャンセルエラー:",
            error
        );


        showMessage(
            "申し込みのキャンセル中にエラーが発生しました。",
            "error"
        );

    }

}



/* ========================================
   メッセージ表示
======================================== */

function showMessage(
    message,
    type
) {

    const messageArea =
        document.getElementById(
            "messageArea"
        );


    if (!messageArea) {

        return;

    }


    messageArea.textContent =
        message;


    messageArea.className =
        "message-area " + type;


    messageArea.style.display =
        "block";


    setTimeout(
        function () {

            messageArea.style.display =
                "none";

        },
        3000
    );

}



/* ========================================
   日時整形
======================================== */

function formatDateTime(
    value
) {

    if (
        value === null
        ||
        value === undefined
        ||
        value === ""
        ||
        value === "null"
    ) {

        return "情報なし";

    }


    const date =
        new Date(value);


    if (
        Number.isNaN(
            date.getTime()
        )
    ) {

        /*
         * Java Timestamp形式
         * "2026-11-27 13:00:00"
         * にも対応
         */

        const normalized =
            String(value)
                .replace(" ", "T");


        const retryDate =
            new Date(normalized);


        if (
            !Number.isNaN(
                retryDate.getTime()
            )
        ) {

            return formatDateObject(
                retryDate
            );

        }


        return escapeHtml(
            String(value)
        );

    }


    return formatDateObject(
        date
    );

}



/* ========================================
   Date → 表示文字列
======================================== */

function formatDateObject(
    date
) {

    const year =
        date.getFullYear();


    const month =
        String(
            date.getMonth() + 1
        ).padStart(
            2,
            "0"
        );


    const day =
        String(
            date.getDate()
        ).padStart(
            2,
            "0"
        );


    const hour =
        String(
            date.getHours()
        ).padStart(
            2,
            "0"
        );


    const minute =
        String(
            date.getMinutes()
        ).padStart(
            2,
            "0"
        );


    return `${year}/${month}/${day} ${hour}:${minute}`;

}



/* ========================================
   利用時間整形
======================================== */

function formatUsageHours(
    value
) {

    const hours =
        Number(value);


    if (
        Number.isNaN(hours)
    ) {

        return "情報なし";

    }


    return `${hours.toFixed(2)}時間`;

}



/* ========================================
   料金整形
======================================== */

function formatPrice(
    value
) {

    const price =
        Number(value);


    if (
        Number.isNaN(price)
    ) {

        return "情報なし";

    }


    return `${price.toLocaleString()}円`;

}



/* ========================================
   HTMLエスケープ
======================================== */

function escapeHtml(
    value
) {

    if (
        value === null
        ||
        value === undefined
    ) {

        return "";

    }


    return String(value)
        .replace(
            /&/g,
            "&amp;"
        )
        .replace(
            /</g,
            "&lt;"
        )
        .replace(
            />/g,
            "&gt;"
        )
        .replace(
            /"/g,
            "&quot;"
        )
        .replace(
            /'/g,
            "&#039;"
        );

}
