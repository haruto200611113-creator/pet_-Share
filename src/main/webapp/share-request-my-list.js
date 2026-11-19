document.addEventListener("DOMContentLoaded", function () {

    const requestList = document.getElementById("requestList");
    const message = document.getElementById("message");


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
     * 日時を見やすく表示
     * =====================================================
     */
    function formatDateTime(dateTime) {

        if (!dateTime) {
            return "";
        }

        return String(dateTime)
            .replace("T", " ")
            .substring(0, 16);
    }


    /*
     * =====================================================
     * 利用時間を表示
     * =====================================================
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


    /*
     * =====================================================
     * シェア料金を表示
     * =====================================================
     */
    function formatSharePrice(price) {

        const value = Number(price);

        if (Number.isNaN(value)) {
            return "";
        }

        return `${value.toLocaleString("ja-JP")}円`;
    }


    /*
     * =====================================================
     * メッセージモーダル
     * =====================================================
     */
    function showMessageModal(
        title,
        messageText,
        buttonText,
        onClose
    ) {

        const existingModal =
            document.getElementById("customMessageModal");

        if (existingModal) {
            existingModal.remove();
        }


        const overlay =
            document.createElement("div");

        overlay.id = "customMessageModal";
        overlay.className = "custom-modal-overlay";


        overlay.innerHTML = `

            <div
                class="custom-modal-content"
                role="dialog"
                aria-modal="true">

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
            document.getElementById(
                "customMessageCloseButton"
            );


        function closeMessageModal() {

            document.removeEventListener(
                "keydown",
                handleEscape
            );

            overlay.remove();

            document.body.style.overflow = "";

            if (typeof onClose === "function") {
                onClose();
            }
        }


        closeButton.addEventListener(
            "click",
            closeMessageModal
        );


        overlay.addEventListener(
            "click",
            function (event) {

                if (event.target === overlay) {
                    closeMessageModal();
                }
            }
        );


        function handleEscape(event) {

            if (event.key === "Escape") {
                closeMessageModal();
            }
        }


        document.addEventListener(
            "keydown",
            handleEscape
        );
    }


    /*
     * =====================================================
     * 確認モーダル
     * =====================================================
     *
     * [ キャンセルする ] [ キャンセルしない ]
     * =====================================================
     */
    function showConfirmModal(
        title,
        messageText,
        onConfirm
    ) {

        const existingModal =
            document.getElementById(
                "customConfirmModal"
            );

        if (existingModal) {
            existingModal.remove();
        }


        const overlay =
            document.createElement("div");

        overlay.id = "customConfirmModal";
        overlay.className = "custom-modal-overlay";


        overlay.innerHTML = `

            <div
                class="custom-modal-content"
                role="dialog"
                aria-modal="true">

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

                        キャンセルする

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

        document.body.style.overflow = "hidden";


        const confirmButton =
            document.getElementById(
                "customModalConfirmButton"
            );

        const cancelButton =
            document.getElementById(
                "customModalCancelButton"
            );


        /*
         * キャンセルする
         */
        confirmButton.addEventListener(
            "click",
            async function () {

                confirmButton.disabled = true;
                cancelButton.disabled = true;

                closeModal();

                if (typeof onConfirm === "function") {
                    await onConfirm();
                }
            }
        );


        /*
         * キャンセルしない
         */
        cancelButton.addEventListener(
            "click",
            function () {
                closeModal();
            }
        );


        /*
         * モーダル外クリック
         */
        overlay.addEventListener(
            "click",
            function (event) {

                if (event.target === overlay) {
                    closeModal();
                }
            }
        );


        /*
         * ESCキー
         */
        function handleEscape(event) {

            if (event.key === "Escape") {
                closeModal();
            }
        }


        document.addEventListener(
            "keydown",
            handleEscape
        );


        /*
         * モーダルを閉じる
         */
        function closeModal() {

            document.removeEventListener(
                "keydown",
                handleEscape
            );

            overlay.remove();

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

            const formData =
                new FormData();

            formData.append(
                "requestId",
                requestId
            );


            const response =
                await fetch(
                    "share-request-cancel",
                    {
                        method: "POST",
                        body: formData
                    }
                );


            /*
             * ログイン切れ
             */
            if (response.status === 401) {

                showMessageModal(
                    "ログインが必要です",
                    "ログインしてからもう一度操作してください。",
                    "ログイン画面へ",
                    function () {
                        window.location.href =
                            "login.html";
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


            const result =
                await response.json();


            /*
             * キャンセル失敗
             */
            if (!result.success) {

                showMessageModal(
                    "キャンセルできませんでした",
                    result.message ||
                    "申込みのキャンセルに失敗しました。",
                    "OK"
                );

                return;
            }


            /*
             * キャンセル成功
             */
            showMessageModal(
                "キャンセルしました",
                "ペットシェアの申込みをキャンセルしました。",
                "OK",
                function () {
                    loadShareRequests();
                }
            );


        } catch (error) {

            console.error(
                "キャンセル処理エラー:",
                error
            );


            showMessageModal(
                "エラー",
                "システムエラーが発生しました。",
                "OK"
            );
        }
    }


    /*
     * =====================================================
     * 申込み一覧取得
     * =====================================================
     */
    async function loadShareRequests() {

        try {

            if (requestList) {

                requestList.innerHTML = `

                    <p class="loading-message">
                        申込み状況を読み込んでいます……
                    </p>

                `;
            }


            const response =
                await fetch(
                    "share-request-my-list"
                );


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
                        window.location.href =
                            "login.html";
                    }
                );

                return;
            }


            /*
             * 通信エラー
             */
            if (!response.ok) {

                throw new Error(
                    "データの取得に失敗しました。"
                );
            }


            const requests =
                await response.json();


            displayRequests(requests);


        } catch (error) {

            console.error(error);


            if (requestList) {

                requestList.innerHTML = `

                    <p class="empty-message">
                        申込み状況の取得に失敗しました。
                    </p>

                `;
            }


            if (message) {

                message.textContent =
                    "申込み状況の取得に失敗しました。";

                message.className =
                    "message message-error";

                message.style.display =
                    "block";
            }
        }
    }


    /*
     * =====================================================
     * 申込み一覧表示
     * =====================================================
     */
    function displayRequests(requests) {

        if (!requestList) {
            return;
        }


        /*
         * 申込みがない場合
         */
        if (!requests ||
            requests.length === 0) {

            requestList.innerHTML = `

                <p class="empty-message">
                    ペットシェアの申込みはありません。
                </p>

            `;

            return;
        }


        requestList.innerHTML = "";


        /*
         * 1件ずつ表示
         */
        requests.forEach(function (request) {

            /*
             * =============================================
             * ステータス
             * =============================================
             */
            let statusText = "";
            let statusClass = "";


            switch (request.status) {

                case "pending":

                    statusText =
                        "承認待ち";

                    /*
                     * CSSの .request-status.pending
                     * に合わせる
                     */
                    statusClass =
                        "pending";

                    break;


                case "approved":

                    statusText =
                        "承認済み";

                    /*
                     * CSSの .request-status.approved
                     * に合わせる
                     */
                    statusClass =
                        "approved";

                    break;


                case "rejected":

                    statusText =
                        "拒否済み";

                    /*
                     * CSSの .request-status.rejected
                     * に合わせる
                     */
                    statusClass =
                        "rejected";

                    break;


                case "cancelled":

                    statusText =
                        "キャンセル済み";

                    /*
                     * CSSの .request-status.cancelled
                     * に合わせる
                     */
                    statusClass =
                        "cancelled";

                    break;


                default:

                    statusText =
                        request.status || "";

                    statusClass =
                        "";
            }


            /*
             * =============================================
             * ペット画像
             * =============================================
             */
            let petImageHtml = "";


            if (
                request.petImage &&
                String(request.petImage).trim() !== ""
            ) {

                let imageSrc =
                    request.petImage;


                /*
                 * Base64だけの場合
                 */
                if (
                    !String(imageSrc)
                        .startsWith("data:image/")
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

                petImageHtml = `

                    <div
                        class="pet-image-box pet-image-default">

                        🐾

                    </div>

                `;
            }


            /*
             * =============================================
             * ペット名
             * =============================================
             */
            const petName =
                request.petName &&
                String(request.petName).trim() !== ""
                    ? request.petName
                    : "名前未登録";


            /*
             * =============================================
             * 飼い主
             * =============================================
             *
             * Java側から ownerName が返ってくる
             * 場合に表示
             */
            const ownerName =
                request.ownerName &&
                String(request.ownerName).trim() !== ""
                    ? request.ownerName
                    : "飼い主情報なし";


            /*
             * =============================================
             * 利用開始日時
             * =============================================
             */
            const startDatetime =
                formatDateTime(
                    request.startDatetime
                );


            /*
             * =============================================
             * 利用終了日時
             * =============================================
             */
            const endDatetime =
                formatDateTime(
                    request.endDatetime
                );


            /*
             * =============================================
             * 利用時間
             * =============================================
             */
            const usageHours =
                formatUsageHours(
                    request.usageHours
                );


            /*
             * =============================================
             * シェア料金
             * =============================================
             *
             * DBに保存されている share_price を
             * そのまま表示する
             */
            const sharePrice =
                formatSharePrice(
                    request.sharePrice
                );


            /*
             * =============================================
             * 申込み日時
             * =============================================
             */
            const createdAt =
                formatDateTime(
                    request.createdAt
                );


            /*
             * =============================================
             * カード作成
             * =============================================
             */
            const card =
                document.createElement("div");

            card.className =
                "request-card";


            card.innerHTML = `

                <!-- ==============================
                     ペット情報
                =============================== -->

                <div class="request-header">

                    <div class="request-pet-info">

                        ${petImageHtml}


                        <h2 class="request-pet-name">

                            ${escapeHtml(
                                petName
                            )}

                        </h2>

                    </div>


                    <span
                        class="request-status ${statusClass}">

                        ${escapeHtml(
                            statusText
                        )}

                    </span>

                </div>


                <!-- ==============================
                     申込み情報
                =============================== -->

                <div class="request-info">


                    <!-- 飼い主 -->

                    <div class="request-row">

                        <span class="request-label">
                            飼い主
                        </span>

                        <span class="request-value">

                            ${escapeHtml(
                                ownerName
                            )}

                        </span>

                    </div>


                    <!-- 利用開始日時 -->

                    <div class="request-row">

                        <span class="request-label">
                            利用開始日時
                        </span>

                        <span class="request-value">

                            ${escapeHtml(
                                startDatetime
                            )}

                        </span>

                    </div>


                    <!-- 利用終了日時 -->

                    <div class="request-row">

                        <span class="request-label">
                            利用終了日時
                        </span>

                        <span class="request-value">

                            ${escapeHtml(
                                endDatetime
                            )}

                        </span>

                    </div>


                    <!-- 利用時間 -->

                    <div class="request-row">

                        <span class="request-label">
                            利用時間
                        </span>

                        <span class="request-value">

                            ${escapeHtml(
                                usageHours
                            )}

                        </span>

                    </div>


                    <!-- シェア料金 -->

                    <div class="request-row">

                        <span class="request-label">
                            シェア料金
                        </span>

                        <span class="request-value">

                            ${escapeHtml(
                                sharePrice
                            )}

                        </span>

                    </div>


                    <!-- 申込み日時 -->

                    <div class="request-row">

                        <span class="request-label">
                            申込み日時
                        </span>

                        <span class="request-value">

                            ${escapeHtml(
                                createdAt
                            )}

                        </span>

                    </div>


                    <!-- ステータス -->

                    <div class="request-row">

                        <span class="request-label">
                            ステータス
                        </span>

                        <span class="request-value">

                            <span
                                class="request-status ${statusClass}">

                                ${escapeHtml(
                                    statusText
                                )}

                            </span>

                        </span>

                    </div>

                </div>
`;


            /*
             * =============================================
             * 承認待ちの場合だけ
             * キャンセルボタンを表示
             * =============================================
             */
            if (
                request.status === "pending"
            ) {

                const buttons =
                    document.createElement(
                        "div"
                    );


                buttons.className =
                    "request-buttons";


                const cancelButton =
                    document.createElement(
                        "button"
                    );


                cancelButton.type =
                    "button";


                cancelButton.className =
                    "cancel-button";


                cancelButton.textContent =
                    "申込みをキャンセル";


                /*
                 * キャンセルボタン
                 */
                cancelButton.addEventListener(
                    "click",
                    function () {

                        showConfirmModal(
                            "申込みをキャンセルしますか？",
                            "このペットシェア申込みをキャンセルします。",
                            async function () {

                                await cancelRequest(
                                    request.requestId
                                );

                            }
                        );

                    }
                );


                buttons.appendChild(
                    cancelButton
                );


                card.appendChild(
                    buttons
                );
            }


            requestList.appendChild(
                card
            );
        });
    }


    /*
     * =====================================================
     * 初期処理
     * =====================================================
     */
    loadShareRequests();

});