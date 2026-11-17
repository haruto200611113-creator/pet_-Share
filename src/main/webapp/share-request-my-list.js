document.addEventListener("DOMContentLoaded", () => {
    loadShareRequests();
});


// ========================================
// 申込み一覧取得
// ========================================

async function loadShareRequests() {

    const requestList =
        document.getElementById("requestList");

    try {

        const response =
            await fetch("share-request-my-list");

        // ログインしていない
        if (response.status === 401) {
            window.location.href = "login.html";
            return;
        }

        if (!response.ok) {
            throw new Error("データ取得に失敗しました。");
        }

        const requests =
            await response.json();

        displayRequests(requests);

    } catch (error) {

        console.error(error);

        requestList.innerHTML = `
            <p class="empty-message">
                申込み状況の取得に失敗しました。
            </p>
        `;
    }
}


// ========================================
// 申込み一覧表示
// ========================================

function displayRequests(requests) {

    const requestList =
        document.getElementById("requestList");

    if (!requests || requests.length === 0) {

        requestList.innerHTML = `
            <p class="empty-message">
                現在、ペットシェアの申込みはありません。
            </p>
        `;

        return;
    }

    requestList.innerHTML = "";

    requests.forEach(request => {

        const card =
            document.createElement("div");

        card.className = "request-card";

        const statusInfo =
            getStatusInfo(request.status);

        card.innerHTML = `
            <div class="request-header">

                <h2 class="request-pet-name">
                    🐾 ペットID：${escapeHtml(
                        String(request.petId)
                    )}
                </h2>

                <span class="request-status ${statusInfo.className}">
                    ${statusInfo.text}
                </span>

            </div>

            <div class="request-info">

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
                        申込み日時
                    </span>

                    <span class="request-value">
                        ${formatDateTime(
                            request.createdAt
                        )}
                    </span>
                </div>

                <div class="request-row">
                    <span class="request-label">
                        申込み状況
                    </span>

                    <span class="request-value ${statusInfo.className}">
                        ${statusInfo.text}
                    </span>
                </div>

            </div>
        `;

        requestList.appendChild(card);
    });
}


// ========================================
// ステータス表示
// ========================================

function getStatusInfo(status) {

    switch (status) {

        case "pending":
            return {
                text: "承認待ち",
                className: "status-pending"
            };

        case "approved":
            return {
                text: "承認済み",
                className: "status-approved"
            };

        case "rejected":
            return {
                text: "拒否済み",
                className: "status-rejected"
            };

        default:
            return {
                text: "不明",
                className: ""
            };
    }
}


// ========================================
// 日時表示
// ========================================

function formatDateTime(datetime) {

    if (!datetime) {
        return "";
    }

    const date =
        new Date(datetime.replace(" ", "T"));

    if (isNaN(date.getTime())) {
        return escapeHtml(datetime);
    }

    const year =
        date.getFullYear();

    const month =
        String(date.getMonth() + 1)
            .padStart(2, "0");

    const day =
        String(date.getDate())
            .padStart(2, "0");

    const hour =
        String(date.getHours())
            .padStart(2, "0");

    const minute =
        String(date.getMinutes())
            .padStart(2, "0");

    return `${year}/${month}/${day} ${hour}:${minute}`;
}


// ========================================
// 利用時間表示
// ========================================

function formatUsageHours(hours) {

    const value =
        Number(hours);

    if (Number.isInteger(value)) {
        return `${value}時間`;
    }

    return `${value}時間`;
}


// ========================================
// HTMLエスケープ
// ========================================

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