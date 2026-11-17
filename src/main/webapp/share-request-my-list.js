document.addEventListener("DOMContentLoaded", function () {

    const requestList = document.getElementById("requestList");
    const message = document.getElementById("message");

    /*
     * =====================================================
     * 画面表示
     * =====================================================
     */
    function showMessage(text, type = "") {
        if (!message) {
            return;
        }

        message.textContent = text;
        message.className = "";

        if (type) {
            message.classList.add(type);
        }

        message.style.display = "block";
    }


    /*
     * =====================================================
     * HTMLエスケープ
     * =====================================================
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


    /*
     * =====================================================
     * 確認モーダル
     *
     * 「申込みをキャンセルしますか？」
     *
     * [ キャンセルする ] [ キャンセルしない ]
     * =====================================================
     */
    function showConfirmModal(title, messageText, confirmText, onConfirm) {

        // 既にモーダルが存在する場合は削除
        const existingModal = document.getElementById("customConfirmModal");

        if (existingModal) {
            existingModal.remove();
        }

        const overlay = document.createElement("div");
        overlay.id = "customConfirmModal";
        overlay.className = "custom-modal-overlay";

        overlay.innerHTML = `
            <div class="custom-modal-content" role="dialog" aria-modal="true">

                <div class="custom-modal-icon">
                    ?
                </div>

                <h2 class="custom-modal-title">
                    ${escapeHtml(title)}
                </h2>

                <p class="custom-modal-message">
                    ${escapeHtml(messageText)}
                </p>

                <div class="custom-modal-buttons">

                    <button
                        type="button"
                        class="custom-modal-confirm-button"
                        id="customModalConfirmButton">
                        ${escapeHtml(confirmText)}
                    </button>

                    <button
                        type="button"
                        class="custom-modal-cancel-button"
                        id="customModalCancelButton">
                        キャンセルしない
                    </button>

                </div>

            </div>
        `;

        document.body.appendChild(overlay);

        // 背景スクロール停止
        document.body.style.overflow = "hidden";

        const confirmButton =
            document.getElementById("customModalConfirmButton");

        const cancelButton =
            document.getElementById("customModalCancelButton");


        /*
         * キャンセルする
         */
        confirmButton.addEventListener("click", async function () {

            confirmButton.disabled = true;
            cancelButton.disabled = true;

            closeModal();

            if (typeof onConfirm === "function") {
                await onConfirm();
            }
        });


        /*
         * キャンセルしない
         */
        cancelButton.addEventListener("click", function () {
            closeModal();
        });


        /*
         * モーダル外をクリックした場合
         * → キャンセルしないと同じ扱い
         */
        overlay.addEventListener("click", function (event) {

            if (event.target === overlay) {
                closeModal();
            }
        });


        /*
         * ESCキー
         * → キャンセルしないと同じ扱い
         */
        function handleEscape(event) {

            if (event.key === "Escape") {
                closeModal();
            }
        }

        document.addEventListener("keydown", handleEscape);


        /*
         * モーダルを閉じる
         */
        function closeModal() {

            document.removeEventListener("keydown", handleEscape);

            if (overlay) {
                overlay.remove();
            }

            document.body.style.overflow = "";
        }
    }


    /*
     * =====================================================
     * メッセージモーダル
     * =====================================================
     */
    function showMessageModal(title, messageText, buttonText, onClose) {

        const existingModal =
            document.getElementById("customMessageModal");

        if (existingModal) {
            existingModal.remove();
        }

        const overlay = document.createElement("div");

        overlay.id = "customMessageModal";
        overlay.className = "custom-modal-overlay";

        overlay.innerHTML = `
            <div class="custom-modal-content" role="dialog" aria-modal="true">

                <div class="custom-modal-icon">
                    ✓
                </div>

                <h2 class="custom-modal-title">
                    ${escapeHtml(title)}
                </h2>

                <p class="custom-modal-message">
                    ${escapeHtml(messageText)}
                </p>

                <div class="custom-modal-buttons">

                    <button
                        type="button"
                        class="custom-modal-confirm-button"
                        id="customMessageCloseButton">
                        ${escapeHtml(buttonText)}
                    </button>

                </div>

            </div>
        `;

        document.body.appendChild(overlay);

        document.body.style.overflow = "hidden";

        const closeButton =
            document.getElementById("customMessageCloseButton");


        closeButton.addEventListener("click", function () {

            closeMessageModal();

            if (typeof onClose === "function") {
                onClose();
            }
        });


        overlay.addEventListener("click", function (event) {

            if (event.target === overlay) {

                closeMessageModal();

                if (typeof onClose === "function") {
                    onClose();
                }
            }
        });


        function handleEscape(event) {

            if (event.key === "Escape") {

                closeMessageModal();

                if (typeof onClose === "function") {
                    onClose();
                }
            }
        }

        document.addEventListener("keydown", handleEscape);


        function closeMessageModal() {

            document.removeEventListener("keydown", handleEscape);

            if (overlay) {
                overlay.remove();
            }

            document.body.style.overflow = "";
        }
    }


    /*
     * =====================================================
     * キャンセル処理
     * =====================================================
     */
    async function cancelRequest(requestId) {

        try {

            const formData = new FormData();

            formData.append("requestId", requestId);

            const response = await fetch("share-request-cancel", {
                method: "POST",
                body: formData
            });


            /*
             * ログイン切れ
             */
            if (response.status === 401) {

                showMessageModal(
                    "ログインが必要です",
                    "ログインしてからもう一度操作してください。",
                    "ログイン画面へ",
                    function () {
                        window.location.href = "login.html";
                    }
                );

                return;
            }


            /*
             * サーバーエラー
             */
            if (!response.ok) {

                throw new Error(
                    "キャンセル処理に失敗しました。"
                );
            }


            /*
             * キャンセル成功
             */
            showMessageModal(
                "キャンセルしました",
                "ペットシェアの申込みをキャンセルしました。",
                "OK",
                function () {
                    loadRequests();
                }
            );

        } catch (error) {

            console.error(error);

            showMessageModal(
                "エラー",
                "申込みのキャンセルに失敗しました。もう一度お試しください。",
                "OK"
            );
        }
    }


    /*
     * =====================================================
     * 申込み一覧取得
     * =====================================================
     */
    async function loadRequests() {

        try {

            if (requestList) {
                requestList.innerHTML = `
                    <div class="loading-message">
                        読み込み中...
                    </div>
                `;
            }

            const response =
                await fetch("share-request-my-list", {
                    method: "GET"
                });


            /*
             * ログインしていない
             */
            if (response.status === 401) {

                if (requestList) {
                    requestList.innerHTML = "";
                }

                showMessageModal(
                    "ログインが必要です",
                    "ログインしてから申込み状況を確認してください。",
                    "ログイン画面へ",
                    function () {
                        window.location.href = "login.html";
                    }
                );

                return;
            }


            /*
             * その他のエラー
             */
            if (!response.ok) {
                throw new Error(
                    "申込み一覧の取得に失敗しました。"
                );
            }


            const data = await response.json();

            renderRequests(data);

        } catch (error) {

            console.error(error);

            if (requestList) {

                requestList.innerHTML = `
                    <div class="error-message">
                        申込み状況の取得に失敗しました。
                    </div>
                `;
            }
        }
    }


    /*
     * =====================================================
     * 申込み一覧表示
     * =====================================================
     */
    function renderRequests(requests) {

        if (!requestList) {
            return;
        }

        requestList.innerHTML = "";


        /*
         * 申込みがない場合
         */
        if (!requests || requests.length === 0) {

            requestList.innerHTML = `
                <div class="empty-message">
                    現在、ペットシェアの申込みはありません。
                </div>
            `;

            return;
        }


        /*
         * 申込みを1件ずつ表示
         */
        requests.forEach(function (request) {

            const card = document.createElement("div");

            card.className = "request-card";


            /*
             * ステータス
             */
            let statusText = "";
            let statusClass = "";

            switch (request.status) {

                case "pending":
                    statusText = "承認待ち";
                    statusClass = "pending";
                    break;

                case "approved":
                    statusText = "承認済み";
                    statusClass = "approved";
                    break;

                case "rejected":
                    statusText = "拒否済み";
                    statusClass = "rejected";
                    break;

                case "cancelled":
                    statusText = "キャンセル済み";
                    statusClass = "cancelled";
                    break;

                default:
                    statusText = request.status || "";
                    statusClass = "";
            }


            /*
             * ペット画像
             */
            let petImage = "";

            if (request.petImage) {

                petImage = `
                    <img
                        src="${escapeHtml(request.petImage)}"
                        alt="${escapeHtml(request.petName || "ペット")}"
                        class="request-pet-image">
                `;

            } else {

                petImage = `
                    <div class="request-pet-image no-image">
                        🐾
                    </div>
                `;
            }


            /*
             * ペット情報
             */
            const petName =
                escapeHtml(request.petName || "名前なし");

            const ownerName =
                escapeHtml(request.ownerName || "不明");

            const startTime =
                escapeHtml(request.startTime || "");

            const endTime =
                escapeHtml(request.endTime || "");


            /*
             * カードHTML
             */
            card.innerHTML = `

                <div class="request-card-main">

                    <div class="request-pet">

                        ${petImage}

                        <div class="request-pet-info">

                            <h3 class="request-pet-name">
                                ${petName}
                            </h3>

                            <p class="request-owner">
                                飼い主：${ownerName}
                            </p>

                        </div>

                    </div>


                    <div class="request-status ${statusClass}">
                        ${statusText}
                    </div>

                </div>


                <div class="request-detail">

                    ${
                        startTime
                            ? `
                                <div class="request-info-row">
                                    <span class="label">
                                        利用開始
                                    </span>

                                    <span class="value">
                                        ${startTime}
                                    </span>
                                </div>
                              `
                            : ""
                    }


                    ${
                        endTime
                            ? `
                                <div class="request-info-row">
                                    <span class="label">
                                        利用終了
                                    </span>

                                    <span class="value">
                                        ${endTime}
                                    </span>
                                </div>
                              `
                            : ""
                    }

                </div>
            `;


            /*
             * =================================================
             * 承認待ちの場合のみキャンセルボタンを表示
             * =================================================
             */
            if (request.status === "pending") {

                const cancelButton =
                    document.createElement("button");

                cancelButton.type = "button";

                cancelButton.className =
                    "cancel-button";

                cancelButton.textContent =
                    "申込みをキャンセル";


                /*
                 * キャンセルボタンをクリック
                 */
                cancelButton.addEventListener(
                    "click",
                    function () {

                        showConfirmModal(
                            "申込みをキャンセルしますか？",
                            "このペットシェア申込みをキャンセルします。",
                            "キャンセルする",
                            async function () {

                                await cancelRequest(
                                    request.requestId
                                );

                            }
                        );

                    }
                );


                card.appendChild(cancelButton);
            }


            requestList.appendChild(card);
        });
    }


    /*
     * =====================================================
     * 初期処理
     * =====================================================
     */
    loadRequests();

});