// ========================================
// ペットシェアリクエスト
// ========================================

document.addEventListener("DOMContentLoaded", () => {

    loadShareRequests();

});


// ========================================
// リクエスト一覧取得
// ========================================

async function loadShareRequests() {

    const requestList =
        document.getElementById("requestList");

    try {

        // --------------------------------
        // データ取得
        // --------------------------------

        const response =
            await fetch("share-request-list");


        // --------------------------------
        // ログイン切れ
        // --------------------------------

        if (response.status === 401) {

            window.location.href =
                "login.html";

            return;
        }


        // --------------------------------
        // HTTPエラー
        // --------------------------------

        if (!response.ok) {

            throw new Error(
                "リクエスト一覧の取得に失敗しました。"
            );

        }


        // --------------------------------
        // JSON取得
        // --------------------------------

        const requests =
            await response.json();


        // --------------------------------
        // 表示
        // --------------------------------

        displayRequests(requests);


    } catch (error) {

        console.error(error);

        requestList.innerHTML = `
            <p class="empty-message">
                リクエストの取得に失敗しました。
            </p>
        `;

    }

}


// ========================================
// リクエスト一覧表示
// ========================================

function displayRequests(requests) {

    const requestList =
        document.getElementById("requestList");


    // ------------------------------------
    // リクエストなし
    // ------------------------------------

    if (!requests ||
        requests.length === 0) {

        requestList.innerHTML = `
            <p class="empty-message">
                現在、ペットシェアリクエストはありません。
            </p>
        `;

        return;
    }


    // ------------------------------------
    // 一覧をクリア
    // ------------------------------------

    requestList.innerHTML = "";


    // ------------------------------------
    // カード作成
    // ------------------------------------

    requests.forEach(request => {

        const card =
            document.createElement("div");

        card.className =
            "request-card";


        // --------------------------------
        // ステータス
        // --------------------------------

        let statusText = "";
        let statusClass = "";

        switch (request.status) {

            case "pending":

                statusText =
                    "承認待ち";

                statusClass =
                    "status-pending";

                break;


            case "approved":

                statusText =
                    "承認済み";

                statusClass =
                    "status-approved";

                break;


            case "rejected":

                statusText =
                    "拒否済み";

                statusClass =
                    "status-rejected";

                break;


            case "cancelled":

                statusText =
                    "キャンセル済み";

                statusClass =
                    "status-rejected";

                break;


            default:

                statusText =
                    request.status;

                statusClass = "";

        }


        // --------------------------------
        // 利用時間
        // --------------------------------

        const usageHours =
            formatUsageHours(
                request.usageHours
            );


        // ========================================
        // ★ ペット画像
        // ========================================

        let petImageHtml = "";

        if (
            request.petImage &&
            String(request.petImage).trim() !== ""
        ) {

            let imageSrc =
                String(request.petImage);


            // --------------------------------
            // Base64だけの場合
            // --------------------------------

            if (
                !imageSrc.startsWith("data:image/")
            ) {

                imageSrc =
                    "data:image/jpeg;base64,"
                    + imageSrc;
            }


            petImageHtml = `
                <div class="pet-image-box">

                    <img
                        src="${escapeHtml(imageSrc)}"
                        alt="${escapeHtml(
                            request.petName ||
                            "ペット"
                        )}"
                        class="pet-request-image">

                </div>
            `;

        } else {

            // --------------------------------
            // 画像がない場合
            // --------------------------------

            petImageHtml = `
                <div class="pet-image-box pet-image-default">
                    🐾
                </div>
            `;
        }


        // --------------------------------
        // カードHTML
        // --------------------------------

        card.innerHTML = `

            <div class="request-pet-info">

                ${petImageHtml}

                <h2 class="request-pet-name">
                    ${escapeHtml(
                        request.petName
                    )}
                </h2>

            </div>


            <div class="request-info">

                <div class="request-row">

                    <span class="request-label">
                        申込者
                    </span>

                    <span class="request-value">
                        ${escapeHtml(
                            request.borrowerName
                        )}
                    </span>

                </div>


                <div class="request-row">

                    <span class="request-label">
                        利用開始
                    </span>

                    <span class="request-value">
                        ${escapeHtml(
                            request.startDatetime
                        )}
                    </span>

                </div>


                <div class="request-row">

                    <span class="request-label">
                        利用終了
                    </span>

                    <span class="request-value">
                        ${escapeHtml(
                            request.endDatetime
                        )}
                    </span>

                </div>


                <div class="request-row">

                    <span class="request-label">
                        利用時間
                    </span>

                    <span class="request-value">
                        ${usageHours}
                    </span>

                </div>


                <div class="request-row">

                    <span class="request-label">
                        状態
                    </span>

                    <span class="request-value">

                        <span
                            class="request-status ${statusClass}">
                            ${statusText}
                        </span>

                    </span>

                </div>

            </div>


            ${
                request.status === "pending"

                ? createActionButtons(
                    request.requestId
                )

                : createProcessedMessage(
                    statusText
                )
            }

        `;


        requestList.appendChild(card);

    });

}


