document.addEventListener("DOMContentLoaded", function () {
    loadShareRequests();
});


/**
 * 自分が申し込んだペットシェアを取得
 */
async function loadShareRequests() {

    const requestList = document.getElementById("requestList");
    const message = document.getElementById("message");

    try {

        const response = await fetch("share-request-my-list");

        // ログインしていない場合
        if (response.status === 401) {
            window.location.href = "login.html";
            return;
        }

        if (!response.ok) {
            throw new Error("データの取得に失敗しました。");
        }

        const requests = await response.json();

        displayRequests(requests);

    } catch (error) {

        console.error(error);

        requestList.innerHTML = `
            <p class="empty-message">
                申込み状況の取得に失敗しました。
            </p>
        `;

        if (message) {
            showMessage(
                "申込み状況の取得に失敗しました。",
                "error"
            );
        }
    }
}


/**
 * 申込み一覧を表示
 */
function displayRequests(requests) {

    const requestList = document.getElementById("requestList");

    // 申込みがない場合
    if (!requests || requests.length === 0) {

        requestList.innerHTML = `
            <p class="empty-message">
                ペットシェアの申込みはありません。
            </p>
        `;

        return;
    }

    requestList.innerHTML = "";

    requests.forEach(function (request) {

        let statusText = "";
        let statusClass = "";

        /*
         * ステータス表示
         */
        switch (request.status) {

            case "pending":
                statusText = "承認待ち";
                statusClass = "status-pending";
                break;

            case "approved":
                statusText = "承認済み";
                statusClass = "status-approved";
                break;

            case "rejected":
                statusText = "拒否済み";
                statusClass = "status-rejected";
                break;

            default:
                statusText = request.status;
                statusClass = "";
                break;
        }


        /*
         * ペット画像
         */
        let petImageHtml = "";

        if (
            request.petImage &&
            request.petImage.trim() !== ""
        ) {

            let imageSrc = request.petImage;

            /*
             * DBにBase64だけ保存されている場合
             * data:image/jpeg;base64, を付ける
             */
            if (!imageSrc.startsWith("data:image/")) {
                imageSrc =
                    "data:image/jpeg;base64," + imageSrc;
            }

            petImageHtml = `
                <div class="pet-image-box">
                    <img
                        src="${escapeHtml(imageSrc)}"
                        alt="${escapeHtml(request.petName || "ペット")}"
                        class="pet-request-image"
                    >
                </div>
            `;

        } else {

            /*
             * 画像が登録されていない場合
             */
            petImageHtml = `
                <div class="pet-image-box pet-image-default">
                    🐾
                </div>
            `;
        }


        /*
         * ペット名
         */
        const petName =
            request.petName &&
            request.petName.trim() !== ""
                ? request.petName
                : "名前未登録";


        /*
         * 申込みカード
         */
        const card = document.createElement("div");

        card.className = "request-card";


        /*
         * ここが今回変更した部分
         *
         * 「ペットID：○○」
         * ↓
         * 「ペット画像＋ペット名」
         *
         * その他の情報は今まで通り
         */
        card.innerHTML = `

            <div class="request-header">

                <div class="request-pet-info">

                    ${petImageHtml}

                    <h2 class="request-pet-name">
                        ${escapeHtml(petName)}
                    </h2>

                </div>

                <span class="request-status ${statusClass}">
                    ${escapeHtml(statusText)}
                </span>

            </div>


            <div class="request-info">

                <div class="request-row">

                    <span class="request-label">
                        利用開始日時
                    </span>

                    <span class="request-value">
                        ${escapeHtml(
                            formatDateTime(
                                request.startDatetime
                            )
                        )}
                    </span>

                </div>


                <div class="request-row">

                    <span class="request-label">
                        利用終了日時
                    </span>

                    <span class="request-value">
                        ${escapeHtml(
                            formatDateTime(
                                request.endDatetime
                            )
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
                        申込み日時
                    </span>

                    <span class="request-value">
                        ${escapeHtml(
                            formatDateTime(
                                request.createdAt
                            )
                        )}
                    </span>

                </div>


                <div class="request-row">

                    <span class="request-label">
                        ステータス
                    </span>

                    <span class="request-value">

                        <span class="request-status ${statusClass}">
                            ${escapeHtml(statusText)}
                        </span>

                    </span>

                </div>

            </div>

        `;


        requestList.appendChild(card);
    });
}


/**
 * 日時を見やすい形式に変換
 */
function formatDateTime(dateTime) {

    if (!dateTime) {
        return "";
    }

    /*
     * Timestampの形式
     * 2026-11-17 14:30:00
     */
    return dateTime
        .replace("T", " ")
        .substring(0, 16);
}


/**
 * 利用時間を表示
 */
function formatUsageHours(hours) {

    const value = Number(hours);

    if (Number.isNaN(value)) {
        return "";
    }

    if (value % 1 === 0) {
        return `${value}時間`;
    }

    return `${value.toFixed(2)}時間`;
}


/**
 * HTMLエスケープ
 */
function escapeHtml(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


/**
 * メッセージ表示
 */
function showMessage(text, type) {

    const message = document.getElementById("message");

    if (!message) {
        return;
    }

    message.textContent = text;

    message.className =
        "message " +
        (type === "error"
            ? "message-error"
            : "message-success");

    message.style.display = "block";
}