// ========================================
// 承認・拒否ボタン
// ========================================

function createActionButtons(
    requestId
) {

    return `

        <div class="request-buttons">

            <button
                type="button"
                class="approve-button"
                onclick="updateRequestStatus(
                    ${requestId},
                    'approved'
                )">

                承認

            </button>


            <button
                type="button"
                class="reject-button"
                onclick="updateRequestStatus(
                    ${requestId},
                    'rejected'
                )">

                拒否

            </button>

        </div>

    `;

}


// ========================================
// 処理済みメッセージ
// ========================================

function createProcessedMessage(
    statusText
) {

    return `

        <p class="processed-message">
            このリクエストは
            「${escapeHtml(statusText)}」
            です。
        </p>

    `;

}


// ========================================
// 承認・拒否処理
// ========================================

async function updateRequestStatus(
    requestId,
    status
) {

    // ------------------------------------
    // 確認メッセージ
    // ------------------------------------

    let confirmMessage = "";

    if (status === "approved") {

        confirmMessage =
            "このシェア申込みを承認しますか？";

    } else {

        confirmMessage =
            "このシェア申込みを拒否しますか？";

    }


    // ------------------------------------
    // 確認
    // ------------------------------------

    if (!confirm(confirmMessage)) {

        return;

    }


    try {

        // --------------------------------
        // POSTデータ
        // --------------------------------

        const formData =
            new URLSearchParams();

        formData.append(
            "requestId",
            requestId
        );


        // ==========================================
        // 承認処理
        // ==========================================

        if (status === "approved") {

            const response =
                await fetch(
                    "share-request-approve",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/x-www-form-urlencoded"
                        },

                        body:
                            formData.toString()
                    }
                );


            // --------------------------------
            // ログイン切れ
            // --------------------------------

            if (response.status === 401) {

                window.location.href =
                    "login.html";

                return;
            }


            // --------------------------------
            // HTTPエラー
            // --------------------------------

            if (!response.ok) {

                throw new Error(
                    "承認処理に失敗しました。"
                );

            }


            // --------------------------------
            // JSON取得
            // --------------------------------

            const result =
                await response.json();


            // --------------------------------
            // 成功
            // --------------------------------

            if (result.success) {

                showMessage(
                    result.message,
                    "success"
                );


                // 最新状態を再取得

                await loadShareRequests();

            } else {

                showMessage(
                    result.message ||
                    "承認できませんでした。",
                    "error"
                );

            }


            return;

        }


        // ==========================================
        // 拒否処理
        // ==========================================

        formData.append(
            "status",
            "rejected"
        );


        const response =
            await fetch(
                "share-request-status",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body:
                        formData.toString()
                }
            );


        // --------------------------------
        // ログイン切れ
        // --------------------------------

        if (response.status === 401) {

            window.location.href =
                "login.html";

            return;
        }


        // --------------------------------
        // HTTPエラー
        // --------------------------------

        if (!response.ok) {

            throw new Error(
                "拒否処理に失敗しました。"
            );

        }


        // --------------------------------
        // JSON取得
        // --------------------------------

        const result =
            await response.json();


        // --------------------------------
        // 成功
        // --------------------------------

        if (result.success) {

            showMessage(
                result.message,
                "success"
            );


            // 最新状態を再取得

            await loadShareRequests();

        } else {

            showMessage(
                result.message,
                "error"
            );

        }


    } catch (error) {

        console.error(error);

        showMessage(
            "処理中にエラーが発生しました。",
            "error"
        );

    }

}


// ========================================
// メッセージ表示
// ========================================

function showMessage(
    message,
    type
) {

    const messageElement =
        document.getElementById("message");


    messageElement.textContent =
        message;


    messageElement.className =
        "message";


    if (type === "success") {

        messageElement.classList.add(
            "message-success"
        );

    } else {

        messageElement.classList.add(
            "message-error"
        );

    }


    messageElement.style.display =
        "block";


    // ------------------------------------
    // 3秒後に非表示
    // ------------------------------------

    setTimeout(() => {

        messageElement.style.display =
            "none";

    }, 3000);

}


// ========================================
// 利用時間表示
// ========================================

function formatUsageHours(
    hours
) {

    const number =
        Number(hours);


    if (Number.isInteger(number)) {

        return `${number}時間`;

    }


    return `${number.toFixed(2)}時間`;

}


// ========================================
// HTMLエスケープ
// ========================================

function escapeHtml(
    value
) {

    if (
        value === null ||
